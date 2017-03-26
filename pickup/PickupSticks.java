package apcs;

import java.util.Scanner;

public class PickUpSticks {

	public static void main(String[] args) {
		PickUpSticks game = new PickUpSticks();

		while (true) {
			System.out.println(game.sticks + " sticks left.");

			int remove = 0;
			if (game.player == 1) {
				remove = game.getHumanMove();
				System.out.print("Human ");
			}
			else {
				remove = game.getComputerMove();
				System.out.print("Computer ");
			}
			System.out.println(" picked up " + remove + " sticks.");
			game.pickUp(remove);

			if (game.sticks <= 0) {
				System.out.println("Player " + game.player + " loses.");
			}
		}
	}

	private void pickUp(int remove) {
		sticks = sticks - remove;
		if (player == 1) {
			player = 2;
		}
		else {
			player = 1;
		}
	}

	int player = 1;
	int sticks = 15;
	Scanner keyboard = new Scanner(System.in);

	public int getHumanMove() {
		System.out.print("How many sticks: ");
		int value = keyboard.nextInt();
		// TODO: check for bad values
		return value;
	}

	public int getComputerMove() {
		int best = 0;
		int bestValue = -2;

		// Try picking up between 1 and 3 sticks
		for (int pickup = 1 ; pickup <= 3 ; pickup++) {
			int value = minimax(sticks - pickup, player, false);

			// If this gave me the best value
			if (value > bestValue) {
				bestValue = value;
				best = pickup;
			}
		}

		// Return the best move
		return best;
	}

	public int minimax(int sticks, int forPlayer, boolean playersTurn) {
		// Base case
		if (sticks <= 0) {
			// Good if its the player's turn, because other player reached 0
			if (playersTurn) {
				return 1;
			}
			else {
				return -1;
			}
		}

		if (playersTurn) {
			int best = -2;
			
			// Try picking up between 1 and 3 sticks
			for (int pickup = 1 ; pickup <= 3 ; pickup++) {
				int value = minimax(sticks - pickup, player, !playersTurn);
	
				// If this gave me the best value
				if (value > best) {
					best = value;
				}
			}
			
			return best;
		}
		else {
			int worst = 2;
			
			// Try picking up between 1 and 3 sticks
			for (int pickup = 1 ; pickup <= 3 ; pickup++) {
				int value = minimax(sticks - pickup, player, !playersTurn);
	
				// If this gave me the best value
				if (value < worst) {
					worst = value;
				}
			}
			
			return worst;
		}
	}
}
