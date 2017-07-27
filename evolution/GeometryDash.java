package neuralnetwork;

import java.util.ArrayList;
import java.util.Comparator;

import apcs.Window;

public class GeometryDash {
	
	static int generation = 0;
	static double mutationRate = 0.1;
	static double mutationRange = 0.2;
	int population = 200;
	
	public static void main(String[] args) {
		
		// generate a list of Genome objects
		int population = 200;
		int elites = population / 3;
		int seeds = population / 6;

		ArrayList <Genome> genomes = new ArrayList <Genome> ();
		while (genomes.size() < population) {
			genomes.add(createGenome());
		}

		// repeatedly
		while (true) {
			generation++;

			// create a new FlappyBird object with that list
			GeometryDash game = new GeometryDash(genomes);
			Window.frame();
			while (game.canStep()) {
				game.step();
			}
			ArrayList <Genome> best = game.getBest(elites);

			// clear the list
			genomes.clear();

			// Add some new genomes to the list
			for (int i = 0 ; i < seeds ; i++) {
				genomes.add(createGenome());
			}

			// breed a bunch of new genomes using the best ones
			for (int i = 0 ; i < best.size() && genomes.size() < population ; i++) {
				for (int j = 0 ; j < i + 1 ; j++) {

					if (genomes.size() < population) {
						genomes.add(new Genome(best.get(i), best.get(j), mutationRate, mutationRange));
					}
					else break;
				}
			}
		}
	}
	
	public static Genome createGenome() {
		return new Genome(4, 4, 1);
	}
	
	ArrayList <Player> players;
	ArrayList <Obstacle> obstacles;
	int steps = 0, lastStep = -50, obstacleRate = 50;
	double[] closest = { 1.0, 1.0, 1.0 };

	public GeometryDash() {
		Window.size(800, 500);
		
		// Initialize the player and list of obstacles
		players = new ArrayList <Player> ();
		players.add(new Player());
		obstacles = new ArrayList <Obstacle> ();
	}
	
	public GeometryDash(ArrayList <Genome> genomes) {
		Window.size(800, 500);
		
		// Initialize the player and list of obstacles
		players = new ArrayList <Player> ();
		for (Genome genome : genomes)
			players.add(new Player(genome));
		population = genomes.size();
			
		obstacles = new ArrayList <Obstacle> ();
	}
	
	/**
	 * Steps the game forward by one frame.
	 */
	public void step() {
		draw();
		move();
		Window.frame(1);
		steps++;
	}
	
	public boolean canStep() {
		for (Player p : players) {
			if (p.isAlive()) 
				return true;
		}
		return false;
	}
	
	/**
	 * Draws the game scene.
	 */
	public void draw() {
		drawBackground();
		for (Player p : players) {
			if (p.isAlive())
				p.draw();
		}
		
		for (Obstacle o : obstacles) {
			o.draw();
		}
	}
	
	public ArrayList <Genome> getBest(int n) {
		players.sort(new Comparator <Player> () {

			@Override
			public int compare(Player p1, Player p2) {
				return (int) (p1.genome.score - p2.genome.score);
			}
			
		});
		
		ArrayList <Genome> best = new ArrayList <Genome> ();
		for (int i = 0 ; i < n ; i++) {
			best.add(players.get(i).genome);
		}
		
		return best;
	}
	
	/**
	 * Draws the background of the game.
	 */
	private void drawBackground() {
		Window.out.background("light blue");
		Window.out.color("green");
		Window.out.rectangle(400, 450, 800, 100);
		Window.out.color("white");
		Window.out.print("Generation: " + generation, 25, 470);
		Window.out.print("Population: " + population, 25, 490);
	}
	
	public void move() {
		for (Player p : players) {
			if (p.isAlive())
				p.move();
		}
		
		for (Obstacle o : obstacles) {
			o.move();
			
			for (Player p : players) {
				if (p.isAlive() && o.isTouching(p)) {
					p.die();
					p.setScore(steps);
					population--;
				}
			}
		}
		
		int k = 0;
		
		for (int i = 0 ; i < obstacles.size() ; i++) {
			if (obstacles.get(i).isGone()) {
				obstacles.remove(i);
				i--;
			}
			else if (obstacles.get(i).isComing() && k < closest.length) {
				closest[k] = obstacles.get(i).toInput();
				k++;
			}
		}
		
		// Every 50 frames
		if (Window.rollDice(15) == 1 && steps - lastStep > 50) {
			lastStep = steps;
			obstacles.add(new Obstacle());
			
			if (Window.rollDice(2) == 1) {
				obstacles.add(new Obstacle(55));
			}
			if (Window.rollDice(3) == 1) {
				obstacles.add(new Obstacle(110));
			}
		}
	}

	/**
	 * Represents the player
	 */
	class Player {
		static final int x = 100;
		int y = 375;
		int dy = 0;
		int angle = 0;
		boolean canJump = true;
		boolean canJumpAgain = true;
		boolean alive = true;
		
		Genome genome;
		
		int r = Window.random(150, 255);
		
		public Player() {}

		public void setScore(int steps) {
			this.genome.score = steps;
		}

		public Player(Genome genome) {
			this.genome = genome;
		}
		
		public void draw() {
			Window.out.color(r, 0, 0);
			Window.out.rectangle(x, y, 50, 50, angle);
		}

		public boolean shouldJump() {
			if (genome == null)
				return Window.key.pressed("space");
			
			double[] output = genome.network.input(new double[] { (375.0 - y) / 300.0, closest[0], closest[1] });
			return output[0] > 0.5;
		}
		
		public void move() {
			boolean jump = shouldJump();
			
			if (jump && canJump) {
				dy = -15;
				canJump = false;
			}
			else if (jump && dy >= 0 && canJumpAgain) {
				dy = -15;
				canJumpAgain = false;
			}
			
			y = y + dy;
			dy = dy + 1;
			
			if (y >= 375) {
				y = 375;
				dy = 0;
				angle = 0;
				canJump = true;
				canJumpAgain = true;
			}
			else {
				angle += 6;
			}
		}
		
		public boolean isAlive() {
			return alive;
		}
		
		public void die() {
			alive = false;
		}
	}
	
	/**
	 * Represents the obstacles
	 */
	class Obstacle {
		int x = Window.width();
		static final int y = 400;
		
		public Obstacle() {
			x = Window.width();
		}
		
		public double toInput() {
			return 1.0 * x / Window.width();
		}

		public boolean isGone() {
			return x < -50;
		}

		public boolean isComing() {
			return x > Player.x - 25;
		}
		
		public Obstacle(int offset) {
			x = Window.width() + offset;
		}

		public void draw() {
			Window.out.color("gray");
			Window.out.polygon(x, y, x + 50, y, x + 25, y - 50);
		}

		public void move() {
			x = x - 5;
		}
		
		public boolean isTouching(Player p) {
			if (x - 10 <= Player.x && Player.x <= x + 60 && p.y >= y - 50) {
				return true;
			}
			return false;
		}
	}
}
