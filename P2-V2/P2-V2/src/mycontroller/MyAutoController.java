package mycontroller;

import controller.CarController;
import mycontroller.findPathAlgorithm.AStar;
import mycontroller.findPathAlgorithm.AStar.Node;
import swen30006.driving.Simulation;
import world.Car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Input;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

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
	private AnalyseMap analyseMap;
	private Strategy strategy;
	
	public MyAutoController(Car car) {
		super(car);
		strategy = new Strategy();
		output = new Output("record.txt");
		analyseMap = new AnalyseMap(getMap(), mapWidth(), mapHeight());
		
		// try to use AStar
		AStar findPathAlgorithm = new AStar(analyseMap.maze);
		findPathAlgorithm.setStart(2, 3);
		findPathAlgorithm.setEnd(18, 15);
		Node parent = findPathAlgorithm.findPath();
		
		ArrayList<Node> arrayList = new ArrayList<Node>();
    	while (parent != null) {
    		arrayList.add(new Node(parent.x, parent.y));
            String path = "(" + parent.x + "," + parent.y + ")\n";
            output.write(path);
            parent = parent.parent;
        }
    	
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
		// TODO Auto-generated method stub					
		//analyseMap.updateCarMap(getView());
		//strategy.getPath(, destCoordinate);
	}
	
}
