import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FloorSubsystem {
	
	// This array list contains all the floor objects in this floor subsystem
	private ArrayList<Floor> floors;
	
	// This is the number of floors serviced by the elevator
	private static final int FLOOR_COUNT = 6;
	
	// Datagram sockets used to send and receive packets to the Scheduler
	private DatagramSocket sendReceive;
	
	// SEND_PORT is the port on the scheduler where data is sent and RECIEVE_PORT is where the floor subsystem listens for incoming data 
	private static final int SEND_PORT = 7000, RECEIVE_PORT = 5001;
	
	// Text file containing events to be sent to scheduler
	private static final String INPUT_PATH = "InputEvents.txt";
	
	// Current line in input file
	private int currentLine;
	
	// List of Events to be sent to the Scheduler
	private ArrayList<InputEvent> eventList;
	
	private static final int BYTE_SIZE = 6400;
	
	public FloorSubsystem() {
		this.floors = new ArrayList<Floor>(FLOOR_COUNT);
		
		try {
	         // Create send socket and bind it to the 
	         this.sendReceive = new DatagramSocket();
	      } catch (SocketException se) {   // Can't create the socket.
	         se.printStackTrace();
	         System.exit(1);
	      }
		
		this.currentLine = 0; 
		
		this.eventList = new ArrayList<InputEvent>();
		
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
			
			// Input event starts with a string of the time log followed by whitespace
			String time = inputEvents[0];
			
			// Then an integer representing the floor on which the passenger is making a request
			Integer currentFloor = Integer.parseInt(inputEvents[1]);
			
			// Check that the current floor read from the file is a valid floor
			if (currentFloor < 1 | currentFloor > FLOOR_COUNT) {

				throw new IllegalArgumentException("Floor read from input file is not valid");
			}
			
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
			
			if (destinationFloor < 1 | destinationFloor > FLOOR_COUNT) {
				throw new IllegalArgumentException("Destination floor read from input file is not valid");
				
			// If passenger has requested the current floor then do nothing and go to next event
			} else if (destinationFloor == currentFloor) {
				continue;
			}
			
			// Create event object
			InputEvent event = new InputEvent(time, currentFloor, up, destinationFloor);
			
			// Add to event object list
			eventList.add(event);
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
			
			this.eventList.clear();
			
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
		}
	}
	
	public static void main(String[] args) {
		FloorSubsystem fs = new FloorSubsystem();
		fs.readInputEvent();
		fs.sendEventList();

	}

}
