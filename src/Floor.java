import java.util.*;

public class Floor {

	//Provides the floor number		
	private int floorNum;
	
	//Up and Down buttons on the floor's elevator
	private boolean upButton, downButton; 
	
	//to check if elevator is currently present on the floor
	private boolean elevatorPresent;
	
	//to check if the elevator door is open or closed
	private boolean doorOpen, doorClosed; 
	
	//Up and Down lamps on the floor's elevator buttons
	private boolean upLamp, downLamp;
			
	public Floor() {
		

	 
	}	
	
/** get the current floor that the elevator is on */
public int getCurrentFloor() {
	return floorNum;
	}

/** true if the elevator is present on the current floor **/
public boolean isElevatorPresent() {
	return elevatorPresent;
}

/** true if the elevator door is open **/
public boolean isDoorOpen() {
	return doorOpen;
}

/** true if the elevator door is closed **/
public boolean isDoorClosed() {
	return doorClosed;
}

/** true if the up button was pressed  **/
public boolean upButtonPressed() {
	return upButton;
}

/** true if the down button was pressed  **/
public boolean downButtonPressed() {
	return downButton;
}

/** true if the up direction lamp is lit **/
public boolean upLampLit() {
	return upLamp;
}

/** true if the down direction lamp is lit **/
public boolean downLampLit() {
	return downLamp;
}



}


