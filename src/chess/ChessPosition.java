package chess;

import boardgame.Position;
import chess.exceptions.ChessException;

public class ChessPosition {
	/*ChessPosition has the "a1" format, it goes from a1 to a8 until h1 to h8*/
	private char column;
	private int row;

	public ChessPosition(char column, int row) {
		if (column < 'a' || column > 'h' || row < 1 || row > 8) {
			throw new ChessException("Error instantiating ChessPosition. Valid values are from a1 to h8");
		}
		this.column = column;
		this.row = row;
	}

	public char getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	protected Position toPosition() {
		return new Position(8 - row, column - 'a');
	}

	protected static ChessPosition fromPosition(Position pos) {
		return new ChessPosition((char) ('a' + pos.getColumn()), 8 - pos.getRow());
	}

	@Override
	public String toString() {
		// return String.format("%c%d", column, row);
		return "" + column + row;
	}

}
