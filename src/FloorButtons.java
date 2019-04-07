import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.beans.PropertyChangeEvent;
import java.awt.Font;
import javax.swing.SwingConstants;

public class FloorButtons implements Observer {

	private JFrame frame;
	private final int FLOOR_COUNT = 22;
	private final int ELEVATOR_COUNT = 4;
	private ArrayList<Integer> currFloor;
	private JTextField[][] floorArray; 
	private GridBagConstraints[][] constArray;
	private JButton[][] buttonArray;
	private JTextField[] titles;
	private JTextField[] status;
	private GridBagConstraints[] statusConst;
	private GridBagConstraints[] titleConstraints;
	private GridBagConstraints[][] buttonConstArray;
	private int[] buttonP;
	private JButton[] doorStuckButtons;
	private GridBagConstraints[] doorStuckConstr;
	private int doorStuckTag[];


	public int getDoorStuckTag(int elevatorNumber) {
		return doorStuckTag[elevatorNumber - 1];
	}

	public void setDoorStuckTag(int elevatorNumber, int code) {
		this.doorStuckTag[elevatorNumber - 1] = code;
		this.doorStuckButtons[elevatorNumber - 1].setEnabled(true);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FloorButtons window = new FloorButtons(1, 2, 3, 4);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public FloorButtons(int a, int b, int c, int d) {
		currFloor = new ArrayList<Integer>(Arrays.asList(a,b,c,d));
		floorArray = new JTextField[22][4];
		constArray = new GridBagConstraints[22][4];
		buttonArray = new JButton[22][4];
		buttonConstArray = new GridBagConstraints[22][4];
		buttonP = new int[] {0,0,0,0};
		titles = new JTextField[4];
		titleConstraints = new GridBagConstraints[4];
		status = new JTextField[4];
		statusConst = new GridBagConstraints[4];
		doorStuckButtons = new JButton[4];
		doorStuckConstr = new GridBagConstraints[4];
		doorStuckTag = new int[] {0, 0, 0, 0};

		initialize();
		frame.setVisible(true);
		
		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			floorArray[22 - (currFloor.get(i))][i].setBackground(Color.YELLOW);;
		}
		
	}
	
