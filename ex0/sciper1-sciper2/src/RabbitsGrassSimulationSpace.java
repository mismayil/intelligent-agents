import uchicago.src.sim.space.Object2DTorus;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author 
 */

public class RabbitsGrassSimulationSpace {
    private Object2DTorus grassSpace;
    private Object2DTorus rabbitSpace;

    public static final int BACKGROUND = 0;
    public static final int GRASS = 1;

    public RabbitsGrassSimulationSpace(int xSize, int ySize){
        grassSpace = new Object2DTorus(xSize, ySize);
        rabbitSpace = new Object2DTorus(xSize, ySize);

        for(int i = 0; i < xSize; i++){
            for(int j = 0; j < ySize; j++){
                grassSpace.putObjectAt(i, j, BACKGROUND);
            }
        }
    }

    public boolean addRabbit(RabbitsGrassSimulationAgent rabbit) {
        int max_tries = rabbitSpace.getSizeX() * rabbitSpace.getSizeY();
        int tries = 0;

        while (tries < max_tries) {
            // Choose coordinates
            int x = (int) (Math.random() * (rabbitSpace.getSizeX()));
            int y = (int) (Math.random() * (rabbitSpace.getSizeY()));

            // Check if the cell is empty and put a rabbit
            if (rabbitSpace.getObjectAt(x, y) == null) {
                rabbitSpace.putObjectAt(x, y, rabbit);
                rabbit.setX(x);
                rabbit.setY(y);
                return true;
            }

            tries++;
        }

        return false;
    }

    public void spreadGrass(int numGrass) {
        // Randomly place grass in grassSpace
        for (int i = 0; i < numGrass; i++) {

            // Choose coordinates
            int x = (int) (Math.random() * (grassSpace.getSizeX()));
            int y = (int) (Math.random() * (grassSpace.getSizeY()));

            // Check if the cell is empty and put a rabbit
            if (getGrassAt(x, y) == BACKGROUND) {
                grassSpace.putObjectAt(x, y, GRASS);
            }
        }
    }

    public Object2DTorus getCurrentGrassSpace(){
        return grassSpace;
    }

    public Object2DTorus getCurrentRabbitSpace(){
        return rabbitSpace;
    }

    public int getGrassAt(int x, int y) {
        Object object = grassSpace.getObjectAt(x, y);

        if (object != null) {
            return ((Integer)object).intValue();
        }

        return BACKGROUND;
    }
}
