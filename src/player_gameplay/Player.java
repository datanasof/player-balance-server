package player_gameplay;

public class Player {
	
	private String username;
	private Balance myBalance;
	
	
	public Player(String username, Balance myBalance) {
		super();
		this.username = username;
		this.myBalance = myBalance;
		
	}

	public String getUsername() {
		return username;
	}

	public float getBalance() {
		return myBalance.getWalletBalance();
	}
	
	public float getBalanceLimit(){
		return myBalance.getLimit();
	}
	
	public int getBalanceVersion() {
		return myBalance.getVersion();
	}
	
	public boolean isBlacklisted(){
		return myBalance.isBlacklisted();
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

}
