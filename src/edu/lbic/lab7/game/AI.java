package edu.lbic.lab7.game;

import java.io.IOException;

import edu.lbic.lab7.exceptions.OutsideBoardException;

public class AI extends GameEngine{
	
	private static Player p1,AI;
	private GUI gui;
	private int lastMoveColumn,lastMoveRow;
	
	protected AI(GUI gui){
		super(p1, AI);
		this.gui=gui;
		
	}

	public boolean avoidFive() throws OutsideBoardException{
		
		lastMoveColumn = engine.getLastColClicked();
		try {
			lastMoveRow = board.getROWS() - board.getColumnHeight(lastMoveColumn);
		} catch (OutsideBoardException e) {
			e.printStackTrace();
		}
		//search.Down , search.left col,row diagonal
		if (threatCheck(SearchNode.LEFT, SearchNode.DOWN, "diagonal")){
			return true;
		}
		//search.left , search.idle col,row horizontal
		else if (threatCheck(SearchNode.LEFT, SearchNode.IDLE, "horizontal")){
			return true;
		}//search.idle , search.down col,row vertical
		else if (threatCheck(SearchNode.IDLE,SearchNode.DOWN, "vertical")){
			return true;
		}
		else{
			return false;
		}
	}
	private boolean threatCheck(SearchNode horzSearchDir, SearchNode vertSearchDir,String searchMode) throws OutsideBoardException
	{
		int row=0 ;
		int col=0 ;

		if (searchMode.equals("diagonal"))
		{
			int mod = topRightBoundsModif(lastMoveColumn, lastMoveRow);
			
			System.out.println("mod :"+ mod);
			
			row = Math.abs(mod-lastMoveRow);
			
			System.out.println("row :"+row+ " "+mod+"-"+lastMoveRow);
			
			col = mod+lastMoveColumn;
			
			System.out.println("col :"+col+ " "+mod+"+"+lastMoveColumn);

		}
		else if (searchMode.equals("horizontal"))
		{
			row = rightBoundsY();
			col = board.getCOLUMNS()-1;
		}
		else if (searchMode.equals("vertical"))
		{
			row = 0;
			col = lastMoveColumn;
		}

		int curRow = row;
		int curCol = col;


		String count0 = "";

		while (!outOfBounds(curRow, curCol)){ //search the whole row
			
			int nodeBelongs = board.getBoardPos(curRow, curCol);
			if(nodeBelongs==1){
				count0+=1;
			}
			else if (nodeBelongs==2){
				count0+=2;
			}
			else if(nodeBelongs==0){
				count0+=0;
			}
			System.out.println("curCol :"+curCol+"    curRow : "+curRow);
			
			curCol += horzSearchDir.getValue();
			curRow += vertSearchDir.getValue();
			
			System.out.println("curCol :"+curCol+"    curRow : "+curRow);
			
		}

		return paternCheck(count0,col,row,searchMode);

	}
	private boolean paternCheck(String count0,int colCheck,int rowCheck,String searchMode) throws OutsideBoardException{
		System.out.println("count0 :"+count0);
		
		String[] pat = count0.split("");
        
		for (int i = 0; i < pat.length; i++) {
			System.out.print("pat["+i+"] "+pat[i]+" ");
		}
		System.out.println();
        if (count0.contains("11110")){ //check for patterns
        	placeDisc(pat, colCheck, rowCheck, 4,"11110", searchMode);
        	return true;
        }
        else if (count0.contains("11101")){
        	placeDisc(pat, colCheck, rowCheck, 3,"11101", searchMode);
        	return true;
        }
        else if (count0.contains("11011")){
        	placeDisc(pat, colCheck, rowCheck, 2,"11011", searchMode);
        	return true;
        }
        else if (count0.contains("10111")){
        	placeDisc(pat, colCheck, rowCheck, 1,"10111", searchMode);
        	return true;
        }
        else if (count0.contains("01111")){
        	placeDisc(pat, colCheck, rowCheck, 0,"01111", searchMode);
        	return true;
        }
        else{
        	return false;
        }
	}
	private void placeDisc(String[] pat,int col,int row ,int pos, String patern,String searchMode) throws OutsideBoardException{
		for (int i = 0; i < pat.length; i++) {
			if ( (pat[i]+pat[i+1]+pat[i+2]+pat[i+3]+pat[i+4]).equals(patern) ){
				System.out.println((pat[i]+pat[i+1]+pat[i+2]+pat[i+3]+pat[i+4])+" = "+patern);
				int mod = i+pos;
				System.out.println("mod :"+mod);
				int R=row,C=col;
				System.out.println("col :"+C+"\n Row :"+R);
				System.out.println();
				if (searchMode.equals("diagonal")){
				R +=mod;
				
				C -=mod;
				System.out.println("diagonal");

				}
				else if (searchMode.equals("horizontal")){
					System.out.println("horizontal");
					R+=0;
					C-=mod;
				}
				else if (searchMode.equals("vertical")){
					//mod=Math.abs(mod-getROWS()-1);
					R+=mod;
					C+=0;
				}
				else{
					System.out.println("bug");
				}
				
				System.out.println("col :"+C+"\n Row :"+R);
				
				try 
				{
					if (outOfBounds(R+1, C)){ //if out of bounds place disc
						gui.putDisc(C);
						return;
					}
					else if (board.getBoardPos(R+1, C)==0){ //if node below threat is empty don't fill it

						gui.randomMoveAR(C); //except C
						return;
					}
					else{
						gui.putDisc(C);
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
	}
	public int rightBoundsY() throws OutsideBoardException{ //row of last clicked node
		return board.getROWS()-board.getColumnHeight(lastMoveColumn);
	}
	/**
	 * Modifier for reaching top right (+ modif column - modif row)
	 */
	public int topRightBoundsModif(int lastMoveColumn,int lastMoveRow){ 
		int i = lastMoveColumn + lastMoveRow;
		if (i>board.getCOLUMNS()-1){
			i=board.getCOLUMNS()-1;
		}
		return Math.abs(i-lastMoveColumn);
	}

}