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
import java.util.concurrent.TimeUnit;

/** 
 * ElevatorSubSystem.java
 * SYSC3303G4
 * 
 * Iteration 1
 * 
 * This Class represents an Elevator Car as a unit. Has basic functionality
 * such as Button and lamp for floors to go, Door and door delay, delay
 * for between floors. Scheduler input nextFloor to run Elevator and can 
 * get current floor status for event log.
 * 
 */
public class ElevatorSubSystem {
	
	// nth Elevator number, DO NOT PUT Same number as some other instance;
	private int elevatorNumber; 
	public ArrayList<Boolean> buttonList;
	public ArrayList<Boolean> elevatorLamp;

	private static final int BYTE_SIZE = 6400;

	private ArrayList<Integer> nextFloorList;

	public Boolean ACTIVE = true;
	static private int timeBtwFloors = 3;
	static private int doorDelay = 1;
	private Boolean dooropen;
	private int currentFloor;
	private int nextFloor;
	private Boolean goingUP;
	private Boolean goingDOWN;
	private static final int GROUND_FLOOR = 1;

	// from update after 28th January
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket, receiveSocket;

	// private static int RECEIVE_PORT = 50002;
	private static int RECEIVE_PORT = 60008;
	
	private static int SCHEDULER_SEND_PORT = 60006;

	/**
	 * @param elevatorNumber : Unique number to represent unique Elevator in the
	 *                       system
	 * @param buttons        : number of button had to be install inside the
	 *                       elevator for floors.
	 */
	public ElevatorSubSystem(int elevatorNumber, int buttons) {

		// create buttonList for buttons floor and Initialize as FALSE
		buttonList = new ArrayList<>(Arrays.asList(new Boolean[buttons]));
		Collections.fill(buttonList, Boolean.FALSE);

		// create elevatorLamp for buttons floor and Initialize as FALSE
		elevatorLamp = new ArrayList<>(Arrays.asList(new Boolean[buttons]));
		Collections.fill(elevatorLamp, Boolean.FALSE);

		// basic implementation
		dooropen = false;
		this.elevatorNumber = elevatorNumber;

		currentFloor = ElevatorSubSystem.GROUND_FLOOR;

		// from update after 28th January
		try {
			receiveSocket = new DatagramSocket(ElevatorSubSystem.RECEIVE_PORT);
		} catch (SocketException se) {
			System.out.println("Some Error in receiveSocket creation \n");
			se.printStackTrace();
			System.exit(1);
		}
	}

	public enum State {
		Ready, Idle, UpdateInput, Run, Arrived;

	}

	public void elevatorState() {

		State state = State.Ready;

		while (ACTIVE) {

			
			switch (state) {

			case Ready:
				System.out.print("\n System Ready \n");

				state = State.Idle;
				break;

			case Idle:
				if (this.dooropen) {
					elevatorCloseDoorAtFloor(currentFloor);
				}
				
				if (nextFloorList.size() > 0) {
					updateNextFloor();
					state = State.Run;

				} else {
					state = State.UpdateInput;
					System.out.printf(" Idle at floor %d \n", currentFloor);
					
				}

				break;// end Idle

			case UpdateInput:
				 
				 
				if (nextFloorList.size() > 0 || (currentFloor != nextFloor) ) {
					state = State.Run;
				} else {
					receiveTaskList();
					//updateGoing_UPorDOWN();
					state = State.Idle;
				}

				break;// end UpdateInput

			case Run:
				
				System.out.printf(" Elevator has %d destinations to visit next\n", nextFloorList.size() + 1);
				
				runElevator();
				
				state = State.Arrived;

				break; // end Run

			case Arrived:
				if (currentFloor == nextFloor) { // later we will use here
					sendArrivalInfo();
					System.out.printf(" Elevator arrived at destination floor: %d \n", currentFloor);
					elevatorOpendDoorAtFloor(currentFloor);
					elevatorCloseDoorAtFloor(currentFloor);
					if(nextFloorList.size() !=0) {
						//System.out.println(nextFloorList.size());
						nextFloor = nextFloorList.remove(0);
						state = State.Run;
						break;
					} else {
						state = State.Idle;
						break;
					}
					
				}

				break;// end ARRIVED

			}
		}

	}

