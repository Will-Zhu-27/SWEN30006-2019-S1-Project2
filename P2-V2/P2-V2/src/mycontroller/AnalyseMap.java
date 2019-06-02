package mycontroller;

import java.util.HashMap;

import mycontroller.findPathAlgorithm.IFindPathAlgorithm;
import mycontroller.findPathAlgorithm.Node;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;

/**
 * AnalyseMap is used to analyse the map and give route according to the 
 * strategy.
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class AnalyseMap {
	public final static int BARRIER_VALUE = 1;
	public static int HEIGHT;
	public static int WIDTH;
	protected volatile HashMap<Coordinate,DetectTile> carMap;
	// two-dimensional arry to represent the maze, only consider wall as barrier
	protected int mazeFuelMode[][];
	// two-dimensional arry to represent the maze, consider wall and lava as barrier
	protected int mazeHealthMode[][];
	
	public AnalyseMap(HashMap<Coordinate,MapTile> initialMap, int width, int height) {
		carMap = new HashMap<Coordinate,DetectTile>();
		HEIGHT = height;
		WIDTH = width;
		mazeFuelMode = new int[WIDTH][HEIGHT];
		mazeHealthMode = new int[WIDTH][HEIGHT];
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				mazeFuelMode[i][j] = 0;
				mazeHealthMode[i][j] = 0;
			}
		}
		initializeCarMap(initialMap);
	}
	
	/**
	 * initialize carMap through import map which doesn't include 
	 * information of TRAP and get map range
	 */
	private void initializeCarMap(HashMap<Coordinate,MapTile> map) {
		for (Coordinate coordinate : map.keySet()) {
			MapTile mapTile = map.get(coordinate);
			if (mapTile.getType() == Type.WALL) {
				mazeFuelMode[coordinate.x][coordinate.y] = BARRIER_VALUE;
				mazeHealthMode[coordinate.x][coordinate.y] = BARRIER_VALUE;
			}
			carMap.put(coordinate, new DetectTile(map.get(coordinate), coordinate.x ,coordinate.y));
			
		}
		//System.err.println("x range: " + WIDTH+ ", y range: " + HEIGHT);
	}
	
	/**
	 * return the maze in format of String
	 */
	public String getMazeGraphString() {
		String ret = "";
		for (int y = HEIGHT - 1; y >= 0; y--) {
			for (int x = 0; x < WIDTH; x++) {
				ret += mazeFuelMode[x][y];
				if (x == WIDTH - 1) {
					ret += "\n";
				}
			}
		}
		return ret;
	}
	

	/**
	 * get the string of carmap that already added tile
	 * @return
	 */
	public String getcarMapString() {
		String ret = "";	
		for (int x, y = HEIGHT - 1; y >= 0; y-- ) {
			for (x = 0; x < WIDTH; x++ ) {
				Coordinate coordinate = new Coordinate(x, y);
				DetectTile tile = carMap.get(coordinate);
				String temp = String.format("%10s", tile.tileType);
				ret += temp;
				if (x == WIDTH - 1) {
					ret += "\n";
				}
			}
		}
		return ret;
	}
	
	public void updateCarMap(HashMap<Coordinate,MapTile> carVisionMap) {
		for (Coordinate coordinate : carVisionMap.keySet()) {
			// discard outer tile info
			if (!carMap.containsKey(coordinate)) {
				continue;
			}
			MapTile mapTile = carVisionMap.get(coordinate);
			DetectTile tile = carMap.get(coordinate);
			tile.setIsUpdated(true);
			// discard tile excluding TRAP
			if (!mapTile.isType(Type.TRAP)) {
				// update mapTile attribute
				tile.tile = mapTile;
				tile.tileType = mapTile.getType().name();
				//System.err.println(coordinate.toString() + " is " + tile.tileType + "\n");
				continue;
			}
			// update tileType of DetectTile in carMap
			TrapTile trapTile = (TrapTile) mapTile;
			tile.setTileType(trapTile.getTrap());
			// update mazeWithoutDamage
			if (tile.tileType.equals("lava")) {
				mazeHealthMode[tile.x][tile.y] = BARRIER_VALUE;
			}
			//System.err.println(coordinate.toString() + " is " + tile.tileType + "\n");
		}
	}
	
	public Coordinate getNearestUnupdatedCoordinate(Coordinate startCoordinate, int maze[][], IFindPathAlgorithm findPathAlgorithm) {
		int shortestDistance = -1;
		int x = -1, y = -1;
		for (Coordinate coordinate : carMap.keySet()) {
			DetectTile tile = carMap.get(coordinate);
			if (tile.isUpdated == true) {
				continue;
			}
			if (shortestDistance == -1) {
				shortestDistance = getDistance(startCoordinate, coordinate, maze, findPathAlgorithm);
				x = coordinate.x;
				y = coordinate.y;
			} else {
				int distance = getDistance(startCoordinate, coordinate, maze, findPathAlgorithm);
				//System.err.println(startCoordinate.toString() + " to " + coordinate.toString() + ":" + distance);
				if (shortestDistance > distance) {
					x = coordinate.x;
					y = coordinate.y;
					shortestDistance = distance;
				}
			}
		}
		
		if (shortestDistance == -1) {
			return null;
		}
		return new Coordinate(x, y);
	}
	
	/**
	 * get the coordinate of nearest given Tile 
	 * @param givenType
	 * @param trapName null if givenType is not TRAP
	 * @param startCoordinate
	 * @param visionLength
	 * @return null if there is no that Tile.
	 */
	public Coordinate getNearestTileCoordinate(String tileType, Coordinate startCoordinate, int maze[][], IFindPathAlgorithm findPathAlgorithm) {
		int shortestDistance = -1;
		int x = -1, y = -1;
		for (Coordinate coordinate : carMap.keySet()) {
			DetectTile detectTile = carMap.get(coordinate);
			if (!detectTile.tileType.equals(tileType)) {
				continue;
			}
			if (shortestDistance == -1) {
				shortestDistance = getDistance(startCoordinate, coordinate, maze, findPathAlgorithm);
				x = coordinate.x;
				y = coordinate.y;
			} else {
				int distance = getDistance(startCoordinate, coordinate, maze, findPathAlgorithm);
				if (shortestDistance > distance) {
					x = coordinate.x;
					y = coordinate.y;
					shortestDistance = distance;
				}
			}
		}
		if (shortestDistance == -1) {
			return null;
		}
		if (shortestDistance == Integer.MAX_VALUE) {
			return getNearestTileCoordinateIgnoreDamage(tileType, startCoordinate, findPathAlgorithm);
		}
		
		return new Coordinate(x, y);
	}
	
	private Coordinate getNearestTileCoordinateIgnoreDamage(String tileType, Coordinate startCoordinate, IFindPathAlgorithm findPathAlgorithm) {
		int shortestDistance = -1;
		int x = -1, y = -1;
		for (Coordinate coordinate : carMap.keySet()) {
			DetectTile detectTile = carMap.get(coordinate);
			if (!detectTile.tileType.equals(tileType)) {
				continue;
			}
			if (shortestDistance == -1) {
				shortestDistance = getDistance(startCoordinate, coordinate, mazeFuelMode, findPathAlgorithm);
				x = coordinate.x;
				y = coordinate.y;
			} else {
				int distance = getDistance(startCoordinate, coordinate, mazeFuelMode, findPathAlgorithm);
				//System.err.println(startCoordinate.toString() + " to " + coordinate.toString() + ":" + distance);
				if (shortestDistance > distance) {
					x = coordinate.x;
					y = coordinate.y;
					shortestDistance = distance;
				}
			}
		}
		if (shortestDistance == -1) {
			return null;
		}
		
		return new Coordinate(x, y);
	}
	
	public int getDistance(Coordinate pointA, Coordinate pointB, int maze[][], IFindPathAlgorithm findPathAlgorithm) {
		if (pointA == pointB) {
			return 0;
		} else {
			findPathAlgorithm.setMaze(maze);
			findPathAlgorithm.setStart(pointA.x, pointA.y);
	    	findPathAlgorithm.setEnd(pointB.x, pointB.y);
	    	Node parent = findPathAlgorithm.findPath();
	    	if (parent == null) {
	    		return Integer.MAX_VALUE;
	    	}
			return parent.getDistance();
		}
	}
	
	public int[][] getMazeFuelMode() {
		return mazeFuelMode;
	}
	
	public int[][] getMazeHealthMode() {
		return mazeHealthMode;
	}
}