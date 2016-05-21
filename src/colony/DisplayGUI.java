package colony;

import javax.swing.*;

import java.awt.*;
/**
 * @author martinop
 * 
 */
@SuppressWarnings("javadoc")
public class DisplayGUI extends JFrame{
	
	public static int WIDTH = 1000, HEIGHT = 800;
	public static int CELLWIDTH = 20;//WIDTH AND HEIGHT MUST BE DIVISIBLE BY CELLWIDTH
	
	private static final long serialVersionUID = 1L;
	private Display view;
	//private double[] agression;

	/**
	 * Sets up a fresh ViewGUI for display.
	 * 
	 */
	public DisplayGUI( ) {
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