	public void buttonPressed(int elevator, int i) {
		this.setButtonP(elevator, i);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setFont(new Font("Arial", Font.PLAIN, 20));
		frame.setBounds(50, 50, 900, 960);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		frame.getContentPane().setLayout(gridBagLayout);
		
		for (int i = 0; i < FLOOR_COUNT; i++) {
			for (int j = 0; j < ELEVATOR_COUNT; j++) {
				floorArray[i][j] = new JTextField();
				floorArray[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				floorArray[i][j].setFont(new Font("Arial", Font.PLAIN, 16));
				floorArray[i][j].setText(String.valueOf(22 - i));
				constArray[i][j] = new GridBagConstraints();
				constArray[i][j].gridwidth = 3;
				constArray[i][j].insets = new Insets(0, 0, 5, 5);
				constArray[i][j].fill = GridBagConstraints.HORIZONTAL;
				if (j == 0) {
					constArray[i][j].gridx = 2;
				} else if (j == 1) {
					constArray[i][j].gridx = 6;
				} else if (j == 2) {
					constArray[i][j].gridx = 10;
				} else {
					constArray[i][j].gridx = 14;
				}
				constArray[i][j].gridy = i + 2;
				frame.getContentPane().add(floorArray[i][j], constArray[i][j]);
				floorArray[i][j].setColumns(10);
			}
		}
		
		for (int i = 0; i < FLOOR_COUNT; i++) {
			for (int j = 0; j < ELEVATOR_COUNT; j++) {
				buttonArray[i][j] = new JButton(String.valueOf(i + 1));
				final Integer x = new Integer(i);
				final Integer y = new Integer(j);
				buttonArray[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						buttonPressed(y + 1,x + 1);
						buttonArray[x][y].setEnabled(false);
					}
				});
				buttonConstArray[i][j] = new GridBagConstraints();
				buttonConstArray[i][j].insets = new Insets(0, 0, 5, 5);
				buttonConstArray[i][j].gridx = i % 3 + 2 + (4*j);
				buttonConstArray[i][j].gridy = i/3 + 24;
				frame.getContentPane().add(buttonArray[i][j], buttonConstArray[i][j]);
				
			}
		}
		
		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			if (i % 2 == 0) {
				doorStuckButtons[i] = new JButton("Block Door");
			} else {
				doorStuckButtons[i] = new JButton("Block Elevator");
			}
			final Integer x = new Integer(i);
			doorStuckButtons[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					doorStuckTag[x] = 2; // Block Elevator
					if (x % 2 == 0) // Block Door
						doorStuckTag[x] = 1;
					doorStuckButtons[x].setEnabled(false);
				}
			});
			doorStuckConstr[i] = new GridBagConstraints();
			doorStuckConstr[i].insets = new Insets(0, 0, 0, 5);
			doorStuckConstr[i].gridx = 3 + 4*i;
			doorStuckConstr[i].gridwidth = 2;
			doorStuckConstr[i].gridy = 31;
			frame.getContentPane().add(doorStuckButtons[i], doorStuckConstr[i]);
		}
		
		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			titles[i] = new JTextField();
			titles[i].setHorizontalAlignment(SwingConstants.CENTER);
			titles[i].setFont(new Font("Arial", Font.PLAIN, 20));
			titles[i].setText("Elevator: " + (i + 1));
			titles[i].setBackground(Color.CYAN);
			titleConstraints[i] = new GridBagConstraints();
			titleConstraints[i].gridwidth = 3;
			titleConstraints[i].insets = new Insets(0, 0, 5, 5);
			titleConstraints[i].fill = GridBagConstraints.HORIZONTAL;
			titleConstraints[i].gridy = 1;
			titleConstraints[i].gridx = 2 + 4*i;
			frame.getContentPane().add(titles[i], titleConstraints[i]);
		}
		
		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			status[i] = new JTextField();
			status[i].setHorizontalAlignment(SwingConstants.CENTER);
			status[i].setFont(new Font("Arial", Font.PLAIN, 14));
			status[i].setText("");
			statusConst[i] = new GridBagConstraints();
			statusConst[i].gridwidth = 3;
			statusConst[i].gridheight = 3;
			statusConst[i].insets = new Insets(0, 0, 5, 5);
			statusConst[i].fill = GridBagConstraints.HORIZONTAL;
			statusConst[i].gridy = 33;
			statusConst[i].gridx = 2 + 4*i;
			frame.getContentPane().add(status[i], statusConst[i]);
		}
		
		

	}

	@Override
	public void update(Observable arg0, Object currentFloor) {
		if (currentFloor instanceof int[]) {
			int[] position = (int[]) currentFloor;
			// Array of elevator number and position
			
			int elevator_index = position[0] - 1;
			int floor = position[1];
			
			floorArray[22 - currFloor.get(elevator_index)][elevator_index].setBackground(Color.WHITE);
			currFloor.set(elevator_index, floor);
			floorArray[22 - floor][elevator_index].setBackground(Color.YELLOW);
			
		} else if (currentFloor instanceof String[]){
			String[] statusUpdate = (String[]) currentFloor;
			
			int elevator_index = Integer.parseInt(statusUpdate[0]) - 1;
			String message = statusUpdate[1];
			
			status[elevator_index].setText(message);
			
		}

	}

	public int getButtonP(int elevator) {
		return buttonP[elevator - 1];
	}

	public void setButtonP(int elevator, int buttonP) {
		this.buttonP[elevator - 1] = buttonP;
	}
	
	public void disable(int elevator) {
		for (int i = 0; i < FLOOR_COUNT; i++) {
			buttonArray[i][elevator - 1].setEnabled(false);
		}
	}
	
	public void enable(int elevator, int floor) {
		for (int i = 0; i < FLOOR_COUNT; i++) {
			buttonArray[floor - 1][elevator - 1].setEnabled(true);
		}
	}

}
