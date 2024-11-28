package loans;

import general.GeneralYazTime;

public class LoanPaymentInfo {

    private int fund; //KEREN
    private int interest; //RIBIT
    private int loanPayment; //fund + interest (RIBIT + KEREN)
    private int StartYaz;
    private int restPayToCustomer;
    private int lastPayment;

    public LoanPaymentInfo(int loanAmount, int interestPrecent){
        this.fund = loanAmount;
        this.interest= ((interestPrecent * loanAmount) / 100);
        this.loanPayment = (this.interest + this.fund);
        this.StartYaz = GeneralYazTime.getCurrYaz(); //LoanYaz.getCurrYazTime();
        this.restPayToCustomer = loanPayment;
    }

    public void addYazTime(int yazToAdd){
        lastPayment += yazToAdd;
    }

    public int getStartYaz(){
        return StartYaz;
    }

    public int getFund() {
        return fund;
    }

    public void addMoneyToSameCustomer(int amount, int interestPrecent){
        fund += amount;
        interest= (interestPrecent/100) * fund;
        loanPayment = this.interest + fund;
    }

    public int getInterest() {
        return interest;
    }

    public int getLoanPayment() {
        return loanPayment;
    }

    public void minusRestPay (int pay)
    {
        this.restPayToCustomer = this.restPayToCustomer-pay;
    }

    public int getRestPayToCustomer (){
        return this.restPayToCustomer;
    }
}
