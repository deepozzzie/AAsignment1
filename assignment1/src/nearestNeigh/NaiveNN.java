package nearestNeigh;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class is required to be implemented.  Naive approach implementation.
 *
 * @author Jeffrey, Youhan
 */

public class NaiveNN implements NearestNeigh{
	List<Point> neigh = null;

	@Override
	public void buildIndex(List<Point> points) {   
		if(points.size()>=1){
			neigh = points;
		}
		else{
			System.out.println("empty list");
		}

	}

	@Override
	public List<Point> search(Point searchTerm, int k) {
		/* return a list of up to length k, the list contains all the nearest neighbours. 
    	for the given point. E.g. if you are after 10 neighbours, k will be 10.
    	Neighbours contain all elements of a specific category from the list Points. 

		 */
		ArrayList<Point>Neighbours = new ArrayList<Point>(k); 
		ArrayList<pointDetails>pointsDetail = new ArrayList<pointDetails>();
		double distance;
		String id;
		Iterator<Point> pointIterator = neigh.iterator();
		/*
		 * This segment of code generates the distance between the given point and all points within the same category in the list
		 * and then adds them all to a new list called points details. Which will then take only first k elements from
		 * and add it to the neighbours list. Which will then be returned as the nearest neighbours. 
		 */
		System.out.println(searchTerm.cat);
		while(pointIterator.hasNext()){

			Point p = pointIterator.next();

			if(p.cat == searchTerm.cat){

				distance = p.distTo(searchTerm);

				id = p.id;


				pointDetails l = new pointDetails(id,distance, p);
				pointsDetail.add(l);


			}

		}
		/*sort the points detail list */

		pointsDetail.sort(new distanceComparator());
		if(k < pointsDetail.size()){
			for(int i = 0; i < k; i++){
				Neighbours.add(pointsDetail.get(i).p);
			}
		}
		else{
			for(int i = 0; i < pointsDetail.size(); i++){
				Neighbours.add(pointsDetail.get(i).p);
			}
		}
		return Neighbours;
	}


	@Override
	public boolean addPoint(Point point) {
		if(point == null || neigh.size() == 0){
			return false;
		}
		/* this function will attempt to add a point to the end of the list. As we dont care about the order of the list no sorting is required. 
		 * 
		 */
		if(neigh.add(point)){
			return true;

		}
		else{
			return false;
		}
	}

	@Override
	public boolean deletePoint(Point point) {
		/* this function removes a point from the list if it is within the current list.
		 * 
		 */
		if(point == null || neigh.size() == 0){
			return false;
		}
		Iterator<Point> pointIterator = neigh.iterator();
		while(pointIterator.hasNext())
		{
			if(pointIterator.next().equals(point)){
				pointIterator.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isPointIn(Point point) {

		// checks if point is already in the list returns true if it is, returns false if it is not. 
		if(point == null || neigh.size() == 0){
			return false;
		}
		Iterator<Point> pointIterator = neigh.iterator();
		while(pointIterator.hasNext())
		{
			if(pointIterator.next().equals(point)){
				return true;
			}
		}
		return false;
	}
	/* helper functions 
	 * the comparator helps find the nearest neighbour based upon distance from any given point.
	 * takes two points, and returns 1 if the distance between point 1 is less than point 2, returns -1 if not, if equal 
	 * returns 0. This is an overloaded compatator.*/
	class distanceComparator implements Comparator<pointDetails> {
		public int compare(pointDetails p1, pointDetails p2) {
			double distance1 = p1.distance;
			double distance2 = p2.distance;

			if (distance1 > distance2) {
				return 1;
			} else if (distance1 < distance2) {
				return -1;
			} else {
				return 0;
			}
		}
	}


}
