package com.end507.project1.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.end507.project1.io.FileReader;
import com.end507.project1.shared.SharedClass;

/**
 * 
 * @author ceyhunkarimov
 *	This class is driver method for controlling inter-clusters operations 
 *	Type : Model according to MVC
 */
public class ClusterEnviroment {
	/**
	 * List of data points
	 */
	private List<HouseholdObject> pointsList;
	/**
	 *  List of centroids.Each centroid consists of List of doubles, to indicate the vector elements
	 */
	private List<List<Double>> centroids;
	/**
	 * Clusters in environment  , there is k number of clusters
	 */
	private List<Cluster> clusters = new ArrayList<>();
	/**
	 * The hashmap that holds the objective value - centroid pair, before swapping values - only one pair
	 */
	private HashMap<Double, List<List<Double>>> objectiveValuesBeforeSwap = new HashMap<Double, List<List<Double>>>();
	/**
	 * The hashmap that holds the objective value - centroid pairs, after swapping values - can be several pairs
	 */
	private HashMap<Double, List<List<Double>>> objectiveValuesAfterSwap = new HashMap<Double, List<List<Double>>>();
	/**
	 * After initial centoids randomly created, copy them to give StandardKMeans function , and at the end compare them
	 */
	private List<List<Double>> copyOfInitialCentroids;
	/**
	 * @param kNumber - given by user
	 * Constructor of class
	 */
	public ClusterEnviroment(int kNumber){
		// read all points from file
		initializePoints();
		//assign k value 
		SharedClass.K_VALUE = kNumber;
		//assign cluster size
		SharedClass.CLUSTER_SIZE = pointsList.size()/ SharedClass.K_VALUE;
		SharedClass.POINT_COUNT_TO_BE_SWAPPED = SharedClass.CLUSTER_SIZE/SharedClass.CLUSTER_SWAP_RATIO;
		// normalize data set so that max vector element = 1, min vector element = 0
		normalizeDataSet();
		// create randomly K centroids
		initializeRandomCentroids();
		initializeClusters(centroids);
	}

	/**
	 * Standard version of kmeans - once converged, stops at local optima
	 */
	public void startStandardKMeans(){
		int iterationNo = 1;
		boolean isItTimeToConverge = false;
		// initialize centroids variable as its initial value, so that it could be compared with heuristic version of KMeans
		centroids = copyOfInitialCentroids;
		initializeClusters(centroids);
		// stop when the cluster converges or iteration reaches max_iteration_no
		while(!isItTimeToConverge && iterationNo < SharedClass.MAX_ITERATION_TO_CONVERGE){
			System.out.println("StandardKMeans Iteration- " + iterationNo + " started...");
			//For all points, 
			for(HouseholdObject hObj : pointsList){
				//calculate point's distance to all centroids
				hObj.calculatePointDistancesToAllCentroids(centroids);
				// chose the best one, as they are sorted
				int clusterIndex = hObj.getDistancesToAllCentroids().get(0).getCentroidIndex();
				Cluster bestCluster = clusters.get(clusterIndex);			
				// give that cluster command to locate this point 
				bestCluster.locateNewPointAsStandardKMeans(hObj);
			}
			// calculate tempCentroids - new centroid values
			List<List<Double>> tempCentroids = new ArrayList<List<Double>>();
			for(Cluster cl: clusters){
				List<Double> newCentroid =  cl.calculateNewCentroid(true);
				tempCentroids.add(newCentroid);
			}
			isItTimeToConverge = true;
			// check if tempCentroid - new centroid values - is eqaul to centroid - old centroid values
			for(int i = 0; i < centroids.size() ; i++){
				List<Double> previousCentroid = centroids.get(i);
				List<Double> newCentdroid = tempCentroids.get(i);
				// constraint the double precision to 2 floating point digits
				DecimalFormat df = new DecimalFormat("#.00");
				if(!df.format(previousCentroid.get(0)).equals(df.format(newCentdroid.get(0))) ||
						!df.format(previousCentroid.get(1)).equals(df.format(newCentdroid.get(1))) ||
						!df.format(previousCentroid.get(2)).equals(df.format(newCentdroid.get(2)))){
					isItTimeToConverge = false;
					break;
				}
			}
			// assign new centroid values
			centroids = tempCentroids;
			System.out.println("StandardKMeans Iteration- " + iterationNo + " finished!!!\n");
			iterationNo ++;
			// if convergence is reached, no not reset clusters, as we will need that
			if(!isItTimeToConverge && iterationNo <  SharedClass.MAX_ITERATION_TO_CONVERGE){
				for(Cluster cl : clusters){
					cl.resetClusterObjects();
				}
			}
		}
	}


