package edu.lbic.lab7.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.lbic.lab7.exceptions.InvalidPlayerNumberException;

/**
	 * Context Menu for quitting the app
	 */
	class MainMenu implements ActionListener
	{
		private JFrame frame;
		private GUI gu;
		
		MainMenu(JFrame frame,GUI gu)
		{
			this.frame = frame;
			this.gu=gu;
		}
		@Override
		public void actionPerformed(ActionEvent e)
		{	
			String com=e.getActionCommand();
			if(com.equals("Reset"))
			{
				int resetGame = JOptionPane.showConfirmDialog(frame, "Are you sure you want to reset?", "", JOptionPane.YES_NO_OPTION);

				if (resetGame == JOptionPane.YES_OPTION) {
					gu.engine.clearBoard();
					gu.updateBoard();
					gu.setTurns(0);
					gu.engine.setScore(0,0);
					gu.updateScoreText();
				}
			}
			else if(com.contains("Save Slot"))
			{
				try {
					if(com.contains("1")){
						gu.engine.save(1);
					}
					else{
						gu.engine.save(2);
					}
				}catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else if(com.contains("Load Slot"))
			{	try {
					if(com.contains("1")){
						gu.load(1);
					}
					else{
						gu.load(2);
					}
				}catch (IOException | InvalidPlayerNumberException e1) {
					e1.printStackTrace();
				}
			}
			else if(com.equals("Pause")){
				
				Object[] option={"Resume"};
				JOptionPane.showOptionDialog(frame, "Press 'Resume' to continue the game", "Game Paused",
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
						null, option, option[0]);
			}
			else if(com.equals("Exit"))
			{
				int playAgain = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?", "Exit", JOptionPane.YES_NO_OPTION);

				if (playAgain == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		}
	}