	/**
	 * @ElevatorRun Use this Function to run the elevator
	 */
	public void runElevator() {
		// Prepare to run for target floor
	
		
		// running until next floor
		while (currentFloor != nextFloor) {
			System.out.printf(" Currently at floor: %d \n", currentFloor);
			updateGoing_UPorDOWN();
			//System.out.printf(" Next Floor %d \n", nextFloor);

			if (isGoingUP().equals(true) && isGoingDOWN().equals(false)) {
				runMotor();
				currentFloor++;
				//System.out.printf(" Current Floor %d \n", currentFloor);
			} else if (isGoingDOWN().equals(true) && isGoingUP().equals(false)) {
				runMotor();
				currentFloor--;
				//System.out.printf(" Current Floor %d \n", currentFloor);
			}
			
		}

		

	}

	
	/**
	 * 
	 */
	public void runMotor() {
		try {

			TimeUnit.SECONDS.sleep(ElevatorSubSystem.timeBtwFloors);

		} catch (Exception e) {

			System.out.println("Some Error in runMotor \n");
			e.printStackTrace();
		}

	}

	/**
	 * @openDoor Elevator Door open function
	 */
	public void openDoor() {
		try {
			TimeUnit.SECONDS.sleep(ElevatorSubSystem.doorDelay);
			setDooropen(true);
			System.out.println(" Elevator door opening \n");
		} catch (InterruptedException e) {

			System.out.println("Some Error in Opening Door \n");

			e.printStackTrace();
		}

	}

	/**
	 * @closeDoor Elevator Door Close function
	 */
	public void closeDoor() {
		try {
			TimeUnit.SECONDS.sleep(ElevatorSubSystem.doorDelay);
			setDooropen(false);
			System.out.println(" Elevator door closing \n");
		} catch (InterruptedException e) {
			System.out.println(" Some Error in Closing Door \n");
			e.printStackTrace();
		}

	}

	/**
	 * @buttonPushed Elevator inside button Pushed function,
	 * @input nth button
	 * @Do: updates button list and lamp list status
	 */
	public void buttonPushed(int n) {

		getButtonList().set(n, true);
		getElevatorLamp().set(n, true);
		setNextFloor(n);

	}

	/**
	 * @elevatorOpendDoorAtFloor Elevator Door Opened at a Floor function
	 */

	public void elevatorOpendDoorAtFloor(int n) {
		getButtonList().set(n - 1, false);
		getElevatorLamp().set(n - 1, false);
		openDoor();

	}

	/**
	 * @elevatorCloseDoorAtFloor Elevator Door Closed at a Floor function
	 */

	public void elevatorCloseDoorAtFloor(int n) {
		getButtonList().set(n - 1, false);
		getElevatorLamp().set(n - 1, false);
		closeDoor();

	}

	/**
	 * @updateGoing_UPorDOWN Call this function, before Run motor to update
	 *                       elevator's Direction
	 */
	public void updateGoing_UPorDOWN() {
		if (currentFloor <= nextFloor) {
			setGoingUP(true);
			setGoingDOWN(false);
			System.out.println(" Elevator Going UP \n");

		} else if (currentFloor > nextFloor) {
			setGoingUP(false);
			setGoingDOWN(true);
			System.out.println(" Elevator Going DOWN \n");

		} else if (currentFloor == nextFloor) {
			setGoingUP(false);
			setGoingDOWN(false);
			System.out.println("Elevator Standby \n");
		} else if (isGoingUP() == isGoingDOWN()) {
			setGoingUP(false);
			setGoingDOWN(false);
		}

	}

	/**
	 * @updateNextFloor update nextFloor using this function from Schedulers command
	 */
	public void updateNextFloor() {// change accordingly
		if (nextFloorList.size() > 0) {
			// setNextFloor(nextFloorList.get(0));// <-- here use schedulers sent next floor
			// packet command
			nextFloor = nextFloorList.remove(0);
			// System.out.printf(" NEXT Floor %d \n", nextFloor);
		}
		if ((currentFloor < 0) || (buttonList.size() < currentFloor)) { // check current floor is valid or not.
			System.out.println("Elevator Cureent Floor Number out of the range \n");

		}
	}
	

	// from update after 28th January
	/**
	 * Send and receive data from Scheduler system.
	 */

