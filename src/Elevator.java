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
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.time.LocalTime;

/**
 * <blockquote> This Thread Class represent Elevator as a unit, to run this unit
 * use ElevatorSubSystem
 * 
 * @author Muhammad Tarequzzaman |100954008| responsible for <b> Class Elevator
 *         </b>
 * @Coauthor Dare Balogun | 101062340| responsible for methods:
 *           <b>PairToByteArray, byteArrayToList 
 */

public class Elevator extends Thread {

	Instant instant;
	private static final int BYTE_SIZE = 6400;
	static private int timeBtwFloors = 4; // time as Canal building main Elevators
	static private int doorDelay = 2;

	// private static int RECEIVE_PORT = 50002;

	private static int SCHEDULER_SEND_PORT = 60006;

	// nth Elevator number, DO NOT PUT Same number as some other instance;
	private int elevatorNumber;
	private ArrayList<Boolean> buttonList;
	private ArrayList<Boolean> elevatorLamp;
	private ArrayList<Integer> nextFloorList;
	private Scheduler.Direction nextDirection;
	private Boolean ACTIVE = true;
	private Boolean dooropen;

	private int currentFloor;
	private int nextFloor;

	private Boolean goingUP = false;
	private Boolean goingDOWN = false;

	private DatagramPacket sendPacket, receivePacket; /* Packet */
	private DatagramSocket sendReceiveSocket; /* Socket */
	/*---------------------------------------------------------------*/

	/**
	 * @param elevatorNumber       : Unique number to represent unique Elevator in
	 *                             the system
	 * @param numberofFloorbuttons : number of button had to be install inside the
	 *                             elevator for floors. same as FloorSubsystem
	 * @param RECEIVE_PORT         : Unique Port Number
	 * @param startFloor           : Default Staring Floor
	 */
	public Elevator(int elevatorNumber, int numberofFloorbuttons, int RECEIVE_PORT, int startFloor) {

		// create buttonList for buttons floor and Initialize as FALSE
		buttonList = new ArrayList<>(Arrays.asList(new Boolean[numberofFloorbuttons]));
		Collections.fill(buttonList, Boolean.FALSE);

		// create elevatorLamp for buttons floor and Initialize as FALSE
		elevatorLamp = new ArrayList<>(Arrays.asList(new Boolean[numberofFloorbuttons]));
		Collections.fill(elevatorLamp, Boolean.FALSE);

		// basic implementation
		dooropen = false;

		this.elevatorNumber = elevatorNumber;

		currentFloor = startFloor;
		
		this.nextDirection = Scheduler.Direction.IDLE;

		receiveSocketPortCreation(RECEIVE_PORT);

		System.out.printf(
				LocalTime.now().toString() + " Elevator E%d...Waiting for the requests from the Scheduler at Time \n",
				elevatorNumber);

	}

	public void run() {

		try {
			receiveTaskList();
			elevatorState();
		} catch (Exception e) {

			System.out.println(LocalTime.now().toString() + "Elevator run call problem \n");
			e.printStackTrace();

		}

	}

	/**
	 * Creating port for ELEVATOR
	 * 
	 * @param PORT_Number
	 */
	public void receiveSocketPortCreation(int PORT_Number) {
		try {
			sendReceiveSocket = new DatagramSocket(PORT_Number);
		} catch (SocketException se) {
			System.out.println("Error in receiveSocketPort creation \n");
			se.printStackTrace();
			System.exit(1);
		}
	}

	/*---------------------------------------------------------------*/
	public enum State {
		READY, STANDBY, UPDATE, RUN, FINISH;

	}

