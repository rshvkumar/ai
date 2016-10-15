package connect4;

import apcs.Window;

public class HumanAI {

	public static void main(String[] args) {
		Connect4 game = new Connect4();
		int humanPlayer = 2;
		
		while (true) {
			game.draw();
			
			if (game.player == humanPlayer) {
				game.active = Window.mouse.getX() / game.size;
				if (Window.mouse.clicked()) {
					Window.mouse.waitForRelease();
					int x = Window.mouse.getX() / game.size;
					if (game.canDrop(x)) {
						game.drop(x);
					}
				}
			}
			else {
				long start = System.currentTimeMillis();
				int depth = 10;
				int maxDepth = 30;
				int best = game.getBestMove(depth);
				System.out.println("Initial search of depth " + depth);
				while (System.currentTimeMillis() - start < 3000 && depth < maxDepth) {
					depth++;
					game.active = best;
					game.draw();
					best = game.getBestMove(depth);
					System.out.println("Did a depth " + depth + " search.");
				}
				game.drop(best);
				
			}
			
			if (game.getWinner() != 0) {
				game.draw();
				Window.sleep(1000);
				game = new Connect4();
			}
		}
	}

}
