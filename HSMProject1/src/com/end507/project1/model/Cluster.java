package com.end507.project1.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.end507.project1.shared.SharedClass;

/**
 * 
 * @author ceyhunkarimov
 *	In this class all methods and variables related with inner-cluster operations, are located .
 *	Type : Model according to MVC
 */

public class Cluster {
	/**
	 *  Because clusters are equal sized, their sizes have restriction as maxClusterSize
	 */
	private int maxClusterSize;
	/**
	 * The PointAndDistancePair is the HouseholdObject and its distance to centroid pair. Cluster needs to keep this pair in order 
	 * to better performance during addition or selection of its elements
	 */
	private List<PointAndDistancePair> clusterObjects = new ArrayList<>();


	/**
	 * The centroid of this cluster. It consists of Doubles. This size of this list is equal to size of feature vector.
	 */
	private List<Double> clusterCenter = new ArrayList<>();
	
	/**
	 * @param maxClusterSize
	 * @param centroid
	 * Constructor of class
	 */
	public Cluster(int maxClusterSize,List<Double> centroid){
		this.maxClusterSize = maxClusterSize;
		this.clusterCenter = centroid;
	}
	
	/**
	 * @param newPoint is the point that is selected this cluster to be placed
	 * When performing standard kmeans, there is no restriction over size of cluster, so as the new point comes, place it
	 */
	public synchronized void locateNewPointAsStandardKMeans(HouseholdObject newPoint){
		Double distance = newPoint.getDistancesToAllCentroids().get(0).getDistance();
		PointAndDistancePair newPair = new PointAndDistancePair(newPoint, distance);
		clusterObjects.add(newPair);
	}

	/**
	 * @param newPoint is the point that is selected this cluster to be placed
	 * @return HouseholdObject object, if return value is null, then the object is successfully placed; else if return value is
	 * 		different from newPoint, then newPoint is placed in this cluster, but some other point is removed and returned; else if 
	 * 		return value is equal to newPoint, then newPoint cannot be placed in this cluster.
	 * Method checks the newPoint's distance to this cluster,if cluster has enough place , then place the point,
	 * 	else if point's distance is less than the last neighbor of cluster centroid,
	 * 	then, it can be placed in this cluster, else it cannot.
	 */
	public synchronized HouseholdObject locateNewPoint(HouseholdObject newPoint){
		// get newPoint's all distances to all centroids
		List<CentroidAndDistanceTriple> allCentroids = newPoint.getDistancesToAllCentroids();
		CentroidAndDistanceTriple selectedOne = null;
		// find the spesific distance of newPoint to this cluster
		for(CentroidAndDistanceTriple onePair : allCentroids){
			if(onePair.getCentroid().equals(clusterCenter)){
				selectedOne = onePair;
				break;
			}
		}
		// if cluster has enough place, place the newPoint
		if(clusterObjects.size() < maxClusterSize){
			// create new pair, as newPoint and its distance to centroid
			PointAndDistancePair newPointPair = new PointAndDistancePair(newPoint, selectedOne.getDistance());
			clusterObjects.add(newPointPair);
			sortPointsAccordingToNeighborhood();
			return null;
		}
		// cluster do not have enough space
		else{
			double pointDistance = selectedOne.getDistance();
			// find the farest element to centroid
			double clustersFarElement = clusterObjects.get(clusterObjects.size()-1).getDistance();
			HouseholdObject returnObject = null;
			// if newPoint is nearer than the farest element
			if(pointDistance < clustersFarElement){
				// get farest element, in order to return value
				returnObject  = clusterObjects.get(clusterObjects.size()-1).getPoint();
				// create new pair, as newPoint is selected to be placed
				PointAndDistancePair newPointPair = new PointAndDistancePair(newPoint, selectedOne.getDistance());
				clusterObjects.set(clusterObjects.size()-1, newPointPair);
				// sort the cluster elements, in their distance to centroid. Because the array is always sorted, sorting the array,
				// is not cost effective function
				sortPointsAccordingToNeighborhood();
				return returnObject;
			}
			//newPoint could find any place in this cluster, so return it
			else{
				return newPoint;
			}
		}
	}
	
