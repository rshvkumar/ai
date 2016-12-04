package tictactoe;

import apcs.Window;
import evolution.Genome;

public class Player {
	
	Genome genome;
	
	public void move(Game game, int piece) {
		double[] input = new double[9];
		
		// iterate over the game's board
		for (int x = 0 ; x < 3 ; x++) {
			for (int y = 0 ; y < 3 ; y++) {
				
				// set each input element to value of the square on the board
				// in the perspective of the player
				if (game.valueOf(x, y) == piece) {
					input[y * 3 + x] = 1;
				}
				else if (game.valueOf(x, y) == 0) {
					input[y * 3 + x] = 0;
				}
				else {
					input[y * 3 + x] = -1;
				}
			}
		}
		
		// Compute the output from the network
		double[] output = genome.network.compute(input);
		
		// Find the maximum index of a valid square that can have a piece placed on it
		int max = -1;
		for (int i = 0 ; i < output.length ; i++) {
			if (game.valid(i % 3, i / 3) && 
			   (max < 0 || output[max] < output[i])) {
				max = i;
			}
		}
		
		// Place a piece on the maximum output
		game.place(max % 3, max / 3, piece);
	}
	
}

class HumanPlayer extends Player {
	public void move(Game game, int piece) {
		// Draw the board, wait for a click
		Window.mouse.waitForClick();
		
		Window.mouse.waitForRelease();
	}
}
