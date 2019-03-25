import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;

public class ElevatorButtons {
	private int elevatorNumber;
	
	private int buttonP;

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public void main(int elevatorNumber) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ElevatorButtons window = new ElevatorButtons(elevatorNumber);
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @param elevatorNumber 
	 */
	public ElevatorButtons(int elevatorNumber) {
		this.elevatorNumber = elevatorNumber;
		initialize();
		this.getFrame().setVisible(true);
		Thread countDown = new Thread() {
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				buttonPressed(-1);
			}
		};
		countDown.start();
	}
	
	public void buttonPressed(int i) {
		this.setButtonP(i);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setFrame(new JFrame());
		getFrame().setTitle("Elevator: " + this.elevatorNumber);
		getFrame().setBounds(100, 100, 425, 406);
		getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getFrame().getContentPane().setLayout(gridBagLayout);
		
		JButton btnNewButton = new JButton("1");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buttonPressed(1);
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 3;
		gbc_btnNewButton.gridy = 1;
		getFrame().getContentPane().add(btnNewButton, gbc_btnNewButton);
		
		JButton button = new JButton("2");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(2);
			}
		});
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 5);
		gbc_button.gridx = 4;
		gbc_button.gridy = 1;
		getFrame().getContentPane().add(button, gbc_button);
		
		JButton button_1 = new JButton("3");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(3);
			}
		});
		GridBagConstraints gbc_button_1 = new GridBagConstraints();
		gbc_button_1.insets = new Insets(0, 0, 5, 5);
		gbc_button_1.gridx = 5;
		gbc_button_1.gridy = 1;
		getFrame().getContentPane().add(button_1, gbc_button_1);
		
		JButton button_2 = new JButton("4");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(4);
			}
		});
		GridBagConstraints gbc_button_2 = new GridBagConstraints();
		gbc_button_2.insets = new Insets(0, 0, 5, 5);
		gbc_button_2.gridx = 3;
		gbc_button_2.gridy = 2;
		getFrame().getContentPane().add(button_2, gbc_button_2);
		
		JButton button_3 = new JButton("5");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(5);
			}
		});
		GridBagConstraints gbc_button_3 = new GridBagConstraints();
		gbc_button_3.insets = new Insets(0, 0, 5, 5);
		gbc_button_3.gridx = 4;
		gbc_button_3.gridy = 2;
		getFrame().getContentPane().add(button_3, gbc_button_3);
		
		JButton button_4 = new JButton("6");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(6);
			}
		});
		GridBagConstraints gbc_button_4 = new GridBagConstraints();
		gbc_button_4.insets = new Insets(0, 0, 5, 5);
		gbc_button_4.gridx = 5;
		gbc_button_4.gridy = 2;
		getFrame().getContentPane().add(button_4, gbc_button_4);
		
		JButton button_5 = new JButton("7");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(7);
			}
		});
		GridBagConstraints gbc_button_5 = new GridBagConstraints();
		gbc_button_5.insets = new Insets(0, 0, 5, 5);
		gbc_button_5.gridx = 3;
		gbc_button_5.gridy = 3;
		getFrame().getContentPane().add(button_5, gbc_button_5);
		
		JButton button_6 = new JButton("8");
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(8);
			}
		});
		GridBagConstraints gbc_button_6 = new GridBagConstraints();
		gbc_button_6.insets = new Insets(0, 0, 5, 5);
		gbc_button_6.gridx = 4;
		gbc_button_6.gridy = 3;
		getFrame().getContentPane().add(button_6, gbc_button_6);
		
		JButton button_7 = new JButton("9");
		button_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(9);
			}
		});
		GridBagConstraints gbc_button_7 = new GridBagConstraints();
		gbc_button_7.insets = new Insets(0, 0, 5, 5);
		gbc_button_7.gridx = 5;
		gbc_button_7.gridy = 3;
		getFrame().getContentPane().add(button_7, gbc_button_7);
		
		JButton button_8 = new JButton("10");
		button_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(10);
			}
		});
		GridBagConstraints gbc_button_8 = new GridBagConstraints();
		gbc_button_8.insets = new Insets(0, 0, 5, 5);
		gbc_button_8.gridx = 3;
		gbc_button_8.gridy = 4;
		getFrame().getContentPane().add(button_8, gbc_button_8);
		
		JButton button_9 = new JButton("11");
		button_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(11);
			}
		});
		GridBagConstraints gbc_button_9 = new GridBagConstraints();
		gbc_button_9.insets = new Insets(0, 0, 5, 5);
		gbc_button_9.gridx = 4;
		gbc_button_9.gridy = 4;
		getFrame().getContentPane().add(button_9, gbc_button_9);
		
		JButton button_10 = new JButton("12");
		button_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(12);
			}
		});
		GridBagConstraints gbc_button_10 = new GridBagConstraints();
		gbc_button_10.insets = new Insets(0, 0, 5, 5);
		gbc_button_10.gridx = 5;
		gbc_button_10.gridy = 4;
		getFrame().getContentPane().add(button_10, gbc_button_10);
		
		JButton button_11 = new JButton("13");
		button_11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(13);
			}
		});
		GridBagConstraints gbc_button_11 = new GridBagConstraints();
		gbc_button_11.insets = new Insets(0, 0, 5, 5);
		gbc_button_11.gridx = 3;
		gbc_button_11.gridy = 5;
		getFrame().getContentPane().add(button_11, gbc_button_11);
		
		JButton button_12 = new JButton("14");
		button_12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(14);
			}
		});
		GridBagConstraints gbc_button_12 = new GridBagConstraints();
		gbc_button_12.insets = new Insets(0, 0, 5, 5);
		gbc_button_12.gridx = 4;
		gbc_button_12.gridy = 5;
		getFrame().getContentPane().add(button_12, gbc_button_12);
		
		JButton button_13 = new JButton("15");
		button_13.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(15);
			}
		});
		GridBagConstraints gbc_button_13 = new GridBagConstraints();
		gbc_button_13.insets = new Insets(0, 0, 5, 5);
		gbc_button_13.gridx = 5;
		gbc_button_13.gridy = 5;
		getFrame().getContentPane().add(button_13, gbc_button_13);
		
		JButton button_14 = new JButton("16");
		button_14.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(16);
			}
		});
		GridBagConstraints gbc_button_14 = new GridBagConstraints();
		gbc_button_14.insets = new Insets(0, 0, 5, 5);
		gbc_button_14.gridx = 3;
		gbc_button_14.gridy = 6;
		getFrame().getContentPane().add(button_14, gbc_button_14);
		
		JButton button_15 = new JButton("17");
		button_15.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(17);
			}
		});
		GridBagConstraints gbc_button_15 = new GridBagConstraints();
		gbc_button_15.insets = new Insets(0, 0, 5, 5);
		gbc_button_15.gridx = 4;
		gbc_button_15.gridy = 6;
		getFrame().getContentPane().add(button_15, gbc_button_15);
		
		JButton button_16 = new JButton("18");
		button_16.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(18);
			}
		});
		GridBagConstraints gbc_button_16 = new GridBagConstraints();
		gbc_button_16.insets = new Insets(0, 0, 5, 5);
		gbc_button_16.gridx = 5;
		gbc_button_16.gridy = 6;
		getFrame().getContentPane().add(button_16, gbc_button_16);
		
		JButton button_17 = new JButton("19");
		button_17.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(19);
			}
		});
		GridBagConstraints gbc_button_17 = new GridBagConstraints();
		gbc_button_17.insets = new Insets(0, 0, 5, 5);
		gbc_button_17.gridx = 3;
		gbc_button_17.gridy = 7;
		getFrame().getContentPane().add(button_17, gbc_button_17);
		
		JButton button_18 = new JButton("20");
		button_18.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(20);
			}
		});
		GridBagConstraints gbc_button_18 = new GridBagConstraints();
		gbc_button_18.insets = new Insets(0, 0, 5, 5);
		gbc_button_18.gridx = 4;
		gbc_button_18.gridy = 7;
		getFrame().getContentPane().add(button_18, gbc_button_18);
		
		JButton button_19 = new JButton("21");
		button_19.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(21);
			}
		});
		GridBagConstraints gbc_button_19 = new GridBagConstraints();
		gbc_button_19.insets = new Insets(0, 0, 5, 5);
		gbc_button_19.gridx = 5;
		gbc_button_19.gridy = 7;
		getFrame().getContentPane().add(button_19, gbc_button_19);
		
		JButton button_20 = new JButton("22");
		button_20.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed(22);
			}
		});
		GridBagConstraints gbc_button_20 = new GridBagConstraints();
		gbc_button_20.insets = new Insets(0, 0, 0, 5);
		gbc_button_20.gridx = 4;
		gbc_button_20.gridy = 8;
		getFrame().getContentPane().add(button_20, gbc_button_20);
	}

	public int getButtonP() {
		return buttonP;
	}

	public void setButtonP(int buttonP) {
		this.buttonP = buttonP;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

}
