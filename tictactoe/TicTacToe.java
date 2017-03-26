import apcs.Window;

public class TicTacToe {

    public static void main(String[] args) {
        // Create the initial game object
        TicTacToe game = new TicTacToe();
        Window.frame();
        
        // Infinitely draw the game, then get a move
        while (true) {
            game.draw();
            
            // Get a move object for the human move
            Move m;
            if (game.player == 1) {
            		m = game.getHumanMove();
            }
            else {
            		m = game.getComputerMove();
            }
            
            // If a valid move was chosen
            if (m != null) {
                game.move(m);
            }
            
            // First check if there is a winner
            if (game.getWinner() > 0) {
                System.out.println(game.getWinner() + " won");
                game.draw();
                Window.sleep(1000);
                game = new TicTacToe();
            }
            // If the game is a draw or there is a winner, then print it out and reset
            else if (game.isDraw()) {
                System.out.println("Game is a draw.");
                game.draw();
                Window.sleep(1000);
                game = new TicTacToe();
            }
        }
    }
    
    int[][] board;
    int player = 1;
    
    public TicTacToe() {
        board = new int[3][3];
    }
    
    // Copy constructor
    public TicTacToe(TicTacToe other) {
        board = new int[3][3];
        for (int x = 0 ; x < 3 ; x++) {
            for (int y = 0 ; y < 3 ; y++) {
                board[x][y] = other.board[x][y];
            }
        }
        
        player = other.player;
    }
    
    // Returns true or false based on whether there is a draw on the board
    public boolean isDraw() {
        // For every x and y position
        for (int x = 0 ; x < 3 ; x++) {
            for (int y = 0 ; y < 3 ; y++) {
                
                // If this is an empty square
                if (board[x][y] == 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // Returns the player number of the current winner, or 0 if there is none
    public int getWinner() {
        // Horizontal three in a row
        for (int i = 0 ; i < 3 ; i++) {
            if (board[0][i] > 0 && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
                return board[0][i];
            }
            if (board[i][0] > 0 && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                return board[i][0];
            }
        }
        if (board[0][0] > 0 && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            return board[0][0];
        }
        if (board[2][0] > 0 && board[2][0] == board[1][1] && board[2][0] == board[0][2]) {
            return board[2][0];
        }
        return 0;
    }
    
    public void move(Move move) {
        board[move.x][move.y] = player;
        if (player == 1) {
            player = 2;
        }
        else {
            player = 1;
        }
    }
    
    // Returns an array of possible moves that can be made on this board
    public Move[] getMoves() {
    		// count empty spaces
    		int count = 0;
    		// For every x, y position on the board
    		for (int x = 0 ; x < 3 ; x++) {
    			for (int y = 0 ; y < 3 ; y++) {
    				
    				// If the position is empty
    				if (board[x][y] == 0) {
    					count++;
    				}
    			}
    		}
    	
    		Move[] moves = new Move[count];
    		int i = 0;
    	
    		// For every x, y position on the board
    		for (int x = 0 ; x < 3 ; x++) {
    			for (int y = 0 ; y < 3 ; y++) {
    				
    				// If the position is empty
    				if (board[x][y] == 0) {
    					moves[i] = new Move(x, y);
    					i++;
    				}
    			}
    		}
    	
    		return moves;
    }
    
    public Move getComputerMove() {
    		// Get all possible moves
    		Move[] possible = getMoves();
    		
    		// Store the best move
    		Move best = null;
    		int bestValue = -9999;
    		
    		// Try each possible move on a copy of the board
    		for (Move m : possible) {
    			TicTacToe copy = new TicTacToe(this);
    			copy.move(m);
    			
    			// Call the minimax method with the copy and player
    			int value = minimax(copy, player, false);
    			
    			// If this move yielded a better value, save it
    			if (value > bestValue) {
    				bestValue = value;
    				best = m;
    			}
    		}
    		
    		// Return the best move
    		return best;
    }
    
    // Rank a state of the game for a certain player
    public int minimax(TicTacToe board, int forPlayer, boolean playersTurn) {
    	
    		// If there is a winner on this board state
    		if (board.getWinner() > 0) {
    			if (board.getWinner() == forPlayer) {
    				return 1;
    			}
    			else {
    				return -1;
    			}
    		}
    		if (board.isDraw()) {
    			return 0;
    		}
    		
    		// Recursively rank the next moves after this one
    		Move[] nextMoves = board.getMoves();
    		
    		if (playersTurn) {
	    		int best = -2;
	    		for (Move m : nextMoves) {
	    			TicTacToe copy = new TicTacToe(board);
	    			copy.move(m);
	    			
	    			int value = minimax(copy, player, !playersTurn);
	    			if (value > best) {
	    				best = value;
	    			}
	    		}
	    		
	    		return best;
    		}
    		else {
    			int worst = 2;
	    		for (Move m : nextMoves) {
	    			TicTacToe copy = new TicTacToe(board);
	    			copy.move(m);
	    			
	    			int value = minimax(copy, player, !playersTurn);
	    			if (value < worst) {
	    				worst = value;
	    			}
	    		}
	    		
	    		return worst;
    		}
    }
    
    public Move getHumanMove() {
        Window.mouse.waitForClick();
        int x = (Window.mouse.getX() - 25) / 150;
        int y = (Window.mouse.getY() - 25) / 150;
        Window.mouse.waitForRelease();
        
        // X and Y are valid, and the board position is empty
        if (x >= 0 && x < 3 && y >= 0 && y < 3 && board[x][y] == 0) {
            return new Move(x, y);
        }
        else {
            return null;
        }
    }
    
    public void draw() {
        Window.out.background("white");
        Window.out.fontSize(100);
        
        Window.out.color("black");
        Window.out.rectangle(175, 250, 4, 450);
        Window.out.rectangle(325, 250, 4, 450);
        Window.out.rectangle(250, 175, 450, 4);
        Window.out.rectangle(250, 325, 450, 4);
        
        for (int x = 0 ; x < 3 ; x++) {
            for (int y = 0 ; y < 3 ; y++) {
                if (board[x][y] == 1) {
                    Window.out.print("X", 75 + x * 150, 125 + y * 150);
                }
                if (board[x][y] == 2) {
                    Window.out.print("O", 75 + x * 150, 125 + y * 150);
                }
            }
        }
        Window.frame();
    }

}

class Move {
    int x;
    int y;
    
    public Move(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
