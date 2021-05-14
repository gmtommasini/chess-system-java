package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece{

	public King(Board board, Color color) {
		super(board, color);
	}
	
	@Override
	public String toString() {
		return "K";
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p == null || p.getColor() != this.getColor();
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean [][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0); // safe initialization
		
		//above
		p.setValues(position.getRow()-1, position.getColumn()); //above the king
		if(getBoard().positionExists(p) &&  canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow()+1, position.getColumn()); //below the king
		if(getBoard().positionExists(p) &&  canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow(), position.getColumn()-1); //left of the king
		if(getBoard().positionExists(p) &&  canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow(), position.getColumn()+1); //right of the king
		if(getBoard().positionExists(p) &&  canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		
		p.setValues(position.getRow()-1, position.getColumn()-1); //NW of the king
		if(getBoard().positionExists(p) &&  canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow()-1, position.getColumn()+1); //NE of the king
		if(getBoard().positionExists(p) &&  canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow()+1, position.getColumn()-1); //SW of the king
		if(getBoard().positionExists(p) &&  canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow()+1, position.getColumn()+1); //SE of the king
		if(getBoard().positionExists(p) &&  canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		return mat;
	}
	

}
