package edu.lbic.lab7.game;

public class Move
{
    private int position;

    /**
     * Create a wrapper for player moves[column]
     *
     * @param position - the column for the move
     */
    public Move(int position)
    {
        this.position = position;
    }

    /**
     * Where is the move taking place?
     * Get the index of the position
     * @return move position
     */
    public int getPositionIndx()
    {
        return position;
    }
}
