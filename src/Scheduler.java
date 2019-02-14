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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

/**
 * Scheduler.java SYSC3303G4
 * 
 * @author Dare Balogun | 101062340
 * 
 * @version Iteration 1
 * 
 *          This class is to receive the information/requests from the
 *          FloorSubsystem and send them to ElevatorSubSystem and send response
 *          back to the FloorSubsystem. The scheduler accepts inputs from the
 *          InputEvent class and send the requests to ElevatorSubSystem. The
 *          Scheduler is also updated when an Elevator reaches it's desired
 *          floor
 * 
 */

public class Scheduler {

	private final int ELEVATOR_COUNT = 4;

	// List of input events received from Floor Subsystem to be handled
	private ArrayList<InputEvent> eventList;

	private ArrayList<InputEvent> upRequests;

	private ArrayList<InputEvent> downRequests;
	
	private ArrayList<Integer> upList;
	
	private ArrayList<Integer> upPosition;
	
	private ArrayList<Integer> downList;
	
	private ArrayList<Integer> downPosition;

	private ArrayList<ArrayList<Integer>> elevatorTaskQueue;

	private ArrayList<Integer> currentPositionList;

	private DatagramPacket sendPacket;

	private enum Direction {
		UP, DOWN, IDLE
	}

	private ArrayList<Direction> directionList;

	// Default byte array size for Datagram packets
	private static final int BYTE_SIZE = 6400;

	private DatagramSocket sendSocket, floorReceiveSocket, elevatorReceiveSocket;

	private static final int FLOOR_RECEIVE_PORT = 60002;

	private static final ArrayList<Integer> elevatorPortList = new ArrayList<Integer>(Arrays.asList(5248, 5249, 5250, 5251));

	private static final int ELEVATOR_RECEIVE_PORT = 60006;

	private static final int FLOOR_SEND_PORT = 60004;

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
		currentPositionList.add(1);
		currentPositionList.set(0, 1);

		upRequests = new ArrayList<>();

		downRequests = new ArrayList<>();

		eventList = new ArrayList<>();

		directionList = new ArrayList<>(ELEVATOR_COUNT);
		
		upList = new ArrayList<Integer>();
		
		upPosition = new ArrayList<Integer>();
		
		downList = new ArrayList<Integer>();
		
		downPosition = new ArrayList<Integer>();

		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			directionList.add(Direction.UP);
		}

		try {
			floorReceiveSocket = new DatagramSocket(Scheduler.FLOOR_RECEIVE_PORT);
			// elevatorReceiveSocket = new DatagramSocket(ELEVATOR_RECEIVE_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

		try {
			elevatorReceiveSocket = new DatagramSocket(ELEVATOR_RECEIVE_PORT);
			// elevatorReceiveSocket = new DatagramSocket(ELEVATOR_RECEIVE_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		
		initElevator();
		
	}

	/**
	 * Receive input event list
	 */
	public void receiveInputEventList() {
		byte[] data = new byte[Scheduler.BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from floor subsystem
		try {
			floorReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		eventList.addAll(byteArrayToList(data));

		for (InputEvent event : eventList) {

			System.out.println(
					"Request from: " + event.getCurrentFloor() + " destination: " + event.getDestinationFloor());
		}

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
	public void processRequests() {
		// Separate events into 2 lists based on direction up or down
		for (InputEvent event : eventList) {
			if (event.getUp() == true) {
				upRequests.add(event);
			} else {
				downRequests.add(event);
			}
		}

		// Clear list once all requests have been moved
		eventList.clear();

		
		if (!upRequests.isEmpty()) {
			Collections.sort(upRequests);
		}

		if (!downRequests.isEmpty()) {
			Collections.sort(downRequests);
		}
		
		for (InputEvent event : upRequests) {
			elevatorTaskQueue.get(upList.get(closest(event.getCurrentFloor(), upPosition))).add(event.getCurrentFloor());
			elevatorTaskQueue.get(upList.get(closest(event.getCurrentFloor(), upPosition))).add(event.getDestinationFloor());
		}
		
		upRequests.clear();
		

		for (InputEvent event : downRequests) {
			elevatorTaskQueue.get(downList.get(closest(event.getCurrentFloor(), downPosition))).add(event.getCurrentFloor());
			elevatorTaskQueue.get(downList.get(closest(event.getCurrentFloor(), downPosition))).add(event.getDestinationFloor());
		}
		
		downRequests.clear();


	}
	
	public void initElevator() {
		ArrayList<Integer> bottomFloor = new ArrayList<Integer>(Arrays.asList(1));
		ArrayList<Integer> topFloor = new ArrayList<Integer>(Arrays.asList(1));
		for (int i = 0; i < 3; i++) {
			elevatorTaskQueue.set(i, bottomFloor);
			sendTask(i);
		} 
		elevatorTaskQueue.set(3, topFloor);
		sendTask(3);
		while (upList.isEmpty()) {
			receiveFromElevator();
		}
	}
	
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
	
	public void updateElevatorPosition() {
		
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

		elevatorTaskQueue.get(elevatorNumber).clear();

		return baos.toByteArray();

	}

	/**
	 * Send task to unique elevator
	 * 
	 * @param elevatorNumber
	 */
	public void sendTask(int elevatorNumber) {
		if (elevatorTaskQueue.get(elevatorNumber).size() > 0) {
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
		}
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
				currentPositionList.set(0, arrival.getInteger());
				if (arrival.getString() == "up") {
					directionList.set(0, Direction.UP);
				} else {
					directionList.set(0, Direction.DOWN);
				}
			case 5249:
				currentPositionList.set(1, arrival.getInteger());
				if (arrival.getString() == "up") {
					directionList.set(1, Direction.UP);
				} else {
					directionList.set(1, Direction.DOWN);
				}
			case 5250:
				currentPositionList.set(2, arrival.getInteger());
				if (arrival.getString() == "up") {
					directionList.set(2, Direction.UP);
				} else {
					directionList.set(2, Direction.DOWN);
				}
			case 5251:
				currentPositionList.set(3, arrival.getInteger());
				if (arrival.getString() == "up") {
					directionList.set(3, Direction.UP);
				} else {
					directionList.set(3, Direction.DOWN);
				}
		}
		

		upList.clear();
		for (int i = 0; i < directionList.size(); i++) {
			if (directionList.get(i) == Direction.UP) {
				upList.add(i);
				upPosition.add(currentPositionList.get(i));
			}
		}
		
		downList.clear();
		for (int i = 0; i < directionList.size(); i++) {
			if (directionList.get(i) == Direction.DOWN) {
				downList.add(i);
				downPosition.add(currentPositionList.get(i));
			}
		}

		System.out.println("The elevator has arrived at floor: " + arrival.getInteger());

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

		Thread runScheduler = new Thread() {
			public void run() {
				while (true) {
					s.receiveInputEventList();
					s.processRequests();
					for (int i = 0; i < s.ELEVATOR_COUNT; i++) {
						s.sendTask(i);
					}
				}
			}
		};

		runScheduler.start();
		receiveFromElevator.start();

	}

}
