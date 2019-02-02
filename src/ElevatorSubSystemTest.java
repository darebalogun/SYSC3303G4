import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElevatorSubSystemTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public final void testElevatorSubSystem() {
		//fail("Not yet implemented"); // TODO
		ElevatorSubSystem E = new ElevatorSubSystem(1, 5);
		assertEquals(false, equals(E));
	}

	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	

}