	public void receiveTaskList() {
		byte[] data = new byte[ElevatorSubSystem.BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from floor subsystem
		try {
			receiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		nextFloorList = byteArrayToList(data);
		// no need to update nextFloor here

	}

	@SuppressWarnings("unchecked")
	private ArrayList<Integer> byteArrayToList(byte[] data) {

		ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
		ObjectInputStream objStream = null;
		try {
			objStream = new ObjectInputStream(byteStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			return (ArrayList<Integer>) objStream.readObject();
		} catch (ClassNotFoundException e) {
			// Class not found
			e.printStackTrace();
		} catch (IOException e) {
			// Could not red object from stream
			e.printStackTrace();
		}

		return null;

	}
	
	public void sendArrivalInfo() {
		byte[] data = IntegerToByteArray(new Integer(this.currentFloor));
		
		// Create Datagram packet containing byte array of event list information
		try {
		     sendPacket = new DatagramPacket(data,
		                                     data.length, InetAddress.getLocalHost(), SCHEDULER_SEND_PORT);
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
		
		System.out.println(" Arrival info sent to Scheduler\n");
	}

	private byte[] IntegerToByteArray(Integer i) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(BYTE_SIZE);
		
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(baos);
		} catch (IOException e1) {
			// Unable to create object output stream
			e1.printStackTrace();
		}
		
		try {
			oos.writeObject(i);
		} catch (IOException e) {
			// Unable to write eventList in bytes
			e.printStackTrace();
		}
		
		return baos.toByteArray();
	}

	// getter and setter
	public DatagramPacket getSendPacket() {
		return sendPacket;
	}

	public void setSendPacket(DatagramPacket sendPacket) {
		this.sendPacket = sendPacket;
	}

	public DatagramPacket getReceivePacket() {
		return receivePacket;
	}

	public void setReceivePacket(DatagramPacket receivePacket) {
		this.receivePacket = receivePacket;
	}

	public DatagramSocket getSendSocket() {
		return sendSocket;
	}

	public void setSendSocket(DatagramSocket sendSocket) {
		this.sendSocket = sendSocket;
	}

	public DatagramSocket getReceiveSocket() {
		return receiveSocket;
	}

	public void setReceiveSocket(DatagramSocket receiveSocket) {
		this.receiveSocket = receiveSocket;
	}

	public ArrayList<Boolean> getButtonList() {
		return buttonList;
	}

	public void setButtonList(ArrayList<Boolean> buttonList) {
		this.buttonList = buttonList;
	}

	public ArrayList<Boolean> getElevatorLamp() {
		return elevatorLamp;
	}

	public void setElevatorLamp(ArrayList<Boolean> elevatorLamp) {
		this.elevatorLamp = elevatorLamp;
	}

	public int getTimeBtwFloors() {
		return ElevatorSubSystem.timeBtwFloors;
	}

	public Boolean getDooropen() {
		return dooropen;
	}

	public void setDooropen(Boolean dooropen) {
		this.dooropen = dooropen;
	}

	public int getElevatorNumber() {
		return elevatorNumber;
	}

	public void setElevatorNumber(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public int getNextFloor() {
		return nextFloor;
	}

	public void setNextFloor(int nextFloor) {
		this.nextFloor = nextFloor;
	}

	public Boolean isGoingUP() {
		return goingUP;
	}

	public void setGoingUP(Boolean goingUP) {
		this.goingUP = goingUP;
	}

	public Boolean isGoingDOWN() {
		return goingDOWN;
	}

	public void setGoingDOWN(Boolean goingDOWN) {
		this.goingDOWN = goingDOWN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ElevatorSubSystem [elevatorNumber=" + elevatorNumber + ", buttonList=" + buttonList + ", elevatorLamp="
				+ elevatorLamp + ", dooropen=" + dooropen + ", currentFloor=" + currentFloor + ", nextFloor="
				+ nextFloor + ", goingUP=" + goingUP + ", goingDOWN=" + goingDOWN + ", sendPacket=" + sendPacket
				+ ", receivePacket=" + receivePacket + ", sendSocket=" + sendSocket + ", receiveSocket=" + receiveSocket
				+ "]";
	}

	public static void main(String[] args) {
		ElevatorSubSystem e = new ElevatorSubSystem(1, 5);
		e.receiveTaskList();
		e.elevatorState();

	}

}
