package colony;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class Cell {
	
	// N, S, W, E, NW, NE, SW, SE
	Cell[] neighbors = new Cell[8];
	
	int row, col;
	ArrayList<Ant> ants;
	
	public static int[] DC = {0, 0, -1, 1, -1, 1, -1, 1};
	public static int[] DR = {-1, 1, 0, 0, -1, -1, 1, 1}; 
	
	public Cell(int row, int col){
		this.row = row;
		this.col = col;
		ants = new ArrayList<Ant>();
		neighbors = new Cell[8];
	}
	
	public ArrayList<Ant> step(double dt) {
		//System.out.println("CELL STEP");
		ArrayList<Ant> transitions = new ArrayList<Ant>();
		for (int a = 0; a < ants.size(); a++) {
			if (ants.get(a).step(dt)) {
				transitions.add(ants.remove(a));
				a--;
			}
		}
		
		return transitions;
	}
	
	public void draw(Graphics2D g) {
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
		}
	}
	
}
