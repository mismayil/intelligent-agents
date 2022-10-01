import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {
	public static final int DEFAULT_ENERGY = 50;
	public static final int MOVE_ENERGY = 5;
	public static final int REPRODUCTION_ENERGY = 10;
	public static final int GRASS_ENERGY = 5;

	private int x = -1;
	private int y = -1;
	private int energy = DEFAULT_ENERGY;
	private RabbitsGrassSimulationSpace rgSpace;

	public RabbitsGrassSimulationAgent() {}
	public RabbitsGrassSimulationAgent(int energy) {
		this.energy = energy;
	}

	public void draw(SimGraphics G){
//		G.drawString("R", Color.blue);
		G.drawFastRect(Color.blue);
	}

	public void setRgSpace(RabbitsGrassSimulationSpace rgSpace) {
		this.rgSpace = rgSpace;
	}

	public RabbitsGrassSimulationSpace getRgSpace() {
		return rgSpace;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void step() {
		move();
		eat();
	}

	public void move() {
		List<String> directions = Arrays.asList("NORTH", "SOUTH", "EAST", "WEST");
		Random rand = new Random();
		String randomDirection = directions.get(rand.nextInt(directions.size()));

		int newX = x;
		int newY = y;

		switch (randomDirection) {
			case "NORTH": newY--; break;
			case "SOUTH": newY++; break;
			case "EAST": newX++; break;
			case "WEST": newX--; break;
		}

		if (rgSpace.isValid(newX, newY) && !rgSpace.isOccupied(newX, newY)) {
			rgSpace.moveRabbit(this, newX, newY);
			energy -= MOVE_ENERGY;
		}
	}

	public void eat() {
		boolean ateGrass = rgSpace.eatGrass(x, y);

		if (ateGrass) {
			energy += GRASS_ENERGY;
		}
	}

	public boolean isAlive() {
		return energy > 0;
	}

	public boolean canReproduce(int threshold) {
		return energy >= threshold;
	}

	public boolean reproduce(int threshold) {
		if (canReproduce(threshold)) {
			energy -= REPRODUCTION_ENERGY;
			return true;
		}

		return false;
	}
}
