package com.end507.project1.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author ceyhunkarimov
 * 	Model class for single data point that was obtained from data set.
 *	Type : Model according to MVC
 */
public class HouseholdObject {
	/**
	 * The datetime of the check
	 */
	private Date operationDate;
	/**
	 * household global minute-averaged active power (in kilowatt) between 10.67 - 0
	 */
	private double globalActivePower;
	/**
	 * household global minute-averaged reactive power (in kilowatt) 1.39 - 0
	 */
	private double globalReactivePower;
	/**
	 * minute-averaged voltage (in volt) 252.14 - 223.49
	 */
	private double voltage;
	/**
	 * household global minute-averaged current intensity (in ampere) 0.2 - 46.4
	 */
	private double globalIntensity;
	/**
	 * energy sub-metering No. 1 (in watt-hour of active energy). It corresponds to the kitchen, 
	 * containing mainly a dishwasher, an oven and a microwave (hot plates are not electric but gas powered). 
	 */
	private double subMetering1;
	/**
	 * energy sub-metering No. 2 (in watt-hour of active energy). It corresponds to the laundry room, 
	 * containing a washing-machine, a tumble-drier, a refrigerator and a light. 
	 */
	private double subMetering2;
	/**
	 * energy sub-metering No. 3 (in watt-hour of active energy). It corresponds to an electric water-heater 
	 * and an air-conditioner
	 */
	private double subMetering3;

	/**
	 * List of< centroids - the distance to that centroid-index of that centroid>
	 * this is needed when data point selects its best cluster
	 */
	private List<CentroidAndDistanceTriple> distancesToAllCentroids = new ArrayList<>();


	/**
	 * The minimum value of first feature vector element
	 */
	public static double GLOBAL_ACTIVE_POWER_MIN_VAL;
	/**
	 * The maximum value of first feature vector element
	 */
	public static double GLOBAL_ACTIVE_POWER_MAX_VAL;
	/**
	 * The minimum value of second feature vector element
	 */
	public static double GLOBAL_REACTIVE_POWER_MIN_VAL;
	/**
	 * The maximum value of second feature vector element
	 */
	public static double GLOBAL_REACTIVE_POWER_MAX_VAL;
	/**
	 * The minimum value of third feature vector element
	 */
	public static double VOLTAGE_MIN_VAL;
	/**
	 * The maximum value of third feature vector element
	 */
	public static double VOLTAGE_MAX_VAL;
	
	/**
	 * Initialize static variables
	 */
	static{
		GLOBAL_ACTIVE_POWER_MIN_VAL = 0.076;
		GLOBAL_ACTIVE_POWER_MAX_VAL = 10.67;
		GLOBAL_REACTIVE_POWER_MIN_VAL = 0;
		GLOBAL_REACTIVE_POWER_MAX_VAL = 1.39;
		VOLTAGE_MIN_VAL = 223.49;
		VOLTAGE_MAX_VAL = 252.14;
	}
	
	/**
	 * 
	 * @param centroids - the current centroids
	 * @return the list that contain pair of distance and centrdoids, sorted in ascending manner in terms of their distances
	 * This method calculates the distance from this object's point to all centroids and returns the list in sorted order of distances
	 */
	public List<CentroidAndDistanceTriple> calculatePointDistancesToAllCentroids(List<List<Double>> centroids){
		List<CentroidAndDistanceTriple> distanceToAllCentroids = new ArrayList<CentroidAndDistanceTriple>();
		for(int i = 0; i < centroids.size() ; i++){
			List<Double> singleCentroid  =  centroids.get(i);
			Double distance = calcutaleBetweenPointAndCentroid( singleCentroid);
			CentroidAndDistanceTriple distanceToCentroid = new CentroidAndDistanceTriple(singleCentroid, distance,i);
			distanceToAllCentroids.add(distanceToCentroid);
		}
		Collections.sort(distanceToAllCentroids, new Comparator<CentroidAndDistanceTriple>() {

			@Override
			public int compare(CentroidAndDistanceTriple o1,
					CentroidAndDistanceTriple o2) {
				return Double.compare(o1.getDistance(),o2.getDistance());
			}
		});
		setDistancesToAllCentroids(distanceToAllCentroids);
		return distanceToAllCentroids;
	}

