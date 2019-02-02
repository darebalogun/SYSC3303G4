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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** 
 * Scheduler.java
 * SYSC3303G4
 *  @author 
 *  
 *  @version Iteration 1
 *  
 * 
 * 
 * This class is to receive the information/requests from the FloorSubsystem and send
 * them to ElevatorSubSystem and send response back to the FloorSubsystem.
 * The scheduler accepts inputs from the InputEvent class and send the requests to 
 * ElevatorSubSystem. The Scheduler is also updated when an Elevator reaches it's desired floor
 * 
 */
public class Scheduler {
	
	private static final int FLOOR_COUNT = 6;
	
	private static final int ELEVATOR_COUNT = 1;
	
	// List of input events received from Floor Subsystem to be handled
	private ArrayList<InputEvent> eventList;
	
	private ArrayList<InputEvent> upRequests;
	
	private ArrayList<InputEvent> downRequests;
	
	private ArrayList<ArrayList<Integer>> elevatorTaskQueue;
	
	private ArrayList<Integer> currentPositionList;
	
	private DatagramPacket sendPacket;
	
	private enum Direction{
		UP, DOWN, IDLE
	}
	
	private ArrayList<Direction> directionList;
	
	// Default byte array size for Datagram packets
	private static final int BYTE_SIZE = 6400;
	
	private DatagramSocket sendSocket, floorReceiveSocket, elevatorReceiveSocket;
	
	private static final int FLOOR_RECEIVE_PORT = 60002;
	
	private static final int ELEVATOR_SEND_PORT = 60008;
	
	private static final int ELEVATOR_RECEIVE_PORT = 60006;
	
	private static final int FLOOR_SEND_PORT = 60004;
	
	
	
	public Scheduler() {
		
	
		this.elevatorTaskQueue = new ArrayList<ArrayList<Integer>>(ELEVATOR_COUNT);	
		this.elevatorTaskQueue.add(new ArrayList<Integer>());
		
		//current position of elevator is 1
		this.currentPositionList = new ArrayList<Integer>(ELEVATOR_COUNT);
		this.currentPositionList.add(1);

		
		this.upRequests = new ArrayList<InputEvent>();
		
		this.downRequests = new ArrayList<InputEvent>();
		
		this.eventList = new ArrayList<InputEvent>();
		
		this.directionList = new ArrayList<Direction>(ELEVATOR_COUNT);
		
		for (Direction direction: directionList) {
			direction = Direction.IDLE;
		}
		
		try {
			floorReceiveSocket = new DatagramSocket(FLOOR_RECEIVE_PORT);
			//elevatorReceiveSocket = new DatagramSocket(ELEVATOR_RECEIVE_PORT);
		} catch (SocketException se) {
	        se.printStackTrace();
	        System.exit(1);
		}
	
		try {
			elevatorReceiveSocket = new DatagramSocket(ELEVATOR_RECEIVE_PORT);
			//elevatorReceiveSocket = new DatagramSocket(ELEVATOR_RECEIVE_PORT);
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
	     
	     this.eventList.addAll(byteArrayToList(data));
	     
	     System.out.println("\nReceived request from floor: " + this.eventList.get(this.eventList.size() - 1).getCurrentFloor());
	     
	     for (InputEvent event : this.eventList) {
	     
	    	 System.out.println("Destination: " + event.getDestinationFloor());
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
		
		this.eventList.clear();
		
		if (!upRequests.isEmpty()) {
			Collections.sort(upRequests);
		}
		
		if (!downRequests.isEmpty()) {
			Collections.sort(downRequests);
		}
		
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
		
		Iterator<InputEvent> i = upRequests.iterator();
		
		while(i.hasNext()) {
			InputEvent e = i.next();
			if (e.getCurrentFloor() != this.currentPositionList.get(0)) {
				this.elevatorTaskQueue.get(0).add(e.getCurrentFloor());
				this.elevatorTaskQueue.get(0).add(e.getDestinationFloor());
			} else {
				this.elevatorTaskQueue.get(0).add(e.getDestinationFloor());
			}
			i.remove();
		}
		
		Iterator<InputEvent> d = downRequests.iterator();
		
		while(i.hasNext()) {
			InputEvent e = d.next();
			if (e.getCurrentFloor() != this.currentPositionList.get(0)) {
				this.elevatorTaskQueue.get(0).add(e.getCurrentFloor());
				this.elevatorTaskQueue.get(0).add(e.getDestinationFloor());
			} else {
				this.elevatorTaskQueue.get(0).add(e.getDestinationFloor());
			}
			d.remove();
		}
		
		
	}
	
	public byte[] taskListToByteArray(int elevatorNumber) {
		Set<Integer> set = new HashSet<Integer>(this.elevatorTaskQueue.get(0));
		this.elevatorTaskQueue.get(0).clear();
		this.elevatorTaskQueue.get(0).addAll(set);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(BYTE_SIZE);
		
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(baos);
		} catch (IOException e1) {
			// Unable to create object output stream
			e1.printStackTrace();
		}
		
		try {
			oos.writeObject(this.elevatorTaskQueue.get(0));
		} catch (IOException e) {
			// Unable to write eventList in bytes
			e.printStackTrace();
		}
		
		this.elevatorTaskQueue.get(0).clear();
		
		return baos.toByteArray();
		
		
	}
	
	public void sendTask(int elevatorNumber) {
		if (this.elevatorTaskQueue.get(0).size() > 0) {
			byte[] data = taskListToByteArray(elevatorNumber);
			
			// Create Datagram packet containing byte array of event list information
			try {
			     sendPacket = new DatagramPacket(data,
			                                     data.length, InetAddress.getLocalHost(), ELEVATOR_SEND_PORT);
			  } catch (UnknownHostException e) {
			     e.printStackTrace();
			     System.exit(1);
			  }
			
			try {
				this.sendSocket = new DatagramSocket();
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
	}	
	
	public void receiveFromElevator() {
		byte[] data = new byte[BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from floor subsystem
		try {
			elevatorReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Integer arrival = byteArrayToInteger(data);
		
		this.currentPositionList.set(0, arrival);
		
		System.out.println("The elevator has arrived at floor: " + arrival);
		
		String direction;
		
		if (this.directionList.get(0) == Direction.UP) {
			direction = "up";
		} else {
			direction = "down";
		}
		
		byte[] sendData = direction.getBytes();
		
		// Create Datagram packet containing byte array of event list information
		try {
		     sendPacket = new DatagramPacket(sendData,
		                                     sendData.length, InetAddress.getLocalHost(), FLOOR_SEND_PORT);
		  } catch (UnknownHostException e) {
		     e.printStackTrace();
		     System.exit(1);
		  }
		
		try {
			this.sendSocket = new DatagramSocket();
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
	
	
	private Integer byteArrayToInteger(byte[] data) {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
	    ObjectInputStream objStream = null;
		try {
			objStream = new ObjectInputStream(byteStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
	    try {
			return (Integer) objStream.readObject();
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
	 * @param args
	 */
	public static void main(String[] args) {
		Scheduler s = new Scheduler();
		
		Thread receiveFromElevator = new Thread() {
			public void run() {
				while(true) {
					s.receiveFromElevator();
				}
			}
		};
		
		Thread runScheduler = new Thread() {
			public void run() {
				while(true) {
					s.receiveInputEventList();
					s.processRequests();
					s.sendTask(1);
				}
			}
		};
		
		runScheduler.start();
		receiveFromElevator.start();
		
		
		

	}

}
