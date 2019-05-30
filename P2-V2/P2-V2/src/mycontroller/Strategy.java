package mycontroller;

import java.util.HashMap;

import utilities.Coordinate;

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
	
	public void getPath(Coordinate carCoordinate, Coordinate destCoordinate) {
		
	}
	
}