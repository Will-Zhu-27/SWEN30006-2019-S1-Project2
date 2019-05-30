package mycontroller;

import java.util.HashMap;
import tiles.MapTile;
import utilities.Coordinate;

/**
 * AnalyseMap is used to analyse the map and give route according to the 
 * strategy.
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class AnalyseMap {
	public static int MAX_X;
	public static int MIN_X;
	public static int MAX_Y;
	public static int MIN_Y;
	protected HashMap<Coordinate,DetectTile> carMap;
	
	public AnalyseMap(HashMap<Coordinate,MapTile> initialMap) {
		carMap = new HashMap<Coordinate,DetectTile>();
		initializeCarMap(initialMap);
	}
	
	/**
	 * initialize carMap through import map which doesn't include 
	 * information of TRAP and get map range
	 */
	private void initializeCarMap(HashMap<Coordinate,MapTile> map) {
		int maxX = 0, minX = 0, maxY = 0, minY = 0;
		for (Coordinate coordinate : map.keySet()) {
			if (coordinate.x > maxX) {
				maxX = coordinate.x;
			}
			if (coordinate.x < minX) {
				minX = coordinate.x;
			}
			if (coordinate.y > maxY) {
				maxY = coordinate.y;
			}
			if (coordinate.y < minY) {
				minY = coordinate.y;
			}
			carMap.put(coordinate, new DetectTile(map.get(coordinate)));
		}
		
		// set map range;
		MAX_X = maxX;
		MIN_X = minX;
		MAX_Y = maxY;
		MIN_Y = minY;
		System.err.println("x range: " + MIN_X + "-" + MAX_X + ", y range: " + MIN_Y + "-" + MAX_Y);
	}
	
	public boolean getDetectTile(Coordinate coordinate, DetectTile tile) {
		carMap.put(coordinate, tile);
		return true;
	}
	
	/**
	 * get the string of carmap that already added tile
	 * @return
	 */
	public String getcarMapString() {
		String ret = "";	
		for (int x, y = MAX_Y; y >= MIN_Y; y-- ) {
			for (x = MIN_X; x <= MAX_X; x++ ) {
				Coordinate coordinate = new Coordinate(x, y);
				DetectTile tile = carMap.get(coordinate);
				String temp = String.format("%10s", tile.tileType);
				ret += temp;
				if (x == MAX_X) {
					ret += "\n";
				}
			}
		}
		return ret;
	}
}