package player_gameplay;

import dbconnector.Connector;

public class Player {
	
	private String username;
	private Balance myBalance;
	Connector conn;
	
	public Player(String username, Balance myBalance) {
		super();
		this.username = username;
		this.myBalance = myBalance;
		conn = new Connector();
	}

	public String getUsername() {
		return username;
	}

	public float getBalance() {
		return myBalance.getWalletBalance();
	}
	
	public int getBalanceVersion() {
		return myBalance.getVersion();
	}
	
	public void setBlacklisted(boolean blacklisted){
		myBalance.setBlacklisted(blacklisted);
	}

	public int changeBalance(float change){
		Error error;
		if(myBalance.isBlacklisted()) error = Error.blackListed;
		else if(myBalance.transactionOverLimit(change)) error = Error.overLimit;
		else if(myBalance.notEnoughBalance(change)) error = Error.notEnough;
		else{
			myBalance.changeWalletBallance(change);
			myBalance.changeVersion();
			error = Error.OK;
		}
		return error.getErrorCode();
	}
	
	public void updateToDB(){
		try {
			conn.updatePlayer(username, myBalance.getVersion(), myBalance.getWalletBalance(), myBalance.getLimit(), myBalance.isBlacklisted());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
