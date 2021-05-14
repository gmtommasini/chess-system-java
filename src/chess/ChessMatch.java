package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check; // booleans are false by default

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
		
		if(testCheck(currentPlayer)) { //"self-check
			undoMove(src, dst, capturedPiece);
			throw new ChessException("You can't put yourselg in check!");
		}
		check = testCheck(opponent(currentPlayer)) ? true : false;
		
		nextTurn();
		return (ChessPiece) capturedPiece;
	}

	private Piece makeMove(Position src, Position dst) {
		Piece p = board.removePiece(src);
		Piece capturedPiece = board.removePiece(dst);
		board.placePiece(p, dst);
		if (capturedPiece != null) {
			piecesOnBoard.remove(capturedPiece);
			piecesCaptured.add(capturedPiece);
		}
		return capturedPiece;
	}

	private void undoMove(Position src, Position dst, Piece captured) {
		// moving piece back
		Piece p = board.removePiece(dst);
		board.placePiece(p, src);

		// returning captured piece
		if (captured != null) {
			board.placePiece(captured, dst);
			piecesCaptured.remove(captured);
			piecesOnBoard.add(captured);
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
		List<Piece> list =  piecesOnBoard.stream().filter(p -> ((ChessPiece)p).getColor()==color).collect(Collectors.toList());
		for (Piece p : list) {
			if ( p instanceof King ) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board."); //This error should NEVER occour.
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnBoard.stream().filter(p -> ((ChessPiece)p).getColor()==opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if(mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
		
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
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
		placeNewPiece('c', 2, new Rook(board, Color.WHITE));
		placeNewPiece('d', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 1, new Rook(board, Color.WHITE));
		placeNewPiece('d', 1, new King(board, Color.WHITE));

		placeNewPiece('c', 7, new Rook(board, Color.BLACK));
		placeNewPiece('c', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 8, new King(board, Color.BLACK));
	}

}
