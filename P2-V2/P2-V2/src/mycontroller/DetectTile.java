package mycontroller;
import tiles.MapTile;
import tiles.MapTile.Type;

/**
 * 
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class DetectTile {
	protected MapTile tile;
	protected int visitedTimes = 0;
	protected String tileType;
	protected boolean isUpdated;
	protected int x;
	protected int y;
	
	public DetectTile(MapTile tile, int x, int y) {
		this.tile = tile;
		tileType = tile.getType().name();
		isUpdated = false;
		visitedTimes = 0;	
		this.x = x;
		this.y = y;
	}
	
	
	public void visitTile() {
		visitedTimes++;
	}
	
	public void setTileType(String tileType) {
		this.tileType = tileType;
	}
	
	public void setIsUpdated(boolean status) {
		isUpdated = status;
	}
}