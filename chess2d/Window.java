package chess2d;

import java.awt.Point;

import javax.swing.JFrame;

public class Window {

	public static JFrame f;
	public static Grid p;
	
	public static Point mousePosition;

	public static void start() {

		//init
		f = new JFrame("My Chess v2.0");

		//utile
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setSize(900, 900);

		//make p
		p = new Grid(800, 800);
		f.add(p);
		f.pack();

		//show
		f.setVisible(true);
	}

	public static void drawState() {
		
		p.repaint();
	}
	
	public static String waitForMove() throws InterruptedException {
		
		String out = "";

		mousePosition = new Point(-1,-1);
		Grid.looker.enable();
		while(true) {
			if(mousePosition.getX() != -1 && mousePosition.getY() != -1) {
				break;
			}
			Thread.sleep(350);
		}
		
		//translate mousePosition to String values, add to out
		int x = (int) mousePosition.getX();
		x = x/100 + 1;
		if(x == 9) {
			x--;
		}
		out += Engine.alpha8.charAt(x);
		
		int y = (int) mousePosition.getY();
		y = y/100 + 1;
		if(y == 9) {
			y--;
		}
		out += Engine.numer8.charAt(y);
		
		mousePosition = new Point(-1,-1);
		while(true) {
			if(mousePosition.getX() != -1 && mousePosition.getY() != -1) {
				break;
			}
			Thread.sleep(350);
		}
		out += " ";
		
		//translate mousePosition to String values, add to out
		x = (int) mousePosition.getX();
		x = x/100 + 1;
		if(x == 9) {
			x--;
		}
		out += Engine.alpha8.charAt(x);
		
		y = (int) mousePosition.getY();
		y = y/100 + 1;
		if(y == 9) {
			y--;
		}
		out += Engine.numer8.charAt(y);
		//check for weird cases like castling
		
		Grid.looker.disable();
		mousePosition = new Point(-1,-1);
		
		return out;
	}
}