	/**
	 * This method implants FSM Using State condition to change state
	 */
	public synchronized void elevatorState() {

		State state = State.READY;

		while (ACTIVE) {

			switch (state) {

			case READY: // READY state
				System.out.printf(LocalTime.now().toString() + " Elevator#: %d READY \n", getElevatorNumber());
				ACTIVE = true;
				state = State.STANDBY;
				break; // end READY

			case STANDBY:// STANDBY state

				if (dooropen == true) {
					elevatorCloseDoorAtFloor(currentFloor);
				}

				if (nextDirection != Scheduler.Direction.IDLE | (currentFloor != nextFloor)) {
					updateNextFloor();
					state = State.RUN;

				} else {
					state = State.UPDATE;
					System.out.printf(LocalTime.now().toString() + " Elevator#: %d STANDBY at Floor: %d \n",
							getElevatorNumber(), currentFloor);

				}

				break;// end STANDBY

			case UPDATE: // UPDATE
				receiveTaskList();

				state = State.STANDBY;

				/*
				 * if ((nextFloorList.size() > 0) || (currentFloor != nextFloor)) {
				 * 
				 * state = State.RUN;
				 * 
				 * } else { receiveTaskList();
				 * 
				 * state = State.STANDBY; }
				 */

				break;// end UPDATE

			case RUN: // RUN

				System.out.printf(LocalTime.now().toString() + " Elevator#: %d Next Destination is : %d\n",
						getElevatorNumber(), nextFloor);

				runToNextFloor();

				state = State.FINISH;

				break; // end RUN

			case FINISH: // FINISH
				if (currentFloor == nextFloor) {

					System.out.printf(LocalTime.now().toString() + " Elevator#: %d Arrived at floor: %d \n",
							getElevatorNumber(), currentFloor);
					sendArrivalInfo();
				}

				state = State.STANDBY;
				break;// end ARRIVED

			}
		}

	}

	/**
	 * @ElevatorRun Use this Function to run the elevator
	 */
	public synchronized void runToNextFloor() {
		// Prepare to run for target floor

		do {
			updateGoing_UPorDOWN();
			System.out.printf(LocalTime.now().toString() + " Elevator#: %d Currently at floor: %d \n",
					getElevatorNumber(), currentFloor);

			// System.out.printf(" Next Floor %d \n", nextFloor);

			if (isGoingUP().equals(true) && isGoingDOWN().equals(false)) {
				runMotor();
				currentFloor++;
				// System.out.printf(" Current Floor %d \n", currentFloor);
			} else if (isGoingDOWN().equals(true) && isGoingUP().equals(false)) {
				runMotor();
				currentFloor--;
				// System.out.printf(" Current Floor %d \n", currentFloor);
			}
			updateNextFloor();

		} while (currentFloor != nextFloor);
		// running until next floor
		/*
		 * while (currentFloor != nextFloor) {
		 * 
		 * updateGoing_UPorDOWN(); System.out.printf(LocalTime.now().toString()+
		 * " Elevator#: %d Currently at floor: %d \n", getElevatorNumber(),
		 * currentFloor);
		 * 
		 * // System.out.printf(" Next Floor %d \n", nextFloor);
		 * 
		 * if (isGoingUP().equals(true) && isGoingDOWN().equals(false)) { runMotor();
		 * currentFloor++; // System.out.printf(" Current Floor %d \n", currentFloor); }
		 * else if (isGoingDOWN().equals(true) && isGoingUP().equals(false)) {
		 * runMotor(); currentFloor--; // System.out.printf(" Current Floor %d \n",
		 * currentFloor); } updateNextFloor(); }
		 */

	}

	//

	/**
	 * runMotor for a time
	 */
	public void runMotor() {
		try {

			TimeUnit.SECONDS.sleep(Elevator.timeBtwFloors);

		} catch (InterruptedException e) {

			System.out.printf(LocalTime.now().toString() + "Some Error in runMotor on Elevator#: %d\n",
					getElevatorNumber());
			e.printStackTrace();
		}

	}

	/**
	 * @updateGoing_UPorDOWN Call this function, before RUN motor to update
	 *                       elevator's Direction
	 */
	public void updateGoing_UPorDOWN() {
		if (currentFloor < nextFloor) {
			setGoingUP(true);
			setGoingDOWN(false);
			System.out.printf(LocalTime.now().toString() + " Elevator#: %d Going UP \n", getElevatorNumber());

		} else if (currentFloor > nextFloor) {
			setGoingUP(false);
			setGoingDOWN(true);
			System.out.printf(LocalTime.now().toString() + " Elevator#: %d Going DOWN \n", getElevatorNumber());

		} else if (currentFloor == nextFloor) {
			setGoingUP(false);
			setGoingDOWN(false);
			System.out.printf(LocalTime.now().toString() + "Elevator#: %d Standby \n", getElevatorNumber());

		}

	}

