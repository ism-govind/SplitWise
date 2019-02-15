package SplitWiseServer;

import java.util.List;

public class UserNotExistInGroupException extends Exception{
	
		public UserNotExistInGroupException(List<String> invalidUsers) {
			// TODO Auto-generated constructor stub
			super("Users"+invalidUsers.toString()+" should be added to Group First");
		}
}
