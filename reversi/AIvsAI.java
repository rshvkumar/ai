package reversi;

import apcs.Window;

public class AIvsAI {

	public static void main(String[] args) {
		Reversi game = new Reversi();
		
		while (true) {
			game.draw();
			
			long start = System.currentTimeMillis();
			int depth = 8;
			ReversiMove move = game.getBestMove(depth);
			while (depth < 30 && System.currentTimeMillis() - start < 2000) {
				depth++;
				move = game.getBestMove(depth);
				System.out.println("Did a depth " + depth + " search.");
				game.highlight(move);
				game.draw();
			}
			game.perform(move);
			game.highlight(move);
			Window.sleep(1000);
		}
	}

}
