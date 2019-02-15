package utility;

import java.util.List;
import SplitWiseServer.*;
import java.util.HashMap; 
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.Iterator;
import utility.*;

public class Group {
	
	/**
	 * ID to represent unique group String 
	 */
	private String mGroupID;
	
	/**
	 * group , UserId -> DnCAmount(debit,credit amount)
	 */
	private Map<String,DnCAmount> mGroup;
	
	/**
	 * total number of Member in group
	 */
	public Integer mtotalMember;
	
	/**
	 * Max number of Member in a group
	 */
	public static final Integer MAX_NUMBER_OF_USER = 100;
	
	
	
	/**
	 * @Constructor,Create an Empty Group
	 */
	public Group(String ID)
	{
		mGroupID = ID;
		mGroup = new HashMap<String,DnCAmount>();
	}
	
	
	/**
	 * Add a user in group
	 * If User is already in group simply update debitAmt and creditAmt
	 * @param User to be added
	 * @return false if already exist otherwise true
	 */
	
	public Boolean addMember(String memberID,Double debitAmt,Double creditAmt)
	{
		if(mGroup.containsKey(memberID))
		{
			DnCAmount tr = mGroup.get(memberID);
			debitAmt = debitAmt + tr.getDebitAmount();
			creditAmt = creditAmt+ tr.getCreditAmount();
		}
		
		mGroup.put(memberID,new DnCAmount(debitAmt,creditAmt));
		return true;
	}
	
	public Boolean addMember(String memberID)
	{
		if(!mGroup.containsKey(memberID))
		{
			mGroup.put(memberID,new DnCAmount(0.0,0.0));
		}
		return true;
	}
	
	/**
	 * AddBill
	 * 1. Validate if all users in bill belongs to group,if not throw UserNotExistException
	 * 2. Check for invalid bill
	 * 3. Add bill to our Store
	 * @return true ,if bill successfully added otherwise false
	 */
	public Boolean addBill(Double totalBill,Integer totalParticipantInBill,String[] userIDs,Double[] debitAmt,Double[] creditAmt) throws UserNotExistInGroupException,InvalidBillException
	{
		/* validate users */
		List<String> invalidUsers = checkIfUserExists(userIDs);
		Integer lengthOfBill = totalParticipantInBill;
		
		if(!invalidUsers.isEmpty())
			throw new UserNotExistInGroupException(invalidUsers);	
		
		boolean validBill = checkForValidBill(totalBill,debitAmt, creditAmt);
		if(!validBill)
		{
			throw new InvalidBillException();
		}
		
		/*iterator over bill and store in local datastore*/
		int billItr = 0;
		Boolean isAdded = false;
		
		for(;billItr != lengthOfBill ; billItr++)
		{
			isAdded = addMember(userIDs[billItr], debitAmt[billItr],creditAmt[billItr]);
		}
		
		return isAdded;
	}
	
	/**
	 * Simplfy debit among group of people
	 * Algo:- reduce number of transaction to n-1.One by one take two people and do a Trasaction between them and Eliminate one.
	 * Alternative will be take min of maxDebitor and maxCreditor and(and do a transaction) but still we end up n-1 transaction. 
	 * @return
	 */
	public List<Transaction> simplyfy()
	{
		Integer noOfMember = mGroup.size();
		
		String prevUsrID = null,currUsrID = null;
		DnCAmount prevUsrDnCAmt = null,currUsrDnCAmt = null;
		Double prevUsrTotalAmt = 0.0,currUsrTotalAmt = null;
		
		Map.Entry<String, DnCAmount> prevMember=null,currMember = null;
		
		List<Transaction> transactionList = new ArrayList<Transaction>();
		
		
		/* Take two member and do a transaction between them*/
		Set<Map.Entry<String,DnCAmount>> memberSet = mGroup.entrySet();
		Iterator<Map.Entry<String,DnCAmount>> grpItr = memberSet.iterator();
		
		/**
		 * Init prevMember detail
		 * Extract (credit-debit) amount for user and fill into Pair obejct 
		 */
		prevMember = grpItr.next();
		prevUsrID = prevMember.getKey();
		prevUsrDnCAmt = prevMember.getValue();
		prevUsrTotalAmt = prevUsrDnCAmt.getCreditAmount() - prevUsrDnCAmt.getDebitAmount();
		
		
		while(grpItr.hasNext())
		{
			/*Extract detail for current memberI*/
			currMember = grpItr.next();
			currUsrID = currMember.getKey();
			currUsrDnCAmt = currMember.getValue();
			currUsrTotalAmt = currUsrDnCAmt.getCreditAmount() - currUsrDnCAmt.getDebitAmount();
			
			if( prevUsrTotalAmt > 0.0d)
			{
				/*Do transaction from currMember->prevMember and Exclude prevMember*/
				/*We can also do optimization, to not log transaction with zero amount*/
				Transaction tr = new Transaction(currUsrID, prevUsrID, prevUsrTotalAmt);
				transactionList.add(tr);
			}
			else
			{
				/*Do transaction from prevMember->currMember and Exclude prevMember*/
				/*We can also do optimization, to not log transaction with zero amount*/
				Transaction tr = new Transaction(prevUsrID, currUsrID, Math.abs(prevUsrTotalAmt));
				transactionList.add(tr);
			}
			
			prevUsrTotalAmt = prevUsrTotalAmt + currUsrTotalAmt;
			prevUsrID = currUsrID;
		}
		
		return transactionList;
		
	}
	
	private boolean checkForValidBill(Double totalBill,Double[] debitAmt,Double[] creditAmt)
	{
		Double dSum = 0.0;
		Double cSum = 0.0;
		boolean validBill = true;
		int itr =0;
		int lenDebiAmtArray = debitAmt.length;
		int lenCreditAmtArray = creditAmt.length;
		
		if(lenCreditAmtArray != lenDebiAmtArray)
		{
			validBill = false;
			return validBill;
		}
		
		for( itr = 0 ; itr < lenDebiAmtArray ;itr++)
		{
			dSum = dSum + debitAmt[itr];
			cSum = cSum + creditAmt[itr];
		}
		
		if(!dSum.equals(cSum) || !totalBill.equals(dSum))
		{
			validBill = false;
		}
		return validBill;
	}
	
	
	private List<String> checkIfUserExists(String[] userIds)
	{
		int noOfUsers = userIds.length;
		int itr =0;
		List<String> invalidUsers = new ArrayList<String>();
		for(itr = 0 ;itr < noOfUsers ; itr++)
		{
			if(!mGroup.containsKey(userIds[itr]))
				invalidUsers.add(userIds[itr]);
		}
		
		return invalidUsers;
	}
	
	/**
	 * @ remove a user from group
	 * @param usr
	 * @return true if successfully remove otherwise false
	 */
	public Boolean removeMember(String ID)
	{
		if(mGroup.containsKey(ID))
		{
			mGroup.remove(ID);
			return true;
		}
		return false;
	}
	
	/**
	 * provide information about all the Member
	 * return Unique ID of users
	 * @return Set<ID> , set of group Members
	 */
	public Set<String> getDetailOfGroupMember()
	{
		return mGroup.keySet();
	}
	
	public String getGroupID()
	{
		return mGroupID;
	}
	
	@Override
	public int hashCode()
	{
		return mGroupID.hashCode();
	}
	
	public boolean equals(Group grp)
	{
		String groupIDOne = this.getGroupID();
		String groupIDTwo = this.getGroupID();
		
		if(Objects.equals(groupIDOne,groupIDTwo))
			return true;
		
		return false;
	}
	
	
}
