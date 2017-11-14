package player_gameplay;

import java.util.List;

public class Gameplay {
	
	PlayersList players;
	TransactionsList transactions;
		
	public Gameplay() {
		super();
		players = new PlayersList();
		transactions = new TransactionsList();
		transactions.populateFromDB();
	}	
	
	public void transactionsToDB(){
		transactions.updateToDB();
	}
	
	public void playerToDB(Player player){
		players.updatePlayerToDB(player);
	}
	
	public Transaction makeTransaction(String username, int transactionID, float balanceChange){
		if(transactions.containsTransaction(transactionID)) return transactions.getTransaction(transactionID);
		
		List<Object> playerTr = players.playerTransaction(username, balanceChange);
		Transaction transaction = new Transaction(transactionID, (int)playerTr.get(0), (int)playerTr.get(1), (float)playerTr.get(2), (float)playerTr.get(3));
		
		transactions.addSingleTransaction(transaction);
		return transaction;		
	}
	
	
}
