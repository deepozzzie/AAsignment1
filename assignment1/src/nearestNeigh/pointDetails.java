/**
 * 
 */
package nearestNeigh;

import java.util.Comparator;

/**
 * @author Joshua
 *
 */
public class pointDetails {
	String id;
	double distance;
	Point p; 
		pointDetails(String id, double distance, Point p){
			this.id=id;
			this.distance = distance;
			this.p = p;
		}
		
}

