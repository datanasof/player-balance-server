package player_gameplay;

import java.util.HashMap;

public class Gameplay {
	
	HashMap<String, Object> players;
	HashMap<Integer, Object> transactions;
	
	public Gameplay(HashMap<String, Object> players, HashMap<Integer, Object> transactions) {
		super();
		this.players = players;
		this.transactions = transactions;
	}
	
	private Player getPlayer(String username){
		return (Player) players.get(username);
	}
	
	private Transaction getTransaction(int transactionID){
		return (Transaction) transactions.get(transactionID);
	}
	
	private boolean containsTransaction(int transactionID){
		return transactions.containsKey(transactionID);
	}
	
	private void updateTransactions(Transaction transaction){
		transactions.put(transaction.getId(), transaction);
	}
	
	private void updatePlayers(Player player){
		players.put(player.getUsername(), player);
	}
	
	public Transaction makeTransaction(String username, int transactionID, float balanceChange){
		if(containsTransaction(transactionID)) return getTransaction(transactionID);
		
		Player player = getPlayer(username);
		float balanceBefore = player.getBalance();
		int errorCode = player.changeBalance(balanceChange);
		float balanceAfter = player.getBalance();
		Transaction transaction = new Transaction(transactionID, errorCode, player.getBalanceVersion(), balanceAfter-balanceBefore, balanceAfter);
		updatePlayers(player);
		updateTransactions(transaction);

		return transaction;		
	}

}
