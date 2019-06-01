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
	public enum AIM {EXPLORE, EXIT, PARCEL, HEALTH, WAITTING};
	protected AIM aim;
	protected Output output;
	protected volatile MyAutoController myAutoController;
	protected Coordinate destCoordinate;
	public Strategy(MyAutoController myAutoController, Output output) {
		this.myAutoController = myAutoController;
		this.output = output;
	}
	
	public void initializeAim() {
		// get enough parcels
		if(myAutoController.numParcelsFound() == myAutoController.numParcels()) {
			aim = AIM.EXIT;
			Coordinate coordinate = myAutoController.analyseMap.getNearestTileCoordinate("FINISH", myAutoController.getPositionCoordinate());
			if (coordinate == null) {
				output.write("Now aim is EXIT, but FINISH doesn't be found, and no unupdated coordinate\n");
				System.exit(1);
			} else {
				destCoordinate = coordinate;
				output.write("Now aim is EXIT, FINISH is " + destCoordinate.toString() + "\n");
			}
			return;
		}
		
		Coordinate coordinate = null;
		// search parcel firstly
		coordinate = myAutoController.analyseMap.getNearestTileCoordinate( 
			"parcel", myAutoController.getPositionCoordinate());
		if (coordinate == null) {
			aim = AIM.EXPLORE;
			destCoordinate = myAutoController.analyseMap.getNearestUnupdatedCoordinate(myAutoController.getPositionCoordinate());
			output.write("Now aim is EXPLORE, DEST:" + destCoordinate.toString() + "\n");
		} else {
			aim = AIM.PARCEL;
			destCoordinate = coordinate;
			output.write("Now aim is PARCEL, DEST:" + destCoordinate.toString() + "\n");
		}
	}
	
	public Direction getMoveOrientation(AnalyseMap map, CarController controller) {
		String coordinateString = controller.getPosition();
		int x = Integer.parseInt(coordinateString.split(",")[0]);
		int y = Integer.parseInt(coordinateString.split(",")[1]);
		Coordinate co = new Coordinate(x, y);
		map.getNearestUnupdatedCoordinate(co);
		// to be continue
		
		return Direction.EAST;
	}
	
	/**
	 * update strategy
	 */
	public void update() {
		if (aim == AIM.WAITTING) {
			return;
		}
		// already arrive destCoordinate
		if (destCoordinate.equals(myAutoController.getPositionCoordinate())) {
			if (aim != AIM.EXIT) {
				initializeAim();
				return;
			} else {
				aim = AIM.WAITTING;
				myAutoController.applyBrake();
			}
		}
		
		Coordinate coordinate = null;
		if (aim == AIM.EXPLORE) {
			// search parcel firstly
			coordinate = myAutoController.analyseMap.getNearestTileCoordinate( 
				"parcel", myAutoController.getPositionCoordinate());
			if (coordinate != null){
				aim = AIM.PARCEL;
				destCoordinate = coordinate;
				output.write("Now aim is PARCEL, DEST:" + destCoordinate.toString() + "\n");
			}
			return;
		}

	}
	
}