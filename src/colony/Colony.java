package colony;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Colony {
	int row, col;
	public Color color;
	double food;
	
	Grid grid;
	double spawnTimer;
	
	double aggression;
	static final double SPAWN_THRESH = 25;
	
	public Colony(int r, int c, Grid grid){
		this.row = r;
		this.col = c;
		this.food = 100;
		this.aggression = Math.random();
		this.color = new Color(Color.HSBtoRGB((float) aggression, 0.7f, 0.7f));
		this.grid = grid;
	}
	
	public void draw(Graphics2D g) {
		// ants per cell indicator
		g.setColor(this.color);
		g.setStroke(new BasicStroke(2));
		g.drawRect(col*DisplayGUI.CELLWIDTH, row*DisplayGUI.CELLWIDTH,
				DisplayGUI.CELLWIDTH, DisplayGUI.CELLWIDTH);
		g.drawString((int)food+"", (int)((col+0.2)*DisplayGUI.CELLWIDTH), (int)((row+0.6)*DisplayGUI.CELLWIDTH));
	}
	
	public void step(double dt) {
		this.spawnTimer = this.spawnTimer + dt;
		if(this.food>SPAWN_THRESH & this.spawnTimer >500){
			this.food = this.food-10;
			Ant newAnt = new Ant(this);
			ArrayList<Ant> temp = new ArrayList<Ant>();
			temp.add(newAnt);
			this.grid.transitionAnts(temp);
			this.spawnTimer = 0;
		}
	}
}
