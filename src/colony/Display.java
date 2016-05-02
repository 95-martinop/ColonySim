package colony;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * @author martinop
 * Basic swing display window.
 */
public class Display extends JPanel implements Runnable {
	
	/** ignore this, req'd for serialization */
	private static final long serialVersionUID = -8586971630399848585L;
	private Thread master;
	
	ArrayList<Ant> ants;
	
	/**
	 * Displays an empty window.
	 * @param d
	 */
	public Display() {
		master = new Thread(this);
		master.start(); // indirectly calls run()
		
		this.setFocusable(true);
		this.setBackground(Color.darkGray);
		
		ants = new ArrayList<Ant>();
		for(int i = 0; i < 25; i ++){
			ants.add(new Ant());
		}
	}
	
	public void step(double dt){
		for(int i = 0; i < ants.size(); i ++){
			ants.get(i).step(dt);;
		}
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		// draw whatever
		for(int i = 0; i < ants.size(); i ++){
			ants.get(i).draw(g2);
		}
	}

	@Override
	public void run() {
		long next, now = System.currentTimeMillis();
		int dt;
		while (true)
		try {
			if(!hasFocus()) requestFocusInWindow();
			next = System.currentTimeMillis();
			dt = (int)(next-now); now = next;
			step(dt);
			repaint(); // indirectly calls paintComponent
			Thread.sleep(50);
		} catch (Exception e) {
			// go back to sleep, little one
		}
	}
}