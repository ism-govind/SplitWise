package SplitWiseServer;

public class UserNotRegisteredException extends Exception{
	public UserNotRegisteredException(String rID)
	{
		super("User "+rID+" need to be registered first");
	}
}
