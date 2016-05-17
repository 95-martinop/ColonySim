package colony;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Colony {
	int row, col;
	public Color color;
	double food;
	
	public Colony(int r, int c, Grid grid){
		this.row = r;
		this.col = c;
		this.food = 1000;
		this.color = new Color(Color.HSBtoRGB((float) Math.random(), 0.7f, 0.7f));
		
		ArrayList<Ant> ants = new ArrayList<Ant>();
		for (int i = 0; i < 40; i++) {
			ants.add(new Ant(this));
		}
		grid.transitionAnts(ants);
		
	}
	
	public void draw(Graphics2D g) {
		// ants per cell indicator
		g.setColor(this.color);
		g.fillRect(col*DisplayGUI.CELLWIDTH, row*DisplayGUI.CELLWIDTH,
				DisplayGUI.CELLWIDTH, DisplayGUI.CELLWIDTH);
	}
}
