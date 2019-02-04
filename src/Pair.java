import java.io.Serializable;

public class Pair implements Serializable {
	/**
	 * InputEvent.java SYSC3303G4
	 * 
	 * @author Dare Balogun | 101062340
	 * @version Iteration 1 This class is used as a data structure to sent
	 *          information as an object between the different subsystems
	 */
	private static final long serialVersionUID = 1L;
	private String string;
	private Integer integer;

	public Pair(String string, Integer integer) {
		this.setString(string);
		this.setInteger(integer);
	}

	public Integer getInteger() {
		return integer;
	}

	public void setInteger(Integer integer) {
		this.integer = integer;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
}