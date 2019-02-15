package SplitWiseServer;


import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utility.*;
import SplitWiseServer.*;
public class SplitWise {
	
	/**
	 * Map grpID -> group Object
	 */
	private Map<String,Group> mGroupMap;
	
	/**
	 * Map userId -> user Object
	 */
	private Map<String,User> mUserMap;
	
	/**
	 * Cache - 
	 * used to save time from re-computation of simplyfy operation for a Group
	 * mCacheValid - tells whether mCache entry is valid or not
	 *    	groupID -> true
	 * mCache -> 
	 * 		groupID -> list of simplified transaction for group
	 */
	private Map<String,Boolean> mCacheValid;
	private Map<String,List<Transaction>> mCache;

	
	/**
	 * Integer used to for providing unique number/ID to group
	 * Increment each time when new group is created
	 */
	private static Integer GrpID = 0;
	
	/**
	 * Constructor to simulate server side code for splitwise
	 */
	public SplitWise()
	{
		mGroupMap = new HashMap<String,Group>();
		mUserMap  = new HashMap<String,User>();
		mCacheValid = new HashMap<String,Boolean>();
		mCache = new HashMap<String,List<Transaction>>();
	}
	
	/**
	 * Create a new group
	 * @return unique ID for the group,Client can use this ID to add Member in group
	 */
	public String createGroup()
	{
		String grpId = SplitWise.generateNewGroupID();
		Group grp = new Group(grpId);
		addNewGroupToDb(grpId,grp);
		return grpId;
	}
	
	
	/**
	 * addMemberInGroup
	 * Check if Group ID is valid
	 * Check if User ID is valid(any user exist with such ID)
	 * @param group Id ,user Id
	 * @return true if user Successfully added in group
	 */
	public Boolean addMemberToGroup(String grpID,String usrID) throws UserNotRegisteredException,GroupNotExistException
	{
		/* Validation for existence of user and group*/
		if(!checkIfGroupExist(grpID))
		{
			throw new GroupNotExistException();
		}
		
		if(!checkIfUserExist(usrID))
		{
			throw new UserNotRegisteredException(usrID);
		}
		
		
		Group grp = mGroupMap.get(grpID);
		Boolean isAdded = grp.addMember(usrID);
		
		/* Invalidate the cache Since new Member is Added in group*/
		mCacheValid.put(grpID,new Boolean(true));
		
		return isAdded;
	}
	
	/**
	 * Add New group to our Db
	 */
	private void addNewGroupToDb(String grpID,Group grp)
	{
		if(checkIfGroupExist(grpID))
			System.out.println("Group ID already Exist");
		
		mGroupMap.put(grpID,grp);
	}
	
	/**
	 * Generating new ID for each group incrementally
	 * @return
	 */
	private static String generateNewGroupID()
	{
		GrpID++;
		return GrpID.toString();
	}
	
	/**
	 * Create New User
	 * @param ID ,Email ID as ID to uniquely identify a user
	 * @return ID 
	 * Ideally returned object as (ID+info if user already exist) 
	 */
	public String createUser(String usrID,String name)
	{
		if(mUserMap.containsKey(usrID))
			return usrID;
		User usr = new User(usrID,name);
		mUserMap.put(usrID,usr);
		return usrID;
	}
	
	/**
	 * 
	 * @param grpID , group in which bill should be added
	 * @param userIDs , participant of bill
	 * @param debitAmount , total amount which userID[i] owes for the bill
	 * @param creditAmount , total amount which userID[i] paid for the bill
	 * @return true ,if bill successfully added otherwise false
	 * @exception GroupNotExistException, throws when client try to add bill for non registered group.
	 * @exception UserNotExistException, throws when client try to add bill for non registered user. 
	 */
	public boolean createBill(String grpID,Double totalBill,Integer totalParticipantInBill,String[] userIDs,Double[] debitAmt,Double[] creditAmt) throws GroupNotExistException,UserNotExistInGroupException,InvalidBillException
	{
		boolean grpExist = false;
		boolean userExist = false;
		boolean validBill = true;
		
		grpExist = checkIfGroupExist(grpID);
		
		/*check for registered group */
		if(!grpExist)
		{
			throw new GroupNotExistException();
		}
		
		/*check for registered user
		 *Currently simply informing client if invalid user,could be extended for return array of invalid users
		 * */
		Group grp = mGroupMap.get(grpID);
		
		boolean BillAdded = grp.addBill(totalBill,totalParticipantInBill,userIDs,debitAmt,creditAmt);
		
		/*Invalidate the cache for this group,since new bill is added*/
		mCacheValid.put(grpID,new Boolean(false));
		
		return BillAdded;	
	}
	
	/**
	 * Call this api to simplyfy transaction in a group.
	 * @param grpID
	 * @return List Of simplified Transaction 
	 */
	public List<Transaction> simplyfy(String grpID)
	{
		List<Transaction> transactionList = null;
		Boolean isValid = false;
		
		if(checkIfGroupExist(grpID))
		{
			/*Check if Simplfy transaction list exist in cache*/
			isValid = mCacheValid.get(grpID);
			if(isValid != null && isValid.equals(true));
			{
				transactionList = mCache.get(grpID);
			}
			
			if(transactionList == null)
			{
				Group grp = mGroupMap.get(grpID);
				transactionList = grp.simplyfy();
			}
		}
		
		if(transactionList != null && !isValid )
		{
			mCacheValid.put(grpID,new Boolean(true));
			mCache.put(grpID,transactionList);
		}
		
		return transactionList;
	}
	
	/**
	 * Util function to check if group exist in database
	 * @param grpID
	 * @return true if Group exist in Db else false
	 */
	private boolean checkIfGroupExist(String grpID)
	{
		if(mGroupMap.containsKey(grpID))
			return true;
		
		return false;
	}
	
	/**
	 * Util function to check if user exist in Db
	 * @param usrID
	 * @return true if user exist in Db
	 */
	private boolean checkIfUserExist(String usrID)
	{
		if(mUserMap.containsKey(usrID))
			return true;
		return false;
	}
	

}

