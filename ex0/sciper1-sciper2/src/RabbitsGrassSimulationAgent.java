import java.awt.Color;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {
	public static final int DEFAULT_ENERGY = 50;
	private int x = -1;
	private int y = -1;
	private int energy = DEFAULT_ENERGY;

	public RabbitsGrassSimulationAgent() {}
	public RabbitsGrassSimulationAgent(int energy) {
		this.energy = energy;
	}

	public void draw(SimGraphics G){
//		G.drawString("R", Color.blue);
		G.drawCircle(Color.blue);
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
}
