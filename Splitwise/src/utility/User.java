package utility;

import java.util.Objects;

public class User {
	
	/**
	 * ID to identify unique user ,Currently a String as Email-ID 
	 * */
	private String mID;
	
	/**
	 * name of the user
	 */
	private String mUserName;
	
	public User(String emailID,String username)
	{
		this.mID = emailID;
		this.mUserName = username;
	}
	
	public String getID()
	{
		return mID;
	}
	
	public String getUserName()
	{
		return mUserName;
	}
	
	public Boolean equals(User user2)
	{
		String userID_first = this.getID();
		String userID_second = this.getID();
		
		if(Objects.equals(userID_first, userID_second))
			return true;
		
		return false;
	}
	
	@Override
	/**
	 * hashCode ,same as String class hashCode
	 */
	public int hashCode()
	{
		return mID.hashCode();
	}
	

}

