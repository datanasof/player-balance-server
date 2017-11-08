package dbconnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import player_gameplay.Player;

public class Connector {
	
	private String urlDB = SQLstatement.urlDB;
	
	private Connection getConnected(String url){
		try {
			Connection conn = DriverManager.getConnection(url);
			return conn;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean createTable(String sqlSt) {
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
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public Player selectPlayer (String username) {
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
	               System.out.println(id+"; "+balanceVersion+"; "+balance);
	               //Player player = new Player(username, balanceVersion, balance);
	               //return player;
	            }
	            rs.close();          
	            
	            
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        }	        
	        cn.close();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;	    
	}
	
	public List<Object> selectPlayerInfo(int id) {
	    Connection cn = getConnected(urlDB);	    
	    try {
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
	            	return playerInfo;
	            }
	            
	            else{
	            	playerInfo.add(SQLstatement.defaultBalanceLimit);
	            	playerInfo.add(false);
	            	return playerInfo;
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
	
	public static void main(String[] args) {
		Connector conn = new Connector();
		conn.createTable(SQLstatement.createPlayers);
		conn.createTable(SQLstatement.createPlayerInfo);
		conn.selectPlayer("user3");
		List<Object> plinfo = new ArrayList<Object>();
		plinfo = conn.selectPlayerInfo(2);
		System.out.println(plinfo.get(0));
		System.out.println(plinfo.get(1));
	}
}
/**
stmt = cn.createStatement();	            
rs = stmt.executeQuery(SQLstatement.selectPlayerInfo + id);	            
	            
if(rs.next()){
   balanceVersion = rs.getInt("balance_version");
   balance = rs.getFloat("balance");
   System.out.println(balanceVersion+"; "+balance);
   //Player player = new Player(username, balanceVersion, balance);
   //return player;
}
rs.close();**/