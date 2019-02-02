import java.io.Serializable;

public class InputEvent implements Serializable,Comparable<InputEvent> {
	/** 
	 * InputEvent.java
	 * SYSC3303G4
	 * 
	 * Iteration 1
	 * 
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Event time log
	private final String time;
	
	// Floor where button was pressed
	private final Integer currentFloor;
	
	// Up or down
	private final Boolean up;
	
	// Car button
	private final Integer destinationFloor;

	public InputEvent(String time, Integer currentFloor, Boolean up, Integer destinationFloor) {
		
		this.time = time;
		
		this.currentFloor = currentFloor;
		
		this.up = up;
		
		this.destinationFloor = destinationFloor;
		
	}

	public String getTime() {
		return time;
	}

	public Boolean getUp() {
		return up;
	}
	
	public Integer getDestinationFloor() {
		return destinationFloor;
	}

	@Override
	public int compareTo(InputEvent event) {
		return (this.getDestinationFloor() < event.getDestinationFloor() ? -1 : (this.getDestinationFloor() == event.getDestinationFloor() ? 0 : 1));
	}

	public Integer getCurrentFloor() {
		return currentFloor;
	}

}
