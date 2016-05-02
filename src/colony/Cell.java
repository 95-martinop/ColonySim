package colony;

//import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Cell {
	
	// N, S, W, E, NW, NE, SW, SE
	Cell[] neighbors = new Cell[8];
	
	int row, col;
	ArrayList<Ant> ants;
	
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
//		ants per cell indicator
//		g.setColor(new Color(Color.HSBtoRGB(ants.size()*0.1f, 0.7f, 0.7f)));
//		g.fillRect(row*DisplayGUI.CELLWIDTH, col*DisplayGUI.CELLWIDTH,
//				DisplayGUI.CELLWIDTH, DisplayGUI.CELLWIDTH);
		
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
