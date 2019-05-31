package mycontroller;

import java.util.HashMap;
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
	public final static int WALL_VALUE = 1;
	public static int HEIGHT;
	public static int WIDTH;
	protected volatile HashMap<Coordinate,DetectTile> carMap;
	// two-dimensional arry to represent the maze
	protected int maze[][];
	public AnalyseMap(HashMap<Coordinate,MapTile> initialMap, int width, int height) {
		carMap = new HashMap<Coordinate,DetectTile>();
		HEIGHT = height;
		WIDTH = width;
		maze = new int[WIDTH][HEIGHT];
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				maze[i][j] = 0;
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
				maze[coordinate.x][coordinate.y] = WALL_VALUE;
			}
			carMap.put(coordinate, new DetectTile(map.get(coordinate), coordinate.x ,coordinate.y));
			
		}
		System.err.println("x range: " + WIDTH+ ", y range: " + HEIGHT);
	}
	
	/**
	 * return the maze in format of String
	 */
	public String getMazeGraphString() {
		String ret = "";
		for (int y = HEIGHT - 1; y >= 0; y--) {
			for (int x = 0; x < WIDTH; x++) {
				ret += maze[x][y];
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
				if (!mapTile.isType(Type.WALL)) {
					tile.tile = mapTile;
					tile.tileType = mapTile.getType().name();
				}
				continue;
			}
			// update tileType of DetectTile in carMap
			TrapTile trapTile = (TrapTile) mapTile;
			tile.setTileType(trapTile.getTrap());
		}
	}
	
	public Coordinate getNearestUnupdatedCoordinate(Coordinate startCoordinate, int visionLength) {
		int shortestDistance = -1;
		int x = 0, y = 0;
		for (Coordinate coordinate : carMap.keySet()) {
			DetectTile tile = carMap.get(coordinate);
			if (tile.isUpdated == true) {
				continue;
			}
			if (shortestDistance == -1) {
				shortestDistance = getDistance(startCoordinate, coordinate);
				x = coordinate.x;
				y = coordinate.y;
			} else {
				int distance = getDistance(startCoordinate, coordinate);
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
	public Coordinate getNearestTileCoordinate(String tileType, Coordinate startCoordinate) {
		int shortestDistance = -1;
		int x = -1, y = -1;
		for (Coordinate coordinate : carMap.keySet()) {
			DetectTile detectTile = carMap.get(coordinate);
			if (!detectTile.tileType.equals(tileType)) {
				continue;
			}
			if (shortestDistance == -1) {
				shortestDistance = getDistance(startCoordinate, coordinate);
				x = coordinate.x;
				y = coordinate.y;
			} else {
				int distance = getDistance(startCoordinate, coordinate);
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
	
	public int getDistance(Coordinate pointA, Coordinate pointB) {
		return Math.abs(pointA.x - pointB.x) + Math.abs(pointA.y - pointB.y);
	}
}