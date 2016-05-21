package colony;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Colony {
	static final int INIT_FOOD = 80;
	static final int ANT_COST = 10;
	static final double SPAWN_FOOD_THRESH = 25;
	static final double SPAWN_TIME_THRESH = 500;
	
	int row, col;
	public Color color;
	double food;
	double totalFoodHarvested;
	double spawnTimer;
	double aggression;
	public int numAnts = 0;
	public int totalAntsGenerated = 0;
	public int killCount;
	Grid grid;
		
	public Colony(int r, int c, Grid grid){//,double aggr){
		this.row = r;
		this.col = c;
		//this.aggression = aggr;//Math.random();
		this.aggression = Math.random();
		this.color = new Color(Color.HSBtoRGB((float) aggression, 0.7f, 0.7f));
		this.food = INIT_FOOD;
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
		if(this.food>SPAWN_FOOD_THRESH & this.spawnTimer >SPAWN_TIME_THRESH){
			this.food = this.food-ANT_COST;
			this.numAnts +=1;
			this.totalAntsGenerated += 1;
			this.grid.totalAntsGenerated +=1;
			this.grid.currentTotalAnts += 1;
			Ant newAnt = new Ant(this);
			ArrayList<Ant> temp = new ArrayList<Ant>();
			temp.add(newAnt);
			this.grid.transitionAnts(temp);
			this.spawnTimer = 0;
		}
	}
}
