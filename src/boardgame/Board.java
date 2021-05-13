package boardgame;

import boardgame.exceptions.BoardException;

public class Board {
	private int rows;
	private int columns;
	private Piece[][] pieces;
	
	/*** Constructors ***/
	public Board(int rows, int columns) {
		if (rows < 1 || columns < 1) {
			throw new BoardException("Error creating board: At least one row and one column are necessary.");
		}
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}

	/*** Getters ***/
	public int getRows() {
		return rows;
	}
	public int getColumns() {
		return columns;
	}

	/*** Functions ***/
	public Piece piece(int r, int c) {
		if(!positionExists(r, c)) {
			throw new BoardException(String.format("Position (%d%d) not on the board", r,c));
		}
		return pieces[r][c];
	}
	public Piece piece(Position pos) {
		return pieces[pos.getRow()][pos.getColumn()];
	}

	public void placePiece(Piece piece, Position pos) {
		if(thereIsAPiece(pos)) {
			throw new BoardException("These is already a piece on the position " + pos);
		}
		pieces[pos.getRow()][pos.getColumn()] = piece;
		piece.position = pos;
	}
	public Piece removePiece(Position pos) {
		if(!positionExists(pos)) {
			throw new BoardException("Position " + pos +" not on the board");
		}
		if(piece(pos) == null) {
			return null;
		}
		Piece temp =  piece(pos);
		temp.position=null; //piece off the board
		pieces[pos.getRow()][pos.getColumn()] = null; // empty position??
		return temp;
	}

	private boolean positionExists(int row, int col) {
		return row >= 0 && row < rows && col >= 0 && col < columns;
	}
	public boolean positionExists(Position pos) {
		return positionExists(pos.getRow(), pos.getColumn());
	}

	public boolean thereIsAPiece(Position pos) {
		if(!positionExists(pos)) {
			throw new BoardException("Position " + pos +" not on the board");
		}
		return piece(pos) != null;
	}

}
