import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.gui.Object2DDisplay;

/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {
	private int gridSize = 20;
	private int numInitRabbits = 100;
	private int numInitGrass = 50;
	private int grassGrowthRate = 10;
	private int birthThreshold = 10;
	private RabbitsGrassSimulationSpace rgsSpace;
	private Schedule schedule;
	private DisplaySurface displaySurf;
	private ArrayList rabbits;

	public static void main(String[] args) {

		System.out.println("Rabbit skeleton");

		SimInit init = new SimInit();
		RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
		// Do "not" modify the following lines of parsing arguments
		if (args.length == 0) // by default, you don't use parameter file nor batch mode
			init.loadModel(model, "", false);
		else
			init.loadModel(model, args[0], Boolean.parseBoolean(args[1]));

	}

	public void begin() {
		System.out.println("Running begin");
		buildModel();
		buildSchedule();
		buildDisplay();
		displaySurf.display();
	}

	public String[] getInitParam() {
		// TODO Auto-generated method stub
		// Parameters to be set by users via the Repast UI slider bar
		// Do "not" modify the parameters names provided in the skeleton code, you can add more if you want
		String[] params = { "GridSize", "NumInitRabbits", "NumInitGrass", "GrassGrowthRate", "BirthThreshold"};
		return params;
	}

	public String getName() {
		return "RabbitGrass";
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setup() {
		System.out.println("Running setup");
		rgsSpace = null;
		rabbits = new ArrayList();

		if (displaySurf != null){
			displaySurf.dispose();
		}
		displaySurf = null;

		displaySurf = new DisplaySurface(this, "RabbitGrass Model Window 1");

		registerDisplaySurface("RabbitGrass Model Window 1", displaySurf);
	}

	public void buildModel() {
		System.out.println("Running buildModel");
		rgsSpace = new RabbitsGrassSimulationSpace(gridSize, gridSize);
		rgsSpace.spreadGrass(numInitGrass);

		for (int i = 0; i < numInitRabbits; i++){
			addNewRabbit();
		}
	}

	private void addNewRabbit(){
		RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent();
		rabbits.add(a);
		rgsSpace.addRabbit(a);
	}

	public void buildSchedule() {
		System.out.println("Running buildSchedule");
	}

	public void buildDisplay() {
		System.out.println("Running buildDisplay");
		ColorMap map = new ColorMap();

		map.mapColor(RabbitsGrassSimulationSpace.BACKGROUND, Color.white);
		map.mapColor(RabbitsGrassSimulationSpace.GRASS, Color.green);

		Value2DDisplay displayGrass = new Value2DDisplay(rgsSpace.getCurrentGrassSpace(), map);
		Object2DDisplay displayRabbits = new Object2DDisplay(rgsSpace.getCurrentRabbitSpace());
		displayRabbits.setObjectList(rabbits);

		displaySurf.addDisplayable(displayGrass, "Grass");
		displaySurf.addDisplayable(displayRabbits, "Rabbits");
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	public int getGridSize() {
		return gridSize;
	}

	public int getNumInitRabbits() {
		return numInitRabbits;
	}

	public void setNumInitRabbits(int numInitRabbits) {
		this.numInitRabbits = numInitRabbits;
	}

	public int getNumInitGrass() {
		return numInitGrass;
	}

	public void setNumInitGrass(int numInitGrass) {
		this.numInitGrass = numInitGrass;
	}

	public int getGrassGrowthRate() {
		return grassGrowthRate;
	}

	public void setGrassGrowthRate(int grassGrowthRate) {
		this.grassGrowthRate = grassGrowthRate;
	}

	public int getBirthThreshold() {
		return birthThreshold;
	}

	public void setBirthThreshold(int birthThreshold) {
		this.birthThreshold = birthThreshold;
	}
}
