import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */

/**
 * @author TZ-WORKSTATION
 *
 */
public class ElevatorTest {

	/**
	 * @throws java.lang.Exception
	 *
	 */
	
	static Elevator E1, E2;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		E1 = new Elevator(1, 5, 5000, 1);
		E2 = new Elevator(2, 5, 5001, 5);
	}

	/**
	 * @throws java.lang.Exception
	 */
	

	/**
	 * Test method for {@link Elevator#Elevator(int, int, int, int)}.
	 */
	@Test
	public void testElevator() {
		assertNotEquals(E1, E2);
	}

	/**
	 * Test method for {@link Elevator#receiveSocketPortCreation(int)}.
	 */
	@Test
	public void testReceiveSocketPortCreation() {
		
		assertNotNull(E1.getSendreceiveSocket());
		assertNotNull(E2.getSendreceiveSocket());
		assertNotEquals(E1.getSendreceiveSocket(), E2.getSendreceiveSocket());

	}

	/**
	 * Test method for {@link Elevator#openDoor()}.
	 */
	@Test
	public void testOpenDoor() {
		E1.openDoor();
		assertEquals(true, E1.getDooropen());
	}

	/**
	 * Test method for {@link Elevator#closeDoor()}.
	 */
	@Test
	public void testCloseDoor() {
		E1.closeDoor();
		assertEquals(false, E1.getDooropen());
	}

	
	
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		E1 = null;
		E2= null;
		assertNull(E1);
		assertNull(E2);
	}

}
