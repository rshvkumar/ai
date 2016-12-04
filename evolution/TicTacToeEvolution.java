package tictactoe;

import java.util.ArrayList;

import evolution.Evolution;
import evolution.Genome;

public class TicTacToeEvolution {

	public static void main(String[] args) {
		Game g = new Game(new HumanPlayer(), new HumanPlayer());
		g.play();
		
		Evolution evolution = new Evolution(9, new int[] { 9 }, 9);
		ArrayList <Player> players = new ArrayList <Player> ();
		
		
		// TODO: 
		for (int generation = 0 ; generation < 100 ; generation++) {
			System.out.println("Generation " + generation);
			// Create a generation of Player objects
			evolution.evolve();
			players.clear();
			for (Genome genome : evolution.genomes) {
				players.add(new Player(genome));
			}
			
			// Play every player against every other player
			for (Player p1 : players) {
				for (Player p2 : players) {
					if (p1 != p2) {
						new Game(p1, p2).play();
						new Game(p2, p1).play();
					}
				}
			}
		}
		
		// Last evolution step
		// TODO: find the highest score player, and play a human game against them
		new Game(players.get(0), new HumanPlayer());
    new Game(new HumanPlayer(), players.get(1));
	}

}
