package colony;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class Grid {
	
	public static int N = 0, S = 1, W = 2, E = 3, NW = 4, NE = 5, SW = 6, SE = 7;
	public static int COLONIES = 5;
	Colony[] colonies;
	Cell[][] cell;
	public int colonyRate = 100;
	
	int rows, cols;
	
	public Grid(){
		rows = DisplayGUI.HEIGHT / DisplayGUI.CELLWIDTH;
		cols = DisplayGUI.WIDTH / DisplayGUI.CELLWIDTH;
		
		
		
		colonies = new Colony[COLONIES];
		
		for(int i = 0; i<COLONIES; i++){
			colonies[i]= new Colony(3*(i%2)+3,3*(i/2)+3, this);
		}
		initCells();
		
		//ArrayList<Ant> ants = new ArrayList<Ant>();
		//for (int i = 0; i < 200; i++) {
		//	ants.add(new Ant());
		//}
		//transitionAnts(ants);
	}
	
	private void initCells() {
		cell = new Cell[rows][cols];
		for(int i = 0; i < rows; i ++){
			for(int j = 0; j < cols; j ++){
				cell[i][j] = new Cell(i,j);
				
			}
		}
		for(int c = 0; c< COLONIES; c++){
			int row = this.colonies[c].row;
			int column = this.colonies[c].col;
			this.cell[row][column].addColony(this.colonies[c]);
		}
		// link neighbors
		for(int i = 0; i < rows; i ++){
			for(int j = 0; j < cols; j ++){
				Cell c = cell[i][j];
				if(i > 0){
					c.neighbors[N] = cell[i-1][j];
					if(j > 0) c.neighbors[NW] = cell[i-1][j-1];
					if(j < cols-1) c.neighbors[NE] = cell[i-1][j+1];
				}
				if(i < rows-1){
					c.neighbors[S] = cell[i+1][j];
					if(j > 0) c.neighbors[SW] = cell[i+1][j-1];
					if(j < cols-1) c.neighbors[SE] = cell[i+1][j+1];
				}
				if(j > 0) c.neighbors[W] = cell[i][j-1];
				if(j < cols-1) c.neighbors[E] = cell[i][j+1];
			}
		}
	}
	
	public void step(double dt) {
		//System.out.println("GRID STEP");
		for(int i = 0; i < rows; i ++){
			for(int j = 0; j < cols; j ++){
				transitionAnts(cell[i][j].step(dt));
			}
		}
		for(int i = 0; i < rows; i ++){
			for(int j = 0; j < cols; j ++){
				cell[i][j].checkCollisions();
			}
		}
		for(int i = 0; i < this.COLONIES; i++){
			this.colonies[i].step(dt);
		}
	}
	
	public void transitionAnts(ArrayList<Ant> ants) {
		for (int i = 0; i < ants.size(); i++) {
			Ant ant = ants.get(i);
			cell[ant.row][ant.col].ants.add(ant);
		}
	}
	
	public void draw(Graphics2D g) {
		for(int i = 0; i < rows; i ++){
			for(int j = 0; j < cols; j ++){
				cell[i][j].draw(g);
			}
		}
		for(int i = 0; i <this.COLONIES; i++){
			this.colonies[i].draw(g);
		}
		for(int i = 0; i < rows; i ++){
			for(int j = 0; j < cols; j ++){
				cell[i][j].drawComponents(g);
			}
		}
	}
	
}
