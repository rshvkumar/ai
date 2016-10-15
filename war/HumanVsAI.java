package war;

import apcs.Window;

public class HumanVsAI {

	public static void main(String[] args) {
		Window.size(500, 600);
		Window.frame();
		
		War game = new War("mountain.txt");
		int scale = Window.width() / game.getSize();
		
		while (true) {
			game.draw();
			
			// Computer's turn
			if (game.player == 2) {
				long start = System.currentTimeMillis();
				int depth = 1;
				Move bestMove = null;
				
				while (System.currentTimeMillis() - start < 5000) {
					bestMove = game.getBestMove(depth);
					game.draw(bestMove);
					System.out.println("Did a depth " + depth + " search.");
					depth++;
				}
				
				game.make(bestMove);
			}
			
			// Human presses the keys for playing a move
			else if (Window.key.pressed("d") || Window.key.pressed("b")) {
				
				// Get the x, y coordinate on the board where a move should be played
				int x = Window.mouse.getX() / scale;
				int y = Window.mouse.getY() / scale;
				
				// If we have a valid x, y coordinate
				if (game.isValid(x, y)) {
					// Check if the intention was to drop a piece
					if (Window.key.pressed("d") && game.canDrop(x, y)) {
						game.drop(x, y);
					}
					// Check if the intention was to blitz
					else if (Window.key.pressed("b") && game.canBlitz(x, y)) {
						game.blitz(x, y);
					}
				}
			}
		}
	}

}
