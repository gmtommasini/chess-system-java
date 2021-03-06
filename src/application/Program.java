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

		while (!chessMatch.getCheckMate()) {
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
				if(chessMatch.getPromoted()!=null) {
					System.out.println("Enter piece for promotion (R/N/B/Q)");
					String type = sc.nextLine().toUpperCase();
					while (!type.equals("R") &&!type.equals("N") &&!type.equals("B") &&!type.equals("Q")) {
						System.out.println("Invalid entry. Enter piece for promotion (R/N/B/Q)");
						type = sc.nextLine().toUpperCase();
					}
					chessMatch.replacePromotedPiece(type);
				}
			} catch (ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}/**/

		}
		UI.clearScreen();
		UI.printMatch(chessMatch, captureds);

	}

}
