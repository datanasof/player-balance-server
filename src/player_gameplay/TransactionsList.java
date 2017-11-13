package player_gameplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dbconnector.Connector;

public class TransactionsList {
	HashMap<Integer, Transaction> transactionsMap;
	Queue<Transaction> transactionsQueue;
	List<Transaction> transactionsToDB;
	Connector conn;
	
	public TransactionsList(){		
		conn = new Connector();
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
	
	public void populateFromDB(){
			List<Transaction> transactions = conn.selectTransactions();
			for(Transaction tr:transactions){
				addTransaction(tr);
			}
	}
	
	public boolean updateToDB(){
		try {
			conn.updateTransactions(transactionsToDB);
			transactionsToDB.clear();
			return true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	

}
