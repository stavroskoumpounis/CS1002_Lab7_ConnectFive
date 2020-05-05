package edu.lbic.lab7.game;

import java.util.ArrayList;

import edu.lbic.lab7.exceptions.OutsideBoardException;

public class GameBoard
{
    /**
     * constant for empty nodes to improve readability
     */
    private static final int EMPTY = 0;
    private static GameBoard gameBoard = null;

    /**
     * integer array of the board.Matches the one in the GUI but this holds 0,1, or 2
     * to indicate if a node is occupied by p1,p2 or it is empty
     */
    int[][] board;
    // constants for rows and columns to improve readability and to easily change if needed
    private final int ROWS    = 7;
    private final int COLUMNS = 8;

    /**
     * Private constructor, using getInstance() instead.
     */
    private GameBoard()
    {
        clearBoard();
    }

    /**
     * Creates a new instance of GameBoard
     * synchronized keyword makes a queue of calling this method
     * when it is called from multiple threads at once
     * @return instance of the board or creates a new one if there this none.
     */
    public static synchronized GameBoard getInstance()
    {
        if (gameBoard == null) {
            gameBoard = new GameBoard();
        }
        return gameBoard;
    }

    /**
     * Checks if said node(row,column) is occupied.
     *
     * @param index of the row
     * @param index of the column
     * @return true if the node is occupied
     */
    public boolean fullAtPos(int row, int column) throws OutsideBoardException
    {
        if (isValidColumn(column) && isValidRow(row)) {
            return board[row][column] != EMPTY; //if not empty return true (i.e. position occupied)
        }
        else {
            throw new OutsideBoardException();
        }
    }

    /**
     * is the board full?
     *
     * @return true if full
     */
    public boolean isBoardFull()
    {
        boolean fullBoard = true;
        for (int i = 0; i < COLUMNS; i++) {
            try {
                fullBoard = fullBoard && fullAtPos(0, i); //if the top row is full set true (check for every node)
            } catch (OutsideBoardException ignored) {
            }
        }
        return fullBoard;
    }
    public ArrayList<Integer> availableNodesTopRow(){
    	
    	ArrayList<Integer> possible = new ArrayList<>();
    	for (int i = 0; i < COLUMNS; i++) {
    		if (board[0][i] == EMPTY){
    			possible.add(i);
    		}
    	}
    	return possible;
    }
    public ArrayList<Integer> availableNodesForRow(int row){
    	
    	ArrayList<Integer> possible = new ArrayList<>();
    	for (int i = 0; i < COLUMNS; i++)
    	{
    		
    		if (board[row][i] == EMPTY){
    			possible.add(i);
    		}
    	}
    	return possible;
    }

    /**
     * Puts the player's number in the bottom most empty spot in the column
     *
     * @param playerNumber - the Player's Number
     * @param columnNumber - the column index to put the player/disc in it.
     * @return true if player disc was added, false if column is full
     *
     * @throws OutsideBoardException if the position is invalid (out of bounds).
     */
    public boolean putDisc(int playerNumber, int columnNumber) throws OutsideBoardException
    {
        if (isValidColumn(columnNumber)) {
            for (int i = ROWS - 1; i >= 0; i--) {
                if (board[i][columnNumber] == EMPTY) {
                    board[i][columnNumber] = playerNumber;
                    return true;
                }
            }
            return false;
        }
        throw new OutsideBoardException();
    }
    
    /**
     * Get a position on the board
     *
     * @param rowIndex - the row position
     * @param columnIndex - the column position
     * @return the player number (disc) for the position in question
     *
     * @throws OutsideBoardException on invalid position (out of bounds)
     */
    public int getBoardPos(int rowIndex, int columnIndex) throws OutsideBoardException
    {
        if (isValidColumn(columnIndex) && isValidRow(rowIndex)) {
            return board[rowIndex][columnIndex];
        }
        throw new OutsideBoardException();
    }

    /**
     * Get the number of occupied nodes in a column
     *
     * @param columnIndex - the column in question
     * @return the number of occupied positions
     *
     * @throws OutsideBoardException if the column in the parameter is out of bounds
     */
    public int getColumnHeight(int columnIndex) throws OutsideBoardException
    {
        if (!isValidColumn(columnIndex)) {
            throw new OutsideBoardException();
        }
        int i = 0;
        while (i < ROWS && board[i][columnIndex] == EMPTY) { // from 0 to rows top to bottom... if empty count it
            i++;
        }
        return ROWS - i; //deduct empty(count) nodes from Row total to find the amount of occupied nodes
    }

    /**
     * Check the validity of the given column number (index).
     *
     * @param columnNumber - the column index to check
     * @return true if the column is within the game board
     */
    public boolean isValidColumn(int columnNumber)
    {
        return columnNumber >= 0 && columnNumber <= this.COLUMNS - 1;
    }

    /**
     * Check the validity of the given row number (index).
     *
     * @param rowNumber - the row index to check
     * @return true if the row is within the game board
     */
    public boolean isValidRow(int rowNumber)
    {
        return rowNumber >= 0 && rowNumber <= this.ROWS - 1;
    }

    /**
     * Get an array representation of the board
     *
     * @return the board as a 2d array
     */
    public int[][] getBoardArray()
    {
        return board;
    }

    public final void clearBoard()
    {
        board = new int[ROWS][COLUMNS];
    }
    public void setBoard(String[] boardArr){
    	int h=0;
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				board[i][j]=Integer.parseInt(boardArr[h]);
				h++;
			}
		}
    }

    /**
     * get the number of rows on the board
     *
     * @return number of rows(constant variable)
     */
    public int getROWS()
    {
        return ROWS;
    }

    /**
     * get the number of columns on the board
     *
     * @return number of columns(constant variable)
     */
    public int getCOLUMNS()
    {
        return COLUMNS;
    }
}