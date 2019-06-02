package mycontroller.findPathAlgorithm;

import utilities.Coordinate;

public interface IFindPathAlgorithm {
	/**
	 * set two dimensional array of Maze
	 */
	public void setMaze(int maze[][]);
	public void setStart(int x, int y);
	public void setStart(Coordinate coordinate);
	public void setEnd(int x, int y);
	public void setEnd(Coordinate coordinate);
	/**
	 * get path from start Node to end Node
	 * @return start Node which next attribute records the next Node.
	 */
	public Node findPath();
}