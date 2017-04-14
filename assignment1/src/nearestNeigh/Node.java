package nearestNeigh;

import java.util.Comparator;
import java.util.List;

public abstract class Node implements Comparable<List> {
    protected static final int X = 0;
	protected static final int Y = 1;
	private final Point p;
    private final int k;
    private final int depth;

    private Node parent = null;
    private Node lesser = null;
    private Node greater = null;

	public Node(Point p){
		this.p = p;
		this.k = 2;
		this.depth = 0;
	}
	public Node(Point p, int k, int depth){
		this.p = p;
		this.k = k;
		this.depth = depth;
	}
    /* helper functions */
    private static final Comparator<Point> X_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.lon < o2.lon)
                return -1;
            if (o1.lon > o2.lon)
                return 1;
            return 0;
        }
    };

    private static final Comparator<Point> Y_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.lat < o2.lat)
                return -1;
            if (o1.lat > o2.lat)
                return 1;
            return 0;
        }
    };

	public int compareTo(Node o) {
		 return compareTo(depth, k, this.p, o.p);
	}
	private int compareTo(int depth2, int k2, Point o1, Point o2) {
        int axis = depth % k;
        if (axis == X)
            return X_COMPARATOR.compare(o1, o2);
        if (axis == Y)
            return Y_COMPARATOR.compare(o1, o2);
		return -1;

	}
	}