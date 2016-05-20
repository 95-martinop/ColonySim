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
	
	private static final double PHERO_DECAY = 0.05;
	private static final double PHERO_CAP = 40;
	public double PHERO_RESET_TIME = 30;
	public double pheroTimer = 0;
	
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
		this.pheroTimer += dt/100000;
		
		//System.out.println("CELL STEP");
		if(Math.random() < this.growth*dt/1000){
			this.food++;
			if (this.food > MAX_FOOD) {
				this.food = MAX_FOOD;
			}
		}
		
		ArrayList<Ant> transitions = new ArrayList<Ant>();
		for (int a = 0; a < ants.size(); a++) {
			Ant ant = ants.get(a);
			boolean transition = ant.step(dt, terrain,this);
			if (ant.energy <= 0) {
				if (ant.hasFood) {
					this.food++;
				}
				this.food += ant.size;
				ants.remove(a);
				a--;
			}
			else if (transition) {
				transitions.add(ants.remove(a));
				a--;
			}
			
			
		}
		
		for(Colony c:pheromones.keySet()){
			pheromones.put(c, Math.min(25,Math.max(0, pheromones.get(c) - this.pheroTimer * dt/1000)));
		}
		
		return transitions;
	}
	
	public void draw(Graphics2D g) {
		g.setColor(new Color(Color.HSBtoRGB(0.3f, Math.min(1, food/20.0f), terrain * 0.2f + 0.2f)));
		g.fillRect(col*DisplayGUI.CELLWIDTH, row*DisplayGUI.CELLWIDTH,
				DisplayGUI.CELLWIDTH, DisplayGUI.CELLWIDTH);
		
		for(Colony c:pheromones.keySet()){
			double cPher = pheromones.get(c);
			if (cPher > Ant.pherThresh) {
				g.setColor(c.color);
				g.setStroke(new BasicStroke(2));//pheromones.get(c).floatValue()/2));
				int offset = (int)((0.5-cPher/(PHERO_CAP*2))*DisplayGUI.CELLWIDTH);
				g.drawRect(col*DisplayGUI.CELLWIDTH + offset, row*DisplayGUI.CELLWIDTH + offset,
						(int)((cPher/PHERO_CAP)*DisplayGUI.CELLWIDTH), (int)((cPher/PHERO_CAP)*DisplayGUI.CELLWIDTH));
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
