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

public class Elevator extends Thread {
	
	

	private static final int BYTE_SIZE = 6400;
	static private int timeBtwFloors = 2; // time as Canal building main Elevators
	static private int doorDelay = 1;

	// private static int RECEIVE_PORT = 50002;
	public int RECEIVE_PORT = 0;
	private static int SCHEDULER_SEND_PORT = 60006;

	// nth Elevator number, DO NOT PUT Same number as some other instance;
	private int elevatorNumber;
	public ArrayList<Boolean> buttonList;
	public ArrayList<Boolean> elevatorLamp;
	private ArrayList<Integer> nextFloorList;
	public Boolean ACTIVE = true;
	private Boolean dooropen;

	private int currentFloor;
	private int nextFloor;

	private Boolean goingUP;
	private Boolean goingDOWN;

	private DatagramPacket sendPacket, receivePacket; /* Packet */
	private DatagramSocket sendSocket, receiveSocket; /* Socket */
	/*---------------------------------------------------------------*/

	/**
	 * @param elevatorNumber : Unique number to represent unique Elevator in the
	 *                       system
	 * @param numberofFloorbuttons        : number of button had to be install inside the
	 *                       elevator for floors.
	 */
	public Elevator(int elevatorNumber, int numberofFloorbuttons, int RECEIVE_PORT, int startFloor) {

		System.out.println("ElevatorSubSystem running...Waiting for the requests from the Scheduler \n");

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

		receiveSocketPortCreation(RECEIVE_PORT);

	}
	
	public void run() {
		
		while(true) {
			receiveTaskList();
			elevatorState();
			
		}
		
	}
	
	/**
	 * Creating port for ELEVATOR
	 * @param PORT_number
	 */
	public void receiveSocketPortCreation(int PORT_number) {
		try {
			receiveSocket = new DatagramSocket(PORT_number);
		} catch (SocketException se) {
			System.out.println("Error in receiveSocketPort creation \n");
			se.printStackTrace();
			System.exit(1);
		}
	}

	/*---------------------------------------------------------------*/
	public enum State {
		Ready, Idle, UpdateInput, Run, Arrived;

	}
	/**
	 * This method implants FSM Using State
	 * condition to change state
	 */
	public void elevatorState() {

		State state = State.Ready;

		while (ACTIVE) {

			switch (state) {

			case Ready: // Ready state
				System.out.print("\n System Ready \n");
				ACTIVE = true;
				state = State.Idle;
				break; // end Ready

			case Idle:// Idle state
				if (dooropen) {
					elevatorCloseDoorAtFloor(currentFloor);
				}

				if ((nextFloorList.size() > 0) || (currentFloor != nextFloor)) {
					updateNextFloor();
					state = State.Run;

				} else {
					state = State.UpdateInput;
					System.out.printf(" Idle at floor %d \n", currentFloor);

				}

				break;// end Idle

			case UpdateInput: // UpdateInput

				if ((nextFloorList.size() > 0) || (currentFloor != nextFloor)) {
					state = State.Run;
				} else {
					receiveTaskList();
					// updateGoing_UPorDOWN();
					state = State.Idle;
				}

				break;// end UpdateInput

			case Run: // Run

				System.out.printf(" Elevator's Next Destination is : %d\n", nextFloor);

				runToNextFloor();

				state = State.Arrived;

				break; // end Run

			case Arrived: // Arrived
				if (currentFloor == nextFloor) { // later we will use here

					System.out.printf(" Elevator Arrived at floor: %d \n", currentFloor);
					sendArrivalInfo();
					elevatorOpendDoorAtFloor(currentFloor);
					elevatorCloseDoorAtFloor(currentFloor);
					if (nextFloorList.size() != 0) {
						// System.out.println(nextFloorList.size());
						nextFloor = nextFloorList.remove(0);
						// state = State.Run;
						// break;
					}

				}

				state = State.Idle;
				break;// end ARRIVED

			}
		}

	}

	/**
	 * @ElevatorRun Use this Function to run the elevator
	 */
	public void runToNextFloor() {
		// Prepare to run for target floor

		// running until next floor
		while (currentFloor != nextFloor) {
			System.out.printf(" Currently at floor: %d \n", currentFloor);
			updateGoing_UPorDOWN();
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

		}

	}

	/**
	 * runMotor for a time
	 */
	public void runMotor() {
		try {

			TimeUnit.SECONDS.sleep(Elevator.timeBtwFloors);

		} catch (Exception e) {

			System.out.println("Some Error in runMotor \n");
			e.printStackTrace();
		}

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
			nextFloor = nextFloorList.get(0);
			// System.out.printf(" NEXT Floor %d \n", nextFloor);
		}
		if ((currentFloor < 0) || (buttonList.size() < currentFloor)) { // check current floor is valid or not.
			System.out.println("Elevator Cureent Floor Number out of the range \n");

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
			TimeUnit.SECONDS.sleep(Elevator.doorDelay);
			setDoorState(false);
			System.out.println(" Elevator door closing \n");
		} catch (InterruptedException e) {
			System.out.println(" Some Error in Closing Door \n");
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
	private ArrayList<Integer> byteArrayToList(byte[] data) {

		ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
		ObjectInputStream objStream = null;
		try {
			objStream = new ObjectInputStream(byteStream);
		} catch (IOException e1) {

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

	/**
	 * Send and receive data from Scheduler system.
	 */

	public void receiveTaskList() {
		byte[] data = new byte[Elevator.BYTE_SIZE];
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

	/**
	 * send arrival floor number from Elevator system to Schedulers
	 */
	public void sendArrivalInfo() {

		Pair pair;
		if (goingUP) {
			pair = new Pair("up", currentFloor);
		} else {
			pair = new Pair("down", currentFloor);
		}

		byte[] data = PairToByteArray(pair);

		// Create Datagram packet containing byte array of event list information
		try {
			sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(),
					Elevator.SCHEDULER_SEND_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			sendSocket = new DatagramSocket();
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

}
