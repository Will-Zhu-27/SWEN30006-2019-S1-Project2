package mycontroller.findPathAlgorithm;

import java.util.ArrayList;
import java.util.List;

import utilities.Coordinate;
 
public class AStar {
 
    public static int[][] NODES;
    public static int xRange;
    public static int yRange;
 
    public static final int STEP = 1;
 
    private ArrayList<Node> openList = new ArrayList<Node>();
    private ArrayList<Node> closeList = new ArrayList<Node>();
    
    private Node start;
    private Node end;
    
    public AStar(int maze[][]) {
    	NODES = maze;
    	xRange = NODES[0].length;
    	yRange = NODES.length;
    }
    
    public void resetMaze(int maze[][]) {
    	NODES = maze;
    	xRange = NODES[0].length;
    	yRange = NODES.length;
    }
    
    public void setStart(int x, int y) {
    	start = new Node(x, y);
    }
    
    public void setStart(Coordinate coordinate) {
    	start = new Node(coordinate.x, coordinate.y);
    }
    
    public void setEnd(int x, int y) {
    	end = new Node(x, y);
    }
    
    public void setEnd(Coordinate coordinate) {
    	end = new Node(coordinate.x, coordinate.y);
    }
 
    public Node findMinFNodeInOpneList() {
        Node tempNode = openList.get(0);
        for (Node node : openList) {
            if (node.F < tempNode.F) {
                tempNode = node;
            }
        }
        return tempNode;
    }
 
    public ArrayList<Node> findNeighborNodes(Node currentNode) {
        ArrayList<Node> arrayList = new ArrayList<Node>();
        
        int topX = currentNode.x;
        int topY = currentNode.y - 1;
        if (canReach(topX, topY) && !exists(closeList, topX, topY)) {
            arrayList.add(new Node(topX, topY));
        }
        int bottomX = currentNode.x;
        int bottomY = currentNode.y + 1;
        if (canReach(bottomX, bottomY) && !exists(closeList, bottomX, bottomY)) {
            arrayList.add(new Node(bottomX, bottomY));
        }
        int leftX = currentNode.x - 1;
        int leftY = currentNode.y;
        if (canReach(leftX, leftY) && !exists(closeList, leftX, leftY)) {
            arrayList.add(new Node(leftX, leftY));
        }
        int rightX = currentNode.x + 1;
        int rightY = currentNode.y;
        if (canReach(rightX, rightY) && !exists(closeList, rightX, rightY)) {
            arrayList.add(new Node(rightX, rightY));
        }
        return arrayList;
    }
 
    public boolean canReach(int x, int y) {
        if (x >= 0 && x < xRange && y >= 0 && y < yRange) {
            return NODES[x][y] == 0;
        }
        return false;
    }
 
    public Node findPath() {
    	openList = new ArrayList<Node>();
        closeList = new ArrayList<Node>();

        openList.add(end);
 
        while (openList.size() > 0) {

            Node currentNode = findMinFNodeInOpneList();

            openList.remove(currentNode);

            closeList.add(currentNode);
 
            ArrayList<Node> neighborNodes = findNeighborNodes(currentNode);
            for (Node node : neighborNodes) {
                if (exists(openList, node)) {
                    foundPoint(currentNode, node);
                } else {
                    notFoundPoint(currentNode, start, node);
                }
            }
            if (find(openList, start) != null) {
                return find(openList, start);
            }
        }
 
        return find(openList, start);
    }
 
    private void foundPoint(Node tempStart, Node node) {
        int G = calcG(tempStart, node);
        if (G < node.G) {
            node.next = tempStart;
            node.G = G;
            node.calcF();
        }
    }
 
    private void notFoundPoint(Node tempStart, Node end, Node node) {
        node.next = tempStart;
        node.G = calcG(tempStart, node);
        node.H = calcH(end, node);
        node.calcF();
        openList.add(node);
    }
 
    private int calcG(Node start, Node node) {
        int G = STEP;
        int parentG = node.next != null ? node.next.G : 0;
        return G + parentG;
    }
 
    private int calcH(Node end, Node node) {
        int step = Math.abs(node.x - end.x) + Math.abs(node.y - end.y);
        return step * STEP;
    }
 
    public static void main(String[] args) {
    	int[][] NODES = { 
    	        { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    	        { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    	        { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
    	        { 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 
    	        { 0, 0, 0, 1, 0, 0, 0, 0, 0 },
    	        { 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 
    	        { 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 
    	        { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    	        { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
    	    };
    	AStar findPathAlgorithm = new AStar(NODES);
    	findPathAlgorithm.setStart(5, 1);
    	findPathAlgorithm.setEnd(5, 5);
    	Node parent = findPathAlgorithm.findPath();
    	
    	ArrayList<Node> arrayList = new ArrayList<Node>();
    	while (parent != null) {
            // System.out.println(parent.x + ", " + parent.y);
            arrayList.add(new Node(parent.x, parent.y));
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
 
    public static Node find(List<Node> nodes, Node point) {
        for (Node n : nodes)
            if ((n.x == point.x) && (n.y == point.y)) {
                return n;
            }
        return null;
    }
 
    public static boolean exists(List<Node> nodes, Node node) {
        for (Node n : nodes) {
            if ((n.x == node.x) && (n.y == node.y)) {
                return true;
            }
        }
        return false;
    }
 
    public static boolean exists(List<Node> nodes, int x, int y) {
        for (Node n : nodes) {
            if ((n.x == x) && (n.y == y)) {
                return true;
            }
        }
        return false;
    }
 
    public static class Node {
        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
 
        public int x;
        public int y;
 
        public int F;
        public int G;
        public int H;
 
        public void calcF() {
            this.F = this.G + this.H;
        }
 
        // next Node
        public Node next;
    }
}