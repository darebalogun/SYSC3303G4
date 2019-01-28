import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Muhammad Tarequzzaman | 100954008
 * 
 *         This Class represent Elevator Car as a unit. Has basic functionality
 *         such as Button and lamp for floors to go , Door and door delay, Delay
 *         for between floors
 * 
 *         Scheduler input numberOfFloorDelayRunning as to run motor for the delay function.
 *         DatagramPacket
 */
public class ElevatorSubSystem {

	private int elevatorNumber; // nth Elevator number, DO NOT PUT Same number as some other instance;  
	public ArrayList<Boolean> buttonList;
	public ArrayList<Boolean> elevatorLamp;

	static private int timeBtwFloors = 3;
	static private int doorDelay = 1;
	private Boolean dooropen;
	private int numberOfFloorDelayRunning;

	// from update after 28th January
	private DatagramPacket sendPacket, receivePacket;
	private DatagramSocket sendSocket, receiveSocket;

	public ElevatorSubSystem(int elevatorNumber) {

		// create buttonList for 13 floor and Initialize as FALSE
		this.buttonList = new ArrayList<Boolean>(Arrays.asList(new Boolean[13]));
		Collections.fill(buttonList, Boolean.FALSE);

		// create elevatorLamp for 13 floor and Initialize as FALSE
		this.elevatorLamp = new ArrayList<Boolean>(Arrays.asList(new Boolean[13]));
		Collections.fill(elevatorLamp, Boolean.FALSE);

		// basic implementation
		this.dooropen = false;
		this.elevatorNumber = elevatorNumber;

		// from update after 28th January
		try {
			receiveSocket = new DatagramSocket(69);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * @param numberOfFloorDelayRunning, which comes from Scheduler
	 */
	public void runMotor() {
		try {

			int delaytime = (timeBtwFloors * this.numberOfFloorDelayRunning) + (2 * doorDelay);
			System.out.printf(" Motor will run for %d sec", delaytime);
			TimeUnit.SECONDS.sleep(delaytime);
			// send scheduler that its Done. 

		} catch (Exception e) {
			// TODO Auto-generated catch block-

			System.out.println("Some Error in runMotor \n");
			e.printStackTrace();
		}

	}
	
	/**
	 * Elevator Door open function
	*/
	public void openDoor() {
		this.setDooropen(true); 
		System.out.println("ElevatorDoor Open \n");
	}
	
	/*
	 * Elevator Door Close function
	*/
	public void closeDoor() {
		this.setDooropen(false); 
		System.out.println("ElevatorDoor Close \n");
	}
	
	/*
	 * Elevator inside button Pushed function
	*/
	public void buttonPushed(int n) {
		
		getButtonList().set(n, true); 
		getElevatorLamp().set(n, true);	
	}
	
	/*
	 * Elevator Door Opened at a Floor function
	*/
	
	public void elevatorOpendDoorAtFloor(int n) {
		getButtonList().set(n, false); 
		getElevatorLamp().set(n, false);	
		openDoor();
		
	}
	/*
	 * Elevator Door Closed at a Floor function
	*/
	
	public void elevatorCloseDoorAtFloor(int n) {
		getButtonList().set(n, false); 
		getElevatorLamp().set(n, false);	
		closeDoor();
		
	}


	// from update after 28th January
	/**
	 * Send and receive data from Scheduler system.
	 */

	// Please use setNumberOfFloor(int numberOfFloorDelayRunning) to update numberOfFloorDelayRunning from
	// the packet --Tareq
	public void receiveAndSendToScheduler() {

		// we have to add here that we receiving from scheduler and what we sending to
		// scheduler

		byte[] data = null;
		receivePacket = new DatagramPacket(data, data.length);

		try {
			receiveSocket.receive(receivePacket); // receiving packets from the host
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			sendSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
			;
		}

		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		sendSocket.close();
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
		return timeBtwFloors;
	}



	public Boolean getDooropen() {
		return dooropen;
	}

	public void setDooropen(Boolean dooropen) {
		this.dooropen = dooropen;
	}

	public int getNumberOfFloor() {
		return numberOfFloorDelayRunning;
	}

	public void setNumberOfFloor(int numberOfFloor) {
		this.numberOfFloorDelayRunning = numberOfFloor;
	}

	public int getElevatorNumber() {
		return elevatorNumber;
	}

	public void setElevatorNumber(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
	}
	
	
	/*
	 * To print information of the current ElevatorSubSystem.
	 * 
	 */
	@Override
	public String toString() {
		return "ElevatorSubSystem [elevatorNumber=" + elevatorNumber + ", buttonList=" + buttonList + ", elevatorLamp="
				+ elevatorLamp + ", timeBtwFloors=" + timeBtwFloors + ", doorDelay=" + doorDelay + ", dooropen="
				+ dooropen + ", numberOfFloorDelayRunning=" + numberOfFloorDelayRunning + ", sendPacket=" + sendPacket + ", receivePacket="
				+ receivePacket + ", sendSocket=" + sendSocket + ", receiveSocket=" + receiveSocket + "]";
	}

}
