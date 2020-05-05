package edu.lbic.lab7.game;

public class Player
{
    private int playerNum;
    private int wins = 0;

    /**
     * A player object to keep track of game players
     *
     * @param playerNum - the id for the player
     */
    public Player(int playerNum)
    {
        this.playerNum = playerNum;
    }

    /**
     * Get this player's current ID
     *
     * @return the player ID (i.e. player 1 or player 2)
     */
    public int getNum()
    {
        return playerNum;
    }

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}
}