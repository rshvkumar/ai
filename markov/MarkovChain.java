import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class MarkovChain {
	
	// 		 word -> frequency table of next words
	HashMap <String, HashMap<String, Integer>> markov = new HashMap <String, HashMap<String, Integer>> ();
	String start = "_start_";
	String end = "_end_";
	
	public MarkovChain() {
		// Put a frequency table for the start state.
		markov.put(start, new HashMap <String, Integer> ());
	}
	
	public ArrayList <String> generate() {
		ArrayList <String> walk = new ArrayList <String> ();
		
		// Get a random key from the start state's frequency table
		String startState = randomKey( markov.get(start) );
		String currentState = startState;
		
		while (currentState != end) {
			walk.add(currentState);
			// If this word is not connected to any other word.
			if (! markov.containsKey(currentState)) {
				break;
			}
			else {
				currentState = randomKey( markov.get(currentState) );
			}
		}
		
		return walk;
	}
	
	public void addFile(String path) {
		String content = getFile(path);
		
		// Sanitize the content
		content = content.replaceAll("\\s+", " ");
		content = content.replaceAll("\\,", " ,");
		
		String[] sentence = content.split("[\\.\\!\\?]");
		
		for (String s : sentence) {
			add(s.trim());
		}
	}
	
	/**
	 * A reusable method to get the contents of a file.
	 * @param path
	 * @return
	 */
	public String getFile(String path) {
		// Create a file object, a scanner, and a StringBuilder
		File f = new File(path);
		try {
			Scanner s = new Scanner(f);
			StringBuilder builder = new StringBuilder();
			
			// While I can read another line from the file
			while (s.hasNextLine()) {
				// Add the line to the StringBuilder
				String line = s.nextLine();
				builder.append(line);
				
				// Add a newline character
				builder.append('\n');
			}
			
			return builder.toString();
		} 
		catch (FileNotFoundException e) {
			System.err.println("File " + path + " not found.");
		}
		return "";
	}
	
	public void add(String sentence) {
		String[] word = sentence.split(" ");
		
		// Prevent empty sentences from being added
		if (word.length == 0) {
			return;
		}
		
		// Add a transition from the start state to the first word of the sentence
		addTransition(start, word[0]);
		
		// For all adjacent words in the array of words
		for (int i = 0 ; i < word.length - 1 ; i++) {
			addTransition(word[i], word[i + 1]);
		}
		
		// Add a transition from the last word to the end state.
		addTransition(word[word.length - 1], end);
	}
	
	/**
	 * Adds a transition between state "a" and state "b".
	 * @param a - initial state
	 * @param b - next state
	 */
	public void addTransition(String a, String b) {
		// Is there a frequency table for state "a"? If not, make one.
		// Guarantee that there is a frequency table for state a
		if (! markov.containsKey(a)) {
			markov.put(a, new HashMap <String, Integer> ());
		}
		
		// Get the frequency table for state "a"
		HashMap <String, Integer> frequency = markov.get(a);
		
		// If "b" is in the frequency table for state "a",
		if (frequency.containsKey(b)) {
			// Add 1 to the frequency of "b"
			int value = frequency.get(b);
			value = value + 1;
			frequency.put(b, value);
		}
		// Otherwise
		else {
			// Add "b" corresponding to 1
			frequency.put(b, 1);
		}
	}
	
	/**
	 * Returns a weighted random key from the frequency table.
	 * @param frequency
	 * @return
	 */
	private String randomKey(HashMap<String, Integer> frequency) {
		// Step 1 - figure out the actual size
		int size = 0;
		// for every key in the frequency table
		for (String key : frequency.keySet()) {
			// add it's value to size
			size += frequency.get(key);
		}
		
		int random = (int) (Math.random() * size);
		
		int count = 0;
		for (String key : frequency.keySet()) {
			// If the random number is between count and count + value
			if ( random >= count && random < count + frequency.get(key) ) {
				return key;
			}
			count = count + frequency.get(key);
		}
		// This should never be reached, but it gives Java a "guarantee"
		// that something will be returned.
		return null;
	}
}
