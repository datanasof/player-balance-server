package player_gameplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dbconnector.DBhelper;

public class PlayersList implements Iterable<Player>{	
	HashMap<String, Player> players;
		
	public PlayersList(){
		players = new HashMap<String, Player>();		
	}
	
	@Override
    public Iterator<Player> iterator() {
		List<Player> plist = new ArrayList<Player>(players.values());
        return plist.iterator();
    }
	
	@SuppressWarnings("static-access")
	private Player getPlayerFromDB(String username){
		try {
			return DBhelper.getInstance().selectPlayer(username);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("static-access")
	public void updatePlayerToDB(Player p){
		try {
			DBhelper.getInstance().updatePlayer(p.getUsername(), p.getBalanceVersion(), p.getBalance(), p.getBalanceLimit(), p.isBlacklisted());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Player getPlayer(String username){
		if(players.containsKey(username)){
			return players.get(username);
		} else {
			return getPlayerFromDB(username);
		}		
	}
			
	private void updatePlayer(Player player){
		players.put(player.getUsername(), player);
	}
		
	public List<Object> playerTransaction(String username, float change){		
		Player player = getPlayer(username);
		
		float balanceBefore = player.getBalance();
		int errorCode = player.changeBalance(change);
		float balanceAfter = player.getBalance();
		float balanceChange = balanceAfter - balanceBefore;
		int balanceVersion = player.getBalanceVersion();
		
		List<Object> tr = Arrays.asList(errorCode, balanceVersion, balanceChange, balanceAfter);
		updatePlayer(player);
		return tr;
	}	

}
