package wordle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * Class that is a clone of the game Wordle. This class uses JFrame and its various methods.
 */

public class WordleGame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel panel;
	private JPanel keyboardPanel;
	private  JLabel[][] letterKeys;
	private JButton[] keyButton;
	private JButton guessButton;
	private String wordToGuess;
	private int attempt;
	private StringBuilder currentGuess;
	private HashMap<Character, Color> keyColors;

	public WordleGame() {
		setTitle("Wordle");
		setSize(800, 800);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		keyColors = new HashMap<>();
		currentGuess = new StringBuilder();

		makeGrid();
		MakeKeyboard();
		makePanel();

		add(panel, BorderLayout.CENTER);
		add(keyboardPanel, BorderLayout.SOUTH);

		wordToGuess = getWord();
		attempt = 0;
		setVisible(true);
	}
	
	/**
	 * Initalizes game
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new WordleGame());
	}

	/**
	 * Sets up the 6x5 grid for the tiles when typing in a word.
	 */
	private void makeGrid() {
		panel = new JPanel();
		panel.setLayout(new GridLayout(6, 5, 5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		letterKeys = new JLabel[6][5]; // 6 guesses, 5 is the length

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 5; j++) {
				JLabel label = new JLabel("", SwingConstants.CENTER);
				label.setOpaque(true);
				label.setBackground(Color.WHITE);
				label.setBorder((new LineBorder(Color.BLACK)));
				label.setFont(new Font("Arial", Font.BOLD, 24));
				letterKeys[i][j] = label;
				panel.add(label);
			}
		}
	}

	/**
	 * Sets up the virtual keyboard used to type in answers.
	 */
	private void MakeKeyboard() {
		keyboardPanel = new JPanel();
		keyboardPanel.setLayout(new GridLayout(3, 10, 5, 5));
		keyboardPanel.setBorder((BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		String[] keys = { "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L",
				"GUESS", "Z", "X", "C", "V", "B", "N", "M", "BACK" };

		keyButton = new JButton[keys.length];
		
		for(int i = 0; i<keys.length; i++) {
			String key = keys[i];
			JButton button = new JButton(key);
			button.setFocusPainted(false);
			button.setFont(new Font("Arial", Font.BOLD, 16));
			button.setPreferredSize(new Dimension(50,50)); // this check
			
			if(key.equals("GUESS")) {
				button.addActionListener(new GuessKey());
			}
			else if(key.equals("BACK")) {
				button.addActionListener(new BackSpace());
			}
			else {
				button.addActionListener(new LetterKey());
			}
			
			keyButton[i] = button;
			keyboardPanel.add(button);
			
		}

	}
	
	/**
	 * Makes the control pannel and establishes the guess button
	 */
	private void makePanel() {
		JPanel control = new JPanel();
		control.setLayout(new FlowLayout());
		guessButton = new JButton("Guess");
		guessButton.setEnabled(false);
		guessButton.addActionListener(new GuessButton());
		control.add(guessButton);
		add(control, BorderLayout.NORTH);
		
		}
	
	/**
	 * Selects a random word out of an array to be the word the user can guess.
	 * @return
	 */
	private String getWord() {
		Random random = new Random();
		String[] words = {"SPORT", "BREAD", "WHISK", "GUESS", "LAYUP", "STATE", "TABLE", "GREAT", "SHIRT" };
		int r = random.nextInt(words.length-1); // maybe
		return words[r];
	}
	
	/**
	 * Private class that implements the ability to click a character.
	 */
	private class LetterKey implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String letter = e.getActionCommand();
			addLetter(letter.charAt(0));
		}
	}
	
	
	/**
	 * Private class that implements the ability to click on guess.
	 */
	private class GuessKey implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(isWordGuessed()) {
				guessButton.doClick();
			}
			else {
				JOptionPane.showMessageDialog(null, "Please enter a 5 letter word", "Word is missing letters",JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	/**
	 * Private class that allows user to delete a character.
	 */
	private class BackSpace implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			removeLetter();
		}
		
	}
	
	/**
	 * Method for logically applying a new character on the game
	 * @param letter
	 */
	private void addLetter(char letter) {
		if(currentGuess.length() < 5) {
		currentGuess.append(letter);
		for(int i = 0; i< 5; i++) {
			if(letterKeys[attempt][i].getText().isEmpty()) {
				letterKeys[attempt][i].setText(String.valueOf(letter)); // add letter
				break;
			}
		}
		updateGuessButton();
	}
	}
	
	/**
	 * Method for deleting a character when backspace key is pressed.
	 */
	private void removeLetter() {
		if(currentGuess.length()>0) {
			currentGuess.deleteCharAt(currentGuess.length()-1);
			for(int i = 4; i>=0; i--) {
				if(!letterKeys[attempt][i].getText().isEmpty()) {
					letterKeys[attempt][i].setText("");
					break;
				}
			}
			updateGuessButton();
		}
		
	}
	
	/**
	 * Helper to determine if the word can be guessed
	 * @return
	 */
	private boolean isWordGuessed() {	
		return currentGuess.length() == 5;
	}
	
	/**
	 * Sets the guess button to be clickable is word is ready to guess.
	 */
	private void updateGuessButton() {
		guessButton.setEnabled(isWordGuessed());
	}
	
	/**
	 * Class that checks how valid a guess is and updates the grid and keyboard.
	 */
	private class GuessButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			StringBuilder string = new StringBuilder();
			for(int i = 0; i < 5; i++) {
				string.append(letterKeys[attempt][i].getText());				
			}
			String userGuess = string.toString().toUpperCase(); // so its ==
			if(userGuess.length() != 5 || userGuess.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Enter a 5 letter word", "Attempt Invalid", JOptionPane.ERROR_MESSAGE);
				return;
			}

			String feedback = getFeedback(userGuess);
			updateGrid(feedback);
			updateKeyboard(userGuess, feedback);
			
			if(userGuess.equals(wordToGuess)) {
				JOptionPane.showMessageDialog(null, "You guessed correctly! It took you" + (attempt + 1) + "attempts.", "Good job", JOptionPane.INFORMATION_MESSAGE);
				guessButton.setEnabled(false);
				return;
			}
			attempt++;
			if(attempt>=6) {
				JOptionPane.showMessageDialog(null, "Incorrect! You ran out of guesses.The word was: " + wordToGuess, "Good try", JOptionPane.INFORMATION_MESSAGE );
				guessButton.setEnabled(false);
			}
			currentGuess.setLength(0);
		}
		
	}
	
	/**
	 * Allows to check if the word to guess has or contains the letter.
	 * @param guess
	 * @return
	 */
	private String getFeedback(String guess) {
		StringBuilder fb = new StringBuilder();
		for(int i = 0; i<5; i++) {
			char guessed = guess.charAt(i);
			if(guessed == wordToGuess.charAt(i)) {
				fb.append("G");
			}else if(wordToGuess.contains(String.valueOf(guessed))) {
					fb.append("Y");
				}
			else {
				fb.append("B");
			}
			
			}
		return fb.toString();
		}
	
	/**
	 * Updates the grid after receiving feedback on guess attempt, changes colors.
	 * Green if in correct spot, Yellow if is in word, and gray if does not contain.
	 * @param feedback of letter guessed.
	 */
	private void updateGrid(String feedback) {
		for(int i = 0; i<5; i++) {
			JLabel label = letterKeys[attempt][i];
			char fb = feedback.charAt(i);
			switch(fb) {
			case 'G':
				label.setBackground(new Color(106, 170, 100));
				break;
			case 'Y':
				label.setBackground(new Color(201, 180, 88));
				break;
			case 'B':
				label.setBackground(new Color(120, 124, 126));
				break;
			}
			label.setForeground(Color.white);
		}
	}
	
	/**
	 * Updates the keyboard after receiving feedback on attempt. 
	 * @param guess being made
	 * @param feedback to compare with guess
	 */
	private void updateKeyboard(String guess, String feedback) {
		
		for(int i = 0; i<5; i++) {
			char letter = guess.charAt(i);
			char fb = feedback.charAt(i);
			Color currentColor = keyColors.getOrDefault(letter, Color.LIGHT_GRAY);
			
			switch(fb) {
			case 'G':
				keyColors.put(letter, new Color(106, 170, 100));
				break;
			case 'Y':
				if(!currentColor.equals(new Color(106, 170, 100)))
				keyColors.put(letter, new Color(201, 180, 88));
				break;
			case 'B':
				if(!keyColors.containsKey(letter)) { // no overwriting
					keyColors.put(letter, new Color(120, 124, 126));
				}
				break;
			}
		}
		
		for(JButton b : keyButton) {
			String text = b.getText();
			if(text.length() == 1 ) {
				char letter = text.charAt(0);
				if(keyColors.containsKey(letter)) {
					b.setBackground(keyColors.get(letter));
					b.setForeground(Color.WHITE);
				}
				else {
					b.setBackground(null);
					b.setForeground(Color.BLACK);
				}
			}
		}
	}
	
	
	
	
	}
	
	
	
	
	
	
	


