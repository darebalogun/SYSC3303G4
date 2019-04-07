import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FloorSubSystem.java SYSC3303G4
 * 
 *  @author: Dare Balogun | 101062340
 *  
 *  @version Iteration 1
 * 
 * This class sends and receives packets from the Scheduler. Converts an
 * ArrayList of input events into an array byte. Also checks if the elevator is
 * already present on the floor in operation.
 * 
 */

public class FloorSubsystem {

	// Datagram sockets used to send and receive packets to the Scheduler
	private DatagramSocket sendReceive, receive;
	
	private boolean ready;

	// SEND_PORT is the port on the scheduler where data is sent and RECIEVE_PORT is
	// where the floor subsystem listens for incoming data
	private static final int SEND_PORT = 60002, RECEIVE_PORT = 60004;

	// Text file containing events to be sent to scheduler
	private static final String INPUT_PATH = "src/InputEvents.txt";

	// Current line in input file
	private int currentLine;

	// List of Events to be sent to the Scheduler
	private ArrayList<InputEvent> eventList;

	private static final int BYTE_SIZE = 6400;

	// Provides the floor number
	public static final int FLOOR_COUNT = 22;

	// Button indicate which direction requests have been made for
	public ArrayList<Boolean> upButton, downButton;

	// to check if the elevator door is open or closed
	private ArrayList<Boolean> doorOpen, doorClosed;

	// Up and Down lamps on the floor's elevator buttons
	private ArrayList<Boolean> upLamp, downLamp;

	// to check if elevator is currently present on the floor
	public ArrayList<Boolean> elevatorPresent;
	
	private static InetAddress SCHEDULER_IP;

