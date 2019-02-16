import static org.junit.Assert.*;
import org.junit.*;

public class SchedulerTest {

	Scheduler stest = new Scheduler();
	static ElevatorSubSystem ES2;
	//Scheduler stest2 = new Scheduler();
	public static void setUpBeforeClass() throws Exception {
		ES2= new ElevatorSubSystem();
	}
	
	public void testMain() {
		assertEquals(Scheduler.class ,stest.getClass());
		//assertEquals(Scheduler.class, stest2.getClass());
	}
	
	public void testOutput() {
		//ES2.getClass()
		
	}
	
}
