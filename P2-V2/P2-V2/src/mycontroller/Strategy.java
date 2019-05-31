package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial.Direction;

/**
 * Strategy class can give route according to given aim
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class Strategy {
	public enum AIM {EXPLORE, EXIT, PARCEL, HEALTH};
	protected AIM aim;
	protected MyAutoController myAutoController;
	protected Coordinate destCoordinate;
	public Strategy(MyAutoController myAutoController) {
		this.myAutoController = myAutoController;
	}
	
	public void initializeAim() {
		Coordinate coordinate = null;
		// search parcel firstly
		coordinate = myAutoController.analyseMap.getNearestTileCoordinate(Type.TRAP, 
			"parcel", myAutoController.getPositionCoordinate(), 
			myAutoController.getViewSquare());
		if (coordinate == null) {
			aim = AIM.EXPLORE;
			//destCoordinate = myAutoController.analyseMap.getNearestUnupdatedCoordinate(myAutoController.getPositionCoordinate(), myAutoController.getViewSquare());
			destCoordinate = new Coordinate(1, 7);
		} else {
			aim = AIM.PARCEL;
			destCoordinate = coordinate;
		}
	}
	
	public Direction getMoveOrientation(AnalyseMap map, CarController controller) {
		String coordinateString = controller.getPosition();
		int x = Integer.parseInt(coordinateString.split(",")[0]);
		int y = Integer.parseInt(coordinateString.split(",")[1]);
		Coordinate co = new Coordinate(x, y);
		map.getNearestUnupdatedCoordinate(co, controller.getViewSquare());
		// to be continue
		
		return Direction.EAST;
	}
	
}