package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.exceptions.ChessException;

public class Program {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captureds = new ArrayList<ChessPiece>();

		while (true) {
			try {

				UI.clearScreen();
				UI.printMatch(chessMatch, captureds);
				System.out.println("Source: ");
				ChessPosition source = UI.readChessPosition(sc);

				boolean[][] possibleMoves = chessMatch.possibleMoves(source);
				// IF DUMMY MODE ON
				UI.clearScreen();
				UI.printBoard(chessMatch.getPieces(), possibleMoves);

				System.out.println();
				System.out.println("Destination: ");
				ChessPosition dest = UI.readChessPosition(sc);
				System.out.println();

				ChessPiece capturedPiece = chessMatch.performChessMove(source, dest);
				if(capturedPiece!=null) {
					captureds.add(capturedPiece);
				}
			} catch (ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}

		}
	}

}
