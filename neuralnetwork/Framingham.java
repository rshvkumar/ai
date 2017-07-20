package neuralnetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Framingham {
	
	ArrayList <HashMap<String, Double>> examples;
	
	public Framingham(String file) {
		// Initialize the list of examples
		examples = new ArrayList <HashMap <String, Double>> ();
		
		try {
			Scanner s = new Scanner(new File(file));
			
			// First line has the labels for the fields
			String label = s.nextLine();
			String[] column = label.split(",");
			
			// While there is data to read
			while (s.hasNextLine()) {
				// Read one training example
				String example = s.nextLine();
				String[] data = example.split(",");
				
				// Create a HashMap of column value to data point
				HashMap <String, Double> map = new HashMap <String, Double> ();
				
				// Go to every index in the data
				for (int i = 0 ; i < data.length ; i++) {
					// If the column is filled in for this data point
					if (data[i].length() > 0) {
						// Parse the value and put it into the hashmap
						map.put(column[i], Double.parseDouble(data[i]));
					}
				}
				
				// Add the map to my list of examples
				examples.add(map);
			}
		}
		catch (FileNotFoundException e) {
			System.err.println("Could not find CSV file at " + file);
		}
	}
	
	public HashMap <String, Double> getExample(int index) {
		return examples.get(index);
	}
	
	public int size() {
		return examples.size();
	}
}
