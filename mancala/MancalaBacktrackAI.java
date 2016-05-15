package mancala;

import apcs.Slider;
import apcs.Window;

/**
 * - apcs.io/lab/mancala
 * - apcs.io/mp/mancala
 * 
 * The objective of the game of Mancala is to collect more marbles than your opponent.
 * 
 *  ____ ____ ____ ____ ____ ____ ____ ____
 * |    | 12 | 11 | 10 |  9 |  8 |  7 |    |  <- player 2
 * | 13 |----|----|----|----|----|----|  6 |
 * |    | 0  | 1  | 2  | 3  | 4  | 5  |    |  <- player 1
 * |____|____|____|____|____|____|____|____| 
 *  Index 14 stores the current player's turn (1 or 2)
 * 
 * @author keshav
 * @email  keshav@techlabeducation.com
 */
public class MancalaBacktrackAI {
	private static final int START = 4;
	private static final int DEFAULT_DEPTH = 12;
	private static final int MAX_DEPTH = 30;
	private static final int MIN_TIME = 5000;
	private static final boolean PRINT = true;

	public static void main(String[] args) {
		int[] board = newBoard();

		boolean viewAI = true;
		int aiPlayer = 2;
		
		Window.size(900, 400);
		Window.frame();
		draw(board);
		
		// While there are still valid moves on the board
		while (canMove(board)) {
			
			// Skip the current player's turn if they cannot move.
			if (! playerCanMove(board)) {
				nextPlayer(board);
				draw(board, 100);
			}

			// If this is an AI players move or an AI vs AI game
			else if (viewAI || getPlayer(board) == aiPlayer) {
				int move = minimax(board, DEFAULT_DEPTH);
				if (move >= 0)
					board = move(board, move);

				// Show the AI's move
				draw(board, 500);
			}

			// Wait for the human to select a valid move. 
			else {
				int move = -1;
				while (move < 0) {
					Window.mouse.waitForClick();

					// Parse the mouse position into a move selection.
					int x = (Window.mouse.getX() - 150) / 100;
					if (x >= 0 && x < 6) {
						if (Window.mouse.getY() > 200 && getPlayer(board) == 1 && canMove(board, x))
							move = x;
						if (Window.mouse.getY() < 200 && getPlayer(board) == 2 && canMove(board, 12 - x))
							move = 12 - x;
					}
				}

				// Move the selected cell and update to the next player.
				board = move(board, move);
				draw(board, 500);
			}
		}		
	}

