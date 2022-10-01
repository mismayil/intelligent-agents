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
	private HashMap<Map.Entry<City, Action>, Double> qTable = new HashMap<Map.Entry<City, Action>, Double>();
	private HashMap<City, Double> vTable = new HashMap<City, Double>();
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
	}

	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
		Action action;

		if (availableTask == null || random.nextDouble() > pPickup) {
			City currentCity = vehicle.getCurrentCity();
			action = new Move(currentCity.randomNeighbor(random));
		} else {
			action = new Pickup(availableTask);
		}
		
		if (numActions >= 1) {
			System.out.println("The total profit after "+numActions+" actions is "+myAgent.getTotalProfit()+" (average profit: "+(myAgent.getTotalProfit() / (double)numActions)+")");
		}
		numActions++;
		
		return action;
	}

	public void learn(Topology topology, TaskDistribution td, Agent agent, double discount) {
		while (true) {
			HashMap<City, Double> currentVTable = (HashMap<City, Double>) vTable.clone();

			for (City city: topology.cities()) {
				List<City> neighbors = city.neighbors();
				Vehicle vehicle = getVehicleByCity(agent, city);
				List<Double> qValues = new ArrayList<Double>();

				for (City neighbor: neighbors) {
					double probability = td.probability(city, neighbor);
					int reward = td.reward(city, neighbor);
					int weight = td.weight(city, neighbor);
					double discountedValue = discount * probability * currentVTable.getOrDefault(neighbor, 0);

					if (vehicle != null && weight < vehicle.capacity()) {
						Task task = new Task(getNewId(), city, neighbor, reward, weight);
						Action pickupAction = new Pickup(task);
						double pickupQValue = computeReward(vehicle, task, city, neighbor) + discountedValue;
						Map.Entry<City, Action> qEntry = new AbstractMap.SimpleImmutableEntry<>(city, pickupAction);
						qTable.put(qEntry, pickupQValue);
						qValues.add(pickupQValue);
					}

					Action moveAction = new Move(neighbor);
					double moveQValue = computeReward(vehicle, null, city, neighbor) + discountedValue;
					Map.Entry<City, Action> qEntry = new AbstractMap.SimpleImmutableEntry<>(city, moveAction);
					qTable.put(qEntry, moveQValue);
					qValues.add(moveQValue);
				}

				vTable.put(city, Collections.max(qValues));
			}


		}
	}

	private int getNewId() {
		id++;
		return id;
	}

	private Vehicle getVehicleByCity(Agent agent, City city) {
		List<Vehicle> vehicles = agent.vehicles();

		for (Vehicle vehicle: vehicles) {
			if (vehicle.homeCity().id == city.id) {
				return vehicle;
			}
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
}
