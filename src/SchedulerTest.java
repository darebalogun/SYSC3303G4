import static org.junit.Assert.*;
import org.junit.*;

public class SchedulerTest {

	Scheduler stest = new Scheduler();
	//Scheduler stest2 = new Scheduler();
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	public void testMain() {
		assertEquals(Scheduler.class ,stest.getClass());
		//assertEquals(Scheduler.class, stest2.getClass());
	}
	
	public void testOutput() {
		
		
	}
	
}
