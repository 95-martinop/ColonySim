package colony;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * @author martinop Basic swing display window.
 */
public class Display extends JPanel implements Runnable {

	/** ignore this, req'd for serialization */
	private static final long serialVersionUID = -8586971630399848585L;
	private Thread master;

	private Grid grid;

	/**
	 * Displays an empty window.
	 * 
	 * @param d
	 */
	public Display() {
		grid = new Grid();
		master = new Thread(this);
		master.start(); // indirectly calls run()

		this.setFocusable(true);
		this.setBackground(Color.darkGray);
	}

	public void step(double dt) {
		// System.out.println(dt);
		grid.step(dt);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// draw whatever
		grid.draw(g2);
	}

	@Override
	public void run() {
		long next, now = System.currentTimeMillis();
		int dt;
		while (true) {
			if (!hasFocus()) {
				requestFocusInWindow();
			}
			next = System.currentTimeMillis();
			dt = (int) (next - now);
			now = next;
			step(20);//dt);
			repaint(); // indirectly calls paintComponent
			try {
				Thread.sleep(0);//50);
			} catch (Exception e) {

			}
		}
	}
}