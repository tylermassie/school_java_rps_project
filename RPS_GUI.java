/*
 * Creator: Tyler Massie
 * Description: Gui Rock, Paper, Scissors game that implements all reqirements for the final project. Has non-stock assets for images and sound. Also it updates the gui (Times played and images) after every individual game and at the end (with different images) instead of just after all games have been played.*/
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Random;
import javax.sound.sampled.*;
import javax.swing.*;

public class RPS_GUI extends JFrame {
	private final int 		WINDOW_WIDTH = 1000;
	private final int 		WINDOW_HEIGHT = 750;

	/* NOG = Number of Games
	 * NOG variables are going to be related to setting/tracking
	 * the numbers of games played
	 */
	private final String[] 		NOG = {"1", "5", "Endless"};
	private JLabel 			NOG_Label;
	private JComboBox 		NOG_Dropdown;
	private JLabel 			NOG_Times_Label;
	private JTextField 		NOG_Times_Text;
	private JPanel 			NOG_Panel;

	// Panels to prevent the elements from being stretched
	private JPanel			Dropdown_Panel;
	private JPanel			Text_Panel;
	private JPanel			Button_Panel;

	//source: https://emojipedia.org/twitter/twemoji-12.1.4/
	private ImageIcon 		Round_Win_Img = new ImageIcon("Round_Win.png"); 
	private ImageIcon 		Round_Lose_Img = new ImageIcon("Round_Lose.png");
	private ImageIcon 		Neutral_Img = new ImageIcon("Neutral.png");
	private ImageIcon 		Game_Win_Img = new ImageIcon("Game_Win.png"); 
	private ImageIcon 		Game_Lose_Img = new ImageIcon("Game_Lose.png");

	private JLabel			My_Label;
	private JLabel			My_Img;
	private JPanel			My_Panel;
	private JLabel			CPU_Label;
	private JLabel			CPU_Img;
	private JPanel			CPU_Panel;
	private JPanel			Player_Panel;
	private JButton			Play_Button;

	private Random			rand = new Random();
	private boolean			Infinite_Games;
	private boolean			Game_Over;

	private int			My_Wins;
	private int			CPU_Wins;
	private int			Games_Played;
	private int			Games_Remaining;

	private int			My_Hand;
	private int			CPU_Hand;

	// Sound Stuff
	// Source: http://soundbible.com/
	private File			Win_File;
	private File			Lose_File;
	private AudioInputStream	Win_Stream;
	private AudioInputStream	Lose_Stream;
	private Clip			Win_Clip ;
	private Clip			Lose_Clip;


	// Constructor
	public RPS_GUI() {

		setTitle("Rock, Paper, Scissors");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());


		buildGameOptionPanel();

		add(NOG_Panel, BorderLayout.NORTH);
		add(Player_Panel, BorderLayout.CENTER);
		add(Button_Panel, BorderLayout.SOUTH);

		pack();
		setVisible(true);

