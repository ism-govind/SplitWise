package SplitClient;

import SplitWiseServer.*;
import utility.*;
import java.util.List;
import java.util.Iterator;


/** Client Should do the following to simplfy transaction 
 * 1. Create User (Email-Id,name) :-> Email id used as User ID
 * 2. Create Group () :-> returns Group ID,can be used later to add User into Group
 * 
 * 3. AddMemberToGroup(grpID,usrID) :-> 
 * 		3.1 Add registerd user to registered group
 * 		3.2 Else throw GroupNotExistException for Non registered Group
 * 		3.3 throw UserNotExistException for Non registered User
 * 
 * 4. createBill(GroupID,TotalAmount,userIDs[],debitAmount[],creditAmount[])
 * 	`	4.1 Parameters
 * 				GroupID
 * 				TotalAmount 
 * 				Array of userIDs -> participants in bill
 * 				Array of debitAmount -> User Liable for debitAmount[usrID]
 * 				Array if creditAmount -> user paid creditAmount[usrID]
 * 				 
 * 		4.2 return true if bill successfully added otherwise false
 * 		4.3 throw GroupNotExistException for Non Registered Group
 * 		4.4 throw UserNotExistException for non registerer user
 * 		4.5 throw UserNotExistInGroupException for Non group Member
 * 
 * 5. simplfy(grpID) :-> Simplyfy transaction for group 
 * 		5.1 return List<Transaction> after simplification
 * 		5.3 throw GroupNotExistException for Non registered Group.
 * @author gpatidar
 *
 */
public class SplitWiseClient {
	
	public static void main(String[] args)
	{
			SplitWise sp = new SplitWise();
			
			try
			{
				System.out.println("Starting Splitwise");
				Integer totalUserInGroup = 6;
				boolean b = false;
				
				
				/** Create User */
				String id1 = sp.createUser("A","Govind");
				String id2 = sp.createUser("B","vijay");
				String id3 = sp.createUser("C","prit");
				String id4 = sp.createUser("D","ajay");
				String id5 = sp.createUser("E","avinash");
				String id6 = sp.createUser("F","anshuman");
				
				/*Create group*/
				String grpID = sp.createGroup();
				/* Bill creation start */
				Double totalBill = 900.0d;
				Integer totalParticipantInBill = 3;
				String [] usrIds = new String[totalParticipantInBill];
				Double [] debtAmt = new Double[totalParticipantInBill];
				Double [] creditAmt = new Double[totalParticipantInBill];
				
				usrIds[0] = id1;
				usrIds[1] = id2;
				usrIds[2] = id3;
				/* Add member to group */
				for(int i=0;i<totalParticipantInBill;i++)
				{
					b = sp.addMemberToGroup(grpID, usrIds[i]);
					if(b)
					{
						System.out.println("user "+usrIds[i]+" is added in Grpup "+grpID);
					}
				}
				
				debtAmt[0] = 300.0;
				debtAmt[1] = 300.0;
				debtAmt[2] = 300.0;
				
				creditAmt[0] = 300.0;
				creditAmt[1] = 300.0;
				creditAmt[2] = 300.0;
			
				/*Create bill for 3 member*/
				 b = sp.createBill(grpID,totalBill,totalParticipantInBill,usrIds,debtAmt,creditAmt);
				 if(b)
				 {
					 System.out.println("Bill added Successfully");
				 }
				 /* Bill creation end*/
				 
				 /*2nd bill creation for next 3 member*/
				 totalBill = 1000.0d;
				 totalParticipantInBill = 3;
				 usrIds[0] = id4;
				 usrIds[1] = id5;
				 usrIds[2] = id6;
				 /* Add member to group */
				 for(int i=0;i<totalParticipantInBill;i++)
				 {
					 b = sp.addMemberToGroup(grpID, usrIds[i]);
					 if(b)
					 {
						System.out.println("user "+usrIds[i]+" is added in Grpup "+grpID);
					 }
				 }
					
				 debtAmt[0] = 300.0;
				 debtAmt[1] = 700.0;
				 debtAmt[2] = 0.0;
					
				 creditAmt[0] = 500.0;
				 creditAmt[1] = 0.0;
				 creditAmt[2] = 500.0; 
				 b = sp.createBill(grpID,totalBill,totalParticipantInBill,usrIds,debtAmt,creditAmt);
				 if(b)
				 {
					 System.out.println("Bill added Successfully");
				 }
				 /* 2nd bill creation end*/
				 
				 /*simplyfy bill */
				 System.out.println("SimplyFy Bill Calling");
				 List<Transaction> lst = sp.simplyfy(grpID);
				 Iterator<Transaction> it = lst.iterator();
				 while(it.hasNext())
				 {
					 Transaction tt = it.next();
					 System.out.println("User = "+tt.getDebitor()+" will pay "+tt.getTransactionAmount()+" to user = "+tt.getCreditor());
				 }
				 
				 /*Simplyfy bill*/
				 /* simply pick transaction list from cache*/
				 System.out.println("SimplyFy Bill Calling");
				 lst = sp.simplyfy(grpID);
				 it = lst.iterator();
				 while(it.hasNext())
				 {
					 Transaction tt = it.next();
					 System.out.println("User = "+tt.getDebitor()+" will pay "+tt.getTransactionAmount()+" to user = "+tt.getCreditor());
				 }
				 
			}
			catch(Exception ex)
			{
				ex.getMessage();
			}
		
	}
}
