package colony;

import java.util.ArrayList;

public class Cell {
	
	Cell N,S,W,E,NW,NE,SW,SE;
	int row, col;
	
	ArrayList<Ant> ants;
	
	public Cell(int row, int col){
		this.row = row;
		this.col = col;
	}
	
}
