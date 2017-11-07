package player_gameplay;

public enum Error {
	OK  (0),
	notEnough (1),
	overLimit   (2),
	blackListed (3),
	duplicateTransaction (4); 
	
	private final int ErrorCode;
	
	private Error(int ErrorCode) {
		this.ErrorCode = ErrorCode;
	}

	public int getErrorCode() {
		return ErrorCode;
	}	
}
