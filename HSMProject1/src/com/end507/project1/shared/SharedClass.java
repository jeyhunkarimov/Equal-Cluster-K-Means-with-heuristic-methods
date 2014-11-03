package com.end507.project1.shared;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author ceyhunkarimov
 *	This class is globally shared class, to keep only static variables.
 */
public class SharedClass {
	/**
	 * The  location file that is processed
	 */
	public static Path INPUT_PATH;
	/**
	 * Hashmap that holds,best pair of objective function value and ist corresponding centroid
	 */
	public static HashMap<Double, List<List<Double>>> KNOWN_BEST;
	/**
	 * Number of clusters
	 */
	public static int K_VALUE;
	/**
	 * 
	 */
	public static int CLUSTER_SIZE;
	/**
	 * This variable is used to generate random numbers
	 */
	private static Random RANDOM;
	/**
	 * The number of feature elements in vector
	 */
	public static int VECTOR_LENGTH;
	/**
	 * When swapping points from cluster to another one, the number of points is specified here
	 */
	public static int POINT_COUNT_TO_BE_SWAPPED;
	/**
	 * Max number of iterations after convergence to get from local optima with swapping
	 */
	public static int MAX_ITERATION_WITH_SWAP;
	/**
	 * Max number of iterations to converge
	 */
	public static int MAX_ITERATION_TO_CONVERGE;
	/**
	 * The ratio 1/x that is multiplied with cluster size, to determine the swapping points upper bound
	 */
	public static int CLUSTER_SWAP_RATIO;
	/**
	 * If user not wants to output to console but to file, change this variable to true, else false, default is false
	 */
	public static boolean WRITE_OUTPUTS_TO_FILE;

	// default values when initializing the static variables
	static{
		INPUT_PATH = Paths.get("./data/input", "household_power_consumption.txt");
		RANDOM =  new Random();
		VECTOR_LENGTH = 3;
		MAX_ITERATION_WITH_SWAP = 4;
		KNOWN_BEST = new HashMap<Double, List<List<Double>>>();
		MAX_ITERATION_TO_CONVERGE = 100;
		CLUSTER_SWAP_RATIO = 3;
		WRITE_OUTPUTS_TO_FILE = false;
	}
	
	/**
	 * @param min 
	 * @param max
	 * @return random double number between min and max
	 */
	public static double randomInRange(double min, double max) {
		  double range = max - min;
		  double scaled = RANDOM.nextDouble() * range;
		  double shifted = scaled + min;
		  return shifted; // == (rand.nextDouble() * (max-min)) + min;
		}
	
    /**
     * @param start
     * @param end
     * @return random int number between start and end
     */
    public static int randomInRange(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }


}
