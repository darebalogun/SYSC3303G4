import java.util.*;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FloorSubSystem.java
 * SYSC3303G4
 * 
 *  @author: Dare Balogun
 *  
 *  @co_author: Sama Adil Sheikh
 *  
 *  @version Iteration 1
 * 
 * This class sends and receives packets from the Scheduler. Converts an ArrayList
 * of input events into an array byte. Also checks if the elevator is already
 * present on the floor in operation.
 * 
 */

public class FloorSubsystem {
	
	// Datagram sockets used to send and receive packets to the Scheduler
	private DatagramSocket sendReceive, receive;
	
	// SEND_PORT is the port on the scheduler where data is sent and RECIEVE_PORT is where the floor subsystem listens for incoming data 
	private static final int SEND_PORT = 60002, RECEIVE_PORT = 60004;
	
	// Text file containing events to be sent to scheduler
	private static final String INPUT_PATH = "src/InputEvents.txt";
	
	// Current line in input file
	private int currentLine;
	
	// List of Events to be sent to the Scheduler
	private ArrayList<InputEvent> eventList;
	
	private static final int BYTE_SIZE = 6400;
	
	// Provides the floor number		
	public int floorNum;

	public boolean upButton, downButton;
	
	//to check if the elevator door is open or closed
	private boolean doorOpen, doorClosed; 
	
	//Up and Down lamps on the floor's elevator buttons
	private boolean upLamp, downLamp;
	
	// to check if elevator is currently present on the floor
	public boolean elevatorPresent;

		
	public FloorSubsystem(int n) {
		
		this.currentLine = 0; 
		
		this.eventList = new ArrayList<InputEvent>();
		
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
		
		this.floorNum = n;
		
		this.upButton = false;
		
		this.downButton = false;
		
		this.elevatorPresent = false; 
		
		System.out.println("\nStarting Floor Number: " + this.floorNum);
		
	}
	
	/** get the current floor that the elevator is on */
	public int getCurrentFloor() {
		return floorNum;
		}

	/** true if the elevator is present on the current floor **/
	public boolean isElevatorPresent() {
		return elevatorPresent;
	}
	
	public void readInputEvent() {
		Path path = Paths.get(INPUT_PATH);
		
		List<String> inputEventList = null;
		 
		// Read the input file line by line into a list of strings
		try {
			inputEventList = Files.readAllLines(path);
		} catch (IOException e) { // Unable to read the input text file
			e.printStackTrace();
		}
		
		int i;
		
		// Starting from the current line saved in the floor subsystem read and parse the string
		for (i = this.currentLine; i < inputEventList.size(); i++ ) {
				
				String inputEvent = inputEventList.get(i);
				
				String[] inputEvents = inputEvent.split(" ");
				
				Integer currentFloor = Integer.parseInt(inputEvents[1]);
				
				if (this.floorNum == currentFloor) {
				
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
				Integer destinationFloor = Integer.parseInt(inputEvents[3]);
				
				// Create event object
				InputEvent event = new InputEvent(time, this.floorNum, up, destinationFloor);
				
				// Add to event object list
				eventList.add(event);
				
				if (i == this.currentLine) {
					System.out.println("\nFloor " + this.floorNum + ": Requests read from file: ");
				}
				System.out.print("Time: " + time);
				System.out.println(" Destination: "+ destinationFloor);
			}
		}
		
		this.currentLine = i;
		return;
		
	}
	
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
	
	public void sendEventList() {
		DatagramPacket sendPacket = null;

		if (!eventList.isEmpty()) {
			
			byte[] data = eventListToByteArray();
			
			// Create Datagram packet containing byte array of event list information
			try {
			     sendPacket = new DatagramPacket(data,
			                                     data.length, InetAddress.getLocalHost(), SEND_PORT);
			  } catch (UnknownHostException e) {
			     e.printStackTrace();
			     System.exit(1);
			  }
			
			// Send event list to scheduler
			try {
		         sendReceive.send(sendPacket);
		      } catch (IOException e) {
		         e.printStackTrace();
		         System.exit(1);
		      }
			
			System.out.println("\nFloor " + this.floorNum + ": Sent " + this.eventList.size() + " requests to scheduler");
		}

		this.eventList.clear();
	}
	
	public void receiveFromScheduler() {
		byte[] data = new byte[BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from floor subsystem
		try {
			receive.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Pair pair = byteArrayToPair(data);
		
		if (pair.getInteger() == this.floorNum) {
			
			String s = pair.getString();
			if (s == "up") {
				this.upLamp = false;
			} else {
				this.downLamp = false;
			}
			
			System.out.println("An elevator going " + s + " has arrived\n");
		}
		
	}
	
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
	
	public static void main(String[] args) {
		FloorSubsystem s = new FloorSubsystem(1);
		
		Thread readSendInput = new Thread() {
			public void run() {
				while(true) {
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
		
		readSendInput.start();
		receiveFromScheduler.start();
	}
	
}
