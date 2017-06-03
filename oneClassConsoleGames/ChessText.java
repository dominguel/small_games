package oneClassConsoleGames;

import java.io.*;

public class ChessText {//some fuckery going on with team detection...

	/*TODO:
	 * 
	 * -special cases
	 *     -castling
	 *     -resigning
	 *     -en passant
	 * -check forces, draw games and checkmate detection
	 * 
	 * note: Check forces, draw games and checkmates could also
	 * be handled by player minds, but a missing king winning
	 * condition would need to be checked for. There is already
	 * such a check in execute after -if(possible(move[])).
	 * 
	 */
	
	/*SPECIAL CODES:
	 * 
	 * 0:current player resigns
	 * 1:current player castles queenside
	 * 2:current player castles kingside
	 * 3:black pawn promotion
	 * 4:white pawn promotion
	 */
	
	//Initializing useful values...
	static String alpha8 = " abcdefgh ";
	static String numer8 = " 87654321 ";
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static char[][] board = prime();
	static Boolean currentPlayer = true;
	static Boolean[] castle = {true, true, true, true};//QB, KB, QW, KW

	public static Boolean team(char piece) {

		if(String.valueOf(piece).toUpperCase().equals(String.valueOf(piece))) {
			return true;
		} else {
			return false;
			//fuck exceptions and your code ocd, it works this way so i keep it
		}
	}
	
	public static Boolean threat(int row, int col, Boolean team) {
		
		char[] enemies;
		if(team) {
			enemies = "prnbqk".toCharArray();
		} else {
			enemies = "PRNBQK".toCharArray();
		}

		//checks for enemy pawns
		if(team) {

			if(board[row-1][col-1] == enemies[0] || board[row-1][col+1] == enemies[0]) {
				return true;
			}
		} else {
			
			if(board[row+1][col-1] == enemies[0] || board[row+1][col+1] == enemies[0]) {
				return true;
			}
		}

		//checks for enemy rooks and lateral queens
		for(int i = 0; row + i < 9; i++) {
			if(board[row+i][col] == enemies[4] || board[row+i][col] == enemies[1]) {
				return true;
			}
			if(board[row+i][col] != '.') {
				break;
			}
		}

		for(int i = 0; row - i > 0; i++) {
			if(board[row-i][col] == enemies[4] || board[row-i][col] == enemies[1]) {
				return true;
			}
			if(board[row-i][col] != '.') {
				break;
			}
		}

		for(int i = 0; col + i < 9; i++) {
			if(board[row][col+i] == enemies[4] || board[row][col+i] == enemies[1]) {
				return true;
			}
			if(board[row][col+i] != '.') {
				break;
			}
		}

		for(int i = 0; col - i > 0; i++) {
			if(board[row][col-i] == enemies[4] || board[row][col-i] == enemies[1]) {
				return true;
			}
			if(board[row][col-i] != '.') {
				break;
			}
		}

		//checks for knights
		int[][] knightThreat = {{-2,-1},{-1,-2},{-2,1},{-1,2},{2,-1},{1,-2},{2,1},{1,2}};
		for(int i = 0; i < 8; i++) {
			if(row + knightThreat[i][0] > 0 && row + knightThreat[i][0] < 9 && col + knightThreat[i][1] > 0 && col + knightThreat[i][1] < 9) {
				if(board[row + knightThreat[i][0]][col + knightThreat[i][1]] == enemies[2]) {
					return true;
				}
			}
		}

		//checks for bishops and diagonal queens
		for(int i = 0; row + i < 9 && col + i < 9; i++) {
			if(board[row+i][col+i] == enemies[4] || board[row+i][col+i] == enemies[3]) {
				return true;
			}
			if(board[row+i][col+i] != '.') {
				break;
			}
		}

		for(int i = 0; row - i > 0 && col - i > 0; i++) {
			if(board[row-i][col-i] == enemies[4] || board[row-i][col-i] == enemies[3]) {
				return true;
			}
			if(board[row-i][col-i] != '.') {
				break;
			}
		}

		for(int i = 0; row - i > 0 && col + i < 9; i++) {
			if(board[row-i][col+i] == enemies[4] || board[row-i][col+i] == enemies[3]) {
				return true;
			}
			if(board[row-i][col+i] != '.') {
				break;
			}
		}

		for(int i = 0; row + i < 9 && col - i > 0; i++) {
			if(board[row+i][col-i] == enemies[4] || board[row+i][col-i] == enemies[3]) {
				return true;
			}
			if(board[row+i][col-1] != '.') {
				break;
			}
		}

		//checks for enemy king
		for(int i = -1; i < 2; i++) {
			for(int j = -1; j < 2; j++) {
				if(board[row+i][col+j] == enemies[5]) {
					return true;
				}
			}
		}
		//else possible!!!
		return false;
	}

