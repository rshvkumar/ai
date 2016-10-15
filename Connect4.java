package connect4;

import apcs.Window;

public class Connect4 {

	int[][] board;
	
	// current player
	int player = 1;
	
	// Determine the look and size of the game
	int size = 50;
	int width = 7;
	int height = 6;
	int active = 1;
	
	public Connect4() {
		Window.size(width * size, height * size);
		board = new int[width][height];
	}
	
	/**
	 * Returns the best column to drop a piece in by performing a minimax search to the
	 * given depth.
	 * 
	 * @param depth
	 * @return
	 */
	public int getBestMove(int depth) {
		int best = -1;
		int bestValue = Integer.MIN_VALUE;
		int forPlayer = player;
		
		for (int column = 0 ; column < width ; column++) {
			if (canDrop(column)) {
				drop(column);
				int value = minimax(forPlayer, false, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
				undrop(column);
				System.out.println("Column " + column + ": " + value);
				
				if (value > bestValue || best < 0) {
					bestValue = value;
					best = column;
				}
			}
		}
		return best;
	}
	
	public int heuristic(int player) {
		int h = 0;
		for (int x = 0 ; x < width ; x++) {
			for (int y = 0 ; y < height ; y++) {
				if (board[x][y] == player) {
					int z = Math.abs(width / 2 - x) + Math.abs(height / 2 - y);
					
					// Horizontal and vertical
					if (y + 1 < height && board[x][y + 1] == player) h += z;
					if (y > 0 && board[x][y - 1] == player) h += z;
					if (x + 1 < width && board[x + 1][y] == player) h += z;
					if (x > 0 && board[x - 1][y] == player) h += z;
					
					// Diagonals
					if (x + 1 < width && y + 1 < height && board[x + 1][y + 1] == player) h += z;
					if (x + 1 < width && y > 0 && board[x + 1][y - 1] == player) h += z;
					if (x > 0 && y + 1 < height && board[x - 1][y + 1] == player) h += z;
					if (x > 0 && y > 0 && board[x - 1][y - 1] == player) h += z;
				}
			}
		}
		return h;
	}
	
	/**
	 * Minimax function.
	 * @param player
	 * @param findBest
	 * @param depth
	 * @return
	 */
	public int minimax(int player, boolean findBest, int depth, int alpha, int beta) {
		int winner = getWinner();
		if (depth == 0 || winner != 0 || ! canDrop()) {
			if (winner == 0) {
				return heuristic(player) - heuristic(1 + player % 2);
			}
			else if (winner == player) {
				return Integer.MAX_VALUE;
			}
			else {
				return Integer.MIN_VALUE;
			}
		}
		
		int best = 0;
		if (findBest) {
			best = -2;
			for (int i = 0 ; i < width ; i++) {
				if (canDrop(i)) {
					drop(i);
					int value = minimax(player, false, depth - 1, alpha, beta);
					undrop(i);
					if (value > best)
						best = value;
					if (value > alpha)
						alpha = value;
					if (beta <= alpha)
						break;
				}
			}
		}
		else {
			best = 2;
			for (int i = 0 ; i < width ; i++) {
				if (canDrop(i)) {
					drop(i);
					int value = minimax(player, true, depth - 1, alpha, beta);
					undrop(i);
					if (value < best)
						best = value;
					if (value < beta)
						beta = value;
					if (beta <= alpha)
						break;
				}
			}
		}
		return best;
	}
	
	public void drawWinner(int winner) {
		Window.out.font("Arial black", 60);
		if (winner == 1) {
			Window.out.color("red");
		}
		else {
			Window.out.color("blue");
		}
		Window.out.print("Player " + winner + " wins!", width * size / 2 - 250, height * size / 2 - 25);
	}

	public int getWinner() {
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
	public void drop(int column) {
		int y = 0;
		while (y + 1 < height && board[column][y + 1] == 0)
			y++;
		board[column][y] = player;
		
		// Switch the current player
		player = 1 + player % 2;
	}
	
	/**
	 * Removes the top piece from the given column.
	 * @param column
	 */
	public void undrop(int column) {
		int y = 0;
		while (y < height && board[column][y] == 0)
			y++;
		board[column][y] = 0;
		
		// Switch the current player
		player = 1 + player % 2;
	}

	/**
	 * Returns true if there are any columns that can be dropped in.
	 * @return
	 */
	public boolean canDrop() {
		for (int i = 0 ; i < width ; i++) {
			if (board[i][0] == 0)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns true if a piece can be dropped in the given column.
	 * @param column - the column to drop a piece in
	 * @return true or false
	 */
	public boolean canDrop(int column) {
		return board[column][0] == 0;
	}
	
	/**
	 * Draw the Connect4 grid.
	 */
	public void draw() {
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
		Window.frame();
	}

}
