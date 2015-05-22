package codes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Cell extends JPanel{

	private int index;
	private int prefferedLenght = 25;
	public ArrayList<String> walls = new ArrayList<String>(); //N , S , W , E
	public ArrayList<String> visitableNeighbours = new ArrayList<String>(); //visitable neighbours
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.BLACK);
		
		int w = getWidth();
		int h = getHeight();
		
		//lines may be deleted depending on the walls array
		if(walls.contains("N"))
			g2d.drawLine(0, 0, w, 0);  			//NORTH : -->
		if(walls.contains("S"))
			g2d.drawLine(w, h-1, 0, h-1);   	//SOUTH : <--
		if(walls.contains("W"))
			g2d.drawLine(0, h, 0, 0);           //WEST  : | ^
		if(walls.contains("E"))
			g2d.drawLine(w-1, 0, w-1, h);   	//EAST  : | v	
		
	}
	
	/*EACH CELL IS A PANEL THAT HAS LINES AROUND EDGES*/
	public Cell(int _index){
		index = _index;
		walls.add("N");
		walls.add("S");
		walls.add("W");
		walls.add("E");
		//neighbours may be deleted with initLegalNeigh() //see Main
		visitableNeighbours.add("N");
		visitableNeighbours.add("S");
		visitableNeighbours.add("W");
		visitableNeighbours.add("E");
		
		MouseAdapter mL = new MyListener();
		addMouseListener(mL);
		
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(prefferedLenght, prefferedLenght));
		repaint();
	}
	
	public int getIndex(){
		return index;
	}
	
	public String getRandomNeighbourString(){
		return visitableNeighbours.get( (int)( Math.random() * visitableNeighbours.size() ) );
		
	}
	
	public boolean isFinish(){
		if(Main.cells[Main.finishIndex].equals(this))
			return true;
		return false;
	}
	
	public void removeNorth(){
		walls.remove("N");
		repaint();
	}
	public void removeSouth(){
		walls.remove("S");	
		repaint();
	}
	public void removeWest(){
		walls.remove("W");
		repaint();
	}
	public void removeEast(){
		walls.remove("E");
		repaint();
	}
	
}
