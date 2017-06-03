package chess2d;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class mouseWatch implements MouseListener {
	
	private boolean enabled;
	
	public mouseWatch() {
		enabled = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		if(enabled) {
			Window.mousePosition = e.getPoint();
		}
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

}