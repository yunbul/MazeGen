package codes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Main extends JFrame
{
	private JPanel cellsPanel = new JPanel();
	private final Font font = new Font(Font.SERIF, Font.PLAIN, 18);
	
	public static Integer width = 10;
	public static Integer height = 10;
	public static Cell[] cells = new Cell[width*height];
	
	public static JRadioButton btnSetStart, btnSetFinish, btnSimulate;
	public static boolean setStart;
	public static boolean setFinish;
	public static int startIndex = 0;
	public static int finishIndex = width*height - 1;
	
	private long elapsedTime = 0;
	private int loopCount = 0;
	private int slackCount = 0;
	private ArrayList<String> unCheckedWalls = new ArrayList<String>(); 
	private boolean checkingWalls = false;
	
	private Thread t;
	private final int sleepTime = 10; //millis
	private Cell currentCell;
	private Collection<Cell> visitedCells = new HashSet<Cell>();
	private Stack<Cell> cellStack = new Stack<Cell>();
	private boolean mazeCreated = true;
	private boolean mazeGenerated = false;
	
	private JLabel message = new JLabel(" ");
	
	private void refreshCellsPanel() {
		remove(cellsPanel);
		cellsPanel = new JPanel();
		cellsPanel.setLayout(new GridLayout(height, width));
		for (int i = 0; i < width*height; i++) {
			cellsPanel.add(cells[i]);
		}
		add(cellsPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
		pack();
		System.out.println("cells panel refreshed.");
	}
	
	private void initCells(){
		cells = new Cell[width*height];
		for (int i = 0; i < width*height; i++) {
			cells[i] = new Cell(i);
		}
		System.out.println("cells[] initialized.");
	}
	
	private void initLegalNeighbours(){
		/*FUNCTION THAT REMOVES INVALID NEIGHBOURS FOR EACH CELL AT EDGES*/
		//upper edge
		for (int i = 0; i < width; i++) {
			cells[i].visitableNeighbours.remove("N");
		}
		//left edge
		for (int j=width, i = 0; i < height  ; i++) {
			cells[i*j].visitableNeighbours.remove("W");
		}
		//right edge
		for (int j=width, i = 1; i <= height  ; i++) {
			cells[(i*j)-1].visitableNeighbours.remove("E");
		}
		//bottom edge
		for (int i = (height-1)*width; i < width*height  ; i++) {
			cells[i].visitableNeighbours.remove("S");
		}
		System.out.println("neighbours initialized.");
	}

	private void removeWall(Cell c, String neigh){
		int i = c.getIndex();
		switch (neigh) {
		case "N":
			cells[i].removeNorth();
			cells[i-width].removeSouth();
			break;
		case "S":
			cells[i].removeSouth();
			cells[i+width].removeNorth();
			break;
		case "W":
			cells[i].removeWest();
			cells[i-1].removeEast();
			break;
		case "E":
			cells[i].removeEast();
			cells[i+1].removeWest();
			break;
		default:
			break;
		}
	}
	
	private Cell getNeighbour(Cell c, String neigh){
		int i = c.getIndex();
		switch (neigh) {
		case "N":
			i = i-width;
			break;
		case "S":
			i = i+width;
			break;
		case "W":
			i--;
			break;
		case "E":
			i++;
			break;
		default:
			break;
		}
		return cells[i];
	}
	
	/*FUNCTION THAT RETURNS IF ALL NEIGHBOURS OF CELL ARE VISITED*/
	private boolean hasNoWay(Cell c){
		//return if there is no unvisited cells exist in visitableNeighbours
		for(int i=0 ; i < c.visitableNeighbours.size() ; i++){
			if( ! visitedCells.contains(getNeighbour(c, c.visitableNeighbours.get(i))) ){
				return false;
			}
		}
		return true;
	}
	
	/*DFS ALGORITHM THAT GENERATES MAZE*/
	private void generateMaze(){
		
		loopCount = slackCount = 0; elapsedTime = 0;
		long init = System.currentTimeMillis();
		
		cellStack = new Stack<Cell>();
		visitedCells = new HashSet<Cell>();
		currentCell = cells[startIndex];
		cellStack.push(currentCell);
		visitedCells.add(currentCell);
		
		Runnable generate = new Runnable() {
			
			@Override
			public void run() {
				try {
					while( visitedCells.size() < width*height ){
						
						currentCell = cellStack.peek();
						currentCell.setBackground(Color.YELLOW);
						
						String str = currentCell.getRandomNeighbourString();
						
						if( ! visitedCells.contains(getNeighbour(currentCell, str)) ){
							removeWall(currentCell, str);
							cellStack.push(getNeighbour(currentCell, str));
							visitedCells.add(getNeighbour(currentCell, str));
						}
						else if(hasNoWay(currentCell)){
							cellStack.pop();
						}
						else
							++slackCount;
						
						if(btnSimulate.isSelected())
							Thread.sleep(sleepTime);
						
						currentCell.setBackground(Color.WHITE);
						++loopCount;
					}
					
					elapsedTime = System.currentTimeMillis() - init;
					System.out.println("generated.");
					message.setForeground(Color.BLACK);
					message.setText("Maze generated.");
					//total run time of generateMaze
					System.out.println("run time : " + elapsedTime + " millis." + " > slack : " + (slackCount*100/loopCount) + "%");
					
					/*IF BTNSIMULATE SELECTED RUNTIME OF ALGORITHM INCREASES ACCORDING TO SLEEPTIME*/
					if(btnSimulate.isSelected()){
						//lost time due to getRandomNeighbour
						System.out.println("slack time : " + slackCount*sleepTime + " millis.");
						//lost time due to simulation
						System.out.println("simulation time : " + loopCount*sleepTime + " millis");
						//exact runtime of algorithm /w simulation sleep
						System.out.println("exact run time : " + (elapsedTime - loopCount*sleepTime) + " millis.");
					}
					System.out.println("************************");
					cells[startIndex].setBackground(Color.GREEN);
					cells[finishIndex].setBackground(Color.RED);
					
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t = new Thread(generate);
		t.start();
		
	}
	
	/*DFS ALGORITHM THAT SOLVES MAZE*/
	private void solveMaze(){
		
		loopCount = 0; elapsedTime = 0;
		long init = System.currentTimeMillis();
		
		checkingWalls = false;
		cellStack = new Stack<Cell>();
		visitedCells = new HashSet<Cell>();
		currentCell = cells[startIndex];
		cellStack.push(currentCell);
		visitedCells.add(currentCell);
		
		Runnable solve = new Runnable() {
			
			@Override
			public void run() {
				try {
					while( ! currentCell.isFinish() ){
						
						currentCell = cellStack.peek();
						currentCell.setBackground(Color.YELLOW);

						//if we are checking walls of the same cell, dont modify unCheckedWalls
						if( ! checkingWalls )
							unCheckedWalls.addAll(currentCell.walls);
						
						//get a random neighbour
						String str = currentCell.getRandomNeighbourString();
						
						//if neighbour is not visited
						if( ! visitedCells.contains(getNeighbour(currentCell, str)) ){
							
							//and if there is no wall between -> make neighbour the currentCell
							if( ! currentCell.walls.contains(str) ){
								cellStack.push(getNeighbour(currentCell, str));
								visitedCells.add(getNeighbour(currentCell, str));
								checkingWalls = false;
							}
							else if( unCheckedWalls.isEmpty() ){
								//if there is no unchecked wall
								//it means this cell is stuck thus, pop it from stack
								checkingWalls = false;
								currentCell.setBackground(Color.WHITE);
								cellStack.pop();
							}
							else{
								//here we start to check which walls is free
								//we shouldnt permit the uncheckedWalls to refresh itself 
								//because it gets currentCell.walls array
								//and currently checked wall should be removed from uncheckedWalls
								checkingWalls = true;
								unCheckedWalls.remove(str);
							}
						}
						else if(hasNoWay(currentCell)){
							//if all neighbours are visited pop cell from stack
							currentCell.setBackground(Color.WHITE);
							cellStack.pop();
						}
						
						if(btnSimulate.isSelected())
							Thread.sleep(sleepTime);
						
						++loopCount;
					}
					
					elapsedTime = System.currentTimeMillis() - init;
					System.out.println("solved.");
					message.setForeground(Color.BLACK);
					message.setText("Maze solved.");
					//total run time of generateMaze
					System.out.println("run time : " + elapsedTime + " millis.");
					
					/*IF BTNSIMULATE SELECTED RUNTIME OF ALGORITHM INCREASES ACCORDING TO SLEEPTIME*/
					if(btnSimulate.isSelected()){
						//lost time due to simulation
						System.out.println("simulation time : " + loopCount*sleepTime + " millis");
						//exact runtime of algorithm /w simulation sleep
						System.out.println("exact run time : " + (elapsedTime - loopCount*sleepTime) + " millis.");
					}
					System.out.println("************************");
					cells[startIndex].setBackground(Color.GREEN);
					cells[finishIndex].setBackground(Color.RED);
					
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t = new Thread(solve);
		t.start();
	}
	
	/*HERE IS ALL GUI DESIGN AND BUTTON ACTIONS DEFINED*/
	public Main(){
		super("Maze Gen");
		setLayout(new BorderLayout());
		add(cellsPanel, BorderLayout.CENTER);
		
		/*CREATE CELLS AND ADD THEM TO PANEL*/
		initCells();
		initLegalNeighbours();
		refreshCellsPanel();
		
		/*CREATE CONTROL PANEL*/ 
		//inputs - btnGen - btnSolv - radios
		JPanel ctrlPanel = new JPanel();
		ctrlPanel.setLayout(new GridLayout(7, 1, 10, 5)); //row ,col ,gap,gap
		
		JPanel inputsPanel = new JPanel();
		inputsPanel.setLayout(new GridLayout(1, 3, 5, 5)); //row ,col ,gap,gap
		JTextField enterWidth = new JTextField(width.toString());
		enterWidth.setFont(font);
		JLabel xLabel = new JLabel("X");
		xLabel.setFont(font);
		JTextField enterHeight = new JTextField(height.toString());
		enterHeight.setFont(font);
		inputsPanel.add(enterWidth);
		inputsPanel.add(xLabel);
		inputsPanel.add(enterHeight);
		
		JButton btnCreate = new JButton("Create");
		btnCreate.setFont(font);
		btnCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					width = Integer.parseInt(enterWidth.getText());
					height = Integer.parseInt(enterHeight.getText());
					System.out.println("inputs : " + width + " x " + height);
					initCells();
					initLegalNeighbours();
					refreshCellsPanel();
					startIndex = 0;
					finishIndex = width*height - 1;
					mazeCreated = true;
					mazeGenerated = false;
					message.setForeground(Color.BLACK);
					message.setText("Maze created.");
				}
				catch(Exception e){
					width = 10;
					height = 10;
					System.err.println("input width x height error.");
				}
			}
		});
		btnCreate.setPreferredSize(new Dimension(100,40));
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setFont(font);
		btnGenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(startIndex != finishIndex && mazeCreated){
					System.out.println("generating maze.");
					generateMaze();
					mazeCreated = false;
					mazeGenerated = true;
				}
				else{
					System.err.println("set start/finish or create maze.");
					message.setForeground(Color.RED);
					message.setText("Create maze first !!!");
				}
					
			}
		});
		btnGenerate.setPreferredSize(new Dimension(100,40));
		
		JButton btnSolve = new JButton("Solve");
		btnSolve.setFont(font);
		btnSolve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(startIndex != finishIndex && mazeGenerated){
					/*TODO :**************************************
					 * CHOOSE EITHER CLEARING THE PATH FOR A RESOLVE RUN (in case of small mazes) 
					 * BUT THIS METHOD HAS A BUG : repaint thread cant catch up solve (I guess)
					 * OR MAKING mazeGenerated FLAG FALSE FOR A REGENERATE MUST (for larger mazes) 
					 * *******************************************/
					/*//clear path
					for (int i = 0; i < cells.length; i++) {
						cells[i].setBackground(Color.WHITE);
					}*/
					mazeGenerated = false;
					
					cells[startIndex].setBackground(Color.GREEN);
					cells[finishIndex].setBackground(Color.RED);
					System.out.println("path cleared.");
					System.out.println("solving maze.");
					solveMaze();
					
				}
				else{
					System.err.println("set start/finish or generate maze.");
					message.setForeground(Color.RED);
					message.setText("Generate maze first !!!");
				}
			}
		});
		btnSolve.setPreferredSize(new Dimension(100, 40));
		
		btnSetStart = new JRadioButton("Set Start");
		btnSetStart.setFont(font);
		btnSetStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnSetFinish.setSelected(false);
				setFinish = false;
				setStart = true;
				System.out.println("set start.");
			}
		});
		
		btnSetFinish = new JRadioButton("Set Finish");
		btnSetFinish.setFont(font);
		btnSetFinish.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnSetStart.setSelected(false);
				setFinish = true;
				setStart = false;
				System.out.println("set finish.");
			}
		});
		
		btnSimulate = new JRadioButton("Simulate");
		btnSimulate.setFont(font);
		
		ctrlPanel.add(inputsPanel);
		ctrlPanel.add(btnCreate);
		ctrlPanel.add(btnSetStart);
		ctrlPanel.add(btnSetFinish);
		ctrlPanel.add(btnSimulate);
		ctrlPanel.add(btnGenerate);
		ctrlPanel.add(btnSolve);
		
		message.setFont(font);
		/*ADD COMPONENTS TO FRAME*/
		add(message, BorderLayout.NORTH);
		add(ctrlPanel, BorderLayout.EAST);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
		Main m = new Main();
	}

}
