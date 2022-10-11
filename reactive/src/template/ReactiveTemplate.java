package template;

import java.util.*;

import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class ReactiveTemplate implements ReactiveBehavior {

	private Random random;
	private double pPickup;
	private int numActions;
	private Agent myAgent;
	public static final int PICKUP_ACTION = 1;
	public static final int MOVE_ACTION = 2;

	private class QState {
		private final City fromCity;
		private final City toCity;
		private final boolean taskAvailable;

		QState(City fromCity, City toCity, boolean taskAvailable) {
			this.fromCity = fromCity;
			this.toCity = toCity;
			this.taskAvailable = taskAvailable;
		}

		QState(City fromCity, City toCity) {
			this(fromCity, toCity, true);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (!(obj instanceof QState)) {
				return false;
			}

			QState q = (QState) obj;

			return (q.fromCity.id == this.fromCity.id) && (q.toCity.id == this.toCity.id) && (q.taskAvailable == this.taskAvailable);
		}

		@Override
		public int hashCode() {
			return this.fromCity.id * 100 + this.toCity.id * 10 + (this.taskAvailable ? 1 : 0);
		}
	}
	private final HashMap<Map.Entry<QState, Integer>, Double> qTable = new HashMap<>();
	private final HashMap<City, Double> vTable = new HashMap<>();
	private int id = 0;

	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);

		this.random = new Random();
		this.pPickup = discount;
		this.numActions = 0;
		this.myAgent = agent;

		learn(topology, td, agent, discount);
	}

	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
		Action action;

		if (availableTask == null) {
			City currentCity = vehicle.getCurrentCity();
			action = new Move(currentCity.randomNeighbor(random));
		} else {
			QState qState = new QState(availableTask.pickupCity, availableTask.deliveryCity);
			double pickupValue = qTable.get(new AbstractMap.SimpleImmutableEntry<>(qState, PICKUP_ACTION));
			double moveValue = qTable.get(new AbstractMap.SimpleImmutableEntry<>(qState, MOVE_ACTION));

			if (pickupValue > moveValue) {
				action = new Pickup(availableTask);
			} else {
				action = new Move(availableTask.deliveryCity);
			}
		}
		
		if (numActions >= 1) {
			System.out.println("The total profit after "+numActions+" actions is "+myAgent.getTotalProfit()+" (average profit: "+(myAgent.getTotalProfit() / (double)numActions)+")");
		}
		numActions++;
		
		return action;
	}

	public void learn(Topology topology, TaskDistribution td, Agent agent, double discount) {
		while (true) {
			HashMap<City, Double> oldVTable = new HashMap<>(vTable);

			for (City fromCity: topology.cities()) {
				Vehicle vehicle = getVehicle(agent, fromCity);

				for (City toCity: topology.cities()) {
					QState qState = new QState(fromCity, toCity);
					double discountedValue = 0;

					for (City nextCity: topology.cities()) {
						discountedValue += td.probability(toCity, nextCity) * oldVTable.getOrDefault(nextCity, 0.0);
					}

					int reward = td.reward(fromCity, toCity);
					int weight = td.weight(fromCity, toCity);

					Task task = new Task(getNewId(), fromCity, toCity, reward, weight);
					double pickupQValue = computeReward(vehicle, task, fromCity, toCity) + discount * discountedValue;
					double moveQValue = computeReward(vehicle, null, fromCity, toCity) + discount * discountedValue;

					qTable.put(new AbstractMap.SimpleImmutableEntry<>(qState, PICKUP_ACTION), pickupQValue);
					qTable.put(new AbstractMap.SimpleImmutableEntry<>(qState, MOVE_ACTION), moveQValue);
				}

				vTable.put(fromCity, getMaxQValue(fromCity));
			}

			if (!changeInVTable(oldVTable)) {
				break;
			}
		}
	}

	private int getNewId() {
		id++;
		return id;
	}

	private Vehicle getVehicle(Agent agent, City city) {
		List<Vehicle> vehicles = agent.vehicles();

		for (Vehicle vehicle: vehicles) {
			if (vehicle.homeCity().id == city.id) {
				return vehicle;
			}
		}

		if (!vehicles.isEmpty()) {
			return vehicles.get(0);
		}

		return null;
	}

	private double computeReward(Vehicle vehicle, Task task, City fromCity, City toCity) {
		double reward = 0;

		if (task != null) {
			reward += task.reward;
		}

		return reward - vehicle.costPerKm() * fromCity.distanceTo(toCity);
	}

	private double getMaxQValue(City city) {
		List<Double> qValues = new ArrayList<>();

		for (Map.Entry<Map.Entry<QState, Integer>, Double> entry: qTable.entrySet()) {
			Map.Entry<QState, Integer> qEntry= entry.getKey();
			QState qState = qEntry.getKey();
			Double qValue = entry.getValue();

			if (qState.fromCity.id == city.id) {
				qValues.add(qValue);
			}
		}

		return Collections.max(qValues);
	}

	private boolean changeInVTable(HashMap<City, Double> oldVTable) {
		for (Map.Entry<City, Double> entry: vTable.entrySet()) {
			City city = entry.getKey();
			Double value = entry.getValue();

			if (!oldVTable.containsKey(city) || oldVTable.get(city).equals(value)) {
				return true;
			}
		}

		return false;
	}
}
