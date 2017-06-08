package neuralnetwork;

import java.util.ArrayList;

import ridesharing.Window;

public class FlappyBird {
	
	static double mutationRate = 0.1;
	static double mutationRange = 0.2;
	
	public static void main(String[] args) {
		
		// generate a list of Genome objects
		int population = 100;
		
		ArrayList <Genome> genomes = new ArrayList <Genome> ();
		while (genomes.size() < population) {
			genomes.add(new Genome(2, 2, 1));
		}
		
		// repeatedly
		while (true) {
			generation++;
			
			// create a new FlappyBird object with that list
			FlappyBird fb = new FlappyBird(genomes);
			while (fb.canStep()) {
				fb.step();
			}
			ArrayList <Genome> best = fb.getBest(10);
			
			// clear the list
			genomes.clear();
			
			// Add 20 new genomes to the list
			for (int i = 0 ; i < 20 ; i++) {
				genomes.add(new Genome(2, 2, 1));
			}
			
			// breed a bunch of new genomes using the best ones
			for (int i = 0 ; i < best.size() ; i++) {
				for (int j = 0 ; j < i + 1 ; j++) {
					
					if (genomes.size() < population) {
						genomes.add(new Genome(best.get(i), best.get(j), mutationRate, mutationRange));
					}
					
				}
			}
		}
	}
	
	private ArrayList<Genome> getBest(int n) {
		ArrayList <Genome> best = new ArrayList <Genome> ();
		
		// Bubble sort the birds into order by score
		for (int i = 0 ; i < birds.size() ; i++) {
			for (int j = 0 ; j < birds.size() - 1 ; j++) {
				
				if (birds.get(j).genome.score < birds.get(j + 1).genome.score) {
					Bird temp = birds.get(j);
					birds.set(j, birds.get(j + 1));
					birds.set(j + 1, temp);
				}
				
			}
		}
		
		for (int i = 0 ; i < n ; i++) {
			best.add(birds.get(i).genome);
		}
		
		return best;
	}

	private boolean canStep() {
		// go to every bird
		for (Bird b : birds) {
			// if you find an alive one
			if (! b.dead) {
				return true;
			}
		}
		return false;
	}

	static int generation = 0;
	
	ArrayList <Bird> birds = new ArrayList <Bird> ();
	ArrayList <Pipe> pipes = new ArrayList <Pipe> ();
	int score = 0;
	
	public FlappyBird(ArrayList <Genome> genomes) {
		// Go to each genome in the list
		for (Genome g : genomes) {
			birds.add(new Bird(g));
		}
	}
	
	public void step() {
		Window.out.background("light blue");
		
		// Every 50 steps, add a new pipe
		if (score % 80 == 0) {
			pipes.add(new Pipe());
		}
		
		Pipe closest = null;
		// Go to each pipe that has an x position greater than 100
		for (Pipe p : pipes) {
			p.draw();
			p.move();
			
			if (p.x > 75) {
				
				// Check if this is closer than my closest pipe
				if (closest == null || p.x < closest.x) {
					closest = p;
				}
				
			}
		}
		
		// draw everything
		for (Bird b : birds) {
			if (! b.dead && closest != null) {
				b.setClosest(closest);
				b.draw();
				b.move();
			}
		}
		
		score++;
		
		Window.out.color("black");
		Window.out.print(score / 200, 10, 50);
		Window.out.print("Generation " + generation, 10, 100);
		Window.frame(1);
	}

	/**
	 * Represents a single bird
	 */
	class Bird {
		double y = Window.height() / 2;
		int dy = 0;
		Genome genome;
		private Pipe closest;
		boolean dead = false;
		
		public Bird(Genome genome) {
			this.genome = genome;
		}
		
		public void draw() {
			Window.out.color("yellow");
			Window.out.oval(100, (int) y, 40, 30);
		}
		
		public void setClosest(Pipe p) {
			closest = p;
		}
		
		public void move() {
			y = y + dy;
			dy = dy + 1;
			
			double[] output = genome.network.input(new double[] { toInput() , closest.toInput() });
			if (output[0] > 0.5) {
				flap();
			}
			
			// Check if the bird died
			if (y < 15 || y > Window.height() - 15 || closest.isTouching(this)) {
				dead = true;
				genome.score = score;
			}
		}
		
		public void flap() {
			dy = -10;
		}
		
		public double toInput() {
			return y / Window.height();
		}
	}
	
	/**
	 * Represents a pair of pipes
	 */
	class Pipe {
		int x = Window.width() + 25;
		double gap = Window.random(100, Window.height() - 100);
		
		public void draw() {
			Window.out.color("green");
			Window.out.rectangle(x, Window.height() / 2, 50, Window.height());
			Window.out.color("light blue");
			Window.out.rectangle(x, (int) gap, 50, 100);
		}
		
		public boolean isTouching(Bird bird) {
			if (Math.abs(x - 100) < 45 && Math.abs(bird.y - gap) > 40) {
				return true;
			}
			return false;
		}

		public void move() {
			x = x - 3;
		}
		
		public double toInput() {
			return gap / Window.height();
		}
	}
}
