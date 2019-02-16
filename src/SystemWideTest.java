import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

public class SystemWideTest {
	
	public void addInput() {
		String time = java.time.LocalTime.now().toString();
		
		Random rand = new Random();
		
		Integer n = rand.nextInt(5) + 1;
		
		String from = n.toString();
		
		Integer m = rand.nextInt(5) + 1;
		
		while (n == m) {
			m = rand.nextInt(5) + 1;
		}
		
		String to = m.toString();
		
		String direction;
		
		if (n > m) {
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

	@Test
	public void SimulateInputs() {
		addInput();
		
	}

}
