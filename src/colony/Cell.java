package colony;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Cell {
	
	// N, S, W, E, NW, NE, SW, SE
	Cell[] neighbors = new Cell[8];
	
	int row, col;
	ArrayList<Ant> ants;
	
	float growth;
	float terrain;
	float[] pheromones;
	
	int colony = -1;
	
	public Cell(int row, int col){
		this.row = row;
		this.col = col;
		ants = new ArrayList<Ant>();
		neighbors = new Cell[8];
		
		growth = (float) Math.random();
		terrain = (float) (Math.random() * 0.75);
		pheromones = new float[Grid.COLONIES];
	}
	
	public ArrayList<Ant> step(double dt) {
		//System.out.println("CELL STEP");
		ArrayList<Ant> transitions = new ArrayList<Ant>();
		for (int a = 0; a < ants.size(); a++) {
			boolean transition = ants.get(a).step(dt, terrain); 
			if (transition) {
				transitions.add(ants.remove(a));
				a--;
			}
		}
		
		return transitions;
	}
	
	public void draw(Graphics2D g) {
		// ants per cell indicator
		g.setColor(new Color(Color.HSBtoRGB(0.3f, growth, terrain * 0.2f + 0.2f)));
		g.fillRect(col*DisplayGUI.CELLWIDTH, row*DisplayGUI.CELLWIDTH,
				DisplayGUI.CELLWIDTH, DisplayGUI.CELLWIDTH);
	}
	
	public void drawComponents(Graphics2D g) {
		for(int i = 0; i < ants.size(); i ++){
			ants.get(i).draw(g);
		}
	}
	
	public void checkCollisions() {
		for (int a = 0; a < ants.size(); a++) {
			for (int i = 0; i < neighbors.length; i++) {
				if (neighbors[i] == null) {
					continue;
				}
				ArrayList<Ant> others = neighbors[i].ants;
				for (int j = 0; j < others.size(); j++) {
					ants.get(a).collidingWith(others.get(j));
				}
			}
			for(int b = 0; b < ants.size(); b++){
				if(b == a) continue;
				ants.get(a).collidingWith(ants.get(b));
			}
		}
	}
	
}
