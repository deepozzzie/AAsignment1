package nearestNeigh;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
/**
 * This class is required to be implemented.  Kd-tree implementation.
 *
 * @author Jeffrey, Youhan
 * @param <T>
 */
public class KDTreeNN<T> implements NearestNeigh{

	protected static final int X_AXIS = 0;
	protected static final int Y_AXIS = 1;
	List<Point> list = null;
	private int k = 2;
	public Node root = null;
	private static final Comparator<Point> X_COMPARATOR = new Comparator<Point>() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(Point o1, Point o2) {
			if (o1.lat < o2.lat)
				return -1;
			else
				return 1;

		}
	};

	private static final Comparator<Point> Y_COMPARATOR = new Comparator<Point>() {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(Point o1, Point o2) {
			if (o1.lon < o2.lon)
				return -1;
			else
				return 1;

		}
	};
	@Override
	public void buildIndex(List<Point> points) {
		// this function should build out the kd tree, where the root node is the node with the median x value. 
		// The next level will consist of the left node being the median on the y-coordinate.
		// use the median node algorithm.
		if(points.size()>=1){
			list = points;
			root = createNode(list, k, 0);
		}
		else{
			System.out.println("empty list");
		}

	}

	@Override
	public List<Point> search(Point searchTerm, int k) {
		if(searchTerm == null || root == null){
			return Collections.EMPTY_LIST;
		}
		// Map used for results
		TreeSet<Node> results = new TreeSet<Node>(new EuclideanComparator(searchTerm));
		Node prev = null;
		Node node = root;
		while (node != null) {
			if (Node.compareTo(node.depth, node.k, searchTerm, node.id) <= 0) {
				// Lesser
				prev = node;
				node = node.lesser;
			} else {
				// Greater
				prev = node;
				node = node.greater;
			}
		}
		Node leaf = prev;
		if (leaf != null) {
			// Used to not re-examine nodes
			Set<Node> examined = new HashSet<Node>();

			// Go up the tree, looking for better solutions
			node = leaf;
			while (node != null) {
				// Search node
				searchNode(searchTerm, node, k, results, examined);
				node = node.parent;
			}
		}

		// Load up the collection of the results
		List<Point> collection = new ArrayList<Point>(k);
		for (Node nodes : results)
			collection.add(nodes.id);
		return collection;
	}
	private static void searchNode(Point value, Node node, int K, TreeSet<Node> results, Set<Node> visited) {
		visited.add(node);

		// Search node
		Node prevNode = null;
		Double distanceToLastNode = Double.MAX_VALUE;
		if (results.size() > 0) {
			prevNode = results.last();
			distanceToLastNode = prevNode.id.distTo(value);
		}
		Double nodeDistance = node.id.distTo(value);
		if (nodeDistance.compareTo(distanceToLastNode) < 0) {
			if (results.size() == K && prevNode != null)
				results.remove(prevNode);
			results.add(node);
		} else if (nodeDistance.equals(distanceToLastNode)) {
			results.add(node);
		} else if (results.size() < K) {
			results.add(node);
		}
		prevNode = results.last();
		distanceToLastNode = prevNode.id.distTo(value);

		int axis = node.depth % node.k;
		Node lesser = node.lesser;
		Node greater = node.greater;

		// Search children branches, if axis aligned distance is less than
		// current distance
		if (lesser != null && !visited.contains(lesser)) {
			visited.add(lesser);

			double nodePoint = Double.MIN_VALUE;
			double valuePlusDistance = Double.MIN_VALUE;
			if (axis == X_AXIS) {
				nodePoint = node.id.lat;
				valuePlusDistance = value.lat - distanceToLastNode;
			} else if (axis == Y_AXIS) {
				nodePoint = node.id.lon;
				valuePlusDistance = value.lon- distanceToLastNode;
			} 
			boolean inter = ((valuePlusDistance <= nodePoint) ? true : false);

			// Continue down lesser branch
			if (inter)
				searchNode(value, lesser, K, results, visited);
		}
		if (greater != null && !visited.contains(greater)) {
			visited.add(greater);

			double nodePoint = Double.MIN_VALUE;
			double valuePlusDistance = Double.MIN_VALUE;
			if (axis == X_AXIS) {
				nodePoint = node.id.lat;
				valuePlusDistance = value.lat + distanceToLastNode;
			} else if (axis == Y_AXIS) {
				nodePoint = node.id.lon;
				valuePlusDistance = value.lon + distanceToLastNode;
			} 
			boolean inter = ((valuePlusDistance >= nodePoint) ? true : false);

			// Continue down greater branch
			if (inter)
				searchNode(value, greater, K, results, visited);
		}
	}

	@Override
	public boolean addPoint(Point point) {
		if(point == null){
			return false;
		}
		if(root == null){
			root = new Node(point);
		}
		Node n = root;
		while(true){
			if(Node.compareTo(n.depth, n.k, point, n.id)<=0){
				if(n.lesser == null){
					Node newNode = new Node(point, k, n.depth+1);
					newNode.parent = n;
					n.lesser =  newNode;
					break;
				}
				n = n.lesser;
			}else{
				if(n.greater == null){
					Node newNode = new Node(point,k,n.depth+1);
					newNode.parent = n;
					n.greater = newNode;
					break;
				}
				n = n.greater;
			}
		}
		return true;
	}

	@Override
	public boolean deletePoint(Point point) {
		if (point  == null || root == null)
			return false;

		Node node = getNode(this, point);
		if (node == null)
			return false;

		Node parent = node.parent;
		if (parent != null) {
			if (parent.lesser != null && node.equals(parent.lesser)) {
				List<Point> nodes = getTree(node);
				if (nodes.size() > 0) {
					parent.lesser = createNode(nodes, node.k, node.depth);
					if (parent.lesser != null) {
						parent.lesser.parent = parent;
					}
				} else {
					parent.lesser = null;
				}
			} else {
				List<Point> nodes = getTree(node);
				if (nodes.size() > 0) {
					parent.greater = createNode(nodes, node.k, node.depth);
					if (parent.greater != null) {
						parent.greater.parent = parent;
					}
				} else {
					parent.greater = null;
				}
			}
		} else {
			// root
			List<Point> nodes = getTree(node);
			if (nodes.size() > 0)
				root = createNode(nodes, node.k, node.depth);
			else
				root = null;
		}

		return true;	
	}

	public List<Point> getTree(Node tree){
		List<Point> p = new ArrayList<Point>();

		if (tree == null)
			return p;

		if (tree.lesser != null) {
			p.add(tree.lesser.id);
			p.addAll(getTree(tree.lesser));

		}
		if (tree.greater != null) {
			p.add(tree.greater.id);
			p.addAll(getTree(tree.greater));
		}

		return p;
	}
	@Override
	public boolean isPointIn(Point point) {
		if(getNode(this, point) != null){
			return true;
		}
		return false;
	}
	public static Node createNode(List<Point> list, int k, int depth){
		if (list == null || list.size() == 0)
			return null;

		int axis = depth % k;
		if (axis == X_AXIS)
			Collections.sort(list, X_COMPARATOR);
		else if (axis == Y_AXIS)
			Collections.sort(list, Y_COMPARATOR);

		Node node = null;
		List<Point> less = new ArrayList<Point>(list.size());
		List<Point> more = new ArrayList<Point>(list.size());
		if (list.size() > 0) {
			int medianIndex = list.size() / 2;
			node = new Node(list.get(medianIndex), k, depth);
			// Process list to see where each non-median point lies
			for (int i = 0; i < list.size(); i++) {
				if (i == medianIndex)
					continue;
				Point p = list.get(i);
				// Cannot assume points before the median are less since they could be equal
				if (Node.compareTo(depth, k, p, node.id) <= 0) {
					less.add(p);
				} else {
					more.add(p);
				}
			}

			if ((medianIndex-1 >= 0) && less.size() > 0) {
				node.lesser = createNode(less, k, depth + 1);
				node.lesser.parent = node;
			}

			if ((medianIndex <= list.size()-1) && more.size() > 0) {
				node.greater = createNode(more, k, depth + 1);
				node.greater.parent = node;
			}
		}

		return node;
	}
	public static Node getNode(KDTreeNN tree, Point n){
		if (tree == null || tree.root == null || n == null)
			return null;

		Node node = tree.root;
		while (true) {
			if (node.id.equals(n)) {
				return node;
			} else if (Node.compareTo(node.depth, node.k, n, node.id) <= 0) {
				// Lesser
				if (node.lesser == null) {
					return null;
				}
				node = node.lesser;
			} else {
				// Greater
				if (node.greater == null) {
					return null;
				}
				node = node.greater;
			}
		}
	}


	public static class Node implements Comparable<Node> {
		public static int compareTo(int depth, int k, Point o1,Point o2) {
			int axis = depth % k;
			if (axis == X_AXIS)
				return X_COMPARATOR.compare(o1, o2);
			return Y_COMPARATOR.compare(o1, o2);

		}
		private final Point id;
		private final int k;
		private final int depth;

		private Node parent = null;
		private Node lesser = null;
		private Node greater = null;

		public Node(Point id) {
			this.id = id;
			this.k = 3;
			this.depth = 0;
		}

		public Node(Point id, int k, int depth) {
			this.id = id;
			this.k = k;
			this.depth = depth;
		}

		@Override
		public int compareTo(Node o) {
			return compareTo(depth, k, this.id, o.id);
		}
	}

	protected static class EuclideanComparator implements Comparator<Node> {

		private final Point point;

		public EuclideanComparator(Point point) {
			this.point = point;
		}
		@Override
		public int compare(Node o1, Node o2) {
			Double d1 = point.distTo(o1.id);
			Double d2 = point.distTo(o2.id);
			if (d1.compareTo(d2) < 0)
				return -1;
			else if (d2.compareTo(d1) < 0)
				return 1;
			return 0;
		}
	}
}
