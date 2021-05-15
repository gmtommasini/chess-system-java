package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check; // booleans are false by default
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;

	private List<Piece> piecesOnBoard = new ArrayList<>();
	private List<Piece> piecesCaptured = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	/***** GETTERS *****/
	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return check;
	}

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	public ChessPiece getPromoted() {
		return promoted;
	}

	/***** FUNCTIONS *****/
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}

	public boolean[][] possibleMoves(ChessPosition srcPos) {
		Position pos = srcPos.toPosition();
		validateSourcePosition(pos);
		return board.piece(pos).possibleMoves();
	}

	public ChessPiece performChessMove(ChessPosition srcPos, ChessPosition dstPos) {
		Position src = srcPos.toPosition();
		Position dst = dstPos.toPosition();
		validateSourcePosition(src);
		validateTargetPosition(src, dst);
		Piece capturedPiece = makeMove(src, dst);

		if (testCheck(currentPlayer)) { // "self-check
			undoMove(src, dst, capturedPiece);
			throw new ChessException("You can't put yourselg in check!");
		}
		ChessPiece movedPiece = (ChessPiece) board.piece(dst);
		
		// promotion
		promoted =null;
		if(movedPiece instanceof Pawn) {
			if( (movedPiece.getColor()==Color.WHITE && dst.getRow()==0 )||
				(movedPiece.getColor()==Color.BLACK && dst.getRow()==7 ) ) {//pawn crossed whole board
				promoted = (ChessPiece)board.piece(dst);
				promoted = replacePromotedPiece("Q");
			}
		}
		

		check = testCheck(opponent(currentPlayer)) ? true : false;
		if (check) {
			checkMate = testCheckMate(opponent(currentPlayer));
		}
		if (checkMate) {
			// to do
		} else {
			nextTurn();
		}

		// en passant
		if (movedPiece instanceof Pawn && 
				(	dst.getRow() == src.getRow() - 2 || // whites
					dst.getRow() == src.getRow() + 2)) // blacks
		{
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
			// after next play, the pawn is not en passant vulnerable anymore
		}

		return (ChessPiece) capturedPiece;
	}
	public ChessPiece replacePromotedPiece(String type) {
		if(promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted.");
		}
		if (!type.equals("R") &&!type.equals("N") &&!type.equals("B") &&!type.equals("Q")  ) {
			return promoted;
		}
		
		Position pos =  promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnBoard.add(newPiece);
		return newPiece;
		
	}
	private ChessPiece newPiece(String type, Color color) {
		if(type.equals("Q")) return new Queen(board, color);
		if(type.equals("R")) return new Rook(board, color);
		if(type.equals("B")) return new Bishop(board, color);
		if(type.equals("N")) return new Knight(board, color);
		return new Queen(board, color);//default
	}

	private Piece makeMove(Position src, Position dst) {
		ChessPiece p = (ChessPiece) board.removePiece(src);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(dst);
		board.placePiece(p, dst);
		if (capturedPiece != null) {
			piecesOnBoard.remove(capturedPiece);
			piecesCaptured.add(capturedPiece);
		}

		// # castling short
		if (p instanceof King && dst.getColumn() == src.getColumn() + 2) {
			Position sourceR = new Position(src.getRow(), src.getColumn() + 3);
			Position targetR = new Position(src.getRow(), src.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceR);
			board.placePiece(rook, targetR);
			rook.increaseMoveCount();
		}

		// #castling long
		if (p instanceof King && dst.getColumn() == src.getColumn() - 2) {
			Position sourceR = new Position(src.getRow(), src.getColumn() - 4);
			Position targetR = new Position(src.getRow(), src.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceR);
			board.placePiece(rook, targetR);
			rook.increaseMoveCount();
		}

		// # en passant
		if (p instanceof Pawn) {
			if (src.getColumn() != dst.getColumn() && capturedPiece == null) {
				Position capturedPawnPosition;
				if (p.getColor() == Color.WHITE) {
					// for whites, the captured pawn is below the moved pawn
					capturedPawnPosition = new Position(dst.getRow() + 1, dst.getColumn());
				} else {
					// for blacks, the captured pawn is above the moved pawn
					capturedPawnPosition = new Position(dst.getRow() - 1, dst.getColumn());
				}
				capturedPiece = board.removePiece(capturedPawnPosition);
				piecesCaptured.add(capturedPiece);
				piecesOnBoard.remove(capturedPiece);
			}
		}

		return capturedPiece;
	}

	private void undoMove(Position src, Position dst, Piece captured) {
		// moving piece back
		ChessPiece p = (ChessPiece) board.removePiece(dst);
		p.decreaseMoveCount();
		board.placePiece(p, src);

		// returning captured piece
		if (captured != null) {
			board.placePiece(captured, dst);
			piecesCaptured.remove(captured);
			piecesOnBoard.add(captured);
		}

		// # castling short
		if (p instanceof King && dst.getColumn() == src.getColumn() + 2) {
			Position sourceR = new Position(src.getRow(), src.getColumn() + 3);
			Position targetR = new Position(src.getRow(), src.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetR);
			board.placePiece(rook, sourceR);
			rook.decreaseMoveCount();
		}

		// #castling long
		if (p instanceof King && dst.getColumn() == src.getColumn() - 2) {
			Position sourceR = new Position(src.getRow(), src.getColumn() - 4);
			Position targetR = new Position(src.getRow(), src.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetR);
			board.placePiece(rook, sourceR);
			rook.decreaseMoveCount();
		}

		// # en passant
		if (p instanceof Pawn) {
			if (src.getColumn() != dst.getColumn() && captured == enPassantVulnerable) {
				//undo move placed the captured pawn in the "wrong" position
				//we must correct the position
				ChessPiece pawn = (ChessPiece) board.removePiece(dst);
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, dst.getColumn());
				} else {
					pawnPosition = new Position(4, dst.getColumn());
				}
				board.placePiece(pawn, pawnPosition);
			}
		}

	}

	public void validateSourcePosition(Position pos) {
		if (!board.thereIsAPiece(pos)) {
			throw new ChessException("There is no piece in the source position.");
		}
		if (currentPlayer != ((ChessPiece) board.piece(pos)).getColor()) {
			throw new ChessException("Chosen piece is not yours.");
		}
		if (!board.piece(pos).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece.");
		}
	}

	private void validateTargetPosition(Position src, Position dst) {
		if (!board.piece(src).possibleMove(dst)) {
			throw new ChessException("The chosen piece cannot be moved to the chosen position.");
		}
	}

	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnBoard.stream().filter(p -> ((ChessPiece) p).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board.");
		// This error should NEVER occour.
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnBoard.stream().filter(p -> ((ChessPiece) p).getColor() == opponent(color))
				.collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		// SHOULD I check for CHECK first?

		List<Piece> list = piecesOnBoard.stream().filter(p -> ((ChessPiece) p).getColor() == color)
				.collect(Collectors.toList());
		// if there is any movement that uncheck the king
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece captured = makeMove(source, target);
						boolean testeCheck = testCheck(color);// still on check?
						undoMove(source, target, captured);
						if (!testeCheck) {
							return false;
						}
					}
				}
			}

		}
		return true;
	}

	private void placeNewPiece(char col, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(col, row).toPosition());
		piecesOnBoard.add(piece);
	}

	private void nextTurn() {
		turn++;
		currentPlayer = currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE;
	}

	private void initialSetup() {
		/* testing castling */
		placeNewPiece('a', 7, new Pawn(board, Color.WHITE, this));
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('d', 8, new Rook(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
/**/
		// Whites
/*
		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));

		// Blacks placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));

		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
		/**/
	}

}
