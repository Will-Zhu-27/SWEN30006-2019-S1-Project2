package mycontroller.findPathAlgorithm;

/**
 * the link list to store path information
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class Node {
	
	public int x;
	public int y;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 *  next Node
	 */
	public Node next = null;

	/**
	 * get distance from this node to the end.
	 */
	public int getDistance() {
		int distance = 0;
		Node tempNode = this.next;
		while (tempNode != null) {
			distance++;
			if (tempNode.next != null) {
				tempNode = tempNode.next;
			} else {
				break;
			}
		}
		return distance;
	}
}