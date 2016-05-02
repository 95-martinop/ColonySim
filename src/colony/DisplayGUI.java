package colony;

import javax.swing.*;

import java.awt.*;
/**
 * @author martinop
 * 
 */
@SuppressWarnings("javadoc")
public class DisplayGUI extends JFrame{
	
	public static int WIDTH = 600, HEIGHT = 600;
	public static int CELLWIDTH = 40;
	
	private static final long serialVersionUID = 1L;
	private Display view;

	/**
	 * Sets up a fresh ViewGUI for display.
	 * 
	 */
	public DisplayGUI() {
		this.view = new Display();
		
		this.add(this.view, BorderLayout.CENTER);
		this.setSize(WIDTH+16, HEIGHT+38);
		this.setVisible(true);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String args[]){
		new DisplayGUI();
	}
}