	/**
	 * Compare the results of StandardKMEans and EqualClusterKMeans with swapping
	 */
	public void compareResults(){
		double kmeansObjectiveVal = 0;
		for(Cluster cl : clusters){
			kmeansObjectiveVal = kmeansObjectiveVal + cl.calculateObjectiveFunction();
		}
		System.out.println("Standard KMeans objective value is: " + kmeansObjectiveVal + " for centroid - " + centroids) ;
		System.out.println("vs");
		// SharedClass.KNOWN_BEST is hashmap that hold only one - the best value and centroid pair , so get its first element
		System.out.println("Heuristic KMeans objective value is: " + SharedClass.KNOWN_BEST.keySet().toArray()[0] 
								+ " for centroid - "  + SharedClass.KNOWN_BEST.values().toArray()[0]);	
	}



	/**
	 * Modified version of KMeans, so that all clusters are equal in their size
	 */
	public void startEqualClusterKMeans(){
		int iterationNo = 1;		
		boolean isItTimeToConverge = false;
		// stop when the cluster converges or iteration reaches max_iteration_no
		while(!isItTimeToConverge && iterationNo < SharedClass.MAX_ITERATION_TO_CONVERGE){
			System.out.println("EqualClusterKMeans Iteration- " + iterationNo + " started...");
			// For each point, calculate their distance to all centroids and ask that cluster to locate point if possible
			for(HouseholdObject hObj : pointsList){
				hObj.calculatePointDistancesToAllCentroids(centroids);
				locatePointToAppropriateCluster(hObj);			
			}
			//Calculate new centroid values
			List<List<Double>> tempCentroids = new ArrayList<List<Double>>();
			for(Cluster cl: clusters){
				List<Double> newCentroid =  cl.calculateNewCentroid(false);
				tempCentroids.add(newCentroid);
			}
			isItTimeToConverge = true;
			// Check if new centroid is equal to old centroid values,so to converge the cluster
			for(int i = 0; i < centroids.size() ; i++){
				List<Double> previousCentroid = centroids.get(i);
				List<Double> newCentdroid = tempCentroids.get(i);
				DecimalFormat df = new DecimalFormat("#.00");
				if(!df.format(previousCentroid.get(0)).equals(df.format(newCentdroid.get(0))) ||
						!df.format(previousCentroid.get(1)).equals(df.format(newCentdroid.get(1))) ||
						!df.format(previousCentroid.get(2)).equals(df.format(newCentdroid.get(2)))){
					isItTimeToConverge = false;
					break;
				}
			}
			centroids = tempCentroids;
			System.out.println("EqualClusterKMeans Iteration- " + iterationNo + " finished!!!\n");
			iterationNo ++;
			// if the cluster converged do not reset the clusters, as we will need them
			if(!isItTimeToConverge && iterationNo <  SharedClass.MAX_ITERATION_TO_CONVERGE){
				for(Cluster cl : clusters){
					cl.resetClusterObjects();
				}
			}
		}
		System.out.println("EqualClusterKMeans converged!!!");
	}

