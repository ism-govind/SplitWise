package SplitWiseServer;

public class GroupNotExistException extends Exception{
	public GroupNotExistException()
	{
		super("Group Needs to registered");
	}
}
