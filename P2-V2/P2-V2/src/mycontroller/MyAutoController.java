package mycontroller;

import controller.CarController;
import mycontroller.Strategy.AIM;
import mycontroller.findPathAlgorithm.AStar;
import mycontroller.findPathAlgorithm.AStar.Node;
import swen30006.driving.Simulation;
import world.Car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Input;

import tiles.MapTile;
import tiles.MapTile.Type;
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
	// Car Speed to move at
	private final int CAR_MAX_SPEED = 1;
	private Output output;
	protected volatile AnalyseMap analyseMap;
	private Strategy strategy;
	private AStar findPathAlgorithm;
	public MyAutoController(Car car) {
		super(car);
		output = new Output("record.txt");
		strategy = new Strategy(this, output);
		
		analyseMap = new AnalyseMap(getMap(), mapWidth(), mapHeight());
		findPathAlgorithm = new AStar(analyseMap.mazeFuelMode);
		// update Start point view
		analyseMap.updateCarMap(getView());
		// get first aim
		strategy.initializeAim();
		output.write("Dest: " + strategy.destCoordinate.toString() + "\n");
		output.write("need find " + numParcels() + "\n");
		/*
		findPathAlgorithm.setStart(16,  14);
		findPathAlgorithm.setEnd(22, 16);
		Node parent = findPathAlgorithm.findPath();
		output.write("from (16, 14) to (22, 16)\n");
    	while (parent != null) {
            output.write("(" + parent.x + "," + parent.y + ")\n");
            parent = parent.next;
        }
    	output.write("******end******\n");
		String graph = ""; 
		for (int x = 0; x < mapWidth(); x++) {
			for (int y = 0; y < mapHeight(); y++) {
				Coordinate coordinate = new Coordinate(x, y);
				DetectTile detectTile = analyseMap.carMap.get(coordinate);
				if (y == 0) {
					graph += "{";
				}
				if (detectTile.tileType.equals("WALL")) {
					graph += "1,"; 
				} else {
					graph += "0,"; 
				}
				if (y == mapHeight() - 1) {
					graph += "},\n";
				}
			}
		}
		output.write(graph);
		*/
		 
		/*
		Coordinate coordinate = analyseMap.getNearestTileCoordinate("health", getPositionCoordinate());
		if (coordinate != null) {
			output.write("find nearest health:" + coordinate.toString() + "\n");
		}
		*/
		/*
		// try to use AStar
		findPathAlgorithm.setStart(2, 3);
		findPathAlgorithm.setEnd(10, 3);
		Node next = findPathAlgorithm.findPath();
		
		ArrayList<Node> arrayList = new ArrayList<Node>();
    	while (next != null) {
    		arrayList.add(new Node(next.x, next.y));
            String path = "(" + next.x + "," + next.y + ")\n";
            output.write(path);
            next = next.next;
        }
    	output.write("**********end**********\n");
    	
    	for (int y = mapHeight() - 1; y >= 0; y--) {
    		String temp = "";
            for (int x = 0; x < mapWidth(); x++) {            	
                if (AStar.exists(arrayList, x, y)) {
                    temp += "@";
                } else {
                    
                    temp += analyseMap.maze[x][y];
                }
            }
            temp += "\n";
            output.write(temp);
        }
        */
		//output.write(analyseMap.getMazeGraphString());
		/*
		String coordinateString = getPosition();
		int x = Integer.parseInt(coordinateString.split(",")[0]);
		int y = Integer.parseInt(coordinateString.split(",")[1]);
		Coordinate co = new Coordinate(x, y);
		*/
		//analyseMap.updateCarMap(getView());
		//output.write(analyseMap.getcarMapString());
		//output.write(analyseMap.getNearestUnupdatedCoordinate(co, getViewSquare()).toString());
		//output.write(Simulation.toConserve().name());
		/*
		HashMap<Coordinate,MapTile> view = getView();
		for (Coordinate coordinate : view.keySet()) {
			String data = "";
			data += coordinate.x + "," + coordinate.y;
			MapTile tile = view.get(coordinate);
			data += ": " + tile.getType().name() + "\n";
			
			output.write(data);
		}
		
		HashMap<Coordinate,MapTile> view = getMap();
		for (Coordinate coordinate : view.keySet()) {
			String data = "";
			data += coordinate.x + "," + coordinate.y;
			MapTile tile = view.get(coordinate);
			data += ": " + tile.getType().name() + "\n";
			output.write(data);
		}
		output.endOutput();
		*/
	}

	@Override
	public void update() {
		if (strategy.aim == AIM.WAITTING) {
			return;
		}
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = getView();
		Coordinate currentCoordinate = getPositionCoordinate();
		analyseMap.updateCarMap(currentView);
		
		strategy.update();
		// make sure the car is moving
		if (getSpeed() == 0) {
			if (checkWallAhead(getOrientation(), currentView)) {
				applyReverseAcceleration();
			} else {
				applyForwardAcceleration();
			}
		}
		findPathAlgorithm.resetMaze(analyseMap.mazeHealthMode);
		findPathAlgorithm.setStart(currentCoordinate);
		findPathAlgorithm.setEnd(strategy.destCoordinate);
		Node next = findPathAlgorithm.findPath();
		if (next == null) {
			findPathAlgorithm.resetMaze(analyseMap.mazeFuelMode);
			findPathAlgorithm.setStart(currentCoordinate);
			findPathAlgorithm.setEnd(strategy.destCoordinate);
			next = findPathAlgorithm.findPath();
		}
		// get Next Coordinate the car should arrive
		// first node is currentCoordinate
		try {
			next = next.next;
			Coordinate destCoordinate = new Coordinate(next.x, next.y);
			output.write("dest coordinate " + strategy.destCoordinate.toString() + "current coordinate "
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
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		
		switch(orientation){
		case EAST:
			return checkNorth(currentView);
		case NORTH:
			return checkWest(currentView);
		case SOUTH:
			return checkEast(currentView);
		case WEST:
			return checkSouth(currentView);
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
	
}
