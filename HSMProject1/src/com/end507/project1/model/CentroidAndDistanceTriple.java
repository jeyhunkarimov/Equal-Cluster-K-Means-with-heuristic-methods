package com.end507.project1.model;

import java.util.List;

/**
 * 
 * @author ceyhunkarimov
 *	This class is a model that consists triple:  point's single centoid , distance from that centroid and centroid's index
 *	Every point have list of CentroidAndDistancePair objects, meaning, triples to all centroid location , their distances,and indexes
 *	Type: Model according to MVC
 */
public class CentroidAndDistanceTriple {
	/**
	 * Representation of single centroid: list of its coordinates
	 */
	private List<Double> centroid;
	/**
	 * Distance from data point to that centroid
	 */
	private double distance;
	/**
	 * Centoid's index in all centroid array(for better finding purposes)
	 */
	private int centroidIndex;

	/**
	 * CentroidAndDistanceTriple class constructor
	 * @param centroid
	 * @param distance
	 * @param centroidIndex
	 */
	public CentroidAndDistanceTriple(List<Double> centroid, double distance, int centroidIndex){
		this.centroid = centroid;
		this.distance = distance;
		this.centroidIndex = centroidIndex;
	}
	



	/**
	 * getter function for distance variable
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * setter function for distance variable
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * getter function for centroid variable
	 */
	public List<Double> getCentroid() {
		return centroid;
	}


	/**
	 * setter function for centorid variable
	 */
	public void setCentroid(List<Double> centroid) {
		this.centroid = centroid;
	}

	/**
	 * getter function for centoidIndex variable
	 */
	public int getCentroidIndex() {
		return centroidIndex;
	}


	/**
	 * setter function for centroidIndex variable
	 */
	public void setCentroidIndex(int centroidIndex) {
		this.centroidIndex = centroidIndex;
	}


}