	/**
	 * @updateNextFloor update nextFloor using this function from Schedulers command
	 */
	public void updateNextFloor() {// change accordingly
		if (nextDirection == Scheduler.Direction.UP) {
			// setNextFloor(nextFloorList.get(0));// <-- here use schedulers sent next floor
			// packet command
			nextFloor = currentFloor + 1;
			// System.out.printf(" NEXT Floor %d \n", nextFloor);
		} else if (nextDirection == Scheduler.Direction.DOWN) {
			nextFloor = currentFloor - 1;
		}
		
		if ((currentFloor < 0) || (buttonList.size() < currentFloor)) { // check current floor is valid or not.
			System.out.printf(LocalTime.now().toString() + "Elevator#: %d Cureent Floor Number out of the range \n",
					getElevatorNumber());

		}
	}
	/*---------------------------------------------------------------*/

	/**
	 * NO NEED TO IMPLEMENT this on any things other then GUI
	 * 
	 * @buttonPushed Elevator inside button Pushed function,
	 * @input nth button
	 * @Do: updates button list and lamp list status
	 */
	public void buttonPushed(int n) {

		getButtonList().set(n, true);
		getElevatorLamp().set(n, true);
		// setNextFloor(n);

	}

	public void floorButtonOff(int n) {
		getButtonList().set(n, false);
		getElevatorLamp().set(n, false);
		// setNextFloor(n);

	}
	/*---------------------------------------------------------------*/

	/**
	 * @openDoor Elevator Door open function
	 */
	public void openDoor() {
		try {
			TimeUnit.SECONDS.sleep(Elevator.doorDelay);
			setDoorState(true);
			System.out.printf(LocalTime.now().toString() + " Elevator#: %d Door Opening \n", getElevatorNumber());
		} catch (InterruptedException e) {

			System.out.printf(LocalTime.now().toString() + "Some Error in Opening Door in Elevator#: %d \n",
					getElevatorNumber());

			e.printStackTrace();
		}

	}

