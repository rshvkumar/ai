package reversi;

import apcs.Window;

public class HumanVsHuman {

	public static void main(String[] args) {
		Reversi game = new Reversi();
		
		while (true) {
			game.draw();
			game.getClick();
		}
	}

}
