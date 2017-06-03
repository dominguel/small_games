package chess2d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Grid extends JPanel {

	private int width;
	private int length;
	public static mouseWatch looker = new mouseWatch();

	private static BufferedImage[] piece = new BufferedImage[12]; {
		for(int i = 0; i < 12; i++) {
			String path = "C:\\Users\\Lucas\\Documents\\0 - Folder for external uses\\chessPieces\\" + Integer.toString(i) +".png";
			try {
				piece[i] = ImageIO.read(new File(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//fuck off eclipse
	private static final long serialVersionUID = 4902128140090362398L;

	public Grid(int length, int width) {
		super();
		setBackground (Color.WHITE);
		setVisible(true);
		this.width = width;
		this.length = length;
		this.setSize(width, length);
		this.addMouseListener(looker);
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		//drawing grid
		g.setColor(Color.GRAY);
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(i%2 == 0) {
					if(j%2 == 1) {
						g.fillRect(i*100, j*100, 100, 100);
					}
				} else {
					if(j%2 ==0) {
						g.fillRect(i*100, j*100, 100, 100);
					}
				}
			}
		}

		//drawing pieces
		int h;
		int l;
		
		for(int i = 1; i < 9; i++) {
			h = (i-1) * 100;
			for(int j = 1; j < 9; j++) {
				l = (j-1) * 100;
				switch(Engine.board[i][j]) {

				case 'k':
					g2d.drawImage(piece[10], l, h, this);
					break;
				case 'K':
					g2d.drawImage(piece[11], l, h, this);
					break;
				case 'q':
					g2d.drawImage(piece[8], l, h, this);
					break;
				case 'Q':
					g2d.drawImage(piece[9], l, h, this);
					break;
				case 'b':
					g2d.drawImage(piece[6], l, h, this);
					break;
				case 'B':
					g2d.drawImage(piece[7], l, h, this);
					break;
				case 'n':
					g2d.drawImage(piece[4], l, h, this);
					break;
				case 'N':
					g2d.drawImage(piece[5], l, h, this);
					break;
				case 'r':
					g2d.drawImage(piece[2], l, h, this);
					break;
				case 'R':
					g2d.drawImage(piece[3], l, h, this);
					break;
				case 'p':
					g2d.drawImage(piece[0], l, h, this);
					break;
				case 'P':
					g2d.drawImage(piece[1], l, h, this);
					break;
				default:
					break;
				}
			}
		}
	}

	// the getPReferredSize will make this JPanel preferentially be this size
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, length);
	}
}