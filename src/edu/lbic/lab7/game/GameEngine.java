package edu.lbic.lab7.game;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import edu.lbic.lab7.exceptions.InvalidPlayerNumberException;
import edu.lbic.lab7.exceptions.OutsideBoardException;

public class GameEngine
{
    // game board starts at 0,0 in the top left corner
    private static final int TOP_ROW      = 0;
    private static final int FAR_LEFT_COL = 0;
    private static final int CONNECT5 = 5;
    
    protected static GameEngine engine = null;
    protected GameBoard board;
    private Player p1,AI,currentPlayer;
	private int turnCount=0;
    // the column that was last clicked by a user
    protected Move lastColumnClicked = null;
    protected Move lastCalumnAI = null;

    /**
     * Denotes the different ways the board (or 2d array) can be searched from a given position.
     * Each value represents a step in a direction one can take when searching a direction.
     * In other words, currentPos += SearchNode.SomeValue (adds/subtracts 1)
     */
    enum SearchNode
    {
        // game board starts at 0,0 in the top left corner
        IDLE(0), // don't search either direction
        LEFT(-1),
        RIGHT(1),
        DOWN(1), // the "last" row is first position filled and not last filled
        UP(-1); // the top of the board is the last open spot in the column
        private int value;

        SearchNode(int value) { this.value = value; }

        public int getValue() { return value; }
    }
    /**
     * Private constructor, use getInstance instead
     *
     * @param p1 - player 1
     * @param AI - Computer
     */
    protected GameEngine(Player p1, Player AI)
    {
        board = GameBoard.getInstance();
        this.p1 = p1;
        this.AI = AI;
        currentPlayer = this.p1;
    }

    /**
     * Gets an instance of the game engine or creates a new instance.
     * 
     * @param p1 - player 1
     * @param AI - player 2
     * @return a game instance
     */
    public static synchronized GameEngine getInstance(Player p1, Player AI)
    {
        if (engine == null) {
            engine = new GameEngine(p1, AI);
        }
        return engine;
    }

