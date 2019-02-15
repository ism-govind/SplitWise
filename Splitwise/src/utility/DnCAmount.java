package utility;

/**
 * Util class to Add Bill information 
 * mDebitAmt -> user is liable to pay for mDebitAmt amount for bill
 * mCreditAmt -> user paid mCreditAmt amount for bill
 * @author gpatidar
 *
 */
public class DnCAmount {

		private Double mDebitAmt;
		private Double mCreditAmt;
		
		public DnCAmount(Double debitAmount,Double creditAmount)
		{
			mDebitAmt = debitAmount;
			mCreditAmt = creditAmount;
		}
		
		public DnCAmount()
		{
			mDebitAmt = 0.0;
			mCreditAmt = 0.0;
		}
		
		public Double getDebitAmount()
		{
			return mDebitAmt;
		}
		
		public Double getCreditAmount()
		{
			return mCreditAmt;
		}	

}