	/**
	 * After EqualClusterKMeans is converged, swap points and run it again
	 */
	public void startSwapAndEqualKMeans(){
		for(int i = 0; i < SharedClass.MAX_ITERATION_WITH_SWAP ; i++){
			// get the even value of cluster numbers - if cluster number is 5 -> get 4, if 6 -> get 6
			int clusterSizeForSwapping = (clusters.size()/2) * 2;
			for(int clusterNo = 0; clusterNo< clusterSizeForSwapping; clusterNo  = clusterNo + 2){
				// get out points from cluster i
				List<HouseholdObject> retrievedObjects1 = clusters.get(clusterNo).getOutPointsForSwap();
				// get out points from cluster i+1
				List<HouseholdObject> retriededObjects2 = clusters.get(clusterNo+1).getOutPointsForSwap();
				// swap the points
				clusters.get(clusterNo).locateSwappedPoints(retriededObjects2);
				clusters.get(clusterNo+1).locateSwappedPoints(retrievedObjects1);
			}
			centroids = new ArrayList<List<Double>>();
			//calculate new cluster centers
			for(Cluster cl: clusters){
				List<Double> newCentroid =  cl.calculateNewCentroid(false);
				centroids.add(newCentroid);
				cl.resetClusterObjects();
			}
			//rerun the EqualClusterKMeans
			startEqualClusterKMeans();
			calculateObjectiveFunctionForClusters(false);
		}
	}


	/**
	 * @param isBeforeSwap - boolean value indicating, is function is called before or after swapping is done
	 * Order each cluster to calculate its local objective value, and calculate global objectiveValue
	 */
	public void calculateObjectiveFunctionForClusters(boolean isBeforeSwap){
		Double globalObjectiveVal = 0.0;
		for(Cluster cl : clusters){
			Double currentObjectiveVal = cl.calculateObjectiveFunction();
			globalObjectiveVal = globalObjectiveVal + currentObjectiveVal;
		}
		if(isBeforeSwap){
			objectiveValuesBeforeSwap = new HashMap<Double, List<List<Double>>>();
			objectiveValuesAfterSwap = new HashMap<Double, List<List<Double>>>();
			objectiveValuesBeforeSwap.put(globalObjectiveVal, centroids);
		}
		else{
			objectiveValuesAfterSwap.put(globalObjectiveVal,centroids);
		}
		System.out.println("Objective value is: " + globalObjectiveVal);

	}

	/**
	 * After swapping max iterations is finished, evaluate their results and select the best one
	 */
	public void evaluateSwappingResults(){
		System.out.println("Result before swap:");
		System.out.println("Objective function value is: " + objectiveValuesBeforeSwap.keySet().toArray()[0] + " for centroid - "
				+ objectiveValuesBeforeSwap.get(objectiveValuesBeforeSwap.keySet().toArray()[0]) + "\n");

		System.out.println("Results after swap:");
		for(Entry<Double, List<List<Double>>> entry: objectiveValuesAfterSwap.entrySet()){
			System.out.println("Objective function value is: " + entry.getKey() + " for centroid - " 
					+ entry.getValue() );
		}
		objectiveValuesAfterSwap.putAll(objectiveValuesBeforeSwap);
		System.out.println("The best known value is: ");
		List<Double> keyList = new ArrayList<>(objectiveValuesAfterSwap.keySet()) ;
		Collections.sort( keyList);
		System.out.println(keyList.get(0) + " for centroid - " + objectiveValuesAfterSwap.get(keyList.get(0)));
		SharedClass.KNOWN_BEST.put(keyList.get(0), objectiveValuesAfterSwap.get(keyList.get(0)));
	}

