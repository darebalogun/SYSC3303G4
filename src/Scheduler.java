import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Scheduler.java SYSC3303G4
 * 
 * @author Dare Balogun | 101062340
 * 
 * @version Iteration 1
 * 
 *          The Scheduler receives the information/requests from the
 *          FloorSubsystem and sends them to ElevatorSubSystem. It also receives responses
 *          for arrival information from the ElevatorSubSytem and forwards the responses
 *          to the FloorSubsystem. 
 * 
 */

public class  Scheduler{

	// Number of elevators to keep track of
	private int ELEVATOR_COUNT = 4;

	// List of input events received from Floor Subsystem to be handled
	private Queue<InputEvent> eventList;

	private ArrayList<ElevatorState> elevatorStates;
	
	private static final String INPUT_PATH = "src/InputEvents.txt";

	// The following ArrayLists track the position of the elevators based on arrival information received from the elevator subsystem
	private ArrayList<InputEvent> upRequests;

	private ArrayList<InputEvent> downRequests;

	private ArrayList<Integer> upList;

	private ArrayList<Integer> upPosition;

	private ArrayList<Integer> downList;

	private ArrayList<Integer> downPosition;

	private ArrayList<ArrayList<Integer>> elevatorTaskQueue;

	private ArrayList<Integer> currentPositionList;

	private DatagramPacket sendPacket;

	// Posible elevator directions
	public enum Direction {
		UP, DOWN, IDLE
	}

	private ArrayList<Direction> directionList;

	// Default byte array size for Datagram packets
	private static final int BYTE_SIZE = 6400;

	// Sockets to send and receive data from the ElevatorSubsystem and FloorSubsystem
	private DatagramSocket sendSocket, floorReceiveSocket, elevatorReceiveSocket, esReceiveSocket;

	private static final int FLOOR_RECEIVE_PORT = 60002;

	// Port list for all elevators in the elevator subsystem
	private static final ArrayList<Integer> elevatorPortList = new ArrayList<Integer>(Arrays.asList(5248, 5249, 5250, 5251));

	private static final int ELEVATOR_RECEIVE_PORT = 60006;

	private static final int FLOOR_SEND_PORT = 60004;
	
	private static final int ES_RECEIVE_PORT = 60009;

	/**
	 * Constructor
	 */
	public Scheduler() {

		elevatorTaskQueue = new ArrayList<>(ELEVATOR_COUNT);

		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			elevatorTaskQueue.add(new ArrayList<Integer>());
		}

		// current position of elevator is 1
		currentPositionList = new ArrayList<>(ELEVATOR_COUNT);

		currentPositionList.addAll(Arrays.asList(1, 1, 1, 5));

		upRequests = new ArrayList<>();

		downRequests = new ArrayList<>();

		eventList = new LinkedList<InputEvent>();

		directionList = new ArrayList<>(ELEVATOR_COUNT);

		directionList.addAll(Arrays.asList(Direction.UP, Direction.UP, Direction.UP, Direction.DOWN));

		upList = new ArrayList<Integer>();

		// Initialize the first 3 elevators going up
		upList.addAll(Arrays.asList(0, 1, 2));

		upPosition = new ArrayList<Integer>();

		upPosition.addAll(Arrays.asList(1, 1, 1));

		downList = new ArrayList<Integer>();

		// And the 4th elevator going down
		downList.addAll(Arrays.asList(3));

		downPosition = new ArrayList<Integer>();

		downPosition.add(5);

