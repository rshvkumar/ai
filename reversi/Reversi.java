package reversi;

import java.util.ArrayList;

import apcs.Window;

public class Reversi {
	
	// Instance variables
	int[][] board;
	int player;
	int[] score;
	int length = 8;
	int moves;

	// Display variables
	static int[][] value;
	static int maxWidth = 600;
	static int size = 80;
	static int highlightX = -1, highlightY = -1;

	// Colors for displaying the board.
	static String boardColor = "green";
	static String player1Piece = "white";
	static String player1Valid = "light gray";
	static String player2Piece = "black";
	static String player2Valid = "dark gray";
	static String gridColor = "dark green";
	static String highlightColor = "yellow";
	
	// Used for quickly calculating the flipping of pieces in 8 directions.
	static int[][] direction = { {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1} };

	/**
	 * Default size is 8 x 8.
	 */
	public Reversi() {
		this(8);
	}
	
	/**
	 * Constructs a Reversi board at the start position.
	 * @param length
	 */
	public Reversi(int length) {
		size = maxWidth / length;
		moves = length * length;
		score = new int[3];
		Window.size(length * size, length * size);
		
		this.length = length;
		board = new int[length][length];
		value = new int[length][length];
		board[length / 2 - 1][length / 2 - 1] = 1;
		board[length / 2][length / 2 - 1] = 2;
		board[length / 2][length / 2] = 1;
		board[length / 2 - 1][length / 2] = 2;
		player = 1;
	}
	
	/**
	 * Copy constructor.
	 * @param other
	 */
	public Reversi(Reversi other) {
		this.length = other.length;
		this.player = other.player;
		this.moves = other.moves;
		this.board = new int[other.length][other.length];
		
		this.score = new int[3];
		this.score[1] = other.score[1];
		this.score[2] = other.score[2];
		
		for (int x = 0 ; x < length ; x++) {
			for (int y = 0 ; y < length ; y++) {
				this.board[x][y] = other.board[x][y];
			}
		}
	}
	
	public void reset() {
		this.board = new int[length][length];
		this.player = 1;
	}
	
	public boolean isValid(ReversiMove move) {
		return isValid(move.x, move.y);
	}
	
	public boolean isValid(int x, int y) {
		return x >= 0 && x < length && y >= 0 && y < length;
	}
	
	public ArrayList <ReversiMove> getMoves() {
		ArrayList <ReversiMove> moves = new ArrayList <ReversiMove> ();
		for (int x = 0 ; x < length ; x++) {
			for (int y = 0 ; y < length ; y++) {
				if (canMove(x, y))
					moves.add(new ReversiMove(x, y));
			}
		}
		return moves;
	}
	
