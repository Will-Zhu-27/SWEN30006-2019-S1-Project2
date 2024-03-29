package mycontroller.strategy;

import mycontroller.MyAutoController;
import mycontroller.findPathAlgorithm.Node;
import utilities.Coordinate;


/**
 * Reducing health loss as first consideration
 * @author yuqiangz@student.unimelb.edu.au
 */
public class HealthFirstStrategyAdapter implements IStrategyAdapter{
	protected AIM aim;
	protected Coordinate destCoordinate;
	protected Node nextNode;
	
	/**
	 * initialize the aim and set destCoordinate
	 * @param controller
	 */
	public void initializeAim(MyAutoController controller) {
		// go to FINISH tile after get enough parcels
		if(controller.numParcelsFound() >= controller.numParcels()) {
			aim = AIM.EXIT;
			Coordinate coordinate = controller.getAnalyseMap().getNearestTileCoordinate("FINISH",
				controller.getPositionCoordinate(), controller.getAnalyseMap().getMazeHealthMode(),
				controller.getFindPathAlgorithm());
			if (coordinate == null) {
				System.exit(1);
			} else {
				destCoordinate = coordinate;
			}
		} else {
			// search parcel firstly
			Coordinate coordinate = null;
			coordinate = controller.getAnalyseMap().getNearestTileCoordinate("parcel", 
				controller.getPositionCoordinate(), controller.getAnalyseMap().getMazeHealthMode(),
				controller.getFindPathAlgorithm());
			// no parcel detected, explore the maze
			if (coordinate == null) {
				aim = AIM.EXPLORE;
				destCoordinate = controller.getAnalyseMap().
						getNearestUnupdatedCoordinate(controller.getPositionCoordinate(),
						controller.getAnalyseMap().getMazeHealthMode(), controller.getFindPathAlgorithm());
			} else {
				aim = AIM.PARCEL;
				destCoordinate = coordinate;
			}
		}
		setNextNode(controller, controller.getAnalyseMap().getMazeHealthMode());
	}
	
	/**
	 * update strategy
	 */
	public void update(MyAutoController controller) {
		// already arrive destCoordinate 
		if (destCoordinate.equals(controller.getPositionCoordinate())) {
			if (aim != AIM.EXIT) {
				initializeAim(controller);
			} else {
				aim = AIM.WAITING;
				controller.applyBrake();
				return;
			}
		} else if (aim == AIM.EXPLORE) {
			// search parcel firstly
			Coordinate coordinate = null;
			coordinate = controller.getAnalyseMap().getNearestTileCoordinate("parcel", 
				controller.getPositionCoordinate(), controller.getAnalyseMap().getMazeHealthMode(),
				controller.getFindPathAlgorithm());
			if (coordinate != null){
				aim = AIM.PARCEL;
				destCoordinate = coordinate;
			}
		} else if (aim == AIM.EXIT) {
			initializeAim(controller);
		} else if (aim == AIM.PARCEL) {
			initializeAim(controller);
		}
		setNextNode(controller, controller.getAnalyseMap().getMazeHealthMode());
		
	}
	
	public void setNextNode(MyAutoController controller, int maze[][]) {
		Node node = null;
		controller.getFindPathAlgorithm().setMaze(maze);
		controller.getFindPathAlgorithm().setStart(controller.getPositionCoordinate());
		controller.getFindPathAlgorithm().setEnd(destCoordinate);
		node = controller.getFindPathAlgorithm().findPath();
		if (node != null) {
			nextNode = node.next;
		} else {
			setNextNode(controller, controller.getAnalyseMap().getMazeFuelMode());
		}
	}
	
	/**
	 * get current destination coordinate
	 * @return
	 */
	public Coordinate getDestCoordinate() {
		return destCoordinate;
	}
	
	public AIM getAim() {
		return aim;
	}

	@Override
	public Node getNextNode() {		
		return nextNode;
	}
}