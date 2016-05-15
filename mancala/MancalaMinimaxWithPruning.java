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
public class MancalaMinimaxWithPruning {
	private static final int START = 4;
	private static final int DEPTH = 12;
	private static final boolean PRINT = true;
	
	private static boolean UPDATE = false;
	private static Slider slider;
	
	public static void main(String[] args) {
		int[] board = newBoard();
		slider = new Slider("depth", 1, 30);
		slider.setValue(DEPTH);
		
		boolean viewAI = true;
		int aiPlayer = 2;
		
		Window.size(900, 400);
		Window.frame();
		draw(board);
		
		while (canMove(board)) {
			
			if (! playerCanMove(board)) {
				nextPlayer(board);
 				draw(board, 100);
			}
			else if (viewAI || getPlayer(board) == aiPlayer) {
				System.out.println("Thinking...");
				long start = System.currentTimeMillis();
				int move = bestMove(board, slider.getValue());
				System.out.println("Done in " + (System.currentTimeMillis() - start) + " ms.");
				if (move >= 0)
					board = move(board, move);
				
				draw(board, 1000);
			}
			else {
				// Wait until a valid move is selected.
				int move = -1;
				while (move < 0) {
					Window.mouse.waitForClick();
					int x = (Window.mouse.getX() - 150) / 100;
					if (x >= 0 && x < 6) {
						if (Window.mouse.getY() > 200 && getPlayer(board) == 1 && canMove(board, x))
							move = x;
						if (Window.mouse.getY() < 200 && getPlayer(board) == 2 && canMove(board, 12 - x))
							move = 12 - x;
					}
				}

				System.out.println("Move is " + move);
				// Move the selected cell and update to the next player.
				board = move(board, move);
				
				draw(board, 500);
			}
		}		
	}
	
	
	public static int bestMove(int[] board, int depth) {
		int move = -1,
			best = Integer.MIN_VALUE,
			alpha = Integer.MIN_VALUE;
		
		// Set alpha if there is an obvious next-turn move.
		for (int i = 0 ; i < 6 ; i++) {
			int index = (getPlayer(board) == 1) ? i : 7 + i;
			if (board[index] > 0 && board[index] == Math.abs(6 - i)) {
				if (UPDATE) depth = slider.getValue();
				
				int value = minimax(board, index, depth, alpha);
				if (value > best) {
					move = index;
					best = value;
					if (PRINT) System.out.println("move = " + move + ", best = " + best);
				}
				if (best > alpha)
					alpha = best;
			}
		}
		
		for (int i = 0 ; i < 6 ; i++) {
			int index = (getPlayer(board) == 1) ? i : 7 + i;
			if (board[index] > 0 && board[index] != Math.abs(6 - i)) {
				if (UPDATE) depth = slider.getValue();
				
				int value = minimax(board, index, depth, alpha);
				if (value > best) {
					move = index;
					best = value;
					if (PRINT) System.out.println("move = " + move + ", best = " + best);
				}
				if (best > alpha)
					alpha = best;
			}
		}
		System.out.println("move = " + move + ", expected = " + best);
		return move;
	}
	
	private static int minimax(int[] board, int index, int depth, int alpha) {
		int[] nextBoard = move(board, index);
		int value = minimax(
				nextBoard, 
				getPlayer(board), 
				getPlayer(nextBoard) == getPlayer(board), 
				depth, alpha, Integer.MAX_VALUE
			);
		
		if (PRINT)
			System.out.println("minimax( " + index + " ) = " + value);
		
		return value;
	}
	
	private static int minimax(int[] board, int player, boolean findBest, int depth, int alpha, int beta) {
		// The base case of the recursive call to minimax
		if (! canMove(board) ||		// If there is no move left on the board
			depth <= 0 || 			// Or the maximum depth has been reached
			getScore(board, player) > 6 * START ||			// Or there is a player with a  
			getScore(board, 1 + player % 2) > 6 * START		// guaranteed win
		) {
			int score = getScore(board, player) - getScore(board, 1 + player % 2);
			return score;
		}
		
		// If the current player has no move.
		if (! playerCanMove(board)) {
			nextPlayer(board);
			return minimax(board, player, !findBest, depth, alpha, beta);
		}
		
		// Either the minimum or maximum value, and the move that reaches that value
		int best = 0;
		
		// Maximizing
		if (findBest) {
			best = Integer.MIN_VALUE;
			
			// Check all empty squares, and favor 
			for (int i = 0 ; i < 6 ; i++) {
				int k = (getPlayer(board) == 1) ? i : 12 - i;
				if (board[k] > 0) {
					int[] nextBoard = move(board, k);
					
					int nextBoardValue = 0;
					if (getPlayer(nextBoard) == getPlayer(board)) {
						nextBoardValue = minimax(nextBoard, player, findBest, depth, alpha, beta);
					}
					else nextBoardValue = minimax(nextBoard, player, !findBest, depth - 1, alpha, beta);
					
					if (nextBoardValue > best)
						best = nextBoardValue;
					
					if (best > alpha)
						alpha = best;
					if (beta <= alpha)
						break;
				}
			}
		}
		else {
			best = Integer.MAX_VALUE;
			for (int i = 0 ; i < 6 ; i++) {
				int k = (getPlayer(board) == 1) ? i : 12 - i;
				if (board[k] > 0) {
					int[] nextBoard = move(board, k);
					
					// If the player got another turn
					int nextBoardValue = 0;
					if (getPlayer(nextBoard) == getPlayer(board)) {
						nextBoardValue = minimax(nextBoard, player, findBest, depth, alpha, beta);
					}
					else nextBoardValue = minimax(nextBoard, player, !findBest, depth - 1, alpha, beta);
					
					if (nextBoardValue < best)
						best = nextBoardValue;
					
					if (best < beta)
						beta = best;
					if (beta <= alpha)
						break;
				}
			}
		}
		
		
		return best;
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
