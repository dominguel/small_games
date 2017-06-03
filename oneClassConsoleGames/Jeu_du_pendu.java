package oneClassConsoleGames;

import java.io.*;

class Jeu_du_pendu {
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Ceci est un jeu de pendu!");
		System.out.println(); //Skip a line to make it prettier. There's probably a proper method for that.
		
		String[] mots = {"sternutation","bandit", "assassin", "brigand", "voleur", "chef", "nutella", "tutu", "zygote", "ornithorynque", "styx", "coq", "jazz", "hydrophobe", "kiwi", "condrichtyen", "halloween"};
		//int essais = 5 + (motchoisi.length - 7)/2 ? --- actually, 6 guesses so drawstate function works
		int essais = 6;
		
		//choose a word from the list
		String motADeviner = mots[(int)(Math.random() * mots.length)];
		
		//generate a malleable copy of the word with "-"
		String motDevine = "";
		for(int i = 0; i < motADeviner.length(); i++) {
			motDevine += "-";
		}
		char[] motDevineChange = motDevine.toCharArray();
		
		String lettresEssayees = "";
		
		//start the guesses!
		while(essais != 0) {
			
			//current guess state readout
			System.out.println("Le mot à trouver: " + motDevine);
			System.out.println("essayé: " + lettresEssayees);
			
			//taking input... (as 1 lowercase char)
			char s = '\0';
			try{
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Il vous reste " + essais + " essais. Écrivez une lettre:");
				String temp = br.readLine().toLowerCase();
				s = temp.charAt(0);
				
				if(lettresEssayees.indexOf(s) != -1) {
					System.out.println("Vous avez déjà essayé cette lettre.");
					System.out.println();
					continue;
				}
				lettresEssayees += s + " ";
				
			} catch (IndexOutOfBoundsException e) {
				System.out.println("J'ai dit: Écrivez une lettre. Maintenant recommencez.");
				continue;
			}
			
			//checking if letter has already been guessed before
			if(motDevine.indexOf(s) != -1) {
				
				continue;
			}
			
			//checking if the guessed char is present at all
			if(motADeviner.indexOf(s) == -1) {
				
				essais--;
				drawState(essais);
				System.out.println("Le mot ne contient pas de " + s + "!");
			} else {
				
				System.out.println("Le mot contient un " + s + "!");
				
				//replacing "-" by s at all indexes of motDevine, comparing to motADeviner (same indexing pattern)
				int i = -1;
				while(i < motADeviner.length()) {
					i = motADeviner.indexOf(s, i + 1);
					//Note: under any circumstance, do not move the above instruction lower, as i is initialized at -1 and it would break the while() at if()
					
					//break condition: if indexOf cannot find s after the current searching index
					if(i != -1) {
						
						motDevineChange[i] = s;
					} else {
						
						break;
					}
				}
				motDevine = String.valueOf(motDevineChange);
			}
			
			//an extra losing break condition (while already has a guess limit), where a message is displayed
			if(essais <= 0) {
				
				System.out.println("Il ne vous reste plus d'essais.");
				break;
			}
			
			//winning break condition
			if(motDevine.equals(motADeviner)) {
				
				System.out.println();
				System.out.println("Vous avez trouvé!");
				break;
			}
		}
		System.out.println("Le mot était: " + motADeviner);
	}
	private static void drawState(int state) {
		switch(state) {
			case 5 :
				System.out.println("------");
				System.out.println("|    |");
				System.out.println("|    @");
				System.out.println("|");
				System.out.println("|");
				System.out.println("|");
				System.out.println("|");
				System.out.println("------");
				break;
			case 4 :
				System.out.println("------");
				System.out.println("|    |");
				System.out.println("|    @");
				System.out.println("|    |");
				System.out.println("|    |");
				System.out.println("|");
				System.out.println("|");
				System.out.println("------");
				break;
			case 3 :
				System.out.println("------");
				System.out.println("|    |");
				System.out.println("|    @");
				System.out.println("|   /|");
				System.out.println("|    |");
				System.out.println("|");
				System.out.println("|");
				System.out.println("------");
				break;
			case 2 :
				System.out.println("------");
				System.out.println("|    |");
				System.out.println("|    @");
				System.out.println("|   /|\\");
				System.out.println("|    |");
				System.out.println("|");
				System.out.println("|");
				System.out.println("------");
				break;
			case 1 :
				System.out.println("------");
				System.out.println("|    |");
				System.out.println("|    @");
				System.out.println("|   /|\\");
				System.out.println("|    |");
				System.out.println("|   /");
				System.out.println("|");
				System.out.println("------");
				break;
			case 0 :
				System.out.println("------");
				System.out.println("|    |");
				System.out.println("|    @");
				System.out.println("|   /|\\");
				System.out.println("|    |");
				System.out.println("|   / \\");
				System.out.println("|");
				System.out.println("------");
				break;
			default :
				System.out.println("------");
				System.out.println("|    |");
				System.out.println("|    ()");
				System.out.println("|");
				System.out.println("|");
				System.out.println("|");
				System.out.println("|");
				System.out.println("------");
				break;
		}
	}
}