import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
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
	
	private DatagramSocket receiveSocket;
	
	private static final int RECEIVE_PORT = 7000;
	
	
	public Scheduler() {
	
	this.eventList = new ArrayList<InputEvent>();
	
	try {
		receiveSocket = new DatagramSocket(RECEIVE_PORT);
	} catch (SocketException se) {
        se.printStackTrace();
        System.exit(1);
	}
	
	}
	public void receiveInputEventList() {
		 byte[] data = new byte[BYTE_SIZE];
	     receivePacket = new DatagramPacket(data, data.length);
	     
	     // Receive datagram socket from floor subsystem
	     try {  
	         receiveSocket.receive(receivePacket);
	      } catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
	     
	     this.eventList = byteArrayToList(data);
	     
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<InputEvent> byteArrayToList(byte[] data){
		
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
	
	
	
	public static void main(String[] args) {
		Scheduler s = new Scheduler();
		s.receiveInputEventList();
		System.out.println(s.eventList.get(1).getTime());
	}

}
