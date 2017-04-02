

import apcs.Window;

public class Connect4 {

	// Determine the look and size of the game
	static int size = 75;
	static int width = 7;
	static int height = 6;

	int[][] board;
	int player = 1;

	// For drawing
	int active = 1;

	public static void main(String[] args) {
		Window.size(width * size, height * size);
		
		Connect4 game = new Connect4();
		Window.frame();

		while (true) {
			game.draw();
			
			if (game.player == 1) {
				int move = Window.mouse.getX() / size;
				game.active = move;
				if (Window.mouse.clicked() && game.canDrop(move)) {
					game.drop(move, true);
					Window.mouse.waitForRelease();
				}
			}
			else {
				int move = game.getBestMove();
				game.drop(move, true);
			}

			// If there is a winner
			if (game.getWinner() > 0) {
				// Show the winner
				game.drawWinner(game.getWinner());
				Window.frame(2000);

				// Reset the board
				game = new Connect4();
			}
			// If the game is a draw
			if (game.isDraw()) {
				game = new Connect4();
			}

			Window.frame();
		}

	}
	
	int states = 0;
	
	private int getBestMove() {
		int bestMove = 0;
		int bestValue = Integer.MIN_VALUE;
		states = 0;
		
		for (int column = 0 ; column < width ; column++) {
			if (canDrop(column)) {
				Connect4 next = new Connect4(this);
				next.drop(column, false);
				
				int value = evaluate(next, player, 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
				if (value > bestValue) {
					bestMove = column;
					bestValue = value;
				}
			}
		}
		
		System.out.println("States evaluated: " + states);
		return bestMove;
	}

	private int evaluate(Connect4 state, int forPlayer, int movesLeft, int alpha, int beta) {
		states++;
		// Base cases (draw, winner, or no moves left)
		if (state.isDraw()) {
			return 0;
		}
		int winner = state.getWinner();
		if (winner > 0) {
			if (winner == forPlayer) {
				return 1000000;
			}
			else {
				return -1000000;
			}
		}
		if (movesLeft == 0) {
			int opponentPlayer = 1 + forPlayer % 2;
			return score(state, forPlayer) - score(state, opponentPlayer);
		}
		
		// Recursive case
		boolean playersTurn = (state.player == forPlayer);
		int bestValue = (playersTurn) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		
		// Go to every valid column
		for (int column = 0 ; column < width ; column++) {
			if (state.canDrop(column)) {
				
				// Get the next state of the game
				Connect4 next = new Connect4(state);
				next.drop(column, false);
				
				// Evaluate the next state
				int value = evaluate(next, forPlayer, movesLeft - 1, alpha, beta);
				
				// Min or max based on whether it is the player's turn
				if (playersTurn) {
					// max
					if (value > bestValue) {
						bestValue = value;
					}
					if (value > alpha) {
						alpha = value;
					}
				}
				else {
					// min
					if (value < bestValue) {
						bestValue = value;
					}
					if (value < beta) {
						beta = value;
					}
				}
				if (alpha >= beta) {
					break;
				}
			}
		}
		
		return bestValue;
	}

	private int score(Connect4 state, int forPlayer) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Construct a Connect4 game
	 */
	public Connect4() {
		board = new int[width][height];
	}
	
	// Copy constructor
	public Connect4(Connect4 other) {
		board = new int[width][height];
		player = other.player;
		
		// Deep copy board
		for (int x = 0 ; x < width ; x++) {
			for (int y = 0 ; y < height ; y++) {
				board[x][y] = other.board[x][y];
			}
		}
	}

	/**
	 * Draws the current winner.
	 */
	private void drawWinner(int winner) {
		Window.out.font("Arial black", 60);
		if (winner == 1) {
			Window.out.color("red");
		}
		else {
			Window.out.color("blue");
		}
		Window.out.print("Player " + winner + " wins!", width * size / 2 - 250, height * size / 2 - 25);
	}
	
	/**
	 * Returns true if the game is a draw.
	 * @return
	 */
	private boolean isDraw() {
		for (int x = 0 ; x < width ; x++) {
			if (board[x][0] == 0) {
				return false;
			}
		}
		return true;
	}

	private int getWinner() {
		// Horizontal four in a row
		for (int x = 0 ; x < width - 3 ; x++) {
			for (int y = 0 ; y < height ; y++) {

				// If all pieces in this four in a row are the same and not empty
				if (board[x][y] > 0 &&
						board[x][y] == board[x + 1][y] &&
						board[x][y] == board[x + 2][y] &&
						board[x][y] == board[x + 3][y]) {
					return board[x][y];
				}
			}
		}

		// Vertical four in a row
		for (int x = 0 ; x < width ; x++) {
			for (int y = 0 ; y < height - 3 ; y++) {

				// If all pieces in this four in a row are the same and not empty
				if (board[x][y] > 0 &&
						board[x][y] == board[x][y + 1] &&
						board[x][y] == board[x][y + 2] &&
						board[x][y] == board[x][y + 3]) {
					return board[x][y];
				}
			}
		}

		// Diagonal four in a row
		for (int x = 0 ; x < width - 3 ; x++) {
			for (int y = 0 ; y < height - 3 ; y++) {

				// If all pieces in this four in a row are the same and not empty
				if (board[x][y] > 0 &&
						board[x][y] == board[x + 1][y + 1] &&
						board[x][y] == board[x + 2][y + 2] &&
						board[x][y] == board[x + 3][y + 3]) {
					return board[x][y];
				}
			}
		}

		for (int x = 0 ; x < width - 3 ; x++) {
			for (int y = 3 ; y < height ; y++) {

				// If all pieces in this four in a row are the same and not empty
				if (board[x][y] > 0 &&
						board[x][y] == board[x + 1][y - 1] &&
						board[x][y] == board[x + 2][y - 2] &&
						board[x][y] == board[x + 3][y - 3]) {
					return board[x][y];
				}
			}
		}

		// There was no winner (default)
		return 0;
	}

	/**
	 * Drops a piece in the given column.
	 * @param column - the column to drop the piece in
	 */
	private void drop(int column, boolean animated) {
		// put the piece into the top space
		board[column][0] = player;

		int next = 1;
		// while the space underneath is valid and is empty,
		while (next < height && board[column][next] == 0) {

			// move the piece from its current space to the space underneath
			board[column][next - 1] = 0;
			board[column][next] = player;
			next = next + 1;

			// Draw the change
			if (animated) {
				draw();
				Window.frame();
			}
		}
		// Redraw the state of the board
		if (animated) {
			draw();
		}

		// Switch the current player
		if (player == 1) {
			player = 2;
		}
		else {
			player = 1;
		}
	}

	/**
	 * Returns true if a piece can be dropped in the given column.
	 * @param column - the column to drop a piece in
	 * @return true or false
	 */
	private boolean canDrop(int column) {
		// Is the top space empty?
		if (board[column][0] == 0) {
			return true;
		}
		// Otherwise, not empty
		else {
			return false;
		}
	}

	/**
	 * Draw the Connect4 grid.
	 */
	private void draw() {
		Window.out.background("yellow");

		// Go to every x, y position
		for (int x = 0 ; x < width ; x++) {
			for (int y = 0 ; y < height ; y++) {

				// Pick the drawing color based on what is in the board position.
				if (board[x][y] == 0) {
					if (active == x) {
						Window.out.color("gray");
					}
					else {
						Window.out.color("black");
					}
				}
				else if (board[x][y] == 1) {
					Window.out.color("red");
				}
				else {
					Window.out.color("blue");
				}

				// Draw a black circle
				Window.out.circle(x * size + size / 2, y * size + size / 2, size * 4 / 9);
			}
		}
	}

}
