package general;

import customers.Customer;
import customers.CustomersList;
import jaxb.AbsCustomer;
import jaxb.AbsDescriptor;
import jaxb.AbsLoan;
import loans.Loan;
import loans.LoansList;
import java.util.List;
import java.util.Map;


public class Descriptor {
    private LoansList loansList;
    private CustomersList customersList;
    private GeneralYazTime currYaz;

    public Descriptor(AbsDescriptor tempDescriptor){
        loansList = new LoansList();
        customersList = new CustomersList();
       List<AbsLoan> tempLoanList = tempDescriptor.getAbsLoans().getAbsLoan();
       for(AbsLoan l : (tempLoanList)) {
           Loan loan = new Loan(l);
           loansList.addNode(loan);
       }

        List<AbsCustomer> tempCustomerList = tempDescriptor.getAbsCustomers().getAbsCustomer();
        for(AbsCustomer c : tempCustomerList) {
            Customer customer = new Customer(c);
            customersList.addNode(customer);
        }

        for (Map.Entry<String, Loan> l : loansList.getAllLoans().entrySet()){
            for(Customer c : customersList.getAllCostumers().values()){
                if(c.getName().equals(l.getValue().getLoanOwner())) {
                    c.addLoanBorrower(l.getValue());
                }
            }
        }
        currYaz = new GeneralYazTime();


    }

    public Map<String, Loan> getLoanList()
    {
        return this.loansList.getAllLoans();
    }
    public Map<String, Customer> getCustomerList() { return this.customersList.getAllCostumers(); }

    public int getCurrYaz() {
        return currYaz.getCurrYaz();
    }
}



