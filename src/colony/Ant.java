package colony;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Ant {

	double size;
	double energy = 2;
	Point2D.Double pos, vel, steer;

	public boolean hasFood = false;
	
	int row, col;

	Color color;
	
	private double wanderAngle;
	private double wanderCircleDistance;
	private double wanderCircleRadius;
	private double wanderAngleChange;
	private Point2D.Double homePoint;
	private Colony home;
	public static Double pherThresh=  .1;
	private Double wanderChance=.001;
	private Cell currentCell;
	
	private final double MAX_VELOCITY;
	
	private enum State {
		Follow, Wander, Home, Attack
	}
	
	private State state = State.Wander;
	
	private final double MAX_WANDER_DISTANCE = DisplayGUI.CELLWIDTH * 25;
	private final double ENERGY_THRESH = 0.35;
	private int wTimer;
	private int wThresh = 500;
	
	private Ant attackTarget;
	
	public Ant(Colony colony) {
		this.home = colony;
		size = DisplayGUI.CELLWIDTH / 8.0; // 8 is correct... Orion says so
		size = Math.random() * size + size;
		
		wanderCircleDistance = 2;
		wanderCircleRadius = size * 2;
		wanderAngleChange = Math.PI/2;

		//pos = new Point2D.Double(Math.random() * DisplayGUI.WIDTH, Math.random() * DisplayGUI.HEIGHT);
		double theta = Math.random() * 2 * Math.PI;
		vel = new Point2D.Double(10 * size * Math.sin(theta), 10 * size * Math.cos(theta));

		color = this.home.color;//new Color(Color.HSBtoRGB((float) Math.random(), 0.7f, 0.7f));
		this.homePoint = new Point2D.Double((this.home.col+.5) * DisplayGUI.CELLWIDTH, (this.home.row+.5) * DisplayGUI.CELLWIDTH);
		pos = new Point2D.Double(homePoint.x, homePoint.y);
		
		col = (int) (pos.x / DisplayGUI.CELLWIDTH);
		row = (int) (pos.y / DisplayGUI.CELLWIDTH);
		
		MAX_VELOCITY = 8 * size;
		wTimer = 0;
	}

	public boolean step(double dt, float terrain, Cell currentCell) {
		tryTakeFood(currentCell);
		tryLeaveFood(currentCell);
		
		this.currentCell = currentCell;
		double sec = dt / 1000.0;
		
		double mvx = vel.x;
		double mvy = vel.y;
		double vx = mvx;
		double vy = mvy;
		
		switch (state) {
			case Follow:
				if (Math.random() < wanderChance) {
					this.state = State.Wander;
					break;
				}
				
				Cell bestCell = null;
				double bestDist = -1.0;
				for(Cell c : this.currentCell.neighbors){
					if(c == null) continue;
					if(c.food > 0){bestCell = c;bestDist = 0; break;}
					int dr = c.row - home.row;
					int dc = c.col - home.col;
					double dist =Math.pow(dr, 2)+Math.pow(dc, 2);
					if(dist > bestDist & c.pheromones.get(home)> pherThresh){
						bestCell = c;
						bestDist = dist;
					}
				}
				if(bestDist == -1){
					this.state = State.Wander;
					break;
				}
			
				Point2D.Double destPoint = new Point2D.Double((bestCell.col+.5)*DisplayGUI.CELLWIDTH, (bestCell.row+.5)*DisplayGUI.CELLWIDTH);
				seek(destPoint);
				break;
			case Wander:
				wTimer += 1;
				if(wTimer>wThresh && currentCell.pheromones.get(home)>pherThresh){
					this.state = State.Follow;
					wTimer =0;
				}
				if (Point2D.distance(pos.x, pos.y, home.col * DisplayGUI.CELLWIDTH, home.row * DisplayGUI.CELLWIDTH) > MAX_WANDER_DISTANCE) {
					this.state = State.Home;
					break;
				}
				wander(dt);
				break;
			case Home:
				seek(homePoint);
				if(!(currentCell.row == home.row & currentCell.col == home.col)){
					if (hasFood) {
						double p = currentCell.pheromones.get(home);
						p += dt/300; 
						currentCell.pheroTimer = 0.00;
						currentCell.pheromones.put(home, p);						
					}
				}
				break;
				
			case Attack:
				seek(attackTarget.pos);
				break;
		}
		
		ArrayList<Ant> neighbors = new ArrayList<Ant>(); 
		for(Cell c: currentCell.neighbors){
			if(c == null){continue;}
			neighbors.addAll(c.ants);
		}
		neighbors.addAll(currentCell.ants);
		
		boolean targetNear = false;
		for(Ant a: neighbors){
			if (state != State.Attack && this.home != a.home && a.size < this.size) {
				double roll = Math.random();
				switch (state) {
					case Wander:
						if (roll < home.aggression) {
							attackTarget = a;
							state = State.Attack;
							targetNear = true;
						}
						break;
					case Follow:
						if (roll < home.aggression * currentCell.pheromones.get(home) / 10) {
							attackTarget = a;
							state = State.Attack;
							targetNear = true;
						}
						break;
					default:
						break;
				}
			}
			if (a != attackTarget) {
				avoid(leadPoint(a),100);
			}
			else {
				targetNear = true;
			}
		}
		
		if (state == State.Attack && !targetNear) {
			attackTarget = null;
			state = State.Wander;
		}
		
		// clamp velocity to maximum
		double mag = Point2D.Double.distance(vel.x, vel.y, 0, 0);
		if (mag > MAX_VELOCITY) {
			vel.x *= MAX_VELOCITY/mag;
			vel.y *= MAX_VELOCITY/mag;
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
		
		energy -= dt/60000;
		if (energy < ENERGY_THRESH) {
			this.state = State.Home;
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
		Point2D.Double vec = new Point2D.Double(vel.x,vel.y);
		scale(vec,size*2);
		Point2D.Double dir = new Point2D.Double(pos.x + vec.x, pos.y + vec.y);
		g.setStroke(new BasicStroke(2));
		g.drawLine((int) pos.x, (int) pos.y, (int) dir.x, (int) dir.y);
		g.setColor(Color.black);
		g.drawOval((int) (pos.x - size), (int) (pos.y - size), (int) size * 2, (int) size * 2);
		
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
			
			if (state == State.Attack && this.home != other.home && size > other.size) {
				other.energy -= 0.08;
				if (other.energy < 0) {
					attackTarget = null;
					state = State.Follow;
				}
			}
			
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
	
	private void wander(double dt) {
		Point2D.Double center = new Point2D.Double(vel.x * wanderCircleDistance, vel.y * wanderCircleDistance);
		Point2D.Double disp = new Point2D.Double(0, -1 * wanderCircleRadius);
		disp = getDisplacement(disp, wanderAngle);
		wanderAngle += Math.random() * wanderAngleChange - wanderAngleChange * 0.5;
		seek(new Point2D.Double(pos.x + center.x + disp.x, pos.y + center.y + disp.y));
	}
	
	private void thrust(Point2D.Double force) {
		scale(force, 0.5 * MAX_VELOCITY);
		this.vel.x += force.x;
		this.vel.y += force.y;
	}
	
	private Point2D.Double leadPoint(Ant a){
		Point2D.Double point = new Point2D.Double(a.pos.x, a.pos.y);
		
		double p = pos.distance(a.pos)/MAX_VELOCITY;
		
		point.x += a.vel.x * p;
		point.y += a.vel.y * p;
		return point;
	}
	
	private void seek(Point2D.Double target) {
		Point2D.Double aim = new Point2D.Double(target.x - pos.x, target.y - pos.y);
		scale(aim, MAX_VELOCITY);
		aim.x -= vel.x;
		aim.y -= vel.y;
		thrust(aim);
	}
	
	private void avoid(Point2D.Double target,double strength) {
		if(pos.distance(target)> strength * MAX_VELOCITY){
			return;
		}
		Point2D.Double aim = new Point2D.Double(target.x - pos.x, target.y - pos.y);
		scale(aim, (strength * MAX_VELOCITY)- pos.distance(target));
		aim.x = vel.x-aim.x;
		aim.y = vel.y-aim.y;
		thrust(aim);
	}
	
	private void scale(Point2D.Double vector, double val) {
		double len = vector.distance(new Point2D.Double(0, 0));
		if (len > val) {
			vector.x *= val/len;
			vector.y *= val/len;
		}
	}
	
	private Point2D.Double getDisplacement(Point2D.Double vector, double value) {
		Point2D.Double result = new Point2D.Double();
		double len = vector.distance(new Point2D.Double(0, 0));
		result.x = Math.cos(value) * len;
		result.y = Math.sin(value) * len;
		return result;
	}
	
	public void tryTakeFood(Cell cell){
		if (cell.food > 0 && !hasFood) {
			cell.food--;
			cell.growth *= 0.8;
			hasFood = true;
			this.state = State.Home;
		}
	}
	public void tryLeaveFood(Cell cell){
		if (state == State.Home && cell.row == home.row && cell.col == home.col) {
			if (hasFood) {
				home.food++;
				hasFood = false;
				this.energy = 2;
			}
			else if (home.food > 0) {
				home.food--;
				this.energy = 1;
			}
			this.state = State.Follow;
		}
	}
}
