package microabl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import microabl.abt.ABTNode;
import microabl.abt.ActionNode;
import microabl.abt.GoalNode;
import microabl.abt.ParallelNode;
import microabl.abt.SequentialNode;

public class ABTViewer extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {
 
	private JFrame frame;

	private double scale = 1.0;
	private double tx = 0;
	private double ty = 0;
	public int mx = 0;
	public int my = 0;
	private boolean mouseDown = false;	

    Font font = new Font("ariel", Font.BOLD, 12);
    int h = 25;
    int stackHeight = 35;

	double scaleAmmount = 0.9;
    
	private Agent agent; 
	
    public ABTViewer(Agent agent) {
    	this.agent = agent; 
		
		setPreferredSize(new Dimension(600, 600));
		frame = new JFrame("ABL Behavior Tree");
		frame.add(this);
		frame.pack();
		this.addMouseWheelListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		frame.setLocation(640, 0);
		frame.setVisible(true);		
	}

	public void paint(Graphics g) {
			
		Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, getWidth(), getHeight());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.translate(tx, ty);
        g2.scale(scale, scale); 

        int y = 0;
 
		synchronized (agent) {
	        for (ABTNode node : agent.getRoots()) {
	        	y = drawNode(g2, node, y, 0);
	        }
		}
	}
  
	public int drawNode(Graphics2D g2, ABTNode node, int y, int depth) {
		drawNode(g2, node, y, depth, h);				
		y++;
 
		for (ABTNode child : node.getChildren()) {
			y = drawNode(g2, child, y, depth + 1);
		}
		
		return y;
	}
 
	private void drawNode(Graphics2D g2, ABTNode node, int y, int depth, int h) {
 
		int tw = 20 + (15*node.toString().length()/2);
		g2.setColor(new Color(230, 230, 230));
		
		if (node instanceof GoalNode) {
			g2.setColor(Color.ORANGE.brighter());
		}
		else if (node instanceof ParallelNode) {
			g2.setColor(new Color(150, 255, 150));			
		}
		else if (node instanceof SequentialNode) {
			g2.setColor(new Color(150, 150, 255));			
		} 
		else if (node instanceof ActionNode) {
			g2.setColor(new Color(150, 255, 255));			
		}
		else {
			g2.setColor(new Color(255, 255, 255));			
		}
		
		RoundRectangle2D rect = new RoundRectangle2D.Double(50*depth, stackHeight*y, tw, h, 20, 20);
		g2.fill(rect);

		g2.setStroke(new BasicStroke(3.0f));
        g2.setColor(Color.GRAY.darker());
		g2.draw(rect);
                
        g2.setColor(Color.black);
        g2.setFont(font);
        g2.drawString(node.toString(), 50*depth + 10, stackHeight*y + 18);		
	}


	public void mouseWheelMoved(MouseWheelEvent e) {
		try {		
			if (e.getWheelRotation() > 0) {
				
				// compute the current mouse location in virtual coordinates
				double screenX = e.getX();
				double screenY = e.getY();			
				AffineTransform transform = new AffineTransform();
				transform.translate(tx, ty);
				transform.scale(scale, scale);
				double[] src = new double[] { screenX, screenY };
				double[] dest1 = new double[2];
				transform.inverseTransform(src, 0, dest1, 0, 1);
	
		        // do the scalle
				scale *= scaleAmmount;			
				
				// compute the new mouse location in virtual coordinates
				screenX = e.getX();
				screenY = e.getY();			
				transform = new AffineTransform();
				transform.translate(tx, ty);
				transform.scale(scale, scale);
				src = new double[] { screenX, screenY };
				double[] dest2 = new double[2];
				transform.inverseTransform(src, 0, dest2, 0, 1);
				
				tx += (dest2[0] - dest1[0])*scale;
				ty += (dest2[1] - dest1[1])*scale;						
			}
			else {			
				// compute the current mouse location in virtual coordinates
				double screenX = e.getX();
				double screenY = e.getY();			
				AffineTransform transform = new AffineTransform();
				transform.translate(tx, ty);
				transform.scale(scale, scale);
				double[] src = new double[] { screenX, screenY };
				double[] dest1 = new double[2];
				transform.inverseTransform(src, 0, dest1, 0, 1);
	
		        // do the scalle
				scale /= scaleAmmount;			
				
				// compute the new mouse location in virtual coordinates
				screenX = e.getX();
				screenY = e.getY();			
				transform = new AffineTransform();
				transform.translate(tx, ty);
				transform.scale(scale, scale);
				src = new double[] { screenX, screenY };
				double[] dest2 = new double[2];
				transform.inverseTransform(src, 0, dest2, 0, 1);
				
				tx += (dest2[0] - dest1[0])*scale;
				ty += (dest2[1] - dest1[1])*scale;
			}			
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		repaint();
	}

	public void mouseDragged(MouseEvent e) {
		
		if (mouseDown) {
			double dx = e.getX() - mx;
			double dy = e.getY() - my;
			
			tx += dx;
			ty += dy;
			
			mx = e.getX();
			my = e.getY();
		}
		
		repaint();
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == 3) {
			mouseDown = true;
			mx = e.getX();
			my = e.getY();
		}
 		
		repaint();
	}
	
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == 3) {
			mouseDown = false;
		}
		
		repaint();
	}

	public void mouseMoved(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}
}