	public static char[][] prime() {

		//Primes the board for normal play
		char[][] primer = new char[10][10];
		String pawns = " pppppppp ";
		String rowPrime = " rnbqkbnr ";

		for(int  i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {

				primer[i][j] = '.';
			}
		}

		primer[1] = rowPrime.toCharArray();
		primer[8] = rowPrime.toUpperCase().toCharArray();

		primer[2] = pawns.toCharArray();
		primer[7] = pawns.toUpperCase().toCharArray();

		primer[0] = alpha8.toCharArray();
		primer[9] = primer[0];

		for(int i = 1; i < 9; i++) {

			primer[i][0] = Integer.toString(9-i).charAt(0);
			primer[i][9] = Integer.toString(9-i).charAt(0);
		}
		
		return primer;
	}

	public static void showBoard() {

		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
	}

	//returns {rowAt, colAt, rowTo, colTo}
	public static int[] takeInput() throws IOException {

		String rawIn = br.readLine();
		int[] move = new int[4];

		//unique commands detection -- returns {-code-,0,0,0}
		switch(rawIn) {
		case "":
			break;
		}

		String[] command = rawIn.split(" ");

		//EXCEPTION HANDLING STARTS HERE

		//sanitizing... command length
		if(command.length != 2) {

			System.out.println("INVALID COMMAND");
			return move;
		}

		//interpreting argument 1
		String[] arg1 = command[0].split("");
		if(arg1.length != 2 || alpha8.indexOf(arg1[0]) == -1 || numer8.indexOf(arg1[1]) == -1) {

			System.out.println("INVALID COMMAND");
			return move;
		}

		move[1] = alpha8.indexOf(arg1[0]);
		move[0] = numer8.indexOf(arg1[1]);

		//interpreting argument 2
		String[] arg2 = command[1].split("");
		if(arg2.length != 2 || alpha8.indexOf(arg2[0]) == -1 || numer8.indexOf(arg2[1]) == -1) {

			System.out.println("INVALID COMMAND");
			return move;
		}

		move[3] = alpha8.indexOf(arg2[0]);
		move[2] = numer8.indexOf(arg2[1]);

		return move;
	}

	/**/public static void execute(int[] move) throws IOException {
		
		//SPECIAL MOVE HANDLING NOT COMPLETE
		
		//inoperable move, dont switch
		if(move[0] == 0) {
			currentPlayer = !currentPlayer;
			return;
		}

		if(move[0] < 0) {
			//Switch move[0] on normalized codes for special actions, check, execute with special(code)
			special(move[0], move);
			return;
		}

		if(possible(move)) {
			char wait = board[move[2]][move[3]];
			board[move[2]][move[3]] = board[move[0]][move[1]];
			board[move[0]][move[1]] = '.';
			
			//insert king threat checking here
			int row = 0;
			int col = 0;
			char king;
			
			if(currentPlayer) {
				king = 'K';
			} else {
				king = 'k';
			}
			
			for(int i = 1; i < 9; i++) {
				for( int j = 1; j < 9; j++) {
					if(board[i][j] == king) {
						row = i;
						col = j;
					}
				}
			}
			
			if(row == 0) {
				//no king present
				return;
			}
			
			if(threat(row, col, currentPlayer)) {
				System.out.println("INVALID COMMAND");
				currentPlayer = !currentPlayer;
				board[move[0]][move[1]] = board[move[2]][move[3]];
				board[move[2]][move[3]] = wait;
			}
			
		} else {
			System.out.println("INVALID COMMAND");
			currentPlayer = !currentPlayer;
		}
		
		if(board[move[2]][move[3]] == 'p' && move[2] == 8) {
			showBoard();
			special(-3, move);
		}
		if(board[move[2]][move[3]] == 'P' && move[2] == 1) {
			showBoard();
			special(-4, move);
		}
	}

