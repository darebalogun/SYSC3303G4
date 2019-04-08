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
import java.util.Timer;
import java.util.TimerTask;
import java.time.Instant;
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

	private ArrayList<Integer> userInputList;

	private static final int ELEVATOR_RECEIVE_PORT = 60006;

	private static final int FLOOR_SEND_PORT = 60004;

	private static final int ES_RECEIVE_PORT = 60009;

	private static final int BOTTOM_FLOOR = 1;

	private static final int TOP_FLOOR = 22;

	private static InetAddress ELEVATOR_IP;

	private static InetAddress FLOOR_IP;

	private static ArrayList<Timer> timers;

	private static final int TIMER_LIMIT = 9000;

	private static Boolean deactivating;

	/**
	 * Constructor
	 */
	public Scheduler() {

		deactivating  = false;

		elevatorTaskQueue = new ArrayList<>(ELEVATOR_COUNT);

		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			elevatorTaskQueue.add(new ArrayList<Integer>());
		}

		// current position of elevator is 1
		currentPositionList = new ArrayList<>(ELEVATOR_COUNT);

		currentPositionList.addAll(Arrays.asList(BOTTOM_FLOOR, 8, 15, TOP_FLOOR));

		eventList = new LinkedList<InputEvent>();

		directionList = new ArrayList<>(ELEVATOR_COUNT);

		directionList.addAll(Arrays.asList(Direction.IDLE, Direction.IDLE, Direction.IDLE, Direction.IDLE));

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

		userInputList = new ArrayList<Integer>(Arrays.asList(null, null, null, null));

		timers = new ArrayList<Timer>(ELEVATOR_COUNT);

		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			timers.add(null);
		}

		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			elevatorStates.add(new ElevatorState(i + 1));
		}
		elevatorStates.get(0).setCurrentFloor(BOTTOM_FLOOR);
		elevatorStates.get(1).setCurrentFloor(8);
		elevatorStates.get(2).setCurrentFloor(15);
		elevatorStates.get(3).setCurrentFloor(TOP_FLOOR);

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

		try {
			FLOOR_IP = InetAddress.getByName("127.0.0.1");
			ELEVATOR_IP = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		System.out.println("Scheduler running, waiting for floor requests...");
	}



	public void resetTimer(int elevatorNumber) {
		try {
			timers.get(elevatorNumber).cancel();
		} catch (NullPointerException e) {

		}

		timers.set(elevatorNumber, new Timer());

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				synchronized (deactivating) {
					deactivating = true;
					elevatorStates.get(elevatorNumber).setActive(false);
					deactivating = false;
					deactivating.notifyAll();
					System.out.print(LocalTime.now() + " Elevator: " + (elevatorNumber + 1) + " is non-responsive, it will be deactivated");
					System.out.println(" and maintenance will be contacted");
					int smallest = 10;
					int j = 0;
					for (int i = 0; i < ELEVATOR_COUNT; i++) {
						if (elevatorStates.get(i).isActive()) {
							if (elevatorStates.get(i).getTaskList().size() < smallest) {
								smallest = elevatorStates.get(i).getTaskList().size();
								j = i;
							}
						}
					}
					elevatorStates.get(j).getTaskList().addAll(elevatorStates.get(elevatorNumber).getTaskList());
				}
			}

		};

		timers.get(elevatorNumber).schedule(timerTask, TIMER_LIMIT);
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

		//start time for floor button interface

		//long startTime3 = System.nanoTime();

		synchronized (this) {
			// Store all input events
			eventList.addAll(byteArrayToList(data));
		}

		for (InputEvent event : eventList) {
			System.out.print(
					LocalTime.now() + " Received request from floor " + event.getCurrentFloor());
			if (event.getUp()) {
				System.out.println(" going up");
			} else {
				System.out.println(" going down");
			}
		}

		processRequests();

		//

		//long endTime3 = System.nanoTime();
		//long timeElapsed3 = endTime3 - startTime3;

		//System.out.println("floor button interface in nanoseconds: " +timeElapsed3);
		//System.out.println("");
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

			//Collections.shuffle(elevatorStates);

			//Iterator<InputEvent> iter = eventList.iterator();
			InputEvent event = eventList.peek();

			while (!eventList.isEmpty()) {
				while (deactivating) {
					try {
						deactivating.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				for (int i = 0; i < ELEVATOR_COUNT; i++) {
					if (elevatorStates.get(i).isActive()) {
						if (elevatorStates.get(i).getTaskList().contains(event.getCurrentFloor())) {
							if ((event.getUp() && elevatorStates.get(i).getDirection() == Direction.UP) || (!event.getUp() && elevatorStates.get(i).getDirection() == Direction.DOWN)) {
								System.out.println("Elevator: " + elevatorStates.get(i).getNumber() + " is already assigned this floor");
								eventList.remove();
								event = eventList.peek();
								break;
							}
						}

						if (elevatorStates.get(i).getDirection() == Direction.UP && (event.getCurrentFloor() > elevatorStates.get(i).getCurrentFloor())) {
							if ((event.getCurrentFloor() - diff) == elevatorStates.get(i).getCurrentFloor()) {
								elevatorStates.get(i).addTask(event.getCurrentFloor());
								eventList.remove();
								event = eventList.peek();
								break;
							}
						} else if (elevatorStates.get(i).getDirection() == Direction.DOWN && (event.getCurrentFloor() < elevatorStates.get(i).getCurrentFloor())) {
							if ((event.getCurrentFloor() + diff) == elevatorStates.get(i).getCurrentFloor()) {
								elevatorStates.get(i).addTask(event.getCurrentFloor());
								eventList.remove();
								event = eventList.peek();
								break;
							}
						} else if (elevatorStates.get(i).getDirection() == Direction.IDLE) {
							if (Math.abs(event.getCurrentFloor() - elevatorStates.get(i).getCurrentFloor()) == diff) {
								elevatorStates.get(i).addTask(event.getCurrentFloor());
								if (event.getCurrentFloor() > elevatorStates.get(i).getCurrentFloor()) {
									elevatorStates.get(i).setDirection(Direction.UP);
								} else if (event.getCurrentFloor() < elevatorStates.get(i).getCurrentFloor()) {
									elevatorStates.get(i).setDirection(Direction.DOWN);
								} 
								eventList.remove();
								event = eventList.peek();
								break;
							}
						}
					}
				}
				diff++;
				if (diff == 21) {
					diff = 0;
				}
			}
		}

		Collections.sort(elevatorStates);

	}

	/**
	 * Converts task list to Bytes
	 * 
	 * @param elevatorNumber
	 * @return
	 */
	public byte[] taskListToByteArray(int elevatorNumber) {

		ArrayList<Integer> list = new ArrayList<>();
		synchronized (this) {
			for (Integer integer : elevatorTaskQueue.get(elevatorNumber)) {
				if (!list.contains(integer)) {
					list.add(integer);
				}
			}
		}

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

		return baos.toByteArray();

	}

	/**
	 * Send task to unique elevator
	 * 
	 * @param elevatorNumber
	 */
	public synchronized void sendTask(int elevatorNumber) {
		if (elevatorStates.get(elevatorNumber).isActive() && (elevatorStates.get(elevatorNumber).getTaskList().size() > 0 || userInputList.get(elevatorNumber) != null)) {

			ArrayList<Integer> alist = new ArrayList<Integer>();

			for (Integer task : elevatorStates.get(elevatorNumber).getTaskList()) {
				alist.add(task);
			}

			for (int i = 0; i < alist.size(); i++) {
				if (!elevatorTaskQueue.get(elevatorNumber).contains(alist.get(i))) {
					elevatorTaskQueue.get(elevatorNumber).add(alist.get(i));
				}
			}

			if (userInputList.get(elevatorNumber) != null) {
				elevatorTaskQueue.get(elevatorNumber).add(userInputList.get(elevatorNumber));
				userInputList.set(elevatorNumber, null);
			}
			
			Collections.sort(elevatorTaskQueue.get(elevatorNumber));

			if (elevatorStates.get(elevatorNumber).getDirection() == Direction.DOWN) {
				Collections.reverse(elevatorTaskQueue.get(elevatorNumber));
			}


			byte[] data = taskListToByteArray(elevatorNumber);

			sendPacket = new DatagramPacket(data, data.length, ELEVATOR_IP, 
					elevatorPortList.get(elevatorNumber));

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

			System.out.println(LocalTime.now() + " Sending Elevator " + (elevatorNumber + 1) + " going " + elevatorStates.get(elevatorNumber).getDirection() + " to floors: "+ elevatorTaskQueue.get(elevatorNumber));

			resetTimer(elevatorNumber);

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

		synchronized (this) {
			if (elevatorStates.get(userInput.getElevator() - 1).getCurrentFloor() > userInput.getDestination() ) {
				up = false;
			} else {
				up = true;
			}

			for (int i = 0; i < ELEVATOR_COUNT; i++) {
				if (elevatorStates.get(i).getNumber() == userInput.getElevator()) {
					if(elevatorStates.get(i).getDirection() == Direction.IDLE || 
						(up && elevatorStates.get(i).getDirection() == Direction.UP) || 
						(!up && elevatorStates.get(i).getDirection() == Direction.DOWN)) {
						elevatorStates.get(i).addTask(userInput.getDestination());;
					} else {
						userInputList.set(i,userInput.getDestination());
					}
					break;
				}
			}
		}

		System.out.println(userInput.getTime() + " user pressed floor " + userInput.getDestination() + " in Elevator " + userInput.getElevator());

		sendTask(userInput.getElevator() - 1);


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


		synchronized (this) {
			switch(receivePacket.getPort()) {
			case 5248:
				//currentPositionList.set(0, arrival.getInteger());

				elevatorStates.get(0).setCurrentFloor(arrival.getInteger());

				if (elevatorTaskQueue.get(0).isEmpty() || arrival.getInteger() == elevatorTaskQueue.get(0).get(0)) {
					elevatorStates.get(0).setDirection(Direction.IDLE);
				} else if (arrival.getInteger() > elevatorTaskQueue.get(0).get(0)) {
					elevatorStates.get(0).setDirection(Direction.DOWN);
				} else if (arrival.getInteger() < elevatorTaskQueue.get(0).get(0)){
					elevatorStates.get(0).setDirection(Direction.UP);
				}

				if (elevatorTaskQueue.get(0).contains(arrival.getInteger())) {
					elevatorTaskQueue.get(0).remove(arrival.getInteger());
					System.out.println(LocalTime.now() + " Elevator 1 has arrived at floor: " + arrival.getInteger());
				}


				resetTimer(0);
				if (elevatorTaskQueue.get(0).isEmpty()) {
					timers.get(0).cancel();
				}

				break;
			case 5249:
				//currentPositionList.set(1, arrival.getInteger());
				
				elevatorStates.get(1).setCurrentFloor(arrival.getInteger());

				if (elevatorTaskQueue.get(1).isEmpty() || arrival.getInteger() == elevatorTaskQueue.get(1).get(0)) {
					elevatorStates.get(1).setDirection(Direction.IDLE);
				} else if (arrival.getInteger() > elevatorTaskQueue.get(1).get(0)) {
					elevatorStates.get(1).setDirection(Direction.DOWN);
				} else if (arrival.getInteger() < elevatorTaskQueue.get(1).get(0)){
					elevatorStates.get(1).setDirection(Direction.UP);
				}

				if (elevatorTaskQueue.get(1).contains(arrival.getInteger())) {
					elevatorTaskQueue.get(1).remove(arrival.getInteger());
					System.out.println(LocalTime.now() + " Elevator 2 has arrived at floor: " + arrival.getInteger());
				}

				resetTimer(1);
				if (elevatorTaskQueue.get(1).isEmpty()) {
					timers.get(1).cancel();
				}

				break;
			case 5250:
				//currentPositionList.set(2, arrival.getInteger());

				elevatorStates.get(2).setCurrentFloor(arrival.getInteger());

				if (elevatorTaskQueue.get(2).isEmpty() || arrival.getInteger() == elevatorTaskQueue.get(2).get(0)) {
					elevatorStates.get(2).setDirection(Direction.IDLE);
				} else if (arrival.getInteger() > elevatorTaskQueue.get(2).get(0)) {
					elevatorStates.get(2).setDirection(Direction.DOWN);
				} else if (arrival.getInteger() < elevatorTaskQueue.get(2).get(0)) {
					elevatorStates.get(2).setDirection(Direction.UP);
				}

				if (elevatorTaskQueue.get(2).contains(arrival.getInteger())) {
					elevatorTaskQueue.get(2).remove(arrival.getInteger());

					System.out.println(LocalTime.now() + " Elevator 3 has arrived at floor: " + arrival.getInteger());
				}

				resetTimer(2);
				if (elevatorTaskQueue.get(2).isEmpty()) {
					timers.get(2).cancel();
				}

				break;
			case 5251:

				//currentPositionList.set(3, arrival.getInteger());

				elevatorStates.get(3).setCurrentFloor(arrival.getInteger());

				if (elevatorTaskQueue.get(3).isEmpty() || arrival.getInteger() == elevatorTaskQueue.get(3).get(0)) {
					elevatorStates.get(3).setDirection(Direction.IDLE);
				} else if (arrival.getInteger() > elevatorTaskQueue.get(3).get(0)) {
					elevatorStates.get(3).setDirection(Direction.DOWN);
				} else if (arrival.getInteger() < elevatorTaskQueue.get(3).get(0)) {
					elevatorStates.get(3).setDirection(Direction.UP);
				} 

				if (elevatorTaskQueue.get(3).contains(arrival.getInteger())) {
					elevatorTaskQueue.get(3).remove(arrival.getInteger());					
					System.out.println(LocalTime.now() + " Elevator 4 has arrived at floor: " + arrival.getInteger());
				}

				resetTimer(3);
				if (elevatorTaskQueue.get(3).isEmpty()) {
					timers.get(3).cancel();
				}

				break;
			}
		}


		byte[] sendData = data;

		sendPacket = new DatagramPacket(sendData, sendData.length, FLOOR_IP,
				Scheduler.FLOOR_SEND_PORT);

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

		//end time for arrival sensor interface
		//long endTime2 = System.nanoTime();
		//long timeElapsed2 = endTime2 - startTime2;

		//System.out.println("Arrival sensor interface in nanoseconds: " +timeElapsed2);
		//System.out.println("");
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
				}
			}
		};

		Thread sendTasks = new Thread(){
			public void run() {
				while(true) {
					synchronized (deactivating) {
						while (deactivating) {
							try {
								deactivating.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						for (int i = 0; i < s.ELEVATOR_COUNT; i++) {
							s.sendTask(i);
						}
					}
				}
			}
		};

		runScheduler.start();
		receiveFromElevator.start();
		receiveFromES.start();
		sendTasks.start();

	}

}