	/**
	 * Constructor for the floor subsystem
	 * 
	 */
	public FloorSubsystem() {

	ready = false;
		

		// Initialize the current line being read on the input file to zero
		this.currentLine = 0;

		this.eventList = new ArrayList<InputEvent>();

		// Create DatagramSockets to send and receive
		try {
			this.sendReceive = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

		try {
			this.receive = new DatagramSocket(RECEIVE_PORT);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		
		try {
			SCHEDULER_IP = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// Turn buttons off


		System.out.println("\nStarting " + FLOOR_COUNT + " floors");

	}

	/** true if the elevator is present on the current floor **/
//	public boolean isElevatorPresent() {
//		return elevatorPresent;
//	}

	public synchronized void readInputEvent() {
		while (ready) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Path path = Paths.get(INPUT_PATH);

		//List<String> inputEventList = null;
		
		ArrayList<String> inputEventArrayList = new ArrayList<String>();
		
		boolean tag = true;
		
		while (tag) {
			try (Stream<String> lines = Files.lines(path)) {
				try {
					inputEventArrayList.add(lines.skip(currentLine).findFirst().get());
				} catch (NoSuchElementException e) {
					tag = false;
					lines.close();
					break;
				}
			    
			} catch (IOException e) {
				e.printStackTrace();
			}
			currentLine++;
		}
		
		out:
		for (int i = 0; i < inputEventArrayList.size(); i++) {
			
			String inputEvent = inputEventArrayList.get(i);
		
		// For each floor starting from the current line saved in the floor subsystem read and parse
		// the string
		for (int floorNum = 1; floorNum <= FLOOR_COUNT; floorNum++) {

				String[] inputEvents = inputEvent.split(" ");
		
				Integer currentFloor = null;
				try {
					currentFloor = Integer.parseInt(inputEvents[1]);
				} catch (ArrayIndexOutOfBoundsException e) {
					break out;
				}
				
				if (floorNum == currentFloor) {

					// Input event starts with a string of the time log followed by whitespace
					String time = inputEvents[0];

					// True if the request was for an elevator going up and false otherwise
					Boolean up;

					if (inputEvents[2].equalsIgnoreCase("up")) {
						up = true;
					} else if (inputEvents[2].equalsIgnoreCase("down")) {
						up = false;
					} else {
						throw new IllegalArgumentException("Floor button read form input file is not valid");
					}

					// Finally an integer representing the requested destination
					//Integer destinationFloor = Integer.parseInt(inputEvents[3]);

					// Create event object
					InputEvent event = new InputEvent(time, floorNum, up);

					// Add to event object list
					eventList.add(event);

					System.out.print("Time: " + time);
					System.out.print(" From: " + currentFloor);
					if (up) {
						System.out.println(" Direction: " + "up");
					} else {
						System.out.println(" Direction: " + "down");
					}
				}
				
			}
		
		}
		
		ready = true;
		notifyAll();
		return;

	}

	/** This helper function converts the class's event list into a byte array that can be sent over UDP
	 * @return byte array
	 */
	public byte[] eventListToByteArray() {
		if (!this.eventList.isEmpty()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(BYTE_SIZE);

			ObjectOutputStream oos = null;

			try {
				oos = new ObjectOutputStream(baos);
			} catch (IOException e1) {
				// Unable to create object output stream
				e1.printStackTrace();
			}

			try {
				oos.writeObject(this.eventList);
			} catch (IOException e) {
				// Unable to write eventList in bytes
				e.printStackTrace();
			}

			byte[] data = baos.toByteArray();

			return data;
		} else {
			throw new IllegalArgumentException("The eventlist must not be empty before being converted to byte array");
		}
	}

	
	/**
	 * This function sends the event list as a byte array to the scheduler
	 */
	/**
	 * 
	 */
	public void sendEventList() {
		DatagramPacket sendPacket = null;

		if (!eventList.isEmpty()) {

			byte[] data = eventListToByteArray();

			sendPacket = new DatagramPacket(data, data.length, SCHEDULER_IP, SEND_PORT);

			// Send event list to scheduler
			try {
				sendReceive.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			System.out.println("\nFloorSubsystem: Sent " + this.eventList.size() + " requests to scheduler");
		}

		this.eventList.clear();
	}

	/**
	 * This function blocks until it receives information from the scheduler. 
	 * This is used to update the floorsubsystem whenever an elevator arrives
	 */
	public void receiveFromScheduler() {
		byte[] data = new byte[BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from Scheduler
		try {
			receive.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Information is received as a pair containing the arrival floor and the direction of elevator
		Pair pair = byteArrayToPair(data);

		for (int floorNum = 1; floorNum <= FLOOR_COUNT; floorNum++) {

			if (pair.getInteger() == floorNum) {

				String s = pair.getString();
//				if (s == "up") {
//					this.setUpLamp(false);
//				} else {
//					this.setDownLamp(false);
//				}

				System.out.println("An elevator going " + s + " has arrived at floor: " + floorNum + "\n");
			}
		}

	}

	/** Convert a byte array into a pair object, so it can read the information received from the scheduler
	 * @param data 
	 * 	the byte array that is to be converted into a Pair object
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
	
	public void addRandomInput() {
		
		String time = java.time.LocalTime.now().toString();
		
		Random rand = new Random();
		
		Integer n = rand.nextInt(FLOOR_COUNT) + 1;
		
		String from = n.toString();
		
		Boolean m = rand.nextBoolean();
		
		String direction;
		
		if (m) {
			direction = "down";
		} else {
			direction = "up";
		}
		
		String request = time + " " + from + " " + direction;
		
		synchronized(this) {
			while (!ready) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("src/InputEvents.txt", true));
				out.newLine();
				out.write(request);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			ready = false;
			notifyAll();
		}
	}

	public static void main(String[] args) {

		FloorSubsystem s = new FloorSubsystem();

		Thread readSendInput = new Thread() {
			public void run() {
				while (true) {
					s.readInputEvent();
					s.sendEventList();
				}
			}
		};

		Thread receiveFromScheduler = new Thread() {
			public void run() {
				while (true) {
					s.receiveFromScheduler();
				}
			}
		};
		
		
		Thread simulateInput = new Thread() {
			public void run() {
				PrintWriter pw = null;
				try {
					pw = new PrintWriter(INPUT_PATH);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				pw.close();
				
				for (int i = 0; i < 10; i++) {
					s.addRandomInput();
					Random rand = new Random();
					int n = rand.nextInt(30);
					try {
						TimeUnit.SECONDS.sleep(n);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		readSendInput.start();
		receiveFromScheduler.start();
		simulateInput.start();
	}
}