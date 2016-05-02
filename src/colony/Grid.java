package colony;

public class Grid {
	
	Cell[][] cell;
	
	public Grid(){
		int rows = DisplayGUI.HEIGHT / DisplayGUI.CELLWIDTH;
		int cols = DisplayGUI.WIDTH / DisplayGUI.CELLWIDTH;
		
		cell = new Cell[rows][cols];
		for(int i = 0; i < rows; i ++){
			for(int j = 0; j < cols; j ++){
				cell[i][j] = new Cell(i,j);
			}
		}
		
		for(int i = 0; i < rows; i ++){
			for(int j = 0; j < cols; j ++){
				Cell c = cell[i][j];
				if(i > 0){
					c.N = cell[i-1][j];
					if(j > 0) c.NW = cell[i-1][j-1];
					if(j < cols-1) c.NE = cell[i-1][j+1];
				}
				if(i < rows-1){
					c.S = cell[i+1][j];
					if(j > 0) c.SW = cell[i+1][j-1];
					if(j < cols-1) c.SE = cell[i+1][j+1];
				}
				if(j > 0) c.W = cell[i][j-1];
				if(j < cols-1) c.E = cell[i][j+1];
			}
		}
	}
	
}
