import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElevatorSubSystemTest {
	static ElevatorSubSystem ES;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ES= new ElevatorSubSystem();
	}

	

	@Test
	public void testMain() {
		assertEquals( ElevatorSubSystem.class ,ES.getClass());
	}

	@Test
	public void testGetReceivePort1() {
		
		assertEquals(5248, ElevatorSubSystem.getReceivePort1());
	}

	@Test
	public void testGetReceivePort2() {
		assertEquals(5249, ElevatorSubSystem.getReceivePort2());
	}

	@Test
	public void testGetReceivePort3() {
		assertEquals(5250, ElevatorSubSystem.getReceivePort3());
	}

	@Test
	public void testGetReceivePort4() {
		assertEquals(5251, ElevatorSubSystem.getReceivePort4());
	}

	@Test
	public void testGetFloors() {
		assertEquals(5, ElevatorSubSystem.getFloors());
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ES = null;
		assertNull(ES);
	}
}
