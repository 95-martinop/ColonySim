package colony;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Ant {

	double size, energy;
	Point2D.Double pos, vel;
	
	int row, col;
	
	Color color;
	
	public Ant(){
		size = DisplayGUI.CELLWIDTH/8.0;
		size = Math.random()*size + size;
		energy = Math.random();
		
		pos = new Point2D.Double(Math.random()*DisplayGUI.WIDTH,
								 Math.random()*DisplayGUI.HEIGHT);
		double theta = Math.random()*2*Math.PI;
		vel = new Point2D.Double(4*size*Math.sin(theta),
								 4*size*Math.cos(theta));
		
		color = new Color(Color.HSBtoRGB((float)Math.random(), 0.7f, 0.7f));
		
		row = (int) (pos.x / DisplayGUI.CELLWIDTH);
		col = (int) (pos.y / DisplayGUI.CELLWIDTH);
	}
	
	public boolean step(double dt){
//		System.out.println("ANT STEP " + dt);
		double sec = dt/1000.0;
		pos.x += vel.x * sec; pos.y += vel.y * sec;

		if(pos.x < size) {
			pos.x = size;
			vel.x *= -1;
		}
		else if (pos.x > DisplayGUI.WIDTH-size) {
			pos.x = DisplayGUI.WIDTH - size;
			vel.x *= -1;
		}
		if(pos.y < size) {
			pos.y = size;
			vel.y *= -1;
		}
		else if (pos.y > DisplayGUI.HEIGHT-size) {
			pos.y = DisplayGUI.HEIGHT - size;
			vel.y *= -1;
		}
		
		int prow = row;
		int pcol = col;
		row = (int) (pos.x / DisplayGUI.CELLWIDTH);
		col = (int) (pos.y / DisplayGUI.CELLWIDTH);
		int dr = row - prow;
		int dc = col - pcol;
		if (dr != 0 || dc != 0) {
			return true;
		}
		
		return false;
	}
	
	public void draw(Graphics2D g){
		g.setColor(color);
		g.fillOval((int)(pos.x-size), (int)(pos.y-size), (int)size*2, (int)size*2);
	}
	
	public boolean collidingWith(Ant other){
		double bound = other.size + this.size;
		double nx = other.pos.x - this.pos.x;
		double ny = other.pos.y - this.pos.y;
		double dist = Math.sqrt(Math.pow(nx, 2) + Math.pow(ny, 2));
		if (dist <= bound) {
			// do something with collision...
			nx /= dist; ny /= dist;
			double delta = vel.x*nx + vel.y*ny;
			vel.x -= 2*delta*nx;
			vel.y -= 2*delta*ny;
			// TODO: collision is wonky
			return true;
		}
		return false;
	}
}
