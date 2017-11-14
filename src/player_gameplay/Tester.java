package player_gameplay;

import java.util.HashMap;

import dbconnector.Connector;

public class Tester {
	
	public static void main(String[] args) {
		HashMap<String, Object> players = new HashMap<String, Object>();
		
		Balance bal = new Balance(0,8888,999);
		Player az = new Player("pesho", bal);
		//az.setBlacklisted(true);
		players.put(az.getUsername(), az);
		
		Gameplay gameplay = new Gameplay();
		
		
		/**System.out.println(gameplay.makeTransaction("Mitio", 201, -400).printTransaction());
		System.out.println(gameplay.makeTransaction("Mitio", 201, -400).printTransaction());
		System.out.println(gameplay.makeTransaction("Mitio", 202, -600).printTransaction());
		System.out.println(gameplay.makeTransaction("Mitio", 203, -100).printTransaction());
		System.out.println(gameplay.makeTransaction("Mitio", 204, 700).printTransaction());
		System.out.println(gameplay.makeTransaction("Mitio", 205, 600).printTransaction());
		System.out.println(gameplay.makeTransaction("Mitio", 211, 200).printTransaction());**/
		gameplay.transactionsToDB();
		gameplay.playerToDB(az);;
		
		
	}

}
