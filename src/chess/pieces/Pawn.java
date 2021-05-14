package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	public Pawn(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "p";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0); // just instantiating a position

		// JUST WHITES
		if (this.getColor() == Color.WHITE) {
			p.setValues(position.getRow() - 1, position.getColumn());
			// ahead
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			// pawn's first move can be 2
			if (this.getMoveCount() == 0) {
				p.setValues(position.getRow() - 2, position.getColumn());
				Position p2 = new Position(position.getRow() - 1, position.getColumn());
				// ahead
				if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && !getBoard().thereIsAPiece(p2)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
			// capturing NE
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			// capturing NW
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
		} else {// BLACKS
			p.setValues(position.getRow() + 1, position.getColumn());
			// ahead
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			// pawn's first move can be 2
			if (this.getMoveCount() == 0) {
				p.setValues(position.getRow() + 2, position.getColumn());
				Position p2 = new Position(position.getRow() + 1, position.getColumn());
				// ahead
				if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && !getBoard().thereIsAPiece(p2)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
			// capturing SE
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			// capturing SW
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
		}

		return mat;
	}
}