    /**
     * Add the player's disc to the selected column on the board
     *
     * @param move - spot to add the disc
     * @return true if the location was not full and added
     *
     * @throws OutsideBoardException on invalid (out of bounds or column full)
     */
    public boolean putDisc(Move move) throws OutsideBoardException
    {
        if (board.putDisc(currentPlayer.getNum(), move.getPositionIndx())) {

        	if(isCurrentPlayerAI()){
            	lastCalumnAI = move;
            }
            
            	lastColumnClicked = move;
            
            nextTurn();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Switches turns to the next player
     */
    private void nextTurn()
    {
        currentPlayer =( currentPlayer == p1 ? AI : p1) ;
        turnCount++;
    }

    /**
     * Looks for a winner in all directions based on last clicked column
     *
     * @return true if a winner is found
     */
    private boolean declareWinner()
    {
        try {
            // search to the left and right for a connect four
            int rowPieces = checkNeighbors(SearchNode.RIGHT, SearchNode.IDLE) + checkNeighbors(SearchNode.LEFT, SearchNode.IDLE) - 1;
            if (rowPieces >= CONNECT5) {
                return true;
            }
            // search up and down for a connect four
            int columnPieces = checkNeighbors(SearchNode.IDLE, SearchNode.DOWN) + checkNeighbors(SearchNode.IDLE, SearchNode.UP) - 1;
            if (columnPieces >= CONNECT5) {
                return true;
            }
            // search diagonally to the right for a connect four
            int backSlashPieces = checkNeighbors(SearchNode.RIGHT, SearchNode.DOWN) + checkNeighbors(SearchNode.LEFT, SearchNode.UP) - 1;
            if (backSlashPieces >= CONNECT5) {
                return true;
            }
            // search diagonally to the left for a connect four
            int foreSlashPieces = checkNeighbors(SearchNode.RIGHT, SearchNode.UP) + checkNeighbors(SearchNode.LEFT, SearchNode.DOWN) - 1;
            if (foreSlashPieces >= CONNECT5) {
                return true;
            }
        } catch (OutsideBoardException ignored) {

        }
        return false;
    }

    /**
     * Searches a given direction for a connect five on the game board
     *
     * @param horzSearchDir - the current horizontal direction to search (left or right or none)
     * @param vertSearchDir - the current vertical direction to search (up or down or none)
     * @return the number of like player discs in a direction (4 being a winner)
     *
     * @throws OutsideBoardException thrown if it tries to search out of bounds
     */
    private int checkNeighbors(SearchNode horzSearchDir, SearchNode vertSearchDir) throws OutsideBoardException
    {
        int lastMoveColumn = lastColumnClicked.getPositionIndx(); //
        int lastMoveRow = board.getROWS() - board.getColumnHeight(lastMoveColumn);

        int curRow = lastMoveRow;
        int curCol = lastMoveColumn;
        int count = 0;

        do {
            curRow += vertSearchDir.getValue();
            curCol += horzSearchDir.getValue();
            ++count;
        }
        while (count < CONNECT5 && !outOfBounds(curRow, curCol) && samePosition(lastMoveColumn, lastMoveRow, curRow, curCol));
        return count;
    }

    /**
     * Is the current position outside of the board?
     *
     * @param curRow - row in question
     * @param curCol - column in question
     * @return true if position is out of bounds for the row/column
     */
    protected boolean outOfBounds(int curRow, int curCol)
    {
        return curRow < TOP_ROW || pastLastRow(curRow) || curCol < FAR_LEFT_COL || pastLastColumn(curCol);
    }

    /**
     * Is the current position the same colour as the last clicked?
     *
     * @param lastMoveColumn - the last column position clicked
     * @param lastMoveRow - the last row position clicked
     * @param curRow - the current row that is being searched
     * @param curCol - the current column that is being searched
     * @return true if the two nodes are occupied by the same player
     *
     * @throws OutsideBoardException if either position is outside of the board
     */
    protected boolean samePosition(int lastMoveColumn, int lastMoveRow, int curRow, int curCol) throws OutsideBoardException
    {
        return board.getBoardPos(curRow, curCol) == board.getBoardPos(lastMoveRow, lastMoveColumn);
    }

    /**
     * Is the current position past the far right in the row?
     *
     * @param curCol - the current column in question
     * @return true if this is the far right column
     */
    private boolean pastLastColumn(int curCol)
    {
        return curCol == board.getCOLUMNS();
    }

    /**
     * Is the current position past the last row(under the board)?
     *
     * @param curRow - the current row in question
     * @return true if this is the last row
     */
    private boolean pastLastRow(int curRow)
    {
        return curRow == board.getROWS();
    }

    /**
     * Is the game over yet?
     *
     * @return the player that won if game over. If a draw (board full), return a new player with
     *         number zero. Otherwise, return null if game is not over.
     */
    public Player isGameOver()
    {
        if (board.isBoardFull()) {
            return new Player(0);
        }
        if (declareWinner()) {

            if (currentPlayer == p1) {
                AI.setWins(AI.getWins() + 1);
                return AI;
            }
            else {
                p1.setWins(p1.getWins() + 1);
                return p1;
            }
        }
        return null;
    }

    /**
     * Get the score of the game
     * @return number of wins by each player
     */
    public int[] getScore()
    {
        return new int[] {p1.getWins(), AI.getWins()};
    }
    /**
     * Sets the score fields for p1 and AI
     */
    public void setScore(int p1Score,int AIScore)
    {
    	p1.setWins(p1Score);
    	AI.setWins(AIScore);
    }

    /**
     * Getter for the current player on this turn
     *
     * @return the current player[Player Object]
     */
    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }
    /**
     * Setter for the current player on this turn
     *
     * @return the current player[Player Object]
     * @throws Exception 
     */
    public void setCurrentPlayer(int playerNum) throws InvalidPlayerNumberException
    {

    	if (!(playerNum==1 || playerNum==2))
    	{
    		throw new InvalidPlayerNumberException();
    	}
    	currentPlayer=(playerNum==1? p1:AI );
    }
    

    /**
     * Get the total rows for the board.
     *
     * @return the total row length
     */
    public int getROWS()
    {
        return board.getROWS();
    }

    /**
     * Get the total columns for the board
     *
     * @return the total column length
     */
    public int getCOLUMNS()
    {
        return board.getCOLUMNS();
    }

    public void clearBoard()
    {
        board.clearBoard();
    }

    /**
     * Return an instance of the current game board
     *
     * @return the current game board
     */
    public GameBoard getBoard()
    {
        return board;
    }
    public String boardToString(){
    	String boardStr="";
		for (int[] i :engine.getBoard().getBoardArray()){
			for (int j : i){
				boardStr+=j;
			}
		}
    	return boardStr;
    }
    /**
     * Getter method for Turn count
     * @return turnCount integer
     */
	public int getTurnCount() 
	{
		return turnCount;
	}
	/**
	 * Setter for Turn Count
	 * @param amount 
	 */
	public void setTurnCount(int amount){
		turnCount=amount;
	}
	public void save(int saveSlot) throws IOException 
	{	
		String savePath;
		if (saveSlot==1){
			savePath="saved_games/save1.txt";
		}
		else if(saveSlot==2){
			savePath="saved_games/save2.txt";
		}
		else
		{
			savePath="saved_games/backup_save.txt";
		}
		/* save format : Turns,Current player,Score for p1,Score for AI,board*/
		String save = getTurnCount()+","+getCurrentPlayer().getNum()+","+
				getScore()[0]+","+getScore()[1]+","+boardToString();
		
		FileWriter writehandle = new FileWriter(savePath);
		BufferedWriter bw =  new BufferedWriter(writehandle);
		bw.write(save);
		bw.close();
		writehandle.close();
		
		final Runnable runnable =
			     (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.menuCommand");
			if (runnable != null) runnable.run();
	}
	public void load(int loadSlot) throws IOException, InvalidPlayerNumberException 
	{	
		String loadPath;
		if (loadSlot==1){
			loadPath="saved_games/save1.txt";
		}
		else if(loadSlot==2) {
			loadPath="saved_games/save2.txt";
		}
		else{
			loadPath="saved_games/backup_save.txt";
		}
			
		FileReader readhandle = new FileReader(loadPath);
		BufferedReader br = new BufferedReader(readhandle);
		
		String[] readStr = br.readLine().split(",");
		
		setTurnCount(Integer.parseInt(readStr[0]));
		setCurrentPlayer(Integer.parseInt(readStr[1]));
		setScore(Integer.parseInt(readStr[2]),Integer.parseInt(readStr[3]));
		board.setBoard(readStr[4].split(""));
		
		br.close();
		readhandle.close(); 
	}
	/**
	 * Boolean check for if it's the AI's turn
	 * @return true if it's AI's turn.
	 */
	public boolean isCurrentPlayerAI(){
		return (currentPlayer.equals(AI));
	}
	protected int getLastColClicked(){
		return lastColumnClicked.getPositionIndx();
	}
	protected int getLastColAI(){
		return lastCalumnAI.getPositionIndx();
	}
/*-----------END CLASS GameEngine-----------*/	
}
