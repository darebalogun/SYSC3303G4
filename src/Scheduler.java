import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 
 */

/**
 * @author TZ-L
 *
 */
public class Scheduler {
	
	private static final int FLOOR_COUNT = 6;
	
	private static final int ELEVATOR_COUNT = 1;
	
	// List of input events received from Floor Subystem to be handled
	private ArrayList<InputEvent> eventList;
	
	private ArrayList<InputEvent> upRequests;
	
	private ArrayList<InputEvent> downRequests;
	
	private ArrayList<ArrayList<Integer>> elevatorTaskQueue;
	
	private ArrayList<Integer> currentPositionList;
	
	private enum Direction{
		UP, DOWN, IDLE
	}
	
	private ArrayList<Direction> directionList;
	
	// Default byte array size for datagram packets
	private static final int BYTE_SIZE = 6400;
	
	private DatagramSocket floorReceiveSocket, elevatorReceiveSocket;
	
	private static final int FLOOR_RECEIVE_PORT = 7000;
	
	private static final int ELEVATOR_RECEIVE_PORT = 70001;
	
	
	
	public Scheduler() {
		
		this.elevatorTaskQueue = new ArrayList<ArrayList<Integer>>(ELEVATOR_COUNT);	
		
		this.currentPositionList = new ArrayList<Integer>(ELEVATOR_COUNT);
		
		for (Integer elevatorPosition : this.currentPositionList) {
			elevatorPosition = 1;
		}
		
		this.eventList = new ArrayList<InputEvent>();
		
		this.directionList = new ArrayList<Direction>(ELEVATOR_COUNT);
		
		for (Direction direction: directionList) {
			direction = Direction.IDLE;
		}
		
		try {
			floorReceiveSocket = new DatagramSocket(FLOOR_RECEIVE_PORT);
			elevatorReceiveSocket = new DatagramSocket(ELEVATOR_RECEIVE_PORT);
		} catch (SocketException se) {
	        se.printStackTrace();
	        System.exit(1);
		}
	
	}
	
	public void receiveInputEventList() {
		 byte[] data = new byte[BYTE_SIZE];
	     DatagramPacket receivePacket = new DatagramPacket(data, data.length);
	     
	     // Receive datagram socket from floor subsystem
	     try {  
	         floorReceiveSocket.receive(receivePacket);
	      } catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
	     
	     this.eventList = byteArrayToList(data);
	     
	}
	
	public void receiveElevatorLocation() {
		byte[] data = new byte[BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		
	     try {  
	         elevatorReceiveSocket.receive(receivePacket);
	      } catch(IOException e) {
	         e.printStackTrace();
	         System.exit(1);
	      }
	     
	     
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
	
	public void processRequests() {
		for (InputEvent event : eventList) {
			if (event.getUp() == true) {
				this.upRequests.add(event);
			} else {
				this.downRequests.add(event);
			}
		}
		
		Collections.sort(upRequests);
		Collections.sort(downRequests);
		
		if (!upRequests.isEmpty()) {
			for (Direction direction : this.directionList) {
				if (direction == Direction.IDLE) {
					direction = Direction.UP;
					break;
				}
			}
		} else if (!downRequests.isEmpty()) {
			for (Direction direction : this.directionList) {
				if (direction == Direction.IDLE) {
					direction = Direction.DOWN;
					break;
				}
			}
		}
		
		for (InputEvent upEvent: upRequests) {
			for (int i = 0; i < ELEVATOR_COUNT; i++) {
				if (this.directionList.get(i) == Direction.UP & this.currentPositionList.get(i) <= upEvent.getCurrentFloor()) {
					this.elevatorTaskQueue.get(i).add(upEvent.getDestinationFloor());
					upRequests.remove(upEvent);
					break;
				}
			}
		}
		
		for (InputEvent downEvent: upRequests) {
			for (int i = 0; i < ELEVATOR_COUNT; i++) {
				if (this.directionList.get(i) == Direction.DOWN & this.currentPositionList.get(i) >= downEvent.getCurrentFloor()) {
					this.elevatorTaskQueue.get(i).add(downEvent.getDestinationFloor());
					break;
				}
			}
		}
		
		
	}
	


	public static void main(String[] args) {
		Scheduler s = new Scheduler();
		s.receiveInputEventList();
		System.out.println(s.eventList.get(1).getTime());
	}

}
