import java.util.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths; 
import java.lang.*;

public class FloorSubsystem {
	
	// This array list contains all the floor objects in this floor subsystem
	private ArrayList<Floor> floors;
	
	// This is the number of floors serviced by the elevator
	private static final int FLOOR_COUNT = 1;
	
	// Datagram sockets used to send and receive packets to the Scheduler
	private DatagramSocket sendReceive;
	
	// SEND_PORT is the port on the scheduler where data is sent and RECIEVE_PORT is where the floor subsystem listens for incoming data 
	private static final int SEND_PORT = 5000, RECEIVE_PORT = 5001;
	
	// Text file containing events to be sent to scheduler
	private static final String INPUT_PATH = "InputEvents.txt";
	
	// Current line in input file
	private int currentLine;
	
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
		
	}
	
	public InputEvent readInputEvent() {
		Path path = Paths.get(INPUT_PATH);
		
		String inputEvent = null;
		try {
			inputEvent = Files.readAllLines(path).get(currentLine);
		} catch (IOException e) { // Unable to read the input text file
			e.printStackTrace();
		}
		
		String[] inputEvents = inputEvent.split(" ");
		
		String time = inputEvents[0];
		
		Integer currentFloor = Integer.parseInt(inputEvents[1]);
		
		if (currentFloor < 1 | currentFloor > floors.size()) {
			throw new IllegalArgumentException("Floor read from input file is not valid");
		}
		
		Boolean up;
		
		if (inputEvents[2].equalsIgnoreCase("up")) {
			up = true;
		} else if (inputEvents[2].equalsIgnoreCase("down")) {
			up = false;
		} else {
			throw new IllegalArgumentException("Floor Button read form input file is not valid");
		}
		
		Integer destinationFloor = Integer.parseInt(inputEvents[3]);
		
		if (destinationFloor < 1 | destinationFloor > floors.size()) {
			throw new IllegalArgumentException("Floor read from input file is not valid");
		} else if (destinationFloor == currentFloor) {
			
		}
		
		InputEvent event = new InputEvent(time, currentFloor, up, );
		
		return null;
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
