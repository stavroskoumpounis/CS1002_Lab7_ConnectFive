package edu.lbic.lab7.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
	 * Context Menu for bringing up the help menu
	 */
	class HelpMenu implements ActionListener
	{
		private JFrame frame;

		HelpMenu(JFrame frame)
		{
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String msg = "Take turns dropping discs into the columns of the board.(Click over the column of your choice) \n\n" +
					"The goal is to connect 5 discs in a row (Horizontally, Vertically  or Diagonally).";

			JOptionPane.showMessageDialog(frame, msg, "How to play", JOptionPane.INFORMATION_MESSAGE);
		}
	}