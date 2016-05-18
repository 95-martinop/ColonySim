package colony;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Colony {
	int row, col;
	public Color color;
	double food;
	ArrayList<Ant> ants = new ArrayList<Ant>();
	Grid grid;
	public Colony(int r, int c, Grid grid){
		this.row = r;
		this.col = c;
		this.food = 500;
		this.color = new Color(Color.HSBtoRGB((float) Math.random(), 0.7f, 0.7f));
		this.grid = grid;
		//ArrayList<Ant> ants = new ArrayList<Ant>();
		//for (int i = 0; i < 40; i++) {
		//	ants.add(new Ant(this));
		//}
		//grid.transitionAnts(ants);
		
	}
	
	public void draw(Graphics2D g) {
		// ants per cell indicator
		g.setColor(this.color);
		g.fillRect(col*DisplayGUI.CELLWIDTH, row*DisplayGUI.CELLWIDTH,
				DisplayGUI.CELLWIDTH, DisplayGUI.CELLWIDTH);
	}
	
	public void step(double dt) {
		if(this.food>200){
			this.food = this.food-10;
			Ant newAnt = new Ant(this);
			this.ants.add(newAnt);
			ArrayList<Ant> temp = new ArrayList<Ant>();
			temp.add(newAnt);
			this.grid.transitionAnts(temp);
		}
	}
}
