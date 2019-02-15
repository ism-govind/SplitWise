package SplitWiseServer;

/**
 * 
 * @author gpatidar
 * Used for representing a transaction
 * user1ID will pay amount values to user2ID
 */
public class Transaction {
	private String mDebitor;
	private String mCreditor;
	private Double mAmount;
	
	public Transaction(String usr1,String usr2,Double amount)
	{
		mDebitor = usr1;
		mCreditor = usr2;
		mAmount = amount;
	}
	
	public String getDebitor()
	{
		return mDebitor;
	}
	
	public String getCreditor()
	{
		return mCreditor;
	}
	
	public Double getTransactionAmount()
	{
		return mAmount;
	}
}


