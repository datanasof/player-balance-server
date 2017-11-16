package player_gameplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dbconnector.DBhelper;

public class TransactionsList {
	HashMap<Integer, Transaction> transactionsMap;
	Queue<Transaction> transactionsQueue;
	List<Transaction> transactionsToDB;
		
	public TransactionsList(){				
		transactionsQueue = new LinkedList<Transaction>(); 
		transactionsToDB = new ArrayList<Transaction>();
		transactionsMap = new HashMap<Integer, Transaction>();
	}
		
	public Transaction getTransaction(int transactionID){
		return (Transaction) transactionsMap.get(transactionID);
	}
	
	public boolean containsTransaction(int transactionID){
		return transactionsMap.containsKey(transactionID);
	}	
	
	private void addTransaction(Transaction transaction){
		transactionsMap.put(transaction.getId(), transaction);
		transactionsQueue.add(transaction);
	}
	
	public void addSingleTransaction(Transaction transaction){
		addTransaction(transaction);
		transactionsToDB.add(transaction);
		
		if(transactionsMap.size()>1000){
			transactionsMap.remove(transactionsQueue.remove().getId());
		}		
	}	
	
	@SuppressWarnings("static-access")
	public void populateFromDB(){
			List<Transaction> transactions = DBhelper.getInstance().selectTransactions();
			for(Transaction tr:transactions){
				addTransaction(tr);
			}
	}
	
	@SuppressWarnings("static-access")
	public boolean updateToDB(){
		try {
			DBhelper.getInstance().updateTransactions(transactionsToDB);
			transactionsToDB.clear();
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	

}
