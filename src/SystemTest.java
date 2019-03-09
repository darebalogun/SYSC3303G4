import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SystemTest {

	@Before
	public void setUp() throws Exception {
		generateInput("1", "5");
		Random rand = new Random();
		int n = rand.nextInt(10);
		try {
			TimeUnit.SECONDS.sleep(n);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	public void generateInput(String from, String to) {
		
		String time = java.time.LocalTime.now().toString();
		
		String direction;
		
		if (Integer.parseInt(from) > Integer.parseInt(to)) {
			direction = "down";
		} else {
			direction = "up";
		}
		
		String request = time + " " + from + " " + direction + " " + to;
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("src/InputEvents.txt", true));
			out.newLine();
			out.write(request);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
