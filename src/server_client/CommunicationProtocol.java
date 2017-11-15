package server_client;

import java.util.List;
import player_gameplay.PlayersList;
import player_gameplay.Transaction;
import player_gameplay.TransactionsList;

public class CommunicationProtocol {
	PlayersList players;
	TransactionsList transactions;
	
	
	public CommunicationProtocol(){
		players = new PlayersList();
		transactions = new TransactionsList();
		transactions.populateFromDB();
		
	}
	
	private Transaction makeTransaction(String username, int transactionID, float balanceChange){
		if(transactions.containsTransaction(transactionID)) return transactions.getTransaction(transactionID);
		
		List<Object> playerTr = players.playerTransaction(username, balanceChange);
		Transaction transaction = new Transaction(transactionID, (int)playerTr.get(0), (int)playerTr.get(1), (float)playerTr.get(2), (float)playerTr.get(3));
		
		transactions.addSingleTransaction(transaction);
		return transaction;		
	}
	
	public PlayersList getPlayersList(){
		return players;
	}
	
	public TransactionsList getTransactionsList(){
		return transactions;
	}
	
    public String processInput(String theInput) {
        String theOutput = null;
        try{
        	 String[] toBeProcessed = theInput.split(",");
             String username = toBeProcessed[0];
             int transactionID = Integer.parseInt(toBeProcessed[1]);
             float balanceChange = Float.parseFloat(toBeProcessed[2]);             
             theOutput = makeTransaction(username, transactionID, balanceChange).printTransaction();
        
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }	        
                return theOutput;
    }
}