	/**
	 * @closeDoor Elevator Door Close function
	 */
	public void closeDoor() {
		try {
			TimeUnit.SECONDS.sleep(Elevator.doorDelay);
			setDoorState(false);
			System.out.printf(LocalTime.now().toString() + " Elevator#: %d Door Closing\n", getElevatorNumber());
		} catch (InterruptedException e) {
			System.out.printf(LocalTime.now().toString() + "Some Error in Closing Door in Elevator#: %d \n",
					getElevatorNumber());
			e.printStackTrace();
		}

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
	 * @elevatorOpendDoorAtFloor Elevator Door Opened at a Floor function
	 */

	public void elevatorOpendDoorAtFloor(int n) {
		getButtonList().set(n - 1, false);
		getElevatorLamp().set(n - 1, false);
		openDoor();

	}

	/*---------------------------------------------------------------*/
	/* Start @coauthor */
	/**
	 * @param i
	 * @return Byte Array from integer value
	 */
	private byte[] PairToByteArray(Pair pair) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(Elevator.BYTE_SIZE);

		ObjectOutputStream oos = null;

		try {
			oos = new ObjectOutputStream(baos);
		} catch (IOException e1) {
			// Unable to create object output stream
			e1.printStackTrace();
		}

		try {
			oos.writeObject(pair);
		} catch (IOException e) {
			// Unable to write eventList in bytes
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	/**
	 * @param data
	 * @return
	 * 
	 * 		Converts bytes packets to ArrayList
	 */
	@SuppressWarnings("unchecked")
	private Scheduler.Direction byteArrayToDirection(byte[] data) {

		ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
		ObjectInputStream objStream = null;
		try {
			objStream = new ObjectInputStream(byteStream);
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		try {
			return (Scheduler.Direction) objStream.readObject();
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
	 * Send and receive data from Scheduler system.
	 */

	public synchronized void receiveTaskList() { // Re factor by @author
		byte[] data = new byte[Elevator.BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from Scheduler
		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		// update next floor
		nextDirection = byteArrayToDirection(data);

	}

	/**
	 * send arrival floor number from Elevator system to Schedulers
	 */
	public void sendArrivalInfo() {

		Pair pair;
		if (goingUP == true && goingDOWN == false) {
			pair = new Pair("up", currentFloor);
		} else if (goingUP == false && goingDOWN == true) {
			pair = new Pair("down", currentFloor);
		} else {
			pair = new Pair("idle", currentFloor);
		}
		sendPacket = packetCreator(pair);
		packetSend(sendPacket);

	}

	
	
	/**
	 * @param packet
	 */
	public void packetSend(DatagramPacket packet) {
		try {
			sendReceiveSocket.send(packet);
			System.out.printf(LocalTime.now().toString() + " Elevator#: %d Arrival info sent to Scheduler\n",
					getElevatorNumber());
		} catch (IOException e) {
			System.out.print("Packet Sending Error, Retrying \n");
			e.printStackTrace();
			packetSend(packet);
			//System.exit(1);
		}
		
		byte[] data = new byte[Elevator.BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from Scheduler
		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//Update direction
		ElevatorState state = byteArrayToState(data);
		
		nextDirection = state.getDirection();
		
		if (state.getTaskList().contains(currentFloor)) {
			elevatorOpendDoorAtFloor(currentFloor);
			elevatorCloseDoorAtFloor(currentFloor);
		}
		
	}
	
	public ElevatorState byteArrayToState(byte[] data){
		ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
		ObjectInputStream objStream = null;
		try {
			objStream = new ObjectInputStream(byteStream);
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		try {
			return (ElevatorState) objStream.readObject();
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
	 * @author Muhammad Tarequzzaman
	 * @param pair
	 * @return 
	 * @Description: Create Datagram packet containing byte array of event list
	 *               information
	 */
	public DatagramPacket packetCreator(Pair pair) {
		// Create Datagram packet containing byte array of event list information
		byte[] byteArr = PairToByteArray(pair);

		try { 
			sendPacket = new DatagramPacket(byteArr, byteArr.length, InetAddress.getLocalHost(),
					Elevator.SCHEDULER_SEND_PORT);
		} catch (UnknownHostException e) {
			System.out.print("sendPacket creation Error, Retrying creation \n");
			e.printStackTrace();
			this.packetCreator(pair);
			//System.exit(1);
		}
		return sendPacket;
	}
	/*-------------------------------------------------------------------------*/

	/* GET AND SET from here */
	public ArrayList<Boolean> getButtonList() {
		return buttonList;
	}

	public ArrayList<Boolean> getElevatorLamp() {
		return elevatorLamp;
	}

	public int getElevatorNumber() {
		return elevatorNumber;
	}

	public int getNextFloor() {
		return nextFloor;
	}

	public void setButtonList(ArrayList<Boolean> buttonList) {
		this.buttonList = buttonList;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public void setDoorState(Boolean dooropen) {
		this.dooropen = dooropen;
	}

	public void setElevatorLamp(ArrayList<Boolean> elevatorLamp) {
		this.elevatorLamp = elevatorLamp;
	}

	public void setGoingDOWN(Boolean goingDOWN) {
		this.goingDOWN = goingDOWN;
	}

	public void setGoingUP(Boolean goingUP) {
		this.goingUP = goingUP;
	}

	public void setNextFloor(int nextFloor) {
		this.nextFloor = nextFloor;
	}

	public DatagramPacket getReceivePacket() {
		return receivePacket;
	}

	public void setReceivePacket(DatagramPacket receivePacket) {
		this.receivePacket = receivePacket;
	}

	public Boolean isGoingDOWN() {
		return goingDOWN;
	}

	public Boolean isGoingUP() {
		return goingUP;
	}

	public DatagramSocket getSendreceiveSocket() {
		return sendReceiveSocket;
	}

	public static int getDoorDelay() {
		return doorDelay;
	}

	public static void setDoorDelay(int doorDelay) {
		Elevator.doorDelay = doorDelay;
	}

	public static int getSCHEDULER_SEND_PORT() {
		return SCHEDULER_SEND_PORT;
	}

	public static void setSCHEDULER_SEND_PORT(int sCHEDULER_SEND_PORT) {
		SCHEDULER_SEND_PORT = sCHEDULER_SEND_PORT;
	}

	public ArrayList<Integer> getNextFloorList() {
		return nextFloorList;
	}

	public void setNextFloorList(ArrayList<Integer> nextFloorList) {
		this.nextFloorList = nextFloorList;
	}

	public Boolean getDooropen() {
		return dooropen;
	}

	public void setDooropen(Boolean dooropen) {
		this.dooropen = dooropen;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public Boolean getGoingUP() {
		return goingUP;
	}

	public Boolean getGoingDOWN() {
		return goingDOWN;
	}

	public void setElevatorNumber(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
	}

}
