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
	
	private static String urlDB = SQLstatement.urlDB;
	
	private static Connection getConnected(String url) throws ClassNotFoundException{
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
	
	public static void createTable(String sqlSt) throws ClassNotFoundException{
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
	
	public static Player selectPlayer (String username) throws ClassNotFoundException {
	    
        try {
        	Connection cn = getConnected(urlDB);
        	Statement stmt = cn.createStatement();	            
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
            cn.close();
    		rs.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }	 
	    return null;	    
	}
	
	public static List<String> selectPlayersNames () throws ClassNotFoundException {		
        try {
        	Connection cn = getConnected(urlDB);
    	    List<String> userNames = new ArrayList<String>();
        	Statement stmt = cn.createStatement();	            
            ResultSet rs = stmt.executeQuery(SQLstatement.selectPlayerNames);	            
            	            
            while(rs.next()){
            	String name = rs.getString("username");
            	userNames.add(name);
            }	
        		cn.close();
        		rs.close();
        		return userNames;
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }	        	        
	    return null;	    
	}
	
	private static int getNewPlayerID() {  
		int id = 0;	
		try {
        	Connection cn = getConnected(urlDB);
	        Statement stmt = cn.createStatement();	            
            ResultSet rs = stmt.executeQuery(String.format("SELECT MAX(id) as maxID FROM players"));	            
            
            if(rs.next()){
            	id = rs.getInt("maxID")+1;	            	
            }
            rs.close();
        	cn.close();
    		return id;            
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
        return id;
	}
	
	
	private static int getPlayerID (String username) {  
		int id = 0;	
		try {
        	Connection cn = getConnected(urlDB);
	        Statement stmt = cn.createStatement();	            
            ResultSet rs = stmt.executeQuery(SQLstatement.selectPlayer + String.format("\"%s\"",username));	            
                        
            if(rs.next()){
            	id = rs.getInt("id");            	         	
            } 
            rs.close();
        	cn.close();
    		return id;   
           
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
        return id;
	}
	
	private static List<Object> selectPlayerInfo(int id) throws ClassNotFoundException, SQLException {
	    Connection cn = getConnected(urlDB);	    
	    Statement stmt;
        try {
            stmt = cn.createStatement();	            
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
            	cn.close();
            	return playerInfo;
            }
            
            /**else{
            	playerInfo.add(SQLstatement.defaultBalanceLimit);
            	playerInfo.add(false);
            	cn.close();
            	return playerInfo;
            }**/
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	    return null;	    
	}
	
	private static void updatePlayers(int id, int balanceVersion, float balance) throws ClassNotFoundException {
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
	
	private static void updatePlayerInfo(int id, float balanceLimit, int blacklisted) throws ClassNotFoundException {
					
        try {
        	Connection cn = getConnected(urlDB);
        	PreparedStatement prepst = cn.prepareStatement(SQLstatement.updatePlayerInfo); 
        	prepst.setFloat(1, balanceLimit);
            prepst.setInt(2, blacklisted);
            prepst.setInt(3, id); 
            prepst.executeUpdate();
            cn.close();   
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }	
    
	private static int transformBlacklisted(boolean blacklisted){		
		if(blacklisted){
			return 1;
		} else return 0;
	}
	
	public static void updatePlayer(String username, int balanceVersion, float balance, float balanceLimit, boolean blacklisted) throws ClassNotFoundException {
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
	
	private static void addNewPlayer(int id, String username, int balanceVersion, float balance) throws ClassNotFoundException {
		
		try {			
			Connection cn = getConnected(urlDB);
        	PreparedStatement prepst = cn.prepareStatement(SQLstatement.addPlayer);         	
        	prepst.setInt(1, id);  
        	prepst.setString(2, username);  
        	prepst.setInt(3, balanceVersion);
            prepst.setFloat(4, balance);
            prepst.executeUpdate();
            cn.close();   
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	private static void addNewPlayerInfo(int id, float balanceLimit, int blacklisted) throws ClassNotFoundException {
		
        try {
        	Connection cn = getConnected(urlDB);
        	PreparedStatement prepst = cn.prepareStatement(SQLstatement.addPlayerInfo); 
        	prepst.setInt(1, id); 
        	prepst.setFloat(2, balanceLimit);
            prepst.setInt(3, blacklisted);            
            prepst.executeUpdate();
            cn.close();   
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }	
	
	private static int takeLastTansactionId(){			    
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
	private static String updateTransactionsSQL(int i){
		StringBuilder sb = new StringBuilder();
		sb.append(SQLstatement.addTransaction);
		
		while(i>1){
			sb.append(SQLstatement.addAdditionalTransaction);
			i--;
		}
		return sb.toString();
	}
	
	public static boolean updateTransactions(List<Transaction>transactions) throws ClassNotFoundException {
		int trSize = transactions.size();		
		if(trSize<1) return false;
				
		String stmtSQL = updateTransactionsSQL(trSize);
		int position = 1;
		int nextTransactionID = takeLastTansactionId()+1;
	
		try {			
			Connection cn = getConnected(urlDB);
        	PreparedStatement prepst = cn.prepareStatement(stmtSQL); 
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
            cn.close();   
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return true;		
    }
	
	public static List<Transaction> selectTransactions() {
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
	
}