	/**
	 * Minimax helper, starts minimax with default parameters for selecting the optimal move.
	 * @param board
	 * @param depth
	 * @return
	 */
	private static int minimax(int[] board, int depth) {
		if (PRINT)
			System.out.println("player = " + getPlayer(board) + ", depth = " + depth);
		
		long start = System.currentTimeMillis();
		int move = minimax(copyBoard(board), getPlayer(board), true, true, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
		long end = System.currentTimeMillis();
		
		// Keep increasing the depth until the maximum depth or until the end is reached.
		while (end - start < MIN_TIME / 2 && depth < MAX_DEPTH && move >= 0) {
			depth++;
			move = minimax(copyBoard(board), getPlayer(board), true, true, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
			end = System.currentTimeMillis();
		}
		
		if (PRINT)
			System.out.println(
					"move = " + move +
					", depth = " + depth +
					", time = " + (end - start) + "ms\n");
		
		return move;
	}

	private static int minimax(
			int[] board, int player, 				// The board and player doing the minimax search
			boolean findBest, boolean returnMove, 	// Flags for searching
			int depth, int alpha, int beta			// For depth limit and pruning
			) {

		// The minimax base case
		if (!   canMove(board) ||		// If there is no move left on the board
				depth <= 0 || 			// Or the maximum depth has been reached
				// Or there is a player with a guaranteed win
				board[6] > 6 * START || board[13] > 6 * START) {
			
			// If this is a guaranteed win/loss, play anything
			if (returnMove) {
				for (int i = 0 ; i < 6 ; i++) {
					int index = (board[14] == 1) ? 5 - i : 12 - i;
					if (board[index] > 0)
						return index;
				}
				return -1;
			}
			// Return the score advantage of the player doing the minimax search.
			return (board[6] - board[13]) * ((board[14] == 1) ? 1 : -1);
		}

		// If the current player has no move.
		if (! playerCanMove(board)) {
			board[14] = 1 + board[14] % 2;
			int value = minimax(board, player, !findBest, false, depth, alpha, beta);
			board[14] = 1 + board[14] % 2;
			return value;
		}

		// Either the minimum or maximum value, and the move that reaches that value
		int best = (findBest) ? Integer.MIN_VALUE : Integer.MAX_VALUE,
			bestMove = -1,
			value = 0;

		// First check moves where the player would get another turn, to quickly set alpha/beta values.
		// Start from the closest pocket and move backwards.
		for (int k = 0 ; k < 2 ; k++) {
			// For k = 0, only check pockets that provide another turn
			// For k = 1, check all other pockets
			for (int i = 0 ; i < 6 ; i++) {
				// Get the board index
				int index = (board[14] == 1) ? i : 7 + i;

				// Make the move and get the minimax value
				if (board[index] > 0 && (
					(k == 0 && board[index] == 6 - i) || 
					(k == 1 && board[index] != 6 - i))) {
					
					// Track whether the player got another turn.
					boolean samePlayer = false;
					
					// Remove the marbles from the selected cell.
					int count = board[index], 
						original = board[index], 
						capture = 0;
					
					board[index] = 0;
					
					// Distribute the marbles to each cell in the board.
					while (count > 0) {
						// Increment the index to the next cell, and wrap around if necessary
						index = index + 1;
						// Player 2 skips player 1's end pocket
						if (board[14] == 2 && index == 6)
							index = index + 1;

						// Wrap around, player 1 skips player 2's pocket
						if (index > 13 || (board[14] == 1 && index == 13)) 
							index = 0;

						// Add a marble to the current cell.
						count -= 1;
						board[index] += 1;
					}
					
					// Land in one's own pocket and get another turn
					if ((board[14] == 1 && index == 6) || (board[14] == 2 && index == 13)) {
						samePlayer = true;
					}
					// Empty square captures opposite cell if on player's side
					else if (board[index] == 1 && board[12 - index] > 0 &&
							((board[14] == 1 && index < 6) || (board[14] == 2 && index > 6))) {
						
						capture = board[12 - index];
						board[(board[14] == 1) ? 6 : 13] += capture + 1;
						board[12 - index] = 0;
						board[index] = 0;
						samePlayer = true;
					} 
					
					// Otherwise it is the next player's turn
					else {
						board[14] = 1 + board[14] % 2;
					}
					
					value = minimax(board, player, 
							findBest == samePlayer, false,
							depth - ((samePlayer) ? 0 : 1), alpha, beta);
					
					// Reset the player to this instance's
					if (! samePlayer) {
						board[14] = 1 + board[14] % 2;
					}
					
					// Reset the board to its original state
					if (capture > 0) {
						board[(board[14] == 1) ? 6 : 13] -= capture + 1;
						board[12 - index] = capture;
						board[index] = 1;
					}
					while (count < original) {
						board[index] -= 1;
						index = index - 1;
						if (index == 6 && board[14] == 2)
							index = 5;
						if (index < 0)
							index = (board[14] == 1) ? 12 : 13;
						
						count = count + 1;
					}
					board[index] = count;
					
					// Set the best move and update alpha/beta accordingly.
					if ((findBest && value > best) || (!findBest && value < best)) {
						best = value;
						bestMove = index;
					}
					if (findBest && best > alpha)
						alpha = best;
					if (!findBest && best < beta)
						beta = best;
					
					if (PRINT && returnMove)
						System.out.println(
								"index = " + index + 
								", value = " + value +
								", best = " + best + 
								", move = " + bestMove +
								", depth = " + depth);
					
					// Alpha beta pruning (the cut-off point).
					if (beta <= alpha)
						break;
				}
			}
			
			// Alpha beta pruning (the cut-off point).
			if (beta <= alpha)
				break;
		}
		
		// For the first minimax call, return the index of the best move.
		if (returnMove) {
			return bestMove;
		}
		else return best;
	}

	/**
	 * Returns the player score.
	 * @param board
	 * @param player
	 * @return
	 */
	public static int getScore(int[] board, int player) {
		return board[(player == 1) ? 6 : 13];
	}

	/**
	 * Returns a Mancala board at the beginning of the game.
	 * @return an integer array representing the initial board state
	 */
	public static int[] newBoard() {
		return new int[] { 
				START, START, START, START, START, START, 0, 
				START, START, START, START, START, START, 0,
				1	// current player's turn
		};
	}
	
	/**
	 * Returns a copy of the given board.
	 */
	public static int[] copyBoard(int[] board) {
		int[] b = new int[15];
		for (int i = 0 ; i < 15 ; i++)
			b[i] = board[i];
		return b;
	}

	/**
	 * Returns the current player of the board.
	 * @param board
	 * @return
	 */
	public static int getPlayer(int[] board) {
		return board[14];
	}

	/**
	 * Advances the player on the board.
	 * @param board
	 * @return
	 */
	public static int[] nextPlayer(int[] board) {
		board[14] = 1 + board[14] % 2;
		return board;
	}

	/**
	 * Returns true if the current player of this board can move.
	 * @param board
	 * @return
	 */
	public static boolean playerCanMove(int[] board) {
		for (int i = 0 ; i < 6 ; i++) {
			if (board[14] == 1 && board[i] > 0)
				return true;
			if (board[14] == 2 && board[12 - i] > 0)
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the board state given represents a finished game.
	 * @param board
	 * @return
	 */
	public static boolean canMove(int[] board) {
		for (int i = 0 ; i < 6 ; i++) {
			if (board[i] > 0 || board[12 - i] > 0)
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the given index can be moved on the given board.
	 * @param board
	 * @param index
	 * @return
	 */
	public static boolean canMove(int[] board, int index) {
		return index >= 0 && index <= 12 && index != 6 && board[index] > 0;
	}

	/**
	 * Returns a new integer array representing the Mancala board after the
	 * cell at the given index has been emptied and distributed.
	 * @param board
	 * @param index - either 0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, or 12 (valid cells that can be moved)
	 * @return a new integer array representing the board state after moving
	 * @throws MancalaException 
	 */
	public static int[] move(int[] board, int index) {
		// Create an integer array to copy the board and change to the
		// new configuration of pieces after moving.
		int[] newBoard = new int[board.length];
		for (int i = 0 ; i < board.length ; i++)
			newBoard[i] = board[i];

		// Remove the marbles from the selected cell.
		int player = getPlayer(board);
		int count = newBoard[index];
		newBoard[index] = 0;

		// Distribute the marbles to each cell in the board.
		while (count > 0) {
			// Increment the index to the next cell, and wrap around if necessary
			index = index + 1;
			// Player 2 skips player 1's end pocket
			if (player == 2 && index == 6)
				index = index + 1;

			// Wrap around, player 1 skips player 2's pocket
			if (index > 13 || (player == 1 && index == 13)) 
				index = 0;

			// Add a marble to the current cell.
			count -= 1;
			newBoard[index] += 1;
		}

		// Land in one's own pocket
		if ((player == 1 && index == 6) || (player == 2 && index == 13)) {
			// another turn
		}
		// Empty square captures opposite cell if on player's side
		else if (newBoard[index] == 1 && newBoard[12 - index] > 0 &&
				((player == 1 && index < 6) || (player == 2 && index > 6))) {
			newBoard[(player == 1) ? 6 : 13] += newBoard[12 - index] + 1;
			newBoard[12 - index] = 0;
			newBoard[index] = 0;
		} 
		else {
			newBoard[14] = 1 + newBoard[14] % 2;
		}

		return newBoard;
	}

	public static void draw(int[] board) {
		draw(board, 100);
	}

	public static void draw(int[] board, int frameLength) {
		Window.size(900, 400);
		Window.out.font("Arial", 50);

		drawBackground();
		drawPocket(100, 200, board[13], getPlayer(board) == 2);
		drawPocket(800, 200, board[6], getPlayer(board) == 1);

		for (int x = 0 ; x < 6 ; x ++) {
			drawCell(200 + x * 100, 120, board[12 - x], getPlayer(board) == 2);
			drawCell(200 + x * 100, 280, board[x], getPlayer(board) == 1);
		}

		Window.frame(frameLength);
	}

	public static void drawBackground() {
		Window.out.background("white");
		Window.out.color("tan");
		drawRectangle(450, 200, 850, 350, 20);
	}

	public static void drawPocket(int x, int y, int count, boolean active) {
		Window.out.color((active) ? "chocolate" : "brown");
		drawRectangle(x, 200, 100, 300, 10);
		for (int i = 0 ; i < count ; i++) {
			Window.out.randomColor();
			Window.out.circle(x + Window.rollDice(40) - 20, y + Window.rollDice(80) - 40, 10);
		}
		Window.out.color("white");
		Window.out.print(count, x - 15, y + 20);
	}

	public static void drawCell(int x, int y, int count, boolean active) {
		Window.out.color((active) ? "chocolate" : "brown");
		Window.out.oval(x, y, 80, 140);

		for (int i = 0 ; i < count ; i++) {
			Window.out.randomColor();
			Window.out.circle(x + Window.rollDice(40) - 20, y + Window.rollDice(80) - 40, 10);
		}
		Window.out.color("white");
		Window.out.print(count, x - 15, y + 20);
	}

	public static void drawRectangle(int x, int y, int width, int height, int radius) {
		Window.out.rectangle(x, y, width, height - 2 * radius);
		Window.out.rectangle(x, y, width - 2 * radius, height);
		Window.out.circle(x - width / 2 + radius, y - height / 2 + radius, radius);
		Window.out.circle(x - width / 2 + radius, y + height / 2 - radius, radius);
		Window.out.circle(x + width / 2 - radius, y - height / 2 + radius, radius);
		Window.out.circle(x + width / 2 - radius, y + height / 2 - radius, radius);
	}
}
