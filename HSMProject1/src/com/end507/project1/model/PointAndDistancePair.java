package com.end507.project1.model;

/**
 * 
 * @author ceyhunkarimov
 * 	Model that consists of a pair: data point object and its distance to cluster
 * 	This is used in Cluster class, to keep all data points in that cluster and their distances accordingly
 *	Type : Model according to MVC
 */
public class PointAndDistancePair {
	/**
	 * First element of pair: data point from data set
	 */
	private HouseholdObject point;
	/**
	 * 	Second element of pair: data point's distance to particular cluster center
	 */
	private Double distance;
	
	/**
	 * Class constructor
	 * @param point
	 * @param distance
	 * 
	 */
	public PointAndDistancePair(HouseholdObject point, Double distance){
		this.point = point;
		this.distance = distance;
	}

	/**
	 * getter method for point variable
	 */
	public HouseholdObject getPoint() {
		return point;
	}

	/**
	 * setter method for point variable
	 */
	public void setPoint(HouseholdObject point) {
		this.point = point;
	}

	/**
	 * getter method for distance variable
	 */
	public Double getDistance() {
		return distance;
	}

	/**
	 * setter method for distance variable
	 */
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
	
}
