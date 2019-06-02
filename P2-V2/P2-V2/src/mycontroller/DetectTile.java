package mycontroller;
import tiles.MapTile;

/**
 * store MapTile and record extra information
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class DetectTile {
	protected MapTile tile;
	protected String tileType = "null";
	/**
	 * true if this tile has been viewed by car.
	 */
	protected boolean isUpdated;
	protected int x;
	protected int y;
	
	public DetectTile(MapTile tile, int x, int y) {
		this.tile = tile;
		tileType = tile.getType().name();
		if (tileType.equals("WALL")) {
			isUpdated = true;
		} else {
			isUpdated = false;
		}
		this.x = x;
		this.y = y;
	}
	
	public void setTileType(String tileType) {
		this.tileType = tileType;
	}
	
	public void setIsUpdated(boolean status) {
		isUpdated = status;
	}
}