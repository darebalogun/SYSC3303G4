import java.util.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;

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
	private static final String = "/Inp"
	
	public FloorSubsystem() {
		this.floors = new ArrayList<Floor>(FLOOR_COUNT);
		
		try {
	         // Create send socket and bind it to the 
	         this.sendReceive = new DatagramSocket();
	      } catch (SocketException se) {   // Can't create the socket.
	         se.printStackTrace();
	         System.exit(1);
	      }
		
		
	}
	
	public Event readInputEvent(File: file) {
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
