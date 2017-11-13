package dbconnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import player_gameplay.Balance;
import player_gameplay.Player;
import player_gameplay.Transaction;

public class Connector {
	
	private String urlDB = SQLstatement.urlDB;
	
	private Connection getConnected(String url) throws ClassNotFoundException{
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection(url);
			conn.setAutoCommit(true);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public void createTable(String sqlSt) throws ClassNotFoundException{
	    Connection cn = getConnected(urlDB);   
	    try {
	        PreparedStatement prepSt;
	        try {
	            prepSt = cn.prepareStatement(sqlSt);
	            prepSt.executeUpdate();
	            prepSt.close();
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	            
	        }	
	        cn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	}
	
	public Player selectPlayer (String username) throws ClassNotFoundException {
	    Connection cn = getConnected(urlDB);
	    
	    try {
	        Statement stmt;
	        try {
	            stmt = cn.createStatement();	            
	            ResultSet rs = stmt.executeQuery(SQLstatement.selectPlayer + String.format("\"%s\"",username));	            
	            	            
	            if(rs.next()){
	            	int id = rs.getInt("id");	               
	            	int balanceVersion = rs.getInt("balance_version");	               
	            	float balance = rs.getFloat("balance");
	            	List<Object> playerInfo = selectPlayerInfo(id);
	            	float balanceLimit = (float) playerInfo.get(0);
	            	boolean blacklisted = (boolean) playerInfo.get(1);
	        		
	            	Balance myBalance = new Balance(balanceVersion, balance, balanceLimit, blacklisted);
	        		Player player = new Player(username, myBalance);
	        		cn.close();
	        		rs.close();
	        		return player;
	            }            
	            
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        }	        
	        cn.close();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;	    
	}
	
	private int getPlayerID (String username) throws ClassNotFoundException {
	    Connection cn = getConnected(urlDB);	    
	    try {
	        Statement stmt;
	        try {
	            stmt = cn.createStatement();	            
	            ResultSet rs = stmt.executeQuery(SQLstatement.selectPlayer + String.format("\"%s\"",username));	            
	            	            
	            if(rs.next()){
	            	int id = rs.getInt("id");
	            	rs.close();
	            	cn.close();
	        		return id;
	            }
	            rs.close();
            	cn.close();	            
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        }	        	               
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return 0;	    
	}
	
	private List<Object> selectPlayerInfo(int id) throws ClassNotFoundException, SQLException {
	    Connection cn = getConnected(urlDB);	    
	    Statement stmt;
        try {
            stmt = cn.createStatement();	            
            ResultSet rs = stmt.executeQuery(SQLstatement.selectPlayerInfo + id);	            
            List<Object> playerInfo = new ArrayList<Object>();	            
            if(rs.next()){         
            	float balanceLimit = rs.getFloat("balancelimit");
            	boolean blacklisted = rs.getBoolean("blacklisted");            	
            	rs.close(); 
            	
            	if(balanceLimit != 0.0){
            		playerInfo.add(balanceLimit);
            	} 
            	else{
            		playerInfo.add(SQLstatement.defaultBalanceLimit);	            		
            	}
            	
            	playerInfo.add(blacklisted);
            	cn.close();
            	return playerInfo;
            }
            
            else{
            	playerInfo.add(SQLstatement.defaultBalanceLimit);
            	playerInfo.add(false);
            	cn.close();
            	return playerInfo;
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	    return null;	    
	}
	
	private void updatePlayers(int id, int balanceVersion, float balance) throws ClassNotFoundException {
		Connection cn = getConnected(urlDB);
				
        try {
        	PreparedStatement prepst = cn.prepareStatement(SQLstatement.updatePlayers); 
        	prepst.setInt(1, balanceVersion);
            prepst.setFloat(2, balance);
            prepst.setInt(3, id);            
            prepst.executeUpdate();
            cn.close();   
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	private void updatePlayerInfo(int id, float balanceLimit, boolean blacklisted) throws ClassNotFoundException {
					
        try {
        	Connection cn = getConnected(urlDB);
        	PreparedStatement prepst = cn.prepareStatement(SQLstatement.updatePlayerInfo); 
        	prepst.setFloat(1, balanceLimit);
            prepst.setBoolean(2, blacklisted);
            prepst.setInt(3, id); 
            prepst.executeUpdate();
            cn.close();   
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }	
    
	public void updatePlayer(String username, int balanceVersion, float balance, float balanceLimit, boolean blacklisted) throws ClassNotFoundException {
		int id = getPlayerID(username);
		updatePlayers(id, balanceVersion, balance);
		updatePlayerInfo(id, balanceLimit, blacklisted);
    }
	
	private int takeLastTansactionId(){			    
	    try {
	    	Connection cn = getConnected(urlDB);
	        Statement stmt;
	        try {
	            stmt = cn.createStatement();	            
	            ResultSet rs = stmt.executeQuery(String.format("SELECT MAX(id) as maxID FROM transactions"));	            
	            	            
	            if(rs.next()){
	            	int id = rs.getInt("maxID");
	            	rs.close();
	            	cn.close();
	        		return id;
	            }
	            rs.close();
            	cn.close();	            
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        }	        	               
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return 0;	    
		
	}
	
	private String addTransactionSQL(Transaction transaction, int id){
		int trid = transaction.getId();
		int error = transaction.getErrorCode();
		int balver = transaction.getBalanceVersion();
		float balchange = transaction.getBalanceCahnge();
		float balafter = transaction.getBalanceAfter();
		String sql = String.format(SQLstatement.addTransaction, id, trid,error,balver,balchange,balafter);
		return sql;
	}
	private String addAdditionalTransactionSQL(Transaction transaction, int id){
		int trid = transaction.getId();
		int error = transaction.getErrorCode();
		int balver = transaction.getBalanceVersion();
		float balchange = transaction.getBalanceCahnge();
		float balafter = transaction.getBalanceAfter();
		String sql = String.format(SQLstatement.addAdditionalTransaction, id, trid,error,balver,balchange,balafter);
		return sql;
	}
	
	public boolean updateTransactions(List<Transaction>transactions) throws ClassNotFoundException {
		if(transactions.size()<1) return false;
		int nextTransactionID = takeLastTansactionId()+1;
		Connection cn = getConnected(urlDB);
		
		StringBuilder sb = new StringBuilder();
		sb.append(addTransactionSQL(transactions.get(0), nextTransactionID));
		if(transactions.size()>1){
			
			for(Transaction transaction: transactions.subList(1,transactions.size()-1)){
				nextTransactionID += 1;
				sb.append(addAdditionalTransactionSQL(transaction,nextTransactionID));
			}
		}
		
        try {
        	System.out.println(sb.toString());
        	PreparedStatement prepst = cn.prepareStatement(sb.toString());         	
            prepst.executeUpdate();
            cn.close();   
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }		
		return true;		
    }
	
	public List<Transaction> selectTransactions() {
        try {
        	Connection cn = getConnected(urlDB);		
    	    Statement stmt;
            stmt = cn.createStatement();	  
            List<Transaction> transactions = new ArrayList<Transaction>();  
            ResultSet rs = stmt.executeQuery(SQLstatement.selectTransactions);	            
                        
            while(rs.next()){   
            	int transactionID = rs.getInt("transactionid");
            	int errorCode = rs.getInt("errorcode");
            	int balanceVersion = rs.getInt("balanceversion");
            	float balanceChange = rs.getFloat("balancechange");
            	float balanceAfter = rs.getFloat("balanceafter");
            	
            	Transaction transaction = new Transaction(transactionID, errorCode, balanceVersion, balanceChange, balanceAfter);
            	transactions.add(transaction);
            }
            rs.close(); 
            cn.close();
        	return transactions;         
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	    return null;	    
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		Connector conn = new Connector();
		//conn.createTable(SQLstatement.createTransactions);
		//conn.createTable(SQLstatement.createPlayerInfo);
		HashMap<Integer, Transaction> trlist = new HashMap<Integer, Transaction>();
		System.out.println(conn.selectTransactions().isEmpty());
		for(Transaction tr:conn.selectTransactions()){
			System.out.println(tr.getId());
			trlist.put(tr.getId(), tr);
		}
		System.out.println(conn.takeLastTansactionId());
	}
}
