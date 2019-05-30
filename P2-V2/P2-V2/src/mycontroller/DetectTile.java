package mycontroller;
import tiles.MapTile;

/**
 * 
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class DetectTile {
	protected MapTile tile;
	protected int visitedTimes = 0;
	protected String tileType;
	
	public DetectTile(MapTile tile) {
		this.tile = tile;
		tileType = tile.getType().name();
		visitedTimes = 0;
	}
	
	public DetectTile(MapTile tile, int visitedTimes) {
		this(tile);
		this.visitedTimes = visitedTimes;	
	}
	
	public void visitTile() {
		visitedTimes++;
	}
	
	public void setTileType(String tileType) {
		this.tileType = tileType;
	}
}