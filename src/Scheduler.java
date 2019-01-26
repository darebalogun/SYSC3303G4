import java.net.DatagramPacket;
import java.util.ArrayList;

/**
 * 
 */

/**
 * @author TZ-L
 *
 */
public class Scheduler {
	// List of input events received from Floor Subystem to be handled
	private ArrayList<InputEvent> eventList;
	
	// Default byte array size for datagram packets
	private static final int BYTE_SIZE = 6400;
	
	// Datagram packet received from floor subsystem
	private DatagramPacket receivePacket;
	
	
	public Scheduler() {
	
	this.eventList = new ArrayList<InputEvent>();
	
	
	}
	public void receiveInputEventList() {
		 byte[] data = new byte[BYTE_SIZE];
	     receivePacket = new DatagramPacket(data, data.length);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
