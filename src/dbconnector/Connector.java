package dbconnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import player_gameplay.Balance;
import player_gameplay.Player;
import player_gameplay.Transaction;

public class Connector {
	private Connection conn;
	private static final String urlDB = SQLstatement.urlDB;
	public Connector() {
		try {
			this.conn = getConnected(urlDB);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
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
	    
	    try {
	        PreparedStatement prepSt;
	        try {
	            prepSt = conn.prepareStatement(sqlSt);
	            prepSt.executeUpdate();
	            prepSt.close();
	        } catch (Exception e) {
	            System.out.println(e.getMessage());	            
	        }	
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	}
	
	public Player selectPlayer (String username) throws ClassNotFoundException {
	    
        try {
        	
        	Statement stmt = conn.createStatement();	            
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
        		
        		rs.close();
        		return player;
            }            
            
    		rs.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }	 
	    return null;	    
	}
	
	public List<String> selectPlayersNames () throws ClassNotFoundException {		
        try {        	
    	    List<String> userNames = new ArrayList<String>();
        	Statement stmt = conn.createStatement();	            
            ResultSet rs = stmt.executeQuery(SQLstatement.selectPlayerNames);	            
            	            
            while(rs.next()){
            	String name = rs.getString("username");
            	userNames.add(name);
            }	
        		
        		rs.close();
        		return userNames;
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }	        	        
	    return null;	    
	}
	
	private int getNewPlayerID() {  
		int id = 0;	
		try {
        	
	        Statement stmt = conn.createStatement();	            
            ResultSet rs = stmt.executeQuery(String.format("SELECT MAX(id) as maxID FROM players"));	            
            
            if(rs.next()){
            	id = rs.getInt("maxID")+1;	            	
            }
            rs.close();        	
    		return id;            
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
        return id;
	}
	
	
	private int getPlayerID (String username) {  
		int id = 0;	
		try {
        	Statement stmt = conn.createStatement();	            
            ResultSet rs = stmt.executeQuery(SQLstatement.selectPlayer + String.format("\"%s\"",username));	            
                        
            if(rs.next()){
            	id = rs.getInt("id");            	         	
            } 
            rs.close();
        	
    		return id;   
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
        return id;
	}
	
	private List<Object> selectPlayerInfo(int id) throws ClassNotFoundException, SQLException {
	    Statement stmt;
        try {
            stmt = conn.createStatement();	            
            ResultSet rs = stmt.executeQuery(SQLstatement.selectPlayerInfo + id);	            
            List<Object> playerInfo = new ArrayList<Object>();	            
            if(rs.next()){         
            	float balanceLimit = rs.getFloat("balancelimit");
            	boolean blacklisted = rs.getInt("blacklisted") > 0;
            	     	
            	rs.close(); 
            	
            	if(balanceLimit == 0.0){
            		balanceLimit = SQLstatement.defaultBalanceLimit;	            		
            	}
            	playerInfo.add(balanceLimit);
            	playerInfo.add(blacklisted);
            	
            	return playerInfo;
            }
            
            /**else{
            	playerInfo.add(SQLstatement.defaultBalanceLimit);
            	playerInfo.add(false);
            	
            	return playerInfo;
            }**/
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	    return null;	    
	}
	
	private void updatePlayers(int id, int balanceVersion, float balance) throws ClassNotFoundException {
		
        try {
        	PreparedStatement prepst = conn.prepareStatement(SQLstatement.updatePlayers);         	
        	prepst.setInt(1, balanceVersion);
            prepst.setFloat(2, balance);
            prepst.setInt(3, id);            
            prepst.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	private void updatePlayerInfo(int id, float balanceLimit, int blacklisted) throws ClassNotFoundException {
					
        try {
        	
        	PreparedStatement prepst = conn.prepareStatement(SQLstatement.updatePlayerInfo); 
        	prepst.setFloat(1, balanceLimit);
            prepst.setInt(2, blacklisted);
            prepst.setInt(3, id); 
            prepst.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }	
    
	private int transformBlacklisted(boolean blacklisted){		
		if(blacklisted){
			return 1;
		} else return 0;
	}
	
	public void updatePlayer(String username, int balanceVersion, float balance, float balanceLimit, boolean blacklisted) throws ClassNotFoundException {
		int id = getPlayerID(username);
		
		if(id == 0){
			int PlayerID = getNewPlayerID();
			//System.out.println(PlayerID);
			addNewPlayer(PlayerID, username, balanceVersion, balance);
			addNewPlayerInfo(PlayerID, balanceLimit, transformBlacklisted(blacklisted));
		} else{
			updatePlayers(id, balanceVersion, balance);
			updatePlayerInfo(id, balanceLimit, transformBlacklisted(blacklisted));
		}		
    }
	
	private void addNewPlayer(int id, String username, int balanceVersion, float balance) throws ClassNotFoundException {
		
		try {			
			
        	PreparedStatement prepst = conn.prepareStatement(SQLstatement.addPlayer);         	
        	prepst.setInt(1, id);  
        	prepst.setString(2, username);  
        	prepst.setInt(3, balanceVersion);
            prepst.setFloat(4, balance);
            prepst.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	private void addNewPlayerInfo(int id, float balanceLimit, int blacklisted) throws ClassNotFoundException {
		
        try {
        	
        	PreparedStatement prepst = conn.prepareStatement(SQLstatement.addPlayerInfo); 
        	prepst.setInt(1, id); 
        	prepst.setFloat(2, balanceLimit);
            prepst.setInt(3, blacklisted);            
            prepst.executeUpdate();
              
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }	
	
	private int takeLastTansactionId(){			    
	    try {
	    	
	        Statement stmt;
	        try {
	            stmt = conn.createStatement();	            
	            ResultSet rs = stmt.executeQuery(String.format("SELECT MAX(id) as maxID FROM transactions"));	            
	            	            
	            if(rs.next()){
	            	int id = rs.getInt("maxID");
	            	rs.close();
	            	
	        		return id;
	            }
	            rs.close();
            } catch (Exception e) {
	            System.out.println(e.getMessage());
	        }	        	               
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return 0;	    
		
	}	
	private String updateTransactionsSQL(int i){
		StringBuilder sb = new StringBuilder();
		sb.append(SQLstatement.addTransaction);
		
		while(i>1){
			sb.append(SQLstatement.addAdditionalTransaction);
			i--;
		}
		return sb.toString();
	}
	
	public boolean updateTransactions(List<Transaction>transactions) throws ClassNotFoundException {
		int trSize = transactions.size();		
		if(trSize<1) return false;
				
		String stmtSQL = updateTransactionsSQL(trSize);
		int position = 1;
		int nextTransactionID = takeLastTansactionId()+1;
	
		try {			
			
        	PreparedStatement prepst = conn.prepareStatement(stmtSQL); 
        	Transaction firstTr = transactions.get(0);
        	
        	prepst.setInt(position, nextTransactionID);
        	prepst.setInt(position+1, firstTr.getId());
        	prepst.setInt(position+2, firstTr.getErrorCode());
        	prepst.setInt(position+3, firstTr.getBalanceVersion());
        	prepst.setFloat(position+4, firstTr.getBalanceCahnge());
        	prepst.setFloat(position+5, firstTr.getBalanceAfter());
        	
        	for(Transaction ntr:transactions.subList(1, transactions.size())){
        		position += 6;
    			nextTransactionID += 1;	
        		prepst.setInt(position, nextTransactionID);
            	prepst.setInt(position+1, ntr.getId());
            	prepst.setInt(position+2, ntr.getErrorCode());
            	prepst.setInt(position+3, ntr.getBalanceVersion());
            	prepst.setFloat(position+4, ntr.getBalanceCahnge());
            	prepst.setFloat(position+5, ntr.getBalanceAfter());
    		}

            prepst.executeUpdate();
             
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return true;		
    }
	
	public List<Transaction> selectTransactions() {
        try {
        	
    	    Statement stmt;
            stmt = conn.createStatement();	  
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
            
        	return transactions;         
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	    return null;	    
	}	
	
}
