package war;

import apcs.Window;

public class AIvsAI {
	
	public static void main(String[] args) {
		Window.size(500, 600);
		Window.frame();
		
		War game = new War("test2.txt");
		
		while (true) {
			game.draw();
			long start = System.currentTimeMillis();
			int depth = 3;
			Move bestMove = null;
			
			while (System.currentTimeMillis() - start < 5000) {
				bestMove = game.getBestMove(depth);
				game.draw(bestMove);
				System.out.println("Did a depth " + depth + " search.");
				depth++;
			}
			
			game.make(bestMove);
			game.draw();
			Window.sleep(1000);
		}
	}

}