	public ReversiMove getBestMove(int depth) {
		ReversiMove best = null;
		int bestValue = Integer.MIN_VALUE;
		
		for (ReversiMove move : getMoves()) {
			Reversi next = new Reversi(this);
			next.perform(move);
			
			int value = minimax(next, player, false, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
			Reversi.value[move.x][move.y] = value;
			if (value > bestValue) {
				bestValue = value;
				best = move;
			}
		}
		return best;
	}
	
	public int minimax(Reversi state, int player, boolean findBest, int depth, int alpha, int beta) {
		if (depth == 0 || ! state.canMove()) {
			return score[player] - score[1 + player % 2];
		}
		
		int best = (findBest) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		ArrayList <ReversiMove> moves = state.getMoves();
		
		if (moves.size() == 0) {
			state.pass();
			return minimax(state, player, ! findBest, depth - 1, alpha, beta);
		}
		
		for (ReversiMove move : moves) {
			Reversi next = new Reversi(state);
			next.perform(move);
			
			int value = minimax(next, player, ! findBest, depth - 1, alpha, beta);
			
			if (findBest) {
				if (value > best)
					best = value;
				if (value > alpha)
					alpha = value;
				if (beta <= alpha)
					break;
			}
			else {
				if (value < best)
					best = value;
				if (value < beta)
					beta = value;
				if (beta <= alpha)
					break;
			}
		}
		
		return best;
	}
	
	public void pass() {
		player = 1 + player % 2;
	}
	
	public boolean canMove(int x, int y) {
		if (board[x][y] != 0)
			return false;
		
		// Next to opposite piece
		int opponent = 1 + player % 2;
		
		for (int[] dir : direction) {
			int dx = dir[0], dy = dir[1];
			if (isValid(x + dx, y + dy) && board[x + dx][y + dy] == opponent) {
				for (int i = 2 ; isValid(x + dx * i, y + dy * i) ; i++) {
					if (board[x + dx * i][y + dy * i] == player)
						return true;
					if (board[x + dx * i][y + dy * i] == 0)
						break;
				}
			}
		}
		return false;
	}
	
	public boolean canMove() {
		return moves > 0;
	}
	
	public void perform(ReversiMove move) {
		if (move == null)
			pass();
		else
			move(move.x, move.y);
		value = new int[length][length];
	}
	
	// Assume validated
	public void move(int x, int y) {
		int opponent = 1 + player % 2;
		
		board[x][y] = player;
		moves--;
		score[player]++;
		
		for (int[] dir : direction) {
			int dx = dir[0], dy = dir[1];
			if (isValid(x + dx, y + dy) && board[x + dx][y + dy] == opponent) {
				for (int i = 2 ; isValid(x + dx * i, y + dy * i) ; i++) {
					if (board[x + dx * i][y + dy * i] == player) {
						for (int j = 1 ; j < i ; j++) {
							board[x + dx * j][y + dy * j] = player;
							score[player]++;
							score[opponent]--;
						}
						break;
					}
				}
			}
		}
		
		player = opponent;
	}

	public void draw() {
		int l = size * length;

		Window.out.background(boardColor);
		Window.out.font("Monaco", 30);
		for (int x = 0 ; x < length ; x++) {
			// Draw grid line before every column except first
			if (x > 0) {
				Window.out.color(gridColor);
				Window.out.rectangle(x * size, l / 2, 4, l);
			}

			for (int y = 0 ; y < length ; y++) {
				// For first x, draw grid line for y
				if (x == 0 && y > 0) {
					Window.out.color(gridColor);
					Window.out.rectangle(l / 2, y * size, l, 4);
				}
				if (board[x][y] == 0 && canMove(x, y)) {
					if (x == highlightX && y == highlightY)
						Window.out.color(highlightColor);
					else 
						Window.out.color((player == 1) ? player1Valid : player2Valid);
					Window.out.square(x * size + size / 2, y * size + size / 2, size - 4);
					
					// If the square has a value
					Window.out.color((value[x][y] > 0) ? "green" : "red");
					Window.out.print(value[x][y], x * size + 5, (y + 1) * size - 5);
				}
				if (board[x][y] == 1) {
					Window.out.color(player1Piece);
					Window.out.circle(x * size + size / 2, y * size + size / 2, size * 4 / 9);
				}
				if (board[x][y] == 2) {
					Window.out.color(player2Piece);
					Window.out.circle(x * size + size / 2, y * size + size / 2, size * 4 / 9);
				}
			}
		}

		Window.frame();
	}
	
	public void getClick() {
		if (getMoves().size() == 0)
			pass();
		
		Window.mouse.waitForClick();
		int x = Window.mouse.getX() / size;
		int y = Window.mouse.getY() / size;
		
		
		if (isValid(x, y) && canMove(x, y)) {
			move(x, y);
		}

		Window.mouse.waitForRelease();
		
		
		highlight(x, y);
		draw();
		Window.sleep(500);
	}

	public void highlight(ReversiMove move) {
		if (move != null)
			highlight(move.x, move.y);
	}
	
	public void highlight(int x, int y) {
		highlightX = x;
		highlightY = y;
	}
}

class ReversiMove {
	int x;
	int y;
	
	public ReversiMove(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
