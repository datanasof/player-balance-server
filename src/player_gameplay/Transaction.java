package player_gameplay;

public class Transaction {
	int id;
	int errorCode;
	int balanceVersion;
	float balanceCahnge;
	float balanceAfter;
	public Transaction(int id, int errorCode, int balanceVersion, float balanceCahnge, float balanceAfter) {
		super();
		this.id = id;
		this.errorCode = errorCode;
		this.balanceVersion = balanceVersion;
		this.balanceCahnge = balanceCahnge;
		this.balanceAfter = balanceAfter;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public int getBalanceVersion() {
		return balanceVersion;
	}
	public void setBalanceVersion(int balanceVersion) {
		this.balanceVersion = balanceVersion;
	}
	public float getBalanceCahnge() {
		return balanceCahnge;
	}
	public void setBalanceCahnge(float balanceCahnge) {
		this.balanceCahnge = balanceCahnge;
	}
	public float getBalanceAfter() {
		return balanceAfter;
	}
	public void setBalanceAfter(float balanceAfter) {
		this.balanceAfter = balanceAfter;
	}
	
	public String printTransaction(){
		return(id+", "+errorCode+", "+balanceVersion+", "+balanceCahnge+", "+balanceAfter);
	}
	
}
