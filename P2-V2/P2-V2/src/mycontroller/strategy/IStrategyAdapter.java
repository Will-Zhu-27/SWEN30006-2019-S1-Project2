package mycontroller.strategy;

import mycontroller.MyAutoController;
import mycontroller.findPathAlgorithm.Node;
import utilities.Coordinate;

public interface IStrategyAdapter {
	public enum AIM {EXPLORE, EXIT, PARCEL, HEALTH, WAITTING};
	/**
	 * initialize the aim and set destCoordinate
	 * @param controller
	 */
	public void initializeAim(MyAutoController controller);
	/**
	 * update strategy
	 */
	public void update(MyAutoController controller);
	/**
	 * get current destination coordinate
	 * @return
	 */
	public Coordinate getDestCoordinate();
	/**
	 * get current aim
	 * @return
	 */
	public AIM getAim();
	
	public Node getNextNode();
}