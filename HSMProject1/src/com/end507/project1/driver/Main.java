package com.end507.project1.driver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.end507.project1.model.ClusterEnviroment;
import com.end507.project1.shared.SharedClass;

/**
 * 
 * @author ceyhunkarimov
 *	Main class of the application. 
 *	All commands are given from here
 *	Type: Controller according to MVC
 */
public class Main {

	public static void main(String args[]){
		
		Scanner scanner = new Scanner(System.in);			
		//read the K -cluster number from the console
		System.out.println("Please enter the K number: ");		
		int k = scanner.nextInt();	
		scanner.close();
		// If user would like to output all results to outputfile, which is located in project_dir/data/output/output.txt
		if(SharedClass.WRITE_OUTPUTS_TO_FILE){
			PrintStream out = null;
			try {
				out = new PrintStream(new FileOutputStream("./data/output/output.txt"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			System.setOut(out);
		}
		
		ClusterEnviroment ce = new ClusterEnviroment(k);	
		// heuristic kmeans start time taken from system
	    long hkmeansStart = System.currentTimeMillis();	
	    // EqualClusterKMeans started
		ce.startEqualClusterKMeans();
		//After it converges, calculate its objective function
		ce.calculateObjectiveFunctionForClusters(true);
		// Start swapping data points from one cluster to another, and check results
		ce.startSwapAndEqualKMeans();
		// Here heuristic version of kmeans ends, so take the time from system
	    long hkmeansStop = System.currentTimeMillis();
	    // From each iteration after swapping values, display all of them, and select the best one
		ce.evaluateSwappingResults();
		// Here begins standard kmeans with the initial centroid values, that EaualClusterKMeans started, so that we could compare
	    long skmeansStart = System.currentTimeMillis();
		ce.startStandardKMeans();
		//Standard KMeans stopped, so take system time
	    long skmeansStop = System.currentTimeMillis();
	    // compare objective function results , with standard and heuristic kmeans versions
		ce.compareResults();
		
		System.out.println("Time elapsed for Heuristic KMeans is: " + (hkmeansStop - hkmeansStart) + " sec");
		System.out.println("Time elapsed for Standard KMeans is: " + (skmeansStop - skmeansStart) + " sec");
	}
	

}
