package player_gameplay;

import java.util.HashMap;

public class Gameplay {
	
	HashMap<String, Object> players;
	TransactionsList transactions;
		
	public Gameplay(HashMap<String, Object> players) {
		super();
		this.players = players;
		transactions = new TransactionsList();
		transactions.populateFromDB();
	}
	
	private Player getPlayer(String username){
		return (Player) players.get(username);
	}
			
	private void updatePlayers(Player player){
		players.put(player.getUsername(), player);
	}
	
	public void transactionsToDB(){
		transactions.updateToDB();
	}
	
	public Transaction makeTransaction(String username, int transactionID, float balanceChange){
		if(transactions.containsTransaction(transactionID)) return transactions.getTransaction(transactionID);
		
		Player player = getPlayer(username);
		float balanceBefore = player.getBalance();
		int errorCode = player.changeBalance(balanceChange);
		float balanceAfter = player.getBalance();
		Transaction transaction = new Transaction(transactionID, errorCode, player.getBalanceVersion(), balanceAfter-balanceBefore, balanceAfter);
		updatePlayers(player);
		transactions.addSingleTransaction(transaction);
		return transaction;		
	}
	
	
}