		elevatorStates = new ArrayList<ElevatorState>();

		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			elevatorStates.add(new ElevatorState(i + 1));
		}

		try {
			floorReceiveSocket = new DatagramSocket(Scheduler.FLOOR_RECEIVE_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

		try {
			elevatorReceiveSocket = new DatagramSocket(ELEVATOR_RECEIVE_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		
		try {
			esReceiveSocket = new DatagramSocket(ES_RECEIVE_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

	}
	
	

	/**
	 * Receive input event list from floor subsystem
	 */
	public void receiveInputEventList() {
		// Create byte array to store incoming datagram packet
		byte[] data = new byte[Scheduler.BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from floor subsystem
		try {
			floorReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		

		synchronized (this) {
			// Store all input events
			eventList.addAll(byteArrayToList(data));
		}

		for (InputEvent event : eventList) {
			System.out.print(
					event.getTime() + " Received request from floor " + event.getCurrentFloor());
			if (event.getUp()) {
				System.out.println(" going up");
			} else {
				System.out.println(" going down");
			}
		}

		processRequests();

	}

	/**
	 * Converts bytes to array
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<InputEvent> byteArrayToList(byte[] data) {

		ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
		ObjectInputStream objStream = null;
		try {
			objStream = new ObjectInputStream(byteStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			return (ArrayList<InputEvent>) objStream.readObject();
		} catch (ClassNotFoundException e) {
			// Class not found
			e.printStackTrace();
		} catch (IOException e) {
			// Could not red object from stream
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Processing request
	 */
	public synchronized void processRequests() {

		int diff = 0;

		if (!eventList.isEmpty()) {

			Collections.shuffle(elevatorStates);
			
			//Iterator<InputEvent> iter = eventList.iterator();
			InputEvent event = eventList.peek();

			for (int i = 0; i < ELEVATOR_COUNT; i++) {
				if (elevatorStates.get(i).getTaskList().contains(event.getCurrentFloor())) {
					System.out.println("Elevator: " + elevatorStates.get(i).getNumber() + "is already assigned this floor");
					eventList.remove();
					event = eventList.peek();
					break;
				}
			}
			
			while (!eventList.isEmpty()){
				for (int i = 0; i < ELEVATOR_COUNT; i++) {
					if (elevatorStates.get(i).getDirection() == Direction.UP) {
						if ((event.getCurrentFloor() - diff) == elevatorStates.get(i).getCurrentFloor()) {
							elevatorStates.get(i).addTask(event.getCurrentFloor());
							eventList.remove();
							event = eventList.peek();
							break;
						}
					} else if (elevatorStates.get(i).getDirection() == Direction.DOWN) {
						if ((event.getCurrentFloor() + diff) == elevatorStates.get(i).getCurrentFloor()) {
							elevatorStates.get(i).addTask(event.getCurrentFloor());
							eventList.remove();
							event = eventList.peek();
							break;
						}
					} else {
						if (Math.abs(event.getCurrentFloor() - elevatorStates.get(i).getCurrentFloor()) == diff) {
							elevatorStates.get(i).addTask(event.getCurrentFloor());
							eventList.remove();
							event = eventList.peek();
							break;
						}
					}
				}
				diff++;
			}
		}
		
		Collections.sort(elevatorStates);

	}


	// Finds the closest value to an integer in an arraylist
	public int closest(Integer request, ArrayList<Integer> positionList) {
		Integer dist = Math.abs(positionList.get(0) - request);
		int closestIndex = 0;

		for (int i = 0; i < positionList.size(); i++) {
			int diff = Math.abs(positionList.get(i) - request);

			if (diff < dist) {
				closestIndex = i;
				dist = diff;
			}
		}

		return closestIndex;
	}


	/**
	 * Converts task list to Bytes
	 * 
	 * @param elevatorNumber
	 * @return
	 */
	public byte[] taskListToByteArray(int elevatorNumber) {

		ArrayList<Integer> list = new ArrayList<>();
		for (Integer integer : elevatorTaskQueue.get(elevatorNumber)) {
			if (!list.contains(integer)) {
				list.add(integer);
			}
		}

		Collections.sort(list);

		if (directionList.get(elevatorNumber) == Direction.DOWN) {
			Collections.reverse(list);
		}

		elevatorTaskQueue.get(elevatorNumber).clear();
		elevatorTaskQueue.get(elevatorNumber).addAll(list);

		ByteArrayOutputStream baos = new ByteArrayOutputStream(Scheduler.BYTE_SIZE);

		ObjectOutputStream oos = null;

		try {
			oos = new ObjectOutputStream(baos);
		} catch (IOException e1) {
			// Unable to create object output stream
			e1.printStackTrace();
		}

		try {
			oos.writeObject(elevatorTaskQueue.get(elevatorNumber));
		} catch (IOException e) {
			// Unable to write eventList in bytes
			e.printStackTrace();
		}

		System.out.println("Sending Elevator " + (elevatorNumber + 1) + " to floors: "+ elevatorTaskQueue.get(elevatorNumber));

		elevatorTaskQueue.get(elevatorNumber).clear();

		return baos.toByteArray();

	}

	/**
	 * Send task to unique elevator
	 * 
	 * @param elevatorNumber
	 */
	public void sendTask(int elevatorNumber) {
		if (elevatorStates.get(elevatorNumber).getTaskList().size() > 0) {
			
			ArrayList<Integer> alist = new ArrayList<Integer>();
			
			for (Integer task : elevatorStates.get(elevatorNumber).getTaskList()) {
				alist.add(task);
			}
			
			Collections.sort(alist);
			
			elevatorTaskQueue.set(elevatorNumber, alist);

			byte[] data = taskListToByteArray(elevatorNumber);

			// Create Datagram packet containing byte array of event list information
			try {
				sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 
						elevatorPortList.get(elevatorNumber));
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}

			try {
				sendSocket = new DatagramSocket();
			} catch (SocketException se) {
				se.printStackTrace();
				System.exit(1);
			}

			try {
				sendSocket.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			sendSocket.close();
			

			elevatorTaskQueue.get(elevatorNumber).clear();
			elevatorStates.get(elevatorNumber).getTaskList().clear();
			
		}
	}
	
	public void receiveFromES() {
		byte[] data = new byte[Scheduler.BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from floor subsystem
		try {
			esReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Pair userInput = byteArrayToPair(data);
		
		Boolean up;
		
		if (userInput.getDestination() == 0) {
			System.out.println("Elevator " + userInput.getElevator() + " door stuck. Retrying...");
			return;
		} else if (userInput.getDestination() == -1) {
			System.out.println("Elevator " + userInput.getElevator() + " hard fault. Disabling elevator...");
			for (int i = 0; i < ELEVATOR_COUNT; i++) {
				if (elevatorStates.get(i).getNumber() == userInput.getElevator()) {
					elevatorStates.remove(i);
					ELEVATOR_COUNT--;
					return;
				}
			}
		}
		
		if (elevatorStates.get(userInput.getElevator() - 1).getCurrentFloor() > userInput.getDestination() ) {
			up = false;
		} else {
			up = true;
		}
		
		InputEvent event = new InputEvent(userInput.getString(), userInput.getDestination(), up);
		synchronized(this) {
			eventList.add(event);
		}
		System.out.println(userInput.getTime() + " user pressed floor " + userInput.getDestination() + " in Elevator " + userInput.getElevator());
	}

	/**
	 * Receive information from elevator
	 */
	public void receiveFromElevator() {
		byte[] data = new byte[Scheduler.BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from floor subsystem
		try {
			elevatorReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		Pair arrival = byteArrayToPair(data);

		switch(receivePacket.getPort()) {
		case 5248:
			//currentPositionList.set(0, arrival.getInteger());
			
			elevatorStates.get(0).setCurrentFloor(arrival.getInteger());
			elevatorStates.get(0).getTaskList().remove(arrival.getInteger());
			
			if (arrival.getString() == "up") {
				elevatorStates.get(0).setDirection(Direction.UP);
			} else if (arrival.getString() == "down") {
				elevatorStates.get(0).setDirection(Direction.DOWN);
			} else {
				elevatorStates.get(0).setDirection(Direction.IDLE);
			}
			
			System.out.println(LocalTime.now() + " Elevator 1 has arrived at floor: " + arrival.getInteger());
			break;
		case 5249:
			//currentPositionList.set(1, arrival.getInteger());
			
			elevatorStates.get(1).setCurrentFloor(arrival.getInteger());
			elevatorStates.get(1).getTaskList().remove(arrival.getInteger());
			
			if (arrival.getString() == "up") {
				elevatorStates.get(1).setDirection(Direction.UP);
			} else if (arrival.getString() == "down") {
				elevatorStates.get(1).setDirection(Direction.DOWN);
			} else {
				elevatorStates.get(1).setDirection(Direction.IDLE);
			}
			
			System.out.println(LocalTime.now() + " Elevator 2 has arrived at floor: " + arrival.getInteger());
			break;
		case 5250:
			//currentPositionList.set(2, arrival.getInteger());
			
			elevatorStates.get(2).setCurrentFloor(arrival.getInteger());
			elevatorStates.get(2).getTaskList().remove(arrival.getInteger());
			
			if (arrival.getString() == "up") {
				elevatorStates.get(2).setDirection(Direction.UP);
			} else if (arrival.getString() == "down") {
				elevatorStates.get(2).setDirection(Direction.DOWN);
			} else {
				elevatorStates.get(2).setDirection(Direction.IDLE);
			}
			
			System.out.println(LocalTime.now() + " Elevator 3 has arrived at floor: " + arrival.getInteger());
			break;
		case 5251:
			
			//currentPositionList.set(3, arrival.getInteger());
			
			elevatorStates.get(3).setCurrentFloor(arrival.getInteger());
			elevatorStates.get(3).getTaskList().remove(arrival.getInteger());
			
			if (arrival.getString() == "up") {
				elevatorStates.get(3).setDirection(Direction.UP);
			} else if (arrival.getString() == "down") {
				elevatorStates.get(3).setDirection(Direction.DOWN);
			} else {
				elevatorStates.get(3).setDirection(Direction.IDLE);
			}
			
			System.out.println(LocalTime.now() + " Elevator 4 has arrived at floor: " + arrival.getInteger());
			break;
		}


		byte[] sendData = data;

		try {
			sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getLocalHost(),
					Scheduler.FLOOR_SEND_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			sendSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		sendSocket.close();
	}

	/**
	 * convert Byte Array to Pair object
	 * 
	 * @param data
	 * @return
	 */
	private Pair byteArrayToPair(byte[] data) {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
		ObjectInputStream objStream = null;
		try {
			objStream = new ObjectInputStream(byteStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			return (Pair) objStream.readObject();
		} catch (ClassNotFoundException e) {
			// Class not found
			e.printStackTrace();
		} catch (IOException e) {
			// Could not red object from stream
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * main function
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Scheduler s = new Scheduler();

		Thread receiveFromElevator = new Thread() {
			public void run() {
				while (true) {
					s.receiveFromElevator();
				}
			}
		};
		
		Thread receiveFromES = new Thread() {
			public void run() {
				while (true) {
					s.receiveFromES();
				}
			}
		};

		Thread runScheduler = new Thread() {
			public void run() {
				while (true) {
					s.receiveInputEventList();
					for (int i = 0; i < s.ELEVATOR_COUNT; i++) {
						s.sendTask(i);
					}
				}
			}
		};

		runScheduler.start();
		receiveFromElevator.start();
		receiveFromES.start();

	}

}