		try {
			Win_File = new File("Win.wav");
			Lose_File = new File("Lose.wav");
			Win_Stream = AudioSystem.getAudioInputStream(Win_File);
			Lose_Stream = AudioSystem.getAudioInputStream(Lose_File);
			Win_Clip = AudioSystem.getClip();
			Lose_Clip = AudioSystem.getClip();
			Win_Clip.open(Win_Stream);
			Lose_Clip.open(Lose_Stream);
		} catch (Exception e) {
			System.out.println("Audio file(s) invalid or not found");
		}
	}

	
	private void buildGameOptionPanel() {
		NOG_Label 	= new JLabel("How Many Games?");
		NOG_Dropdown 	= new JComboBox(NOG);

		NOG_Times_Label = new JLabel("Games Played: ");
		NOG_Times_Text	= new JTextField(3);
		NOG_Times_Text.setEditable(false);

		NOG_Panel 	= new JPanel();
		NOG_Panel.setLayout(new GridLayout(2,2));

		Dropdown_Panel	= new JPanel();
		Text_Panel	= new JPanel();

		Dropdown_Panel.add(NOG_Dropdown);
		Text_Panel.add(NOG_Times_Text);

		NOG_Panel.add(NOG_Label);
		NOG_Panel.add(Dropdown_Panel);
		NOG_Panel.add(NOG_Times_Label);
		NOG_Panel.add(Text_Panel);

		My_Label 	= new JLabel("Me");
		My_Img		= new JLabel(Neutral_Img);
		CPU_Label 	= new JLabel("Computer");
		CPU_Img		= new JLabel(Neutral_Img);
		My_Panel	= new JPanel();
		CPU_Panel	= new JPanel();
		My_Img.addMouseListener(new MyMouseListener());
		CPU_Img.addMouseListener(new CPUMouseListener());

		My_Panel.setLayout(new BorderLayout());
		CPU_Panel.setLayout(new BorderLayout());

		My_Panel.add(My_Label, BorderLayout.CENTER);
		My_Panel.add(My_Img, BorderLayout.SOUTH);
		CPU_Panel.add(CPU_Label, BorderLayout.CENTER);
		CPU_Panel.add(CPU_Img, BorderLayout.SOUTH);

		Player_Panel = new JPanel();
		Player_Panel.add(My_Panel);
		Player_Panel.add(CPU_Panel);

		Play_Button  = new JButton("Play");
		Play_Button.addActionListener(new PlayButtonListener());

		Button_Panel = new JPanel();
		Button_Panel.add(Play_Button);

	}

	private void ClearValues() {
		My_Hand = 0;
		My_Wins = 0;
		CPU_Hand = 0;
		CPU_Wins = 0;
		Games_Played = 0;
		Game_Over = false;
		NOG_Times_Text.setText("");
		CPU_Img.setIcon(Neutral_Img);
		My_Img.setIcon(Neutral_Img);
	}
	private void RPS_Backend() {

		while(Games_Remaining > 0 || Infinite_Games) {
			CPU_Hand = 1 + rand.nextInt(3);
			String output;
			char temp; //alternative to redundant uses of .toUpperCase().charAt(0)
			do {
				output = JOptionPane.showInputDialog("(R)ock, (P)aper, or (S)cissors? (No Input or Cancel to Quit)");
				if ((output != null) && (output.length() > 0)) {
					temp = output.toUpperCase().charAt(0);
				}
				else {
					Infinite_Games = false;
					Games_Remaining = 0;
					temp = '0';
					break;
				}
			} while (temp != 'R' && temp != 'P' && temp != 'S' && temp != '1' && temp != '2' && temp != '3');  
			
			switch (temp) {
				case '1':
				case 'R':
					My_Hand = 1;
					break;
				case '2':
				case 'P':
					My_Hand = 2;
					break;
				case '3':
				case 'S':
					My_Hand = 3;
					break;
				default:
					My_Hand = 0;
			}
			// 1 - Rock
			// 2 - Paper
			// 3 - Scissors
			if(My_Hand == CPU_Hand) {
				CPU_Img.setIcon(Neutral_Img);
				My_Img.setIcon(Neutral_Img);
				Games_Played++;
				UpdateUI();
				JOptionPane.showMessageDialog(null, "Tied Game.");
			}
			else if(My_Hand == 1) {
				if(CPU_Hand == 2) 
					CPUVictory();
				else if(CPU_Hand == 3)
					MyVictory();
			}
			else if(My_Hand == 2) {
				if(CPU_Hand == 1)
					MyVictory();
				else if(CPU_Hand == 3)
					CPUVictory();
			}
			else if (My_Hand == 3) {
				if(CPU_Hand == 1) 
					CPUVictory();
				else if(CPU_Hand == 2)
					MyVictory();
			}
			Games_Remaining--;
		}
		Game_Over = true;
		UpdateUI();
		if (My_Wins > CPU_Wins) {
			My_Img.setIcon(Game_Win_Img);
			CPU_Img.setIcon(Game_Lose_Img);
		}
		else if (My_Wins < CPU_Wins) {
			CPU_Img.setIcon(Game_Win_Img);
			My_Img.setIcon(Game_Lose_Img);
		}
		else {
			CPU_Img.setIcon(Neutral_Img);
			My_Img.setIcon(Neutral_Img);
		}
	}

	private void MyVictory() {
		My_Img.setIcon(Round_Win_Img);
		CPU_Img.setIcon(Round_Lose_Img);
		My_Wins++;
		Games_Played++;
		UpdateUI();
		JOptionPane.showMessageDialog(null, "I Won!");
	}
	private void CPUVictory() {
		CPU_Img.setIcon(Round_Win_Img);
		My_Img.setIcon(Round_Lose_Img);
		CPU_Wins++;
		Games_Played++;
		UpdateUI();
		JOptionPane.showMessageDialog(null, "Computer Wins!");
	}
	private void UpdateUI() {
		NOG_Times_Text.setText("" + Games_Played);
		My_Label.setText("Me: " + My_Wins);
		CPU_Label.setText("Computer: " + CPU_Wins);
	}
	

	private void debug() {
		System.out.println("My_Wins: " + My_Wins);
		System.out.println("My_Hand: " + My_Hand);
		System.out.println("CPU_Wins: " + CPU_Wins);
		System.out.println("CPU_Hand: " + CPU_Hand);
	}


	private class PlayButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(NOG_Dropdown.getSelectedItem() == "Endless") {
				Infinite_Games = true;
				Games_Remaining = 0;
			}
			else {
				Infinite_Games = false;
				Games_Remaining = Integer.parseInt((String)NOG_Dropdown.getSelectedItem());
			}

			ClearValues();
			RPS_Backend();
		}
	}
	// Having two mouse listener classes is probably redundant
	private class MyMouseListener implements MouseListener {
		public void mouseEntered(MouseEvent e) {
			if(Game_Over) {
				if(CPU_Wins > My_Wins) {
					Lose_Clip.loop(Clip.LOOP_CONTINUOUSLY);
				}
				else if (My_Wins > CPU_Wins) {
					Win_Clip.loop(Clip.LOOP_CONTINUOUSLY);
				}
			}
		}
		public void mouseExited(MouseEvent e) {
			if(Game_Over) {
				if(CPU_Wins > My_Wins) {
					Lose_Clip.stop();
				}
				else if (My_Wins > CPU_Wins) {
					Win_Clip.stop();
				}
			}
		}

		public void mousePressed(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
	private class CPUMouseListener implements MouseListener {
		public void mouseEntered(MouseEvent e) {
			if(Game_Over) {
				if(CPU_Wins > My_Wins) {
					Win_Clip.loop(Clip.LOOP_CONTINUOUSLY);
				}
				else if (My_Wins > CPU_Wins) {
					Lose_Clip.loop(Clip.LOOP_CONTINUOUSLY);
				}
			}
		}
		public void mouseExited(MouseEvent e) {
			if(Game_Over) {
				if(CPU_Wins > My_Wins) {
					Win_Clip.stop();
				}
				else if (My_Wins > CPU_Wins) {
					Lose_Clip.stop();
				}
			}
		}

		public void mousePressed(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
	public static void main(String[] args) {
		RPS_GUI RPS_Main = new RPS_GUI();
	}
}