	/**
	 * 
	 * @param centroid- the single centroid coordinates
	 * @return - the distance between this point and parameter centroid, with euqlidean distance
	 */
	private Double calcutaleBetweenPointAndCentroid(List<Double> centroid){
		double finalValue = Math.pow(this.getGlobalActivePower() - centroid.get(0), 2) + 
							Math.pow(this.getGlobalReactivePower() - centroid.get(1), 2) + 
							Math.pow(this.getVoltage() - centroid.get(2), 2) ;
		finalValue = Math.sqrt(finalValue);
		return finalValue;
	}

	
	/**
	 * Constructor of the class
	 * @param operationDate - not used as feature vector
	 * @param globalActivePower - used as feature fector
	 * @param globalReactivePower- used as feature fector
	 * @param voltage- used as feature fector
	 * @param globalIntensity- not used as feature fector
	 * @param subMetering1- not used as feature fector
	 * @param subMetering2- not used as feature fector
	 * @param subMetering3- not used as feature fector
	 */
	public HouseholdObject(Date operationDate,double globalActivePower,double globalReactivePower,double voltage,
			double globalIntensity,double subMetering1,double subMetering2, double subMetering3){
		this.operationDate = operationDate;
		this.globalActivePower = globalActivePower;
		this.globalReactivePower = globalReactivePower;
		this.voltage = voltage;
		this.globalIntensity = globalIntensity;
		this.subMetering1 = subMetering1;
		this.subMetering2 = subMetering2;
		this.subMetering3 = subMetering3;
	}
	
	/**
	 * 
	 * getter function for operationDate variable
	 */
	public Date getOperationDate() {
		return operationDate;
	}
	/**
	 * 
	 * getter function for globalActivePower variable
	 */
	public double getGlobalActivePower() {
		return globalActivePower;
	}
	/**
	 * 
	 * getter function for globalReactivePower variable
	 */
	public double getGlobalReactivePower() {
		return globalReactivePower;
	}
	/**
	 * 
	 * getter function for voltage variable
	 */
	public double getVoltage() {
		return voltage;
	}
	/**
	 * getter function for globalIntencity variable
	 * 
	 */
	public double getGlobalIntensity() {
		return globalIntensity;
	}
	/**
	 * 
	 * getter function for submetering1 variable
	 */
	public double getSubMetering1() {
		return subMetering1;
	}
	/**
	 * 
	 * getter function for subMetering2 variable
	 */
	public double getSubMetering2() {
		return subMetering2;
	}
	
	/**
	 * 
	 * getter function for subMetering3 variable
	 */
	public double getSubMetering3() {
		return subMetering3;
	}
	
	/**
	 * setter function for globalActivePower variable
	 * 
	 */
	public void setGlobalActivePower(double globalActivePower) {
		this.globalActivePower = globalActivePower;
	}

	/**
	 * 
	 * setter function for globalReactivePower variable
	 */
	public void setGlobalReactivePower(double globalReactivePower) {
		this.globalReactivePower = globalReactivePower;
	}

	/**
	 * 
	 * setter function for voltage variable 
	 */
	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}
	
	/**
	 * 
	 * getter function for distancesToAllCentroids variable
	 */
	public List<CentroidAndDistanceTriple> getDistancesToAllCentroids() {
		return distancesToAllCentroids;
	}
	/**
	 * 
	 * setter function for distancesToAllCentroids variable
	 */
	public void setDistancesToAllCentroids(
			List<CentroidAndDistanceTriple> distancesToAllCentroids) {
		this.distancesToAllCentroids = distancesToAllCentroids;
	}


}
