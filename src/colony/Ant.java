package colony;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Ant {

	double size, energy;
	Point2D.Double pos, vel, steer;

	int row, col;

	Color color;
	
	private double wanderAngle;
	private double wanderCircleDistance;
	private double wanderCircleRadius;
	private double wanderAngleChange;
	private double maxWanderForce = 15;

	public Ant() {
		size = DisplayGUI.CELLWIDTH / 8.0;
		size = Math.random() * size + size;
		energy = Math.random();
		
		wanderCircleDistance = 0.5;
		wanderCircleRadius = size * 2;
		wanderAngleChange = Math.PI/2;

		pos = new Point2D.Double(Math.random() * DisplayGUI.WIDTH, Math.random() * DisplayGUI.HEIGHT);
		double theta = Math.random() * 2 * Math.PI;
		vel = new Point2D.Double(4 * size * Math.sin(theta), 4 * size * Math.cos(theta));

		color = new Color(Color.HSBtoRGB((float) Math.random(), 0.7f, 0.7f));

		col = (int) (pos.x / DisplayGUI.CELLWIDTH);
		row = (int) (pos.y / DisplayGUI.CELLWIDTH);
	}

	public boolean step(double dt, float terrain) {
		// System.out.println("ANT STEP " + dt);
		double sec = dt / 1000.0;
		
		double mvx = vel.x;
		double mvy = vel.y;
		double vx = mvx;
		double vy = mvy;
		
		steer = wander(dt);
		double mag = steer.distance(new Point2D.Double(0, 0));
		if (mag > maxWanderForce) {
			steer.x *= maxWanderForce/mag;
			steer.y *= maxWanderForce/mag;
		}
		vel.x += steer.x;
		vel.y += steer.y;

		mag = Point2D.Double.distance(vel.x, vel.y, 0, 0);
		double maxVel = Point2D.Double.distance(0, 0, mvx, mvy);
		if (mag > maxVel) {
			vel.x *= maxVel/mag;
			vel.y *= maxVel/mag;
		}
		
		vx = vel.x * (1 - terrain);
		vy = vel.y * (1 - terrain);
		
		pos.x += vx * sec;
		pos.y += vy * sec;

		if (pos.x < size) {
			pos.x = size;
			vel.x *= -1;
		} else if (pos.x > DisplayGUI.WIDTH - size) {
			pos.x = DisplayGUI.WIDTH - size;
			vel.x *= -1;
		}
		if (pos.y < size) {
			pos.y = size;
			vel.y *= -1;
		} else if (pos.y > DisplayGUI.HEIGHT - size) {
			pos.y = DisplayGUI.HEIGHT - size;
			vel.y *= -1;
		}

		int prow = row;
		int pcol = col;
		col = (int) (pos.x / DisplayGUI.CELLWIDTH);
		row = (int) (pos.y / DisplayGUI.CELLWIDTH);
		int dr = row - prow;
		int dc = col - pcol;
		if (dr != 0 || dc != 0) {
			return true;
		}

		return false;
	}

	public void draw(Graphics2D g) {
		g.setColor(color);
		g.fillOval((int) (pos.x - size), (int) (pos.y - size), (int) size * 2, (int) size * 2);
		
		// draw velocity
		Point2D.Double dir = new Point2D.Double(pos.x + vel.x, pos.y + vel.y);
		g.setStroke(new BasicStroke(2));
		g.drawLine((int) pos.x, (int) pos.y, (int) dir.x, (int) dir.y);
		
		// draw wander force
		//dir = new Point2D.Double(pos.x + steer.x, pos.y + steer.y);
		//g.drawLine((int) pos.x, (int) pos.y, (int) dir.x, (int) dir.y);
	}

	public boolean collidingWith(Ant other) {
		double bound = other.size + this.size;
		double nx = other.pos.x - this.pos.x;
		double ny = other.pos.y - this.pos.y;
		double dist = Math.sqrt(Math.pow(nx, 2) + Math.pow(ny, 2));
		if (dist <= bound) {
			// do something with collision...
			nx /= dist;
			ny /= dist;
			double correctDist = (bound - dist) * 0.5;
			
			collide(nx, ny, correctDist);
			other.collide(-nx, -ny, correctDist);
			
			// TODO: collision is wonky
			return true;
		}
		return false;
	}

	public void collide(double nx, double ny, double correctDist) {
		// do something with collision...
		double delta = vel.x * nx + vel.y * ny;
		vel.x -= 2 * delta * nx;
		vel.y -= 2 * delta * ny;

		this.pos.x -= nx * correctDist;
		this.pos.y -= ny * correctDist;
	}
	
	private Point2D.Double wander(double dt) {
		Point2D.Double center = new Point2D.Double(vel.x * wanderCircleDistance, vel.y * wanderCircleDistance);
		Point2D.Double disp = new Point2D.Double(0, -1 * wanderCircleRadius);
		
		disp = getDisplacement(disp, wanderAngle);
		
		wanderAngle += Math.random() * wanderAngleChange - wanderAngleChange * 0.5;
		System.out.println(wanderAngle);
		
		return new Point2D.Double((center.x + disp.x), (center.y + disp.y));
	}
	
	private Point2D.Double getDisplacement(Point2D.Double vector, double value) {
		Point2D.Double result = new Point2D.Double();
		double len = vector.distance(new Point2D.Double(0, 0));
		result.x = Math.cos(value) * len;
		result.y = Math.sin(value) * len;
		return result;
	}
}
