package customers;

import loans.Loan;
import java.util.HashMap;
import java.util.Map;

public class CustomersList {

    private Map<String, Customer> allCostumers;

    public CustomersList(){
        allCostumers = new HashMap<>();
    }

    public Map<String, Customer> getAllCostumers() {
        return this.allCostumers;
    }

    public void addNode(Customer customer){
        allCostumers.put(customer.getName(), customer);
    }

}
