package mycontroller;

import java.util.HashMap;

import controller.CarController;
import utilities.Coordinate;
import world.WorldSpatial.Direction;

/**
 * Strategy class can give route according to given aim
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class Strategy {
	public enum AIM {EXPLORE, EXIT, PARCEL, HEALTH};
	protected AIM currentAim;
	
	public Strategy(AIM aim) {
		currentAim = aim;
	}
	
	public Strategy() {
		currentAim = AIM.EXPLORE;
	}
	
	public void setAim(AIM aim) {
		currentAim = aim;
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