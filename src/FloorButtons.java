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
	private GridBagConstraints[][] buttonConstArray;
	private int[] buttonP;


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
					}
				});
				buttonConstArray[i][j] = new GridBagConstraints();
				buttonConstArray[i][j].insets = new Insets(0, 0, 5, 5);
				buttonConstArray[i][j].gridx = i % 3 + 2 + (4*j);
				buttonConstArray[i][j].gridy = i/3 + 24;
				frame.getContentPane().add(buttonArray[i][j], buttonConstArray[i][j]);
				
			}
		}

	}

	@Override
	public void update(Observable arg0, Object currentFloor) {
		int[] position = (int[]) currentFloor;
		// Array of elevator number and position
		
		int elevator_index = position[0] - 1;
		int floor = position[1];
		
		floorArray[22 - currFloor.get(elevator_index)][elevator_index].setBackground(Color.WHITE);
		currFloor.set(elevator_index, floor);
		floorArray[22 - floor][elevator_index].setBackground(Color.YELLOW);
		//rePaint();
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
	
	public void enable(int elevator) {
		for (int i = 0; i < FLOOR_COUNT; i++) {
			buttonArray[i][elevator - 1].setEnabled(true);
		}
	}

}
