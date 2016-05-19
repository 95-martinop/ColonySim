package colony;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

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
	private boolean hasFood = false;
	private Point2D.Double homePoint;
	private Colony home;
	public static Double pherThresh=  1.0;
	private Double wanderChance=.1;
	private double offPathChance=.1;
	private Point2D.Double followPoint;
	private Cell currentCell;
	private Colony Colony;
	
	private final double MAX_VELOCITY;
	
	private enum State {
		Follow, Wander, Home, OffPath, JoinPath
	}
	
	private State state = State.Wander;

	public Ant(Colony Colony) {
		this.home = Colony;
		size = DisplayGUI.CELLWIDTH / 8.0; // 8 is correct... Orion says so
		size = Math.random() * size + size;
		energy = Math.random();
		this.Colony = Colony;
		
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
				if (currentCell.pheromones.get(this.home)>pherThresh){
					steer.x = this.pos.x - this.homePoint.x;
					steer.y = this.pos.y - this.homePoint.y;
					seek(steer);
					break;
				}
			case OffPath:
				if(Math.random() < wanderChance){
					this.state = State.Wander;
				}
				else {
					Double totalPher = 0.0;
					ArrayList<Cell> cells = new ArrayList<Cell>();
					for (Cell c :currentCell.neighbors){
						if(c == null) continue;
						int dr = c.row-home.row;
						int dc = c.col-home.col;
						if((dr*dr+dc*dc)*(DisplayGUI.CELLWIDTH*DisplayGUI.CELLWIDTH) > pos.distanceSq(homePoint)){
							totalPher+=c.pheromones.get(home);
							cells.add(c);
						}
					}

					int i = 0;
					totalPher = Math.random()*totalPher;
					while (totalPher>0){
						for (i=0;i<cells.size();i++){
							totalPher-= cells.get(i).pheromones.get(home);
							if (totalPher <= 0) break;
						}
					}
					//point towards cell
					if(i < cells.size())
						this.followPoint = new Point2D.Double((cells.get(i).row+.5)*DisplayGUI.CELLWIDTH,(cells.get(i).col+.5)*DisplayGUI.CELLWIDTH);
				}
				break;
			case JoinPath:
				if(Math.random()<offPathChance){
					this.state = State.OffPath;
				}
				else {
					if(currentCell.pheromones.get(this.home)>pherThresh){
						this.state = State.Follow;
					}
					else{
						double mag = steer.distance(new Point2D.Double(0, 0));
						steer.x = this.followPoint.x = this.pos.x;
						steer.y = this.followPoint.y - this.pos.y;
						mag = steer.distance(new Point2D.Double(0, 0));
						if (mag > maxWanderForce) {
							steer.x *= maxWanderForce/mag;
							steer.y *= maxWanderForce/mag;
						}
					}
				}
				break;
			case Wander:
				if(currentCell.pheromones.get(this.home)>pherThresh){
					this.state = State.Follow;
					break;
				}
				steer = wander(dt);
				double mag = steer.distance(new Point2D.Double(0, 0));
				if (mag > maxWanderForce) {
					steer.x *= maxWanderForce/mag;
					steer.y *= maxWanderForce/mag;
				}
				// steer
				vel.x += steer.x;
				vel.y += steer.y;
				break;
			case Home:
				seek(homePoint);
				double p = currentCell.pheromones.get(home);
				p += dt/1000; 
				currentCell.pheromones.put(home, p);
				break;
				
		}
		
		ArrayList<Ant> neighbors = new ArrayList<Ant>(); 
		for(Cell c: currentCell.neighbors){
			if(c == null){continue;}
			neighbors.addAll(c.ants);
		}
		neighbors.addAll(currentCell.ants);
		
		for(Ant a: neighbors){
			avoid(leadPoint(a),100);
		}
		
		
		//if(state != State.Wander) System.out.println(state);
		
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
		//System.out.println(wanderAngle);
		
		return new Point2D.Double((center.x + disp.x), (center.y + disp.y));
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
		if (cell.row == home.row && cell.col == home.col && hasFood) {
			home.food++;
			hasFood = false;
			this.state = State.Wander;
		}
	}
}
