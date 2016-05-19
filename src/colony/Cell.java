package colony;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Cell {
	
	// N, S, W, E, NW, NE, SW, SE
	Cell[] neighbors = new Cell[8];
	
	int row, col;
	ArrayList<Ant> ants;
	
	float terrain;
	float growth;
	int food;
	HashMap<Colony,Double> pheromones;
	
	boolean hasColony = false;
	Colony colony = null;
	
	private static final double PHERO_DECAY = 0.02;
	
	private static final int MAX_FOOD = 20;
	
	public Cell(int row, int col, Grid gr){
		this.row = row;
		this.col = col;
		ants = new ArrayList<Ant>();
		neighbors = new Cell[8];
		
		
		
		terrain = (float) (Math.random() * 0.1);
		pheromones = new HashMap<Colony,Double>();
		food = 0;

		//growth = (float) (Math.random());
		
		growth = 0;
		if (Math.random() < 0.02){//row == 10 && col == 10) {
			growth = 0.5f;
		}
		
	}
	
	public ArrayList<Ant> step(double dt) {
		//System.out.println("CELL STEP");
		if(Math.random() < this.growth*dt/1000){
			this.food++;
			if (this.food > MAX_FOOD) {
				this.food = MAX_FOOD;
			}
		}
		
		ArrayList<Ant> transitions = new ArrayList<Ant>();
		for (int a = 0; a < ants.size(); a++) {
			boolean transition = ants.get(a).step(dt, terrain,this); 
			if (transition) {
				transitions.add(ants.remove(a));
				a--;
			}
		}
		
		for(Colony c:pheromones.keySet()){
			pheromones.put(c, pheromones.get(c) * (1 - (Cell.PHERO_DECAY * dt/1000)));
		}
		
		return transitions;
	}
	
	public void draw(Graphics2D g) {
		g.setColor(new Color(Color.HSBtoRGB(0.3f, Math.min(1, food/20.0f), terrain * 0.2f + 0.2f)));
		g.fillRect(col*DisplayGUI.CELLWIDTH, row*DisplayGUI.CELLWIDTH,
				DisplayGUI.CELLWIDTH, DisplayGUI.CELLWIDTH);
		
		for(Colony c:pheromones.keySet()){
			if (pheromones.get(c) > Ant.pherThresh) {
				g.setColor(c.color);
				g.setStroke(new BasicStroke(pheromones.get(c).floatValue()/2));
				g.drawRect(col*DisplayGUI.CELLWIDTH, row*DisplayGUI.CELLWIDTH,
						DisplayGUI.CELLWIDTH, DisplayGUI.CELLWIDTH);
			}
		}
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
	
	public void addColony(Colony colony){
		this.colony = colony;
		this.hasColony = true;
	}
	
}
