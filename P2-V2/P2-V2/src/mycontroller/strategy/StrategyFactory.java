package mycontroller.strategy;

import swen30006.driving.Simulation.StrategyMode;
/**
 * factory of IStrategyAdapter
 * @author yuqiangz@student.unimelb.edu.au
 *
 */
public class StrategyFactory {
	public static IStrategyAdapter getStrategyAdapter(StrategyMode mode) {
		if (mode == StrategyMode.FUEL) {
			return new FuelFirstStrategyAdapter();
		}else if (mode == StrategyMode.HEALTH) {
			return new HealthFirstStrategyAdapter();
		}
		return null;
	}
}