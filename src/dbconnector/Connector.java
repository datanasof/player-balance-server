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
		Connection cn = getConnected(urlDB);
				
        try {
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
	
	public static void main(String[] args) throws ClassNotFoundException {
		Connector conn = new Connector();
		//conn.createTable(SQLstatement.createPlayers);
		//conn.createTable(SQLstatement.createPlayerInfo);
		Player gosho = conn.selectPlayer("user3");
		System.out.println(gosho.getUsername()+gosho.getBalance()+gosho.getBalanceVersion());
		conn.updatePlayer("user3", 6, 1999, 888, true);		
		Player pesho = conn.selectPlayer("user3");
		System.out.println(pesho.getUsername()+pesho.getBalance()+pesho.getBalanceVersion());
		
	}
}
