//A chess game that almost works... It should just be rewritten from the start.
package chess2d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Engine {//some fuckery going on with team detection...

	/*TODO
	 * 
	 * -HUUUUUGE refraction job: get rid of the game loop and make everything event-based
	 * 
	 * -special cases
	 *     -castling + GUI castling
	 *     -GUI pawn promotion
	 *     -resigning
	 * -draw games and checkmate detection
	 * -enPassant value may linger between turns, allowing a pawn to move in a diagonal it shouldnt
	 * 
	 * note: Check forces, draw games and checkmates could also
	 * be handled by player minds, but a missing king winning
	 * condition would need to be checked for. There is already
	 * such a check in execute after -if(possible(move[])).
	 */
	
	/*SPECIAL CODES:
	 ** 0:general error code
	 * 1:current player castles queenside
	 * 2:current player castles kingside
	 * 3:black pawn promotion
	 * 4:white pawn promotion
	 * 5:current player resigns
	 * 6:perform enPassant*/
	
	//Initializing useful values...
	static String alpha8 = " abcdefgh ";
	static String numer8 = " 87654321 ";
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static char[][] board = prime();
	static Boolean currentPlayer = true;
	static Boolean[] castle = {true, true, true, true};//QB, KB, QW, KW
	static Boolean gameOn = true;
	static int[] enPassant = {0, 0};

	//returns char team (TRUE == white, FALSE == black or .)
	public static Boolean team(char piece) {

		if(String.valueOf(piece).toUpperCase().equals(String.valueOf(piece))) {
			return true;
		} else {
			return false;
			//fuck exceptions and your code ocd, it works this way so i keep it
		}
	}
	
	public static Boolean threat(int row, int col, Boolean team) {
		
		//identifies pieces that can pose a threat to the team's king
		char[] enemies;
		if(team) {
			enemies = "prnbqk".toCharArray();
		} else {
			enemies = "PRNBQK".toCharArray();
		}

		//checks for enemy pawns
		//for whites
		if(team) {
			
			//look to the upper left...........................and right
			if(board[row-1][col-1] == enemies[0] || board[row-1][col+1] == enemies[0]) {
				return true;
			}
			
		//for blacks
		} else {
			
			//look to the lower left...........................and right
			if(board[row+1][col-1] == enemies[0] || board[row+1][col+1] == enemies[0]) {
				return true;
			}
		}

		//checks for enemy rooks and lateral queens
		//same column, rows going down
		for(int i = 1; row + i < 9; i++) {
			//reminder: enemies[4] is queen, [1] is rook
			if(board[row+i][col] == enemies[4] || board[row+i][col] == enemies[1]) {
				return true;
			}
			//encountering a piece (enemy or not) breaks line of sight and check must end
			if(board[row+i][col] != '.') {
				break;
			}
		}
		
		//similarly, same column, going up
		for(int i = 1; row - i > 0; i++) {
			if(board[row-i][col] == enemies[4] || board[row-i][col] == enemies[1]) {
				return true;
			}
			if(board[row-i][col] != '.') {
				break;
			}
		}
		
		//same row, going right
		for(int i = 1; col + i < 9; i++) {
			if(board[row][col+i] == enemies[4] || board[row][col+i] == enemies[1]) {
				return true;
			}
			if(board[row][col+i] != '.') {
				break;
			}
		}
		
		//same row, going left
		for(int i = 1; col - i > 0; i++) {
			if(board[row][col-i] == enemies[4] || board[row][col-i] == enemies[1]) {
				return true;
			}
			if(board[row][col-i] != '.') {
				break;
			}
		}

		//checks for knights
		//init a list of int modifiers to a knights position to envision all possible movement outcomes
		int[][] knightThreat = {{-2,-1},{-1,-2},{-2,1},{-1,2},{2,-1},{1,-2},{2,1},{1,2}};
		//big mess in here: check through the list to see if a knight is in the vicinity of the square being checked
		for(int i = 0; i < 8; i++) {
			if(row + knightThreat[i][0] > 0 && row + knightThreat[i][0] < 9 && col + knightThreat[i][1] > 0 && col + knightThreat[i][1] < 9) {
				if(board[row + knightThreat[i][0]][col + knightThreat[i][1]] == enemies[2]) {
					return true;
				}
			}
		}

		//checks for bishops and diagonal queens, similarly to rook checks
		//down right
		for(int i = 1; row + i < 9 && col + i < 9; i++) {
			if(board[row+i][col+i] == enemies[4] || board[row+i][col+i] == enemies[3]) {
				return true;
			}
			if(board[row+i][col+i] != '.') {
				break;
			}
		}
		
		//up left
		for(int i = 1; row - i > 0 && col - i > 0; i++) {
			if(board[row-i][col-i] == enemies[4] || board[row-i][col-i] == enemies[3]) {
				return true;
			}
			if(board[row-i][col-i] != '.') {
				break;
			}
		}

		//up right
		for(int i = 1; row - i > 0 && col + i < 9; i++) {
			if(board[row-i][col+i] == enemies[4] || board[row-i][col+i] == enemies[3]) {
				return true;
			}
			if(board[row-i][col+i] != '.') {
				break;
			}
		}

		//down left
		for(int i = 1; row + i < 9 && col - i > 0; i++) {
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
		//creates a temporary board
		char[][] primer = new char[10][10];
		//rows of pawns and other pieces
		String pawns = " pppppppp ";
		String rowPrime = " rnbqkbnr ";
		
		//fill the empty spaces with dots
		for(int  i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {

				primer[i][j] = '.';
			}
		}
		
		//transfer black, the white pieces
		primer[1] = rowPrime.toCharArray();
		primer[8] = rowPrime.toUpperCase().toCharArray();
		
		//transfer black, then white pawns
		primer[2] = pawns.toCharArray();
		primer[7] = pawns.toUpperCase().toCharArray();
		
		//transfer reference letters to the edge rows
		primer[0] = alpha8.toCharArray();
		primer[9] = primer[0];
		
		//...and the numbers to the columns...
		for(int i = 1; i < 9; i++) {

			primer[i][0] = Integer.toString(9-i).charAt(0);
			primer[i][9] = Integer.toString(9-i).charAt(0);
		}
		
		return primer;
	}

	public static void showBoard() {
		
		//calls drawState in Window, which is used for refreshing, not init.
		Window.drawState();
		
		/* i also kept the old console output to help with debugging
		* but alas, i am too lazy to make it work efficiently with
		* the new event-based system so it may sometimes be ugly or not work properly*/
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
	}

	//return format: {rowAt, colAt, rowTo, colTo}
	public static int[] takeInput() throws IOException, InterruptedException {

		/* calls waitForMove in Window, which is very inefficient for several reasons:
		 * it uses a delayed infinite loop to check for click inputs instead of an event
		 * - this make the controls either not work or feel sluggish
		 * - also, there is no click indicator
		 * it takes in ints, translates them to Strings and stores them in a STATIC value!
		 * - this is bad, as this method does the exact opposite to get back to the ints
		 */
		String rawIn = Window.waitForMove();
		int[] move = new int[4];

		//unique commands detection -- returns {-code-,0,0,0}
		//pawn promotion handled elsewhere
		
		switch(rawIn) {
		case "":
			//will return error as move[0] is initialized at 0, which will OOB the board and return to player.
			return move;
		case "castle":
			//ask for castle type, check player color, modify move[0]
			System.out.println("(Q)ueenside or (K)ingside ?");
			String choice;
			while(true) {
				choice = br.readLine();

				if(choice.equals("Q")) {
					move[0] = -1;
					break;
				} else if(choice.equals("K")) {
					move[0] = -2;
					break;
				} else {
					System.out.println("INVALID CHOICE");
				}
			}
			
			return move;
			
		case "resign":
			move[0] = -5;
			return move;
		}

		String[] command = rawIn.split(" ");

		//EXCEPTION HANDLING STARTS HERE

		//sanitizing... command length
		if(command.length != 2) {

			System.out.println("INVALID COMMAND");
			return move;
		}

		//interpreting argument 1, OOB detection
		String[] arg1 = command[0].split("");
		if(arg1.length != 2 || alpha8.indexOf(arg1[0]) == -1 || numer8.indexOf(arg1[1]) == -1) {

			System.out.println("INVALID COMMAND");
			return move;
		}

		move[1] = alpha8.indexOf(arg1[0]);
		move[0] = numer8.indexOf(arg1[1]);

		//interpreting argument 2, OOB detection
		String[] arg2 = command[1].split("");
		if(arg2.length != 2 || alpha8.indexOf(arg2[0]) == -1 || numer8.indexOf(arg2[1]) == -1) {

			System.out.println("INVALID COMMAND");
			return move;
		}

		move[3] = alpha8.indexOf(arg2[0]);
		move[2] = numer8.indexOf(arg2[1]);

		return move;
	}

	public static void execute(int[] move) throws IOException {
		
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
			//move is possible, but ay still incur a threat to the king
			//putting the move aside to evaluate this
			char wait = board[move[2]][move[3]];
			board[move[2]][move[3]] = board[move[0]][move[1]];
			board[move[0]][move[1]] = '.';
			
			//king threat checking
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
				//no king present, 'sum-ting-wong'
				return;
			}
			
			//if threat is true, the king can't move there and possible() has to be 'overriden' by execute()
			if(threat(row, col, currentPlayer)) {
				System.out.println("INVALID COMMAND");
				currentPlayer = !currentPlayer;
				board[move[0]][move[1]] = board[move[2]][move[3]];
				board[move[2]][move[3]] = wait;
			}
			
			//after move is evaluated, enPassant is useless
			enPassant[0] = 0;
			enPassant[1] = 0;
			
		} else {
			//this is if the move isnt even possible()
			System.out.println("INVALID COMMAND");
			currentPlayer = !currentPlayer;
		}
		
		//pawn promotions treated here isntead of takeInput, as move[] must be know to move pawn as well as executing promotion
		if(board[move[2]][move[3]] == 'p' && move[2] == 8) {
			showBoard();
			special(-3, move);
		}
		if(board[move[2]][move[3]] == 'P' && move[2] == 1) {
			showBoard();
			special(-4, move);
		}
	}

	public static Boolean possible(int[] normalMove) throws IOException {
		
		//used to get the values straight from normalMove[], it was a clusterfuck
		//normalMove[] is basically useless, but i dont like having 4 arguments in a method
		//it's basically just temporary packaging for the ints
		int rowAt = normalMove[0];
		int colAt = normalMove[1];
		int rowTo = normalMove[2];
		int colTo = normalMove[3];
		
		//only used at the end, to see if piece can be captured in the event there is one
		char at = board[rowAt][colAt];
		char to = board[rowTo][colTo];

		//first check: can player play?
		if(team(at) != currentPlayer || at == '.') {
			return false;
		}

		//BIGASS SWITCH DECLARATION! All movement types handled here. Except kings threats, theyre too complicated.
		//see execute() for threats to kings
		//checks return false, or break so further checking can occur after the switch
		//eventually, there is a 'return true' , don't worry
		switch(at) {
		case 'p':
			
			//two-ahead move, enPassant opportunity created
			if(rowTo == rowAt+2 && colTo == colAt) {
				if(rowAt == 2 && board[4][colAt] == '.') {
					enPassant[0] = rowTo - 1;
					enPassant[1] = colTo;
					break;
				}
				return false;
			}
			
			//one-ahead move
			if(rowTo == rowAt+1) {
				
				//diagonal capturing (with enPassant) handled here
				if(colTo == colAt+1 || colTo == colAt-1) {
					if(team(board[rowTo-1][colTo]) != currentPlayer && board[rowTo-1][colTo] != '.') {
						if(enPassant[0] == rowTo && enPassant[1] == colTo) {
							special(-6, normalMove);
							break;
						}
					}
					
					if(team(board[rowTo][colTo]) != currentPlayer) {
						if(board[rowTo][colTo] != '.') {
							break;
						}
					}
				}
				
				//normal one-ahead move
				if(colTo == colAt && board[rowTo][colTo] == '.') {
					break;
				}
			}
			return false;

		case 'P':
			
			//two-ahead move, enPassant opportunity created
			if(rowTo == rowAt-2 && colTo == colAt) {
				if(rowAt == 7 && board[5][colAt] == '.') {
					enPassant[0] = rowTo + 1;
					enPassant[1] = colTo;
					break;
				}
				return false;
			}
			
			//one-ahead move
			if(rowTo == rowAt-1) {
				
				//diagonal capturing (with enPassant) handled here
				if(colTo == colAt+1 || colTo == colAt-1) {
					if(team(board[rowTo+1][colTo]) != currentPlayer && board[rowTo+1][colTo] != '.') {
						if(enPassant[0] == rowTo && enPassant[1] == colTo) {
							special(-6, normalMove);
							break;
						}
					}
					
					if(team(board[rowTo][colTo]) != currentPlayer) {
						if(board[rowTo][colTo] != '.') {
							break;
						}
					}
				}
				
				//normal one-ahead move
				if(colTo == colAt && board[rowTo][colTo] == '.') {
					break;
				}
			}
			return false;

		case 'r': case 'R':
			
			//rowMove
			if(colTo == colAt & rowTo != rowAt) {
				if(rowTo > rowAt) {
					for(int i = rowAt + 1; i < rowTo; i++) {
						if(board[i][colTo] != '.') {
							return false;
						}
					}
				} else {
					for(int i = rowAt - 1; i > rowTo; i--) {
						if(board[i][colTo] != '.') {
							return false;
						}
					}
				}
				
			//column move
			} else if(rowTo == rowAt && colTo != colAt) {
				if(colTo > colAt) {
					for(int i = colAt + 1; i < colTo; i++) {
						if(board[rowTo][i] != '.') {
							return false;
						}
					}
				} else {
					for(int i = colAt - 1; i > colTo; i--) {
						if(board[rowTo][i] != '.') {
							return false;
						}
					}
				}
			//rooks must stay on colum or row, if not, move impossible
			} else {
				return false;
			}
			
			//tiny bit of code to deal with castling possibilities
			if(rowAt == 1) {
				if(colAt == 1) {
					castle[0] = false;
				} else if(colAt == 8) {
					castle[1] = false;
				}
			} else if(rowAt == 8) {
				if(colAt == 1) {
					castle[2] = false;
				} else if(colAt == 8) {
					castle[3] = false;
				}
			}
			
			break;

		case 'n': case 'N':
			
			/*expressing the move as two ints, being the differend between row
			 * and column from original position
			 * then, comparing the move being tried with all 8 possible movements of the knight
			 * (that's what the list is for) and if index is found (!= -1), the move is within
			 * accepted moveset!
			 */
			String move = "";
			move += Integer.toString(rowTo - rowAt);
			move += Integer.toString(colTo - colAt);
			String ref = "-2-1,-21,-1-2,-12,1-2,12,2-1,21";

			if(ref.indexOf(move) == -1) {
				return false;
			}
			break;

		case 'b': case 'B':
			
			//checking if the movement stays within a diagonal by checking the difference
			if(Math.abs(rowAt - rowTo) != Math.abs(colAt - colTo)) {
				return false;
			}
			
			//init row an col directions to simplify the loop writing
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
			
			//yes, useless values, but it felt more comfortable this way
			int Bi = rowIncr;
			int Bj = colIncr;
			//checking from piece until at destination
			while(rowAt + Bi != rowTo && colAt + Bj != colTo) {
				//the usual stuff...
				if(board[rowAt + Bi][colAt + Bj] != '.') {
					return false;
				}
				
				Bi += rowIncr;
				Bj += colIncr;
			}
			break;

		case 'q': case 'Q':
			
			if(rowTo == rowAt ^ colTo == colAt) {
				
				//treat as rook, see case 'r': case 'R':
				if(colTo == colAt) {
					if(rowTo > rowAt) {
						for(int i = rowAt + 1; i < rowTo; i++) {
							if(board[i][colTo] != '.') {
								return false;
							}
						}
					} else {
						for(int i = rowAt - 1; i > rowTo; i--) {
							if(board[i][colTo] != '.') {
								return false;
							}
						}
					}
				} else if(rowTo == rowAt) {
					if(colTo > colAt) {
						for(int i = colAt + 1; i < colTo; i++) {
							if(board[rowTo][i] != '.') {
								return false;
							}
						}
					} else {
						for(int i = colAt - 1; i > colTo; i--) {
							if(board[rowTo][i] != '.') {
								return false;
							}
						}
					}
				}
			} else if(Math.abs(rowTo-rowAt) == Math.abs(colTo-colAt)) {

				//treat as bishop, see case 'b': case 'B':
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
				
				//duplicate variables? but this is another 'case:' !
				int Qi = rowInc;
				int Qj = colInc;
				while(rowAt + Qi != rowTo && colAt + Qj != colTo) {
					
					if(board[rowAt + Qi][colAt + Qj] != '.') {
						return false;
					}
					
					Qi += rowInc;
					Qj += colInc;
				}
				break;
			} else {
				return false;
			}
			break;

		case 'k': case 'K':

			//you can't move to a threatened square
			if(threat(rowTo, colTo, team(board[rowAt][colAt]))) {
				return false;
			}

			//final check: are piece teams compatible or space empty?
			//also: castling impossible if moving the king
			//Q: is the castling value changing too early in the move check?
			if(to == '.') {
				
				if(currentPlayer) {
					castle[2] = false;
					castle[3] = false;
				} else {
					castle[0] = false;
					castle[1] = false;
				}
				return true;
			}
			
			if(team(at) != team(to)) {
				
				if(currentPlayer) {
					castle[2] = false;
					castle[3] = false;
				} else {
					castle[0] = false;
					castle[1] = false;
				}
				return true;
			}
		}
		
		return true;
		
	}

	/**/public static void special(int code, int[] move) throws IOException {
		
		//resigning
		if(code == -5) {
			gameOn = false;
			return;
		}
		
		//castling queenside
		if(code == -1) {
			
			if(currentPlayer) {
				if(castle[2]) {
					//castling possible
				}
			} else {
				if(castle[0]) {
					//castling possible
				}
			}
			
			//prevent further castling
			if(currentPlayer) {
				castle[2] = false;
				castle[3] = false;
			} else {
				castle[0] = false;
				castle[1] = false;
			}
		}
		
		//castling kingside
		if(code == -2) {
			
			if(currentPlayer) {
				if(castle[3]) {
					//castling possible
				}
			} else {
				if(castle[1]) {
					//castling possible
				}
			}
			
			//prevent further castling
			if(currentPlayer) {
				castle[2] = false;
				castle[3] = false;
			} else {
				castle[0] = false;
				castle[1] = false;
			}
		}
		
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
		
		//white pawn promotion
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
		
		//en passant
		if(code == -6) {
			int capture = -1;
			if(currentPlayer) {
				capture = 1;
			}
			
			board[move[2] + capture][move[3]] = '.';
			
			return;
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {

		Window.start();
		
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