package player_gameplay;

public class Balance {
	
	private int version;
	private float walletBalance;
	private float limit;
	private boolean blacklisted;
	
	public Balance(int version, float walletBalance, float limit, boolean blacklisted) {
		super();
		this.version = version;
		this.walletBalance = walletBalance;
		this.limit = limit;
		this.blacklisted = blacklisted;
	}
	
	public Balance(){		
	}
	
	public boolean isBlacklisted() {
		return blacklisted;
	}

	public void setBlacklisted(boolean blacklisted) {
		this.blacklisted = blacklisted;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public int changeVersion(){
		version += 1;
		return version;
	}

	public float getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(float walletBalance) {
		this.walletBalance = walletBalance;
	}
	
	public void changeWalletBallance(float change){
		walletBalance += change;
	}

	public float getLimit() {
		return limit;
	}

	public void setLimit(float limit) {
		this.limit = limit;
	}
	
	public boolean transactionOverLimit(float change){
		if(getLimit()>=change && change>=-1*getLimit()){
			return false;
		}
		return true;
	}
	
	public boolean notEnoughBalance(float change){
		if(getWalletBalance()+change >= 0){
			return false;
		}
		return true;
	}
}
