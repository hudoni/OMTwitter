/**
 * 
 */
package com.maalaang.omtwitter.ml;

/**
 * @author Sangwon Park
 *
 */
public interface SvmFeatureVector {
	
	/**
	 * Set a feature with the value.
	 * @param feature feature number 
	 * @param value feature value
	 */
	public void setFeatureValue(int feature, double value);
	
	/**
	 * Returns the value of the feature
	 * @param feature feature number
	 * @return feature value
	 */
	public double getFeatureValue(int feature);
	
	/**
	 * Returns the feature vector string that is compatible with the format used in SVMLight. [feature number]:[value] [feature number]:[value] ...
	 * @return the string representation of this feature vector
	 */
	public String toString();
	
	/**
	 * Returns the size of the feature vector.
	 * @return size of feature vector
	 */
	public int size();
	
	/**
	 * Store the feature vector to the given two arrays. The size of each array should be same with the size of this feature vector.
	 * @param features the array for storing feature numbers
	 * @param values the array for storing feature values
	 */
	public void toArrays(int[] features, double[] values);
	 
}