	/**
	 * @param hObj - object that wants to be located in cluster
	 * In EqualClusterKMeans,the point is sent to this function, so to ask best cluster to locate the point in it if possible
	 */
	private synchronized void locatePointToAppropriateCluster(HouseholdObject hObj){
		// For each centroid from the best one
		for(CentroidAndDistanceTriple cdp: hObj.getDistancesToAllCentroids()){
			int clusterIndex = cdp.getCentroidIndex();
			Cluster bestCluster = clusters.get(clusterIndex);
			// ask cluster to locate point in it, if possible
			HouseholdObject returnedObject =  bestCluster.locateNewPoint(hObj);
			// OK, point is located
			if(returnedObject == null){
				return;
			}
			// returned point is same as sent one, so it cannot be located , -> try the next best cluster
			else if(returnedObject.equals(hObj)){
				continue;
			}
			// Point is located but, another point is get out from the cluster,-> locate that point to appropriate cluster
			else{
				locatePointToAppropriateCluster(returnedObject);
				return;
			}
		}
	}


	/**
	 * @param cntrds - list of centroids
	 * Assign each cluster its centroid and max_cluster_size
	 */
	private void initializeClusters(List<List<Double>> cntrds){
		System.out.println("Initializing clusters...");
		clusters = new ArrayList<>();
		for(int i = 0; i < SharedClass.K_VALUE ; i++){
			Cluster cluster = new Cluster(SharedClass.CLUSTER_SIZE, cntrds.get(i));
			clusters.add(cluster);
		}
		System.out.println("Clusters successfully initialized!!!\n");
	}



	/**
	 * Normalize data set so that max vector element is 1 min vector elment is 0, with formula (x - x(min)) / ( x(max) - x(min) )
	 */
	private void normalizeDataSet(){
		System.out.println("Normalizing data set...");
		double field1Difference = HouseholdObject.GLOBAL_ACTIVE_POWER_MAX_VAL - HouseholdObject.GLOBAL_ACTIVE_POWER_MIN_VAL;
		double field2Difference = HouseholdObject.GLOBAL_REACTIVE_POWER_MAX_VAL - HouseholdObject.GLOBAL_REACTIVE_POWER_MIN_VAL;
		double field3Difference = HouseholdObject.VOLTAGE_MAX_VAL - HouseholdObject.VOLTAGE_MIN_VAL;

		for(HouseholdObject obj: pointsList){
			double tempField1 = (obj.getGlobalActivePower() - obj.GLOBAL_ACTIVE_POWER_MIN_VAL) / field1Difference;
			double tempField2 = (obj.getGlobalReactivePower() - obj.GLOBAL_REACTIVE_POWER_MIN_VAL) / field2Difference;
			double tempField3 = (obj.getVoltage() - obj.VOLTAGE_MIN_VAL) / field3Difference;
			obj.setGlobalActivePower(tempField1);
			obj.setGlobalReactivePower(tempField2);
			obj.setVoltage(tempField3);
		}
		HouseholdObject obj = pointsList.get(0);
		System.out.println("Data set normalized successfully!!!\n");
	}

	/**
	 * Read all points from file and construct List of HouseholdObjects
	 */
	private void initializePoints(){
		System.out.println("Reading file...");
		pointsList =  FileReader.readInputFile(SharedClass.INPUT_PATH);
		System.out.println("File successfully read !!!\n");
	}

	/**
	 * Randomly create K centroids
	 */
	private void initializeRandomCentroids(){
		centroids = new ArrayList<List<Double>>();
		System.out.println("Initialing random centroids...");
		for(int i = 0;i< SharedClass.K_VALUE; i++){
			List<Double> tempOneCentroid = new ArrayList<>();
			for(int j = 0; j < SharedClass.VECTOR_LENGTH ; j++){
				tempOneCentroid.add( SharedClass.randomInRange(0.0, 1.0) );
			}
			centroids.add(tempOneCentroid);
		}
		// Keep the copy of initial centroids, it will be needed when calling StandardKMeans, in order compare results
		copyOfInitialCentroids = centroids;
		System.out.println("Random centroids initialied!!!\n");
	}

}
