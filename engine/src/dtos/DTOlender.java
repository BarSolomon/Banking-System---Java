package dtos;

import loans.LoanPaymentInfo;

public class DTOlender {

    private String lenderName;
    private int LenderFund; //KEREN
    private int LenderInterest; //RIBIT
    private int LenderLoanPayment; //fund + interest (RIBIT + KEREN)
    private int LenderStartYaz;
    private int LenderLastPayment;

    public DTOlender(String lenderName,  LoanPaymentInfo info){
        this.lenderName = lenderName;
        this.LenderFund=info.getFund();
        this.LenderInterest = info.getInterest();
        this.LenderLoanPayment = info.getLoanPayment();
        this.LenderStartYaz = info.getStartYaz();
        this.LenderLastPayment = info.getLoanPayment();
    }


    public String getLenderName() {
        return lenderName;
    }

    public int getLenderFund() {
        return LenderFund;
    }

    public int getLenderLastPayment() {
        return LenderLastPayment;
    }

    public int getLenderInterest() {
        return LenderInterest;
    }

    public int getLenderLoanPayment() {
        return LenderLoanPayment;
    }

    public int getLenderStartYaz() {
        return LenderStartYaz;
    }
}
