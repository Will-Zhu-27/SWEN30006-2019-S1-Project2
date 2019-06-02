package mycontroller;

import controller.CarController;
import mycontroller.findPathAlgorithm.AStar;
import mycontroller.findPathAlgorithm.IFindPathAlgorithm;
import mycontroller.findPathAlgorithm.Node;
import mycontroller.strategy.IStrategyAdapter;
import mycontroller.strategy.StrategyFactory;
import mycontroller.strategy.IStrategyAdapter.AIM;
import swen30006.driving.Simulation;
import world.Car;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

/**
 * 
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class MyAutoController extends CarController {
	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 1;
	private Output output;
	protected volatile AnalyseMap analyseMap;
	private IStrategyAdapter strategy;
	protected IFindPathAlgorithm findPathAlgorithm;
	public MyAutoController(Car car) {
		super(car);
		output = new Output("record.txt");
		strategy = StrategyFactory.getStrategyAdapter(Simulation.toConserve());
		
		analyseMap = new AnalyseMap(getMap(), mapWidth(), mapHeight());
		findPathAlgorithm = new AStar(analyseMap.mazeFuelMode);
		// update Start point view
		analyseMap.updateCarMap(getView());
		// get first aim
		strategy.initializeAim(this);
		output.write("Dest: " + strategy.getDestCoordinate().toString() + "\n");
		output.write("need find " + numParcels() + "\n");
	}

	@Override
	public void update() {
		if (strategy.getAim() == AIM.WAITTING) {
			return;
		}
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = getView();
		Coordinate currentCoordinate = getPositionCoordinate();
		analyseMap.updateCarMap(currentView);
		
		strategy.update(this);
		// make sure the car is moving
		if (getSpeed() == 0) {
			if (checkWallAhead(getOrientation(), currentView)) {
				applyReverseAcceleration();
			} else {
				applyForwardAcceleration();
			}
		}
		Node next = strategy.getNextNode();
		/*
		if (next == null) {
			findPathAlgorithm.setMaze(analyseMap.mazeFuelMode);
			findPathAlgorithm.setStart(currentCoordinate);
			findPathAlgorithm.setEnd(strategy.getDestCoordinate());
			next = findPathAlgorithm.findPath();
		}
		*/
		// get Next Coordinate the car should arrive
		// first node is currentCoordinate
		try {
			//next = next.next;
			Coordinate destCoordinate = new Coordinate(next.x, next.y);
			output.write("dest coordinate " + strategy.getDestCoordinate().toString() + "current coordinate "
					+ currentCoordinate.toString() + " next coordinate:" + destCoordinate.toString() + "\n");
			moveHandler(currentCoordinate, getOrientation(), destCoordinate);
		} catch (Exception e) {
			applyBrake();
		}
		
	}
	
	public Coordinate getPositionCoordinate() {
		String temp = getPosition();
		int x = Integer.parseInt(temp.split(",")[0]);
		int y = Integer.parseInt(temp.split(",")[1]);
		return new Coordinate(x, y);
	}
	
	public void moveHandler(Coordinate currentCoordinate, Direction direction, Coordinate destCoordinate) {
		int diffX = destCoordinate.x - currentCoordinate.x;
		int diffY = destCoordinate.y - currentCoordinate.y;
		Direction relativeDirection = null;
		if (diffX == 0) {
			if (diffY > 0) {
				relativeDirection = Direction.NORTH;
			} else {
				relativeDirection = Direction.SOUTH;
			}
		} else {
			if (diffX > 0) {
				relativeDirection = Direction.EAST;
			} else {
				relativeDirection = Direction.WEST;
			}
		}
		
		switch (direction) {
		case EAST: {
			if (relativeDirection == Direction.EAST) {
				applyForwardAcceleration();
			} else if (relativeDirection == Direction.WEST) {
				applyReverseAcceleration();
			} else if (relativeDirection == Direction.SOUTH) {
				turnRight();
			} else {
				turnLeft();
			}
			break;
		}
		case WEST: {
			if (relativeDirection == Direction.EAST) {
				applyReverseAcceleration();
			} else if (relativeDirection == Direction.WEST) {
				applyForwardAcceleration();
			} else if (relativeDirection == Direction.SOUTH) {
				turnLeft();
			} else {
				turnRight();
			}
			break;
		}
		case SOUTH: {
			if (relativeDirection == Direction.EAST) {
				turnLeft();
			} else if (relativeDirection == Direction.WEST) {
				turnRight();
			} else if (relativeDirection == Direction.SOUTH) {
				applyForwardAcceleration();
			} else {
				applyReverseAcceleration();
			}
			break;
		}
		case NORTH: {
			if (relativeDirection == Direction.EAST) {
				turnRight();
			} else if (relativeDirection == Direction.WEST) {
				turnLeft();
			} else if (relativeDirection == Direction.SOUTH) {
				applyReverseAcceleration();
			} else {
				applyForwardAcceleration();
			}
			break;
		}
		}
	}
	
	/**
	 * Check if you have a wall in front of you!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
		switch(orientation){
		case EAST:
			return checkEast(currentView);
		case NORTH:
			return checkNorth(currentView);
		case SOUTH:
			return checkSouth(currentView);
		case WEST:
			return checkWest(currentView);
		default:
			return false;
		}
	}
	
	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public IFindPathAlgorithm getFindPathAlgorithm() {
		return findPathAlgorithm;
	}
	
	public AnalyseMap getAnalyseMap() {
		return analyseMap;
	}
}