	/**/public static Boolean possible(int[] normalMove) {
		
		//KING MOVEMENT, EN PASSANT, PROMOTIONS
		
		int rowAt = normalMove[0];
		int colAt = normalMove[1];
		int rowTo = normalMove[2];
		int colTo = normalMove[3];

		char at = board[rowAt][colAt];
		char to = board[rowTo][colTo];

		//first check: can player play?
		if(team(at) != currentPlayer || at == '.') {
			return false;
		}

		//BIGASS SWITCH DECLARATION! All movement types handled here. Except kings, theyre too complicated.
		switch(at) {
		case 'p':

			if(rowTo == rowAt+2 && colTo == colAt) {
				if(rowAt == 2 && board[4][colAt] == '.') {
					break;
				}
				return false;
			}

			if(rowTo == rowAt+1) {
				if(colTo == colAt+1 || colTo == colAt-1) {
					if(team(board[rowTo][colTo]) != currentPlayer && board[rowTo][colTo] != '.') {
						break;
					}
				}

				if(colTo == colAt && board[rowTo][colTo] == '.') {
					break;
				}
			}
			return false;

		case 'P':

			if(rowTo == rowAt-2 && colTo == colAt) {
				if(rowAt == 7 && board[5][colAt] == '.') {
					break;
				}
				return false;
			}

			if(rowTo == rowAt-1) {
				if(colTo == colAt+1 || colTo == colAt-1) {
					if(team(board[rowTo][colTo]) != currentPlayer && board[rowTo][colTo] != '.') {
						break;
					}
				}

				if(colTo == colAt && board[rowTo][colTo] == '.') {
					break;
				}
			}
			return false;

		case 'r': case 'R':

			if(colTo == colAt & rowTo != rowAt) {
				if(rowTo > rowAt) {
					for(int i = rowAt; i < rowTo; i++) {
						if(board[i][colTo] != '.') {
							return false;
						}
					}
				} else {
					for(int i = rowAt; i > rowTo; i--) {
						if(board[i][colTo] != '.') {
							return false;
						}
					}
				}
			} else if(rowTo == rowAt && colTo != colAt) {
				if(colTo > colAt) {
					for(int i = colAt; i < colTo; i++) {
						if(board[rowTo][i] != '.') {
							return false;
						}
					}
				} else {
					for(int i = colAt; i > colTo; i--) {
						if(board[rowTo][i] != '.') {
							return false;
						}
					}
				}
			}
			return false;

		case 'n': case 'N':

			String move = "";
			move += Integer.toString(rowTo - rowAt);
			move += Integer.toString(colTo - colAt);
			String ref = "-2-1,-21,-1-2,-12,1-2,12,2-1,21";

			if(ref.indexOf(move) == -1) {
				return false;
			}
			break;

		case 'b': case 'B':

			if(Math.abs(rowAt - rowTo) != Math.abs(colAt - colTo)) {
				return false;
			}

			int rowIncr;
			if(rowTo > rowAt) {
				rowIncr = 1;
			} else {
				rowIncr = -1;
			}

			int colIncr;
			if(colTo > colAt) {
				colIncr = 1;
			} else {
				colIncr = -1;
			}
			
			int Bi = rowIncr;
			int Bj = colIncr;
			while(rowAt + Bi != rowTo && colAt + Bj != colTo) {
				
				if(board[rowAt + Bi][colAt + Bj] != '.') {
					return false;
				}
				
				Bi += rowIncr;
				Bj += colIncr;
			}
			break;

		case 'q': case 'Q':

			if(Math.abs(rowAt - rowTo) == Math.abs(colAt - colTo)) {

				//treat as bishop
				int rowInc;
				if(rowTo > rowAt) {
					rowInc = 1;
				} else {
					rowInc = -1;
				}

				int colInc;
				if(colTo > colAt) {
					colInc = 1;
				} else {
					colInc = -1;
				}

				for(int i = rowInc; rowAt + i != rowTo; i += rowInc) {
					for(int j = colInc; colAt + j != colTo; j += colInc) {
						if(board[rowAt + i][colAt + j] != '.') {
							return false;
						}
					}
				}
			} else if(colAt == colTo || rowAt == rowTo) {

				//treat as rook
				if(colTo == colAt & rowTo != rowAt) {
					if(rowTo > rowAt) {
						for(int i = rowAt; i < rowTo; i++) {
							if(board[i][colTo] != '.') {
								return false;
							}
						}
					} else {
						for(int i = rowAt; i > rowTo; i--) {
							if(board[i][colTo] != '.') {
								return false;
							}
						}
					}
				} else if(rowTo == rowAt && colTo != colAt) {
					if(colTo > colAt) {
						for(int i = colAt; i < colTo; i++) {
							if(board[rowTo][i] != '.') {
								return false;
							}
						}
					} else {
						for(int i = colAt; i > colTo; i--) {
							if(board[rowTo][i] != '.') {
								return false;
							}
						}
					}
				} else {
					return false;
				}
			}
			break;

		case 'k': case 'K':

			if(threat(rowTo, colTo, team(board[rowAt][colAt]))) {
				return false;
			}

			//final check: are piece teams compatible or space empty?
			if(to == '.') {
				return true;
			}
			if(team(at) != team(to)) {
				return true;
			}
		}
		
		return true;
		
	}

	/**/public static void special(int code, int[] move) throws IOException {
		
		//black pawn promotion
		if(code == -3) {
			while(true) {
				System.out.println("Enter promotion: r, n, b, q");
				char[] promotion = br.readLine().toCharArray();

				if(promotion.length == 1 && (promotion[0] == 'r' || promotion[0] == 'n' || promotion[0] == 'b' || promotion[0] == 'q')) {
					board[move[2]][move[3]] = promotion[0];
					break;
				} else {
					continue;
				}
			}
			return;
		}
		
		if(code == -4) {
			while(true) {
				System.out.println("Enter promotion: R, N, B, Q");
				char[] promotion = br.readLine().toCharArray();

				if(promotion.length == 1 && (promotion[0] == 'R' || promotion[0] == 'N' || promotion[0] == 'B' || promotion[0] == 'Q')) {
					board[move[2]][move[3]] = promotion[0];
					break;
				} else {
					continue;
				}
			}
			return;
		}
	}

	public static void main(String[] args) throws IOException {

		Boolean gameOn = true;
		showBoard();

		while(gameOn) {

			execute(takeInput());
			showBoard();
			currentPlayer = !currentPlayer;
			if(currentPlayer) {
				System.out.println("White to play.");
			} else {
				System.out.println("Black to play.");
			}
		}
	}
}