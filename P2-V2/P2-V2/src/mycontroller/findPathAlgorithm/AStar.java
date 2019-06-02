package mycontroller.findPathAlgorithm;

import java.util.ArrayList;
import java.util.List;

import utilities.Coordinate;
 /**
  * A* algorithm to get shortest path
  * @author yuqiangz@student.unimelb.edu.au
  *
  */
public class AStar implements IFindPathAlgorithm {
 
    private static int[][] NODES;
    private static int xRange;
    private static int yRange;
 
    public static final int STEP = 1;
 
    private ArrayList<AStarNode> openList;
    private ArrayList<AStarNode> closeList;
    
    private Node start;
    private Node end;
    
    public AStar(int maze[][]) {
    	NODES = maze;
    	xRange = NODES.length;
    	yRange = NODES[0].length;
    	System.out.println("xRange:" + xRange + " yRange:" + yRange);
    }
    
    public AStar() {
    	
    }
    
    public void setMaze(int maze[][]) {
    	NODES = maze;
    	xRange = NODES.length;
    	yRange = NODES[0].length;
    }
    
    public void setStart(int x, int y) {
    	start = new AStarNode(x, y);
    }
    
    public void setStart(Coordinate coordinate) {
    	start = new AStarNode(coordinate.x, coordinate.y);
    }
    
    public void setEnd(int x, int y) {
    	end = new AStarNode(x, y);
    }
    
    public void setEnd(Coordinate coordinate) {
    	end = new AStarNode(coordinate.x, coordinate.y);
    }
 
    public AStarNode findMinFNodeInOpneList() {
    	AStarNode tempNode = openList.get(0);
        for (AStarNode node : openList) {
            if (node.F < tempNode.F) {
                tempNode = node;
            }
        }
        return tempNode;
    }
 
    public ArrayList<AStarNode> findNeighborNodes(AStarNode currentNode) {
        ArrayList<AStarNode> arrayList = new ArrayList<AStarNode>();
        
        int topX = currentNode.x;
        int topY = currentNode.y - 1;
        if (canReach(topX, topY) && !exists(closeList, topX, topY)) {
            arrayList.add(new AStarNode(topX, topY));
        }
        int bottomX = currentNode.x;
        int bottomY = currentNode.y + 1;
        if (canReach(bottomX, bottomY) && !exists(closeList, bottomX, bottomY)) {
            arrayList.add(new AStarNode(bottomX, bottomY));
        }
        int leftX = currentNode.x - 1;
        int leftY = currentNode.y;
        if (canReach(leftX, leftY) && !exists(closeList, leftX, leftY)) {
            arrayList.add(new AStarNode(leftX, leftY));
        }
        int rightX = currentNode.x + 1;
        int rightY = currentNode.y;
        if (canReach(rightX, rightY) && !exists(closeList, rightX, rightY)) {
            arrayList.add(new AStarNode(rightX, rightY));
        }
        return arrayList;
    }
 
    public boolean canReach(int x, int y) {
        if (x >= 0 && x < xRange && y >= 0 && y < yRange) {
            return NODES[x][y] == 0;
        }
        return false;
    }
    
    /**
     * 
     * @return Node which from start Node to end Node path, null if the start 
     * node and end node is same; 
     */
    public Node findPath() {
    	openList = new ArrayList<AStarNode>();
        closeList = new ArrayList<AStarNode>();

        openList.add((AStarNode)end);
 
        while (openList.size() > 0) {

        	AStarNode currentNode = findMinFNodeInOpneList();

            openList.remove(currentNode);

            closeList.add(currentNode);
 
            ArrayList<AStarNode> neighborNodes = findNeighborNodes(currentNode);
            for (AStarNode node : neighborNodes) {
                if (exists(openList, node)) {
                    foundPoint(currentNode, node);
                } else {
                    notFoundPoint(currentNode, (AStarNode)start, node);
                }
            }
            if (find(openList, (AStarNode)start) != null) {
                return find(openList, (AStarNode)start);
            }
        }
 
        return find(openList, (AStarNode)start);
    }
 
    private void foundPoint(AStarNode tempStart, AStarNode node) {
        int G = calcG(tempStart, node);
        if (G < node.G) {
            node.next = tempStart;
            node.G = G;
            node.calcF();
        }
    }
 
    private void notFoundPoint(AStarNode tempStart, AStarNode end, AStarNode node) {
        node.next = tempStart;
        node.G = calcG(tempStart, node);
        node.H = calcH(end, node);
        node.calcF();
        openList.add(node);
    }
 
    private int calcG(AStarNode start, AStarNode node) {
        int G = STEP;
        int parentG = node.next != null ? ((AStarNode)(node.next)).G : 0;
        return G + parentG;
    }
 
    private int calcH(Node end, Node node) {
        int step = Math.abs(node.x - end.x) + Math.abs(node.y - end.y);
        return step * STEP;
    }
 
    /*
    public static void main(String[] args) {
    	int[][] NODES = { 
    			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,1,1,1,1,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    			{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
    			{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
    			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    	    };
    	AStar findPathAlgorithm = new AStar(NODES);
    	findPathAlgorithm.setStart(2, 2);
    	findPathAlgorithm.setEnd(2, 5);
    	Node parent = findPathAlgorithm.findPath();
    	//System.out.println(parent.getDistance());
    	ArrayList<AStarNode> arrayList = new ArrayList<AStarNode>();
    	while (parent != null) {
            // System.out.println(parent.x + ", " + parent.y);
            arrayList.add(new AStarNode(parent.x, parent.y));
            System.out.println("(" + parent.x + "," + parent.y + ")");
            parent = parent.next;
        }
    	for (int i = 0; i < NODES.length; i++) {
            for (int j = 0; j < NODES[0].length; j++) {
                if (exists(arrayList, i, j)) {
                    System.out.print("@, ");
                } else {
                    System.out.print(NODES[i][j] + ", ");
                }
 
            }
            System.out.println();
        }
 
    }
    */
 
    public static Node find(List<AStarNode> nodes, AStarNode point) {
        for (AStarNode n : nodes)
            if ((n.x == point.x) && (n.y == point.y)) {
                return n;
            }
        return null;
    }
 
    public static boolean exists(List<AStarNode> nodes, AStarNode node) {
        for (Node n : nodes) {
            if ((n.x == node.x) && (n.y == node.y)) {
                return true;
            }
        }
        return false;
    }
 
    public static boolean exists(List<AStarNode> nodes, int x, int y) {
        for (Node n : nodes) {
            if ((n.x == x) && (n.y == y)) {
                return true;
            }
        }
        return false;
    }
    
    public static class AStarNode extends Node {
    	public int F;
        public int G;
        public int H;
 
        public AStarNode(int x, int y) {
			super(x, y);
			// TODO Auto-generated constructor stub
		}
 
        public void calcF() {
            this.F = this.G + this.H;
        }
    }
}