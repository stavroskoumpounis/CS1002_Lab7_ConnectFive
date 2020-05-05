package edu.lbic.lab7.game;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import edu.lbic.lab7.exceptions.InvalidPlayerNumberException;
import edu.lbic.lab7.exceptions.OutsideBoardException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GUI extends MouseAdapter
{
	/**
	 * Game disc icons *
	 */
	private static final String EMPTY_ICON = "img/empty.png";
	private static final String RED_ICON   = "img/red.png";
	private static final String YELLOW_ICON = "img/yellow.png";

	private static final int IMG_SIZE = 60;
	GameEngine engine = null;
	private AI ai;
	private JFrame frame;
	Random randomizer;
	

	/**
	 * Two dimensional array that contains the image that build the board.
	 */
	private JLabel[][] board = null;
	private JLabel score;
	private JLabel currentTurn = null,turnsPassed = null;
	private JMenuBar menuBar;
	private JButton pauseButton;
	/**
	 * Possible values for a disc dropped into a board column
	 * Default value is None (empty)
	 */
	private enum Disc
	{
		None,
		Player1,
		AI
	}

	/**
	 * Create a new graphical representation of the game. In other words,
	 * Create the graphical interface for playing the game.
	 *
	 * @param p1 - player 1
	 * @param AI - Computer
	 */
	public GUI(Player p1, Player AI)
	{
		addLookAndFeel();
		frame = new JFrame();
		frame.setTitle("Connect 5!");
		score = new JLabel();
		pauseButton = new JButton("Pause");
		menuBar = new JMenuBar();
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		engine = GameEngine.getInstance(p1, AI);
		ai = new AI(this);
		createMenu();
		randomizer = new Random();
		
	}

	/**
	 * Start up the game by setting the board boundary, loading the images
	 * and adding the proper input listeners.
	 */
	public void startGame()
	{
		initBoard();
		frame.setSize(900, (int) (IMG_SIZE * 8.2));
		frame.addMouseListener(this);
		newGameMsg(frame);
	}

	/**
	 * Create the top menu for the game.
	 */
	public final void createMenu()
	{
		JMenu menu = new JMenu("Menu");
		JMenuItem exitItem = new JMenuItem("Exit");
		JMenuItem resetItem = new JMenuItem("Reset");
		JMenu save = new JMenu("Save");
		JMenu load = new JMenu("Load");
		JMenuItem save1Item = new JMenuItem("Save Slot 1");
		JMenuItem save2Item = new JMenuItem("Save Slot 2");
		JMenuItem load1Item = new JMenuItem("Load Slot 1");
		JMenuItem load2Item = new JMenuItem("Load Slot 2");
		
		
		resetItem.addActionListener(new MainMenu(frame,this));
		exitItem.addActionListener(new MainMenu(frame, this));
		
		
		
		save1Item.addActionListener(new MainMenu(frame,this));
		save2Item.addActionListener(new MainMenu(frame,this));
		load1Item.addActionListener(new MainMenu(frame,this));
		load2Item.addActionListener(new MainMenu(frame,this));
		save.add(save1Item);
		save.add(save2Item);
		load.add(load1Item);
		load.add(load2Item);
		menu.add(save);
		menu.add(load);
		menu.add(resetItem);
		menu.add(exitItem);
		menuBar.add(menu);
		JMenu help = new JMenu("Help");
		JMenuItem helpItem = new JMenuItem("How to Play");
		helpItem.addActionListener(new HelpMenu(frame));
		helpItem.setSize(300, 200);
		help.add(helpItem);
		menuBar.add(help);
		frame.setJMenuBar(menuBar);
	}

	/**
	 * Updates and links the 2D arrays ,the integer array with the values for the nodes
	 * and the JLabel array with the images
	 */
	public void updateBoard()
	{
		int[][] a = engine.getBoard().getBoardArray();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				Disc pos = Disc.values()[a[i][j]];
				switch (pos) {
				case None:
					board[i][j].setIcon(new ImageIcon(EMPTY_ICON ));
					break;
				case Player1:
					board[i][j].setIcon(new ImageIcon(RED_ICON));
					break;
				case AI:
					board[i][j].setIcon(new ImageIcon(YELLOW_ICON));
					break;
				}
			}
		}
	}
	/**
	 * Called when the game is over to determine what to do next.
	 *
	 * @param winner - the game winner (if there is one)
	 */
	public boolean gameOver(Player winner)
	{
		String msg = "Game Over, Draw.";
		if (winner.getNum() != 0) {
			msg = String.format("Game Over, Player %d wins.", winner.getNum());
		}
		int playAgain = JOptionPane.showConfirmDialog(frame, msg, "Want to Play Again?", JOptionPane.YES_NO_OPTION);

		if (playAgain == JOptionPane.YES_OPTION) {
			engine.clearBoard();
			updateBoard();
			setTurns(0);
			newGameMsg(frame);
			
			return false;
		}
		return true;
	}
	public void setTurns(int amount){
		engine.setTurnCount(amount);
		updateTurnText(engine.getCurrentPlayer());
	}
	public void playSaound(){
	
		Synthesizer synth;
		try {
			synth = MidiSystem.getSynthesizer();
			 synth.open();
		 MidiChannel[] mc = synth.getChannels();
		 Instrument[] instr = synth.getDefaultSoundbank().getInstruments();

		 synth.loadInstrument(instr[90]);
		                     

		mc[9].noteOn(60,300);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		};
		
	}
	public void updateScoreText()
	{
		playSaound();
		int[] playerScores = engine.getScore();
		score.setText(String.format("Score: %s - %s", playerScores[0], playerScores[1]));
	}

	public void updateTurnText(Player currentPlayer)
	{
		currentPlayer =( currentPlayer == null ? new Player(1) : currentPlayer );
		String color = currentPlayer.getNum() == 1 ? "Red" : "Yellow";
		if (engine.isCurrentPlayerAI()){
			currentTurn.setText("Current player : AI ("+ color+")");
		}
		else{
		currentTurn.setText("Current player : Player 1 ("+ color+")");
		}
		turnsPassed.setText("Turns passed : "+engine.getTurnCount());

	}

	/**
	 * Add the player's disc to the game board.
	 *
	 * @param columnNumber - the column the disc should be added to
	 * @return true if the disc was added (column was not full)
	 *
	 * @throws OutsideBoardException if the column is full or out of bounds
	 * @throws IOException 
	 */
	public boolean putDisc(int columnNumber) throws OutsideBoardException, IOException
	{
		boolean discPlaced = engine.putDisc(new Move(columnNumber));
		updateTurnText(engine.getCurrentPlayer());

		if (discPlaced) {
			Player p = engine.isGameOver();
			updateBoard();
			updateScoreText();
			if (p != null) {
				boolean noMoreGames = gameOver(p);

				if (noMoreGames) {
					frame.removeMouseListener(this);
				}
			}
			
		}
		return discPlaced;
	}

	/**
	 * Determine where the mouse was just clicked and put a disc in that column
	 *
	 * @param mouseEvent the mouse event
	 */
	@Override
	public void mousePressed(MouseEvent mouseEvent)
	{	
		System.out.println(mouseEvent.getY()*7.5f+"  "+mouseEvent.getX()/ IMG_SIZE);
		if (!engine.isCurrentPlayerAI()){
			// get the column that was clicked and putDisc down the correct image
			if (mouseEvent.getY() < IMG_SIZE * (engine.getROWS() + 0.5f) && mouseEvent.getX() < IMG_SIZE * engine.getCOLUMNS()) {
				try{
				/*Player's move*/	
					putDisc(mouseEvent.getX() / IMG_SIZE);
				
				/*AI's move*/
				if(engine.getTurnCount()>0){ // bug handling
					
					if(!ai.avoidFive()){ //if avoid five didn't happen move randomly
						randomMoveAR(-1);
					}
				}
				} catch (OutsideBoardException | IOException  e) {
					e.printStackTrace();
				}
			}
		}

		//mousePressed end		
	}

	/**
	 * Initialise the board. Set all board positions to empty and load
	 * the empty image icon for each position.
	 */
	private void initBoard()
	{
		engine.clearBoard();
		frame.getContentPane().removeAll();
		board = new JLabel[engine.getROWS()][engine.getCOLUMNS()];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				board[i][j] = new JLabel();
				board[i][j].setBounds(j * IMG_SIZE, i * IMG_SIZE, IMG_SIZE, IMG_SIZE);
				board[i][j].setIcon(new ImageIcon(EMPTY_ICON));
				frame.getContentPane().add(board[i][j]);
			}
		}
		currentTurn = new JLabel();
		turnsPassed = new JLabel();
		currentTurn.setBounds((int) (8.2 * IMG_SIZE), 20, 200, 20);
		turnsPassed.setBounds((int) (8.2 * IMG_SIZE), 0, 200, 20);
		score.setBounds(12 * IMG_SIZE, 0, 150, 50);
		updateScoreText();
		updateTurnText(null);
		pauseButton.setBounds(10* IMG_SIZE, IMG_SIZE, 100, 30);
		frame.getContentPane().add(pauseButton);
		pauseButton.addActionListener(new MainMenu(frame, this));
		frame.getContentPane().add(turnsPassed);
		frame.getContentPane().add(currentTurn);
		frame.getContentPane().add(score);
		frame.setVisible(true);
	}
	
	/**
	 * Provides a message for when a new game is ready to start.
	 * If the AI is to play first ,it makes it's move.
	 * @param frame
	 */
	public void newGameMsg(JFrame frame){

		Object[] options={"AI starts 1st","Player starts 1st"};
		int option = JOptionPane.showOptionDialog(frame, "Choose who is to start 1st", "New Game",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
				null, options, options[0]);
		try {
			if(option == 0){ //AI

				engine.setCurrentPlayer(2);
				updateTurnText(engine.getCurrentPlayer());
				randomMoveAR(-1);

			}
			else{
				engine.setCurrentPlayer(1);
				updateTurnText(engine.getCurrentPlayer());

			}
		} catch (InvalidPlayerNumberException | OutsideBoardException | IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Loads a game state from specified slot
	 * Creates a backup save in case an error is encountered with loading
	 * to restore the current game.
	 * 
	 * @param slot
	 * @throws IOException
	 * @throws InvalidPlayerNumberException
	 */
	public void load(int slot) throws IOException, InvalidPlayerNumberException{
		engine.save(0); //make backup
		engine.clearBoard();
		try{
		engine.load(slot);
		}
		catch (NullPointerException e){
			JOptionPane.showMessageDialog(frame, "Load file doesn't contain a saved game", "Error", JOptionPane.ERROR_MESSAGE);
			engine.load(0);
		}
		updateBoard();
		updateScoreText();
		updateTurnText(engine.getCurrentPlayer());
	}
	/**
	 * Simply adds the Nimbus look and feel if the system has it.
	 */
	private void addLookAndFeel(){
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {}

	}
	/**
	 * Artificial randomness to put a disc randomly
	 * @throws OutsideBoardException
	 * @throws IOException
	 */
	public void randomMoveAR(int notThere) throws OutsideBoardException, IOException{
		/* put the thread to sleep so the move 
		 * doesn't instantly appear on the board in real time
		 * to improve smoothness of user experience
		 */
		if(engine.isCurrentPlayerAI()){
			new java.util.Timer().schedule( 
					new java.util.TimerTask() {
						@Override
						public void run() {
							placeAIdisc(notThere);
						}
					}, 
					600 
					);
		}
	}
	private void placeAIdisc(int notThere){
		int randomColumn;
		while(true){
			/*if not the game reaches the top row , valid column options are less than 8
			 *so this if attempts to get the possible/valid column choices for the end of the game */
			if (engine.getTurnCount()<49){
				randomColumn = randomizer.nextInt(engine.getCOLUMNS());
			}
			else{
				ArrayList<Integer> possible=engine.board.availableNodesTopRow();
				while(true){
					randomColumn = possible.get(randomizer.nextInt(possible.size()));
					if(notThere!=-1){
						if (randomColumn != notThere){
							break;
						}
						else{
							break;
						}
					}
				}
			}

			try {
				if (putDisc(randomColumn)){
					break;
				}
			} catch (OutsideBoardException | IOException e) {
				e.printStackTrace();
			}
		}

	}
/*-----------END CLASS GUI-----------*/	
}
