import java.io.Serializable;

public class InputEvent implements Serializable {
	/** 
	 * InputEvent.java
	 * SYSC3303G4
	 * 
	 * @author Dare Balogun | 101062340
	 * @version Iteration 1
	 * 
	 * Reads inputs from the InputEvents test file.Contains information about the
	 * current floor, destination floor, time between floors and direction of the elevator.
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Event time log
	private final String time;
	
	// Floor where button was pressed
	private final Integer currentFloor;
	
	// Up or down
	private final Boolean up;
	

	public InputEvent(String time, Integer currentFloor, Boolean up) {
		
		this.time = time;
		
		this.currentFloor = currentFloor;
		
		this.up = up;
		
	}

	public String getTime() {
		return time;
	}

	public Boolean getUp() {
		return up;
	}


	
	@Override
	public boolean equals (Object o) {
		if (o == null) return false;
		if (o == this) return true;
		if (!(o instanceof InputEvent)) return false;
		InputEvent e = (InputEvent) o;
		return (this.getCurrentFloor() == e.getCurrentFloor());
		
	}

	public Integer getCurrentFloor() {
		return currentFloor;
	}

}
