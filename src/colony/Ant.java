package colony;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Ant {

	double size, energy;
	Point2D.Double pos, vel;
	
	Color col;
	
	public Ant(){
		size = DisplayGUI.CELLWIDTH/8.0;
		size = Math.random()*size + size;
		energy = Math.random();
		
		pos = new Point2D.Double(Math.random()*DisplayGUI.WIDTH,
								 Math.random()*DisplayGUI.HEIGHT);
		double theta = Math.random()*2*Math.PI;
		vel = new Point2D.Double(4*size*Math.sin(theta),
								 4*size*Math.cos(theta));
		
		col = new Color(Color.HSBtoRGB((float)Math.random(), 0.7f, 0.7f));
	}
	
	public void step(double dt){
		double sec = dt/1000.0;
		pos.x += vel.x * sec; pos.y += vel.y * sec;

		if(pos.x < size || pos.x > DisplayGUI.WIDTH-size) vel.x *= -1;
		if(pos.y < size || pos.y > DisplayGUI.HEIGHT-size) vel.y *= -1;
	}
	
	public void draw(Graphics2D g){
		g.setColor(col);
		g.fillOval((int)(pos.x-size), (int)(pos.y-size), (int)size*2, (int)size*2);
	}
}
