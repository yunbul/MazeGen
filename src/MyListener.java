package codes;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class MyListener extends MouseAdapter {

	public void mousePressed(MouseEvent e) {
		
	       Cell cellPanel = (Cell)e.getSource();
	       
		       if(Main.setStart){
		    	   //if maze is resized to smaller version -> cells[startIndex] causes ArrayIndexOutOfBoundException
		    	   try{ Main.cells[Main.startIndex].setBackground(Color.WHITE); }catch(Exception ex){}
	    		   cellPanel.setBackground(Color.GREEN);
		    	   Main.startIndex = cellPanel.getIndex();
		    	   System.out.println("start index : " + Main.startIndex);
	    	   }
	    	   if(Main.setFinish){
	    		   //same as situation for cells[finishIndex]
	    		   try{ Main.cells[Main.finishIndex].setBackground(Color.WHITE); }catch(Exception ex){}
	    		   cellPanel.setBackground(Color.RED);
		    	   Main.finishIndex = cellPanel.getIndex();
		    	   System.out.println("finish index : " + Main.finishIndex);
	    	   }
	       
	       Main.setStart = false;
	       Main.setFinish = false;
	       Main.btnSetFinish.setSelected(false);
	       Main.btnSetStart.setSelected(false);
	       
	}
	
}
