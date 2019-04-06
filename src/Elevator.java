import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Random;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
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

public class Elevator extends Observable {

	Instant instant;
	private static final int BYTE_SIZE = 6400;
	static private int timeBtwFloors = 4; // time as Canal building main Elevators
	static private int doorDelay = 2;
	private static final int MAINTENANCE_PORT = 60009;

	// private static int RECEIVE_PORT = 50002;

	private static int SCHEDULER_SEND_PORT = 60006;

	// nth Elevator number, DO NOT PUT Same number as some other instance;
	private int elevatorNumber;
	private ArrayList<Boolean> buttonList;
	private ArrayList<Boolean> elevatorLamp;
	private ArrayList<Integer> nextFloorList;
	private Boolean ACTIVE = true;
	private Boolean dooropen;
	FloorButtons floorButtons;

	private Integer currentFloor;
	private int nextFloor, numberofFloorbuttons;

	private Boolean goingUP = false;
	private Boolean goingDOWN = false;

	private DatagramPacket sendPacket, receivePacket; /* Packet */
	private DatagramSocket sendReceiveSocket; /* Socket */
	
	private static InetAddress SCHEDULER_IP;
	/*---------------------------------------------------------------*/

	/**
	 * @param elevatorNumber       : Unique number to represent unique Elevator in
	 *                             the system
	 * @param numberofFloorbuttons : number of button had to be install inside the
	 *                             elevator for floors. same as FloorSubsystem
	 * @param RECEIVE_PORT         : Unique Port Number
	 * @param startFloor           : Default Staring Floor
	 */
	public Elevator(int elevatorNumber, int numberofFloorbuttons, int RECEIVE_PORT, int startFloor, FloorButtons buttons) {
		this.numberofFloorbuttons = numberofFloorbuttons;
		// create buttonList for buttons floor and Initialize as FALSE
		buttonList = new ArrayList<>(Arrays.asList(new Boolean[numberofFloorbuttons]));
		Collections.fill(buttonList, Boolean.FALSE);

		// create elevatorLamp for buttons floor and Initialize as FALSE
		elevatorLamp = new ArrayList<>(Arrays.asList(new Boolean[numberofFloorbuttons]));
		Collections.fill(elevatorLamp, Boolean.FALSE);

		// basic implementation
		dooropen = false;

		this.elevatorNumber = elevatorNumber;
		
		this.floorButtons = buttons;

		currentFloor = startFloor;
		int[] posInfo = {elevatorNumber,currentFloor};
		notifyObservers(posInfo);
				
		this.addObserver(floorButtons);

		receiveSocketPortCreation(RECEIVE_PORT);

		System.out.printf(
				LocalTime.now().toString() + " Elevator E%d...Waiting for the requests from the Scheduler at Time \n",
				elevatorNumber);
		
		nextFloorList = new ArrayList<Integer>();
		
		try {
			SCHEDULER_IP = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

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
		READY, STANDBY, UPDATE, RUN, FINISH, DOOR_ERROR, ELEVATOR_ERROR;

	}

	/**
	 * This method implants FSM Using State condition to change state
	 */
	public void elevatorState() {

		State state = State.READY;
		
		Thread readButtonInput = new Thread() {
			public void run() {
				while (true) {
					Integer dest = floorButtons.getButtonP(elevatorNumber);
					
					if (dest > 0) {
						System.out.println("User pressed " + dest + " in elevator " + elevatorNumber);
						generateInput(elevatorNumber, dest);
						floorButtons.setButtonP(elevatorNumber, 0);
					}

				}
			}
		};
		
		readButtonInput.start();
		
		Thread receiveTasks = new Thread() {
			public void run() {
				while (true) {
					receiveTaskList();
				}
			}
		};
		
		receiveTasks.start();

		while (ACTIVE) {

			switch (state) {
			
			case DOOR_ERROR:
				System.out.println(LocalTime.now().toString() + " Elevator#: %d DOOR STUCK \n");
				
				System.out.println(LocalTime.now() + " Retrying...");
				
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				state = State.FINISH;
				
				break;
			case ELEVATOR_ERROR:

				ACTIVE = false;
				System.out.println(LocalTime.now().toString() + " Elevator#: " + getElevatorNumber() + " Elevator Stuck \n");

				break;

			case READY: // READY state
				System.out.printf(LocalTime.now().toString() + " Elevator#: %d READY \n", getElevatorNumber());
				ACTIVE = true;
				state = State.STANDBY;
				
				
				break; // end READY

			case STANDBY:// STANDBY state

				if (dooropen == true) {
					elevatorCloseDoorAtFloor(currentFloor);
				}

				if ((nextFloorList.size() > 0) || (currentFloor != nextFloor)) {
					updateNextFloor();
					state = State.RUN;
					break;
					
				} else {
					String[] status = new String[] {String.valueOf(elevatorNumber), "Elevator idle"};
					synchronized (this) {
						setChanged();
						notifyObservers(status);
					}
					state = State.UPDATE;
					System.out.printf(LocalTime.now().toString() + " Elevator#: %d STANDBY at Floor: %d \n",
							getElevatorNumber(), currentFloor);
					break;

				}

				//break;// end STANDBY

			case UPDATE: // UPDATE
				synchronized(this) {
					while (nextFloorList.isEmpty()) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				state = State.STANDBY;
				break;

			case RUN: // RUN
				String[] status = new String[] {String.valueOf(elevatorNumber), "Destination: " + nextFloor};
				synchronized (this) {
					setChanged();
					notifyObservers(status);
				}

				System.out.printf(LocalTime.now().toString() + " Elevator#: %d Next Destination is : %d\n",
						getElevatorNumber(), nextFloor);

				runToNextFloor();

				state = State.FINISH;

				break;

			case FINISH: // FINISH
				if (currentFloor == nextFloor) {
					if (!nextFloorList.isEmpty()) {
						nextFloorList.remove(currentFloor);
					}

					System.out.printf(LocalTime.now().toString() + " Elevator#: %d Arrived at floor: %d \n",
							getElevatorNumber(), currentFloor);
					
					status = new String[] {String.valueOf(elevatorNumber), "Door opening"};
					synchronized (this) {
						setChanged();
						notifyObservers(status);
					}
					
					if (floorButtons.getDoorStuckTag(elevatorNumber) == 1) {
						status = new String[] {String.valueOf(elevatorNumber), "Door Stuck"};
						synchronized (this) {
							setChanged();
							notifyObservers(status);
						}
						
						state = State.DOOR_ERROR;
						break;
					}
					
					elevatorOpendDoorAtFloor(currentFloor);
					status = new String[] {String.valueOf(elevatorNumber), "Door closing"};
					synchronized (this) {
						setChanged();
						notifyObservers(status);
					}
					elevatorCloseDoorAtFloor(currentFloor);	
					
					floorButtons.enable(elevatorNumber, currentFloor);

				} else {
					state = State.ELEVATOR_ERROR;
					break;
				}

				state = State.STANDBY;
				break;// end ARRIVED

			}
		}

	}

	/**
	 * @ElevatorRun Use this Function to run the elevator
	 */
	public void runToNextFloor() {
		// Prepare to run for target floor

		do {
			updateGoing_UPorDOWN();
			System.out.printf(LocalTime.now().toString() + " Elevator#: %d Currently at floor: %d \n",
					getElevatorNumber(), currentFloor);
			
			if (floorButtons.getDoorStuckTag(elevatorNumber) == 2) {
				String[] status = new String[] {String.valueOf(elevatorNumber), "Elevator Stuck"};
				synchronized (this) {
					setChanged();
					notifyObservers(status);
				}
				
				return;
			}

			if (isGoingUP().equals(true) && isGoingDOWN().equals(false)) {
				runMotor();
				synchronized(this) {
					currentFloor++;
					updateNextFloor();
					String[] status = new String[] {String.valueOf(elevatorNumber), "Destination: " + nextFloor};
					synchronized (this) {
						setChanged();
						notifyObservers(status);
					}
					setChanged();
					int[] posInfo = {elevatorNumber,currentFloor};
					notifyObservers(posInfo);
					sendArrivalInfo();
				}		

			} else if (isGoingDOWN().equals(true) && isGoingUP().equals(false)) {
				runMotor();
				synchronized(this) {
					currentFloor--;
					updateNextFloor();
					String[] status = new String[] {String.valueOf(elevatorNumber), "Destination: " + nextFloor};
					synchronized (this) {
						setChanged();
						notifyObservers(status);
					}
					setChanged();
					int[] posInfo = {elevatorNumber,currentFloor};
					notifyObservers(posInfo);
					sendArrivalInfo();
				}

			} else {
				updateNextFloor();
				sendArrivalInfo();
			}

		} while (currentFloor != nextFloor);

	
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
	public synchronized void updateNextFloor() {// change accordingly
		if (nextFloorList.size() > 0) {
			nextFloor = nextFloorList.get(0);
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

	public void receiveTaskList() { // Re factor by @author
		byte[] data = new byte[Elevator.BYTE_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);

		// Receive datagram socket from Scheduler
		try {
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		synchronized (this) {
			nextFloorList = byteArrayToList(data);
			notifyAll();
		}		

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
			pair = new Pair("standby", currentFloor);
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
			// System.exit(1);
		}

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

		sendPacket = new DatagramPacket(byteArr, byteArr.length, SCHEDULER_IP,
				Elevator.SCHEDULER_SEND_PORT);
		return sendPacket;
	}
	/*-------------------------------------------------------------------------*/

	public DatagramPacket packetCreator2(Pair pair) {
		// Create Datagram packet containing byte array of event list information
		byte[] byteArr = PairToByteArray(pair);

		sendPacket = new DatagramPacket(byteArr, byteArr.length, SCHEDULER_IP, MAINTENANCE_PORT);
		return sendPacket;
	}

	public void generateInput(Integer elevatorNum, Integer dest) {
		String time = LocalTime.now().toString();

		elevatorNum = getElevatorNumber();
		Integer destination = dest;

		Pair pair = new Pair(time, elevatorNum, destination);

		DatagramPacket pac = packetCreator2(pair);

		packetSend(pac);

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

	// ------------------------------------------------------------------------------------------------------//
	/*
	 * private static final String INPUT_PATH = "src/InputEvents.txt"; private int
	 * currentLine = 0; private boolean moreToRead;
	 * 
	 * public synchronized void ElevatorInputRead () {
	 * 
	 * //get text file path Path path = Paths.get(INPUT_PATH);
	 * 
	 * ArrayList<String> inputArrayList = new ArrayList<String>(); moreToRead =
	 * true;
	 * 
	 * 
	 * //iterate through the file and read each line while (moreToRead) { try
	 * (Stream<String> lines = Files.lines(path)) { try {
	 * inputArrayList.add(lines.skip(currentLine).findFirst().get()); }catch
	 * (NoSuchElementException e) { moreToRead = false; lines.close(); break; } }
	 * catch (IOException e) { e.printStackTrace(); } currentLine++; }
	 * 
	 * for (int i = 0; i < inputArrayList.size(); i++) {
	 * 
	 * }
	 * 
	 * for (int i = 0; i < inputArrayList.size(); i++) { String inputElevatorEvent =
	 * inputArrayList.get(i); String[] inputEvents = inputElevatorEvent.split(" ");
	 * 
	 * if ("e" + String.valueOf(elevatorNumber) == inputEvents [1]) { ArrayList
	 * <Integer> userDest = new ArrayList<Integer>(1);
	 * userDest.add(Integer.parseInt(inputEvents[2])); setNextFloorList(userDest); }
	 * }
	 * 
	 * notifyAll(); return;
	 * 
	 * }
	 */
}