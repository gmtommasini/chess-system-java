package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	private ChessMatch match;

	public King(Board board, Color color, ChessMatch match) {
		super(board, color);
		this.match = match;
	}

	@Override
	public String toString() {
		return "K";
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p == null || p.getColor() != this.getColor();
	}

	private boolean testRookCastling(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p != null && p.getMoveCount() == 0; // && p instanceof Rook && p.getColor() == this.getColor();
		/* this condition should be enough, unless we get the wrong position */
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0); // safe initialization

		p.setValues(position.getRow() - 1, position.getColumn()); // above the king
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow() + 1, position.getColumn()); // below the king
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow(), position.getColumn() - 1); // left of the king
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow(), position.getColumn() + 1); // right of the king
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		p.setValues(position.getRow() - 1, position.getColumn() - 1); // NW of the king
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow() - 1, position.getColumn() + 1); // NE of the king
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow() + 1, position.getColumn() - 1); // SW of the king
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		p.setValues(position.getRow() + 1, position.getColumn() + 1); // SE of the king
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		/* CASTLING */
		if (getMoveCount() == 0 && !match.getCheck()) {
			// castling short
			Position r1 = new Position(this.position.getRow(), this.position.getColumn() + 3);
			if (testRookCastling(r1)) {
				Position p1 = new Position(this.position.getRow(), this.position.getColumn() + 1);
				Position p2 = new Position(this.position.getRow(), this.position.getColumn() + 2);
				if (	getBoard().piece(p1) == null && 
						getBoard().piece(p2) == null	) {
					// missing condition of squares under attack
					
					if (true) {
						mat[position.getRow()][position.getColumn() + 2] = true;
					}
				}
			}
			// castling long
			Position r2 = new Position(this.position.getRow(), this.position.getColumn() - 4);
			if (testRookCastling(r2)) {
				Position p1 = new Position(this.position.getRow(), this.position.getColumn() - 1);
				Position p2 = new Position(this.position.getRow(), this.position.getColumn() - 2);
				Position p3 = new Position(this.position.getRow(), this.position.getColumn() - 3);
				if (	getBoard().piece(p1) == null && 
						getBoard().piece(p2) == null &&
						getBoard().piece(p3) == null	) {
					// missing condition of squares under attack
					//if (true) {
						mat[position.getRow()][position.getColumn() - 2] = true;
					//}
				}
			}

		}

		return mat;
	}

}
