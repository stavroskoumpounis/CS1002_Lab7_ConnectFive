package edu.lbic.lab7.main;

import edu.lbic.lab7.game.GUI;
import edu.lbic.lab7.game.Player;

public class Main
{
    public static void main(String[] args)
    {
        Player p1 = new Player(1);
        Player AI = new Player(2);
        GUI ui = new GUI(p1, AI);
        ui.startGame();
    }
}