	/** 
	 * @return the List of points that are supposed to be placed in other cluster for swapping
	 * This method randomly selects SharedClass.POINT_COUNT_TO_BE_SWAPPED number of points, and removes them from cluster
	 */
	public List<HouseholdObject> getOutPointsForSwap(){
		List<HouseholdObject> retVal = new ArrayList<>();
		for(int i = 0; i < SharedClass.POINT_COUNT_TO_BE_SWAPPED ; i++){
			int randomToBeSelected = SharedClass.randomInRange(0,clusterObjects.size() - 1);
			retVal.add(clusterObjects.remove(randomToBeSelected).getPoint());
		}
		return retVal;
	}
	
	/**
	 * @param objs is List of HouseholdObject that come from another cluster as a result of swapping
	 * This method is supposed to locate the list of HouseholdObject objects in cluster
	 */
	public void locateSwappedPoints(List<HouseholdObject> objs){
		for(HouseholdObject obj: objs){
			PointAndDistancePair thisPair = new PointAndDistancePair(obj, 0.0);
			clusterObjects.add(thisPair);
		}
	}

	
	/**
	 * When new iteration starts or KMeans converges, clusters are resetted.
	 */
	public void resetClusterObjects(){
		clusterObjects = new ArrayList<>();
	}
	
	/**
	 * @return after finishing StandardKMeans or KMeans with heuristic features, calculate objective fucntion in order to 
	 * compare diffence between them.Objective function is: sum of  ( distance ( x(i), centroid)) ^ 2 for all i
	 */
	public double calculateObjectiveFunction(){
		double retVal = 0;
		for(PointAndDistancePair cdPair : clusterObjects){
			retVal = retVal + cdPair.getDistance() * cdPair.getDistance();
		}
		return retVal;
	}
	
	/**
	 * @param isStandardKMeans - boolean value indication whether standard or heuristic version of kmeans is needed.
	 * @return the list of single center's vector values
	 * 	Get sum of all point's vector values and divide them with the number of cluster elements
	 */
	public List<Double> calculateNewCentroid(boolean isStandardKMeans){
		double val1 = 0;
		double val2 = 0;
		double val3 = 0;

		for(PointAndDistancePair singlePair: clusterObjects){
			val1 = val1 + singlePair.getPoint().getGlobalActivePower();
			val2 = val2 + singlePair.getPoint().getGlobalReactivePower();
			val3 = val3 + singlePair.getPoint().getVoltage();
		}
		List<Double> newCentroid = new ArrayList<>();
		int divisionValue ;
		if(isStandardKMeans){
			divisionValue = clusterObjects.size();
		}
		else{
			divisionValue = maxClusterSize;
		}
		newCentroid.add(val1/divisionValue);
		newCentroid.add(val2/divisionValue);
		newCentroid.add(val3/divisionValue);
		clusterCenter = newCentroid;
		return newCentroid;
	}
	
	/**
	 * After insertion of new element, sort the cluster elements with their distance to centroid, so that,
	 * the nearest and the farest element could be known
	 */
	private synchronized void sortPointsAccordingToNeighborhood(){
		Collections.sort(clusterObjects, new Comparator<PointAndDistancePair>() {
			@Override
			public int compare(PointAndDistancePair o1, PointAndDistancePair o2) {
				return Double.compare(o1.getDistance(), o2.getDistance());
			}
		});
	}
	
	
	/**
	 * getter function for clusterCenter variable
	 */
	public List<Double> getClusterCenter() {
		return clusterCenter;
	}
	/**
	 * getter function for clusterObjects variable
	 */
	public List<PointAndDistancePair> getClusterObjects() {
		return clusterObjects;
	}

}
