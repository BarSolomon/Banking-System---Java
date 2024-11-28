package general;

import customers.Customer;
import dtos.*;
import exceptions.fileCategoriesException;
import exceptions.fileCustomersNamesException;
import exceptions.fileDivideYazPaymentException;
import exceptions.fileExtentionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import jaxb.AbsCustomer;
import jaxb.AbsDescriptor;
import jaxb.AbsLoan;
import loans.Loan;
import loans.LoanCategories;
import loans.LoanPaymentInfo;
import loans.status.StatusENUM;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Transport {

    private final static String GOOD_EXTENSION = "xml";
    private final static String JAXB_XML_PACKAGE_NAME = "jaxb";
    private Descriptor descriptor;
    private Boolean ifFileIsGood = false;

    public void loadNewXMLFile(String str) throws FileNotFoundException, JAXBException,
            fileCategoriesException, fileCustomersNamesException,
            fileDivideYazPaymentException, fileExtentionException {
        File path = new File(str);

        /*if(path.exists()) {
            Optional<String> checkXML = Optional.ofNullable(str)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(str.lastIndexOf(".") + 1));
            if (!(checkXML.get().equals(GOOD_EXTENSION)) &&
                    (!checkXML.get().equals(GOOD_EXTENSION.toUpperCase()))) {
                throw new fileExtentionException(checkXML.get());
            }
        }*/
        InputStream inputStream = new FileInputStream(path);
        AbsDescriptor tmpDescriptor = deserializeFrom(inputStream);

        if (!IsFileCategoriesAreCorrect(tmpDescriptor)) {
            throw new fileCategoriesException();
        }

        if (!IsFileDivideYazPaymentIsCorrect(tmpDescriptor)) {
            throw new fileDivideYazPaymentException();
        }

        if (TwoNamesAreTheSame(tmpDescriptor)) {
            throw new fileCustomersNamesException();
        }

        descriptor = new Descriptor(tmpDescriptor);
        ifFileIsGood = true;
        /* to get out all sources we need */
    }


    public static AbsDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(AbsDescriptor.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (AbsDescriptor) u.unmarshal(in);
    }

    public DTOallLoans getDTOLoans() {

        DTOallLoans dtoallLoans = new DTOallLoans(descriptor.getLoanList());
        return dtoallLoans;
    }

    public ObservableList<DTOLoan> getObservableLoanList() {
        ObservableList<DTOLoan> list = FXCollections.observableArrayList();
        for (DTOLoan l : getDTOLoans().getDTOloanList()) {
            list.add(l);
        }
        return list;
    }

    public ObservableList<DTOcustomer> getObservableCustomersList() {
        ObservableList<DTOcustomer> list = FXCollections.observableArrayList();
        for (DTOcustomer c : getDTOCustomers().getAllCustomers()) {
            list.add(c);
        }
        return list;
    }

    public ObservableList<DTOLoan> getObservableLoanerListByName(String name) {
        ObservableList<DTOLoan> loanerList = FXCollections.observableArrayList();
        for (DTOcustomer c : getDTOCustomers().getAllCustomers()) {
            if (c.getName().equals(name)) {
                for (DTOLoan l : c.getBorrowers().getDTOloanList()) {
                    loanerList.add(l);
                }
            }
        }
        return loanerList;
    }

    public ObservableList<DTOLoan> getObservableLendersListByName(String name) {
        ObservableList<DTOLoan> lendersList = FXCollections.observableArrayList();
        for (DTOcustomer d : getDTOCustomers().getAllCustomers()) {
            if (d.getName().equals(name)) {
                for (DTOLoan l : d.getLenders().getDTOloanList()) {
                    lendersList.add(l);
                }
            }
        }
        return lendersList;
    }

    public DTOcustomer getCustomerByHisName(String name) {
        DTOcustomer dtocustomer = null;
        for (DTOcustomer d : getDTOCustomers().getAllCustomers()) {
            if (d.getName().equals(name)) {
                dtocustomer = d;
            }
        }
        return dtocustomer;
    }

    public DTOallCustomers getDTOCustomers() {
        DTOallCustomers dtoallCustomers = new DTOallCustomers(descriptor.getCustomerList());
        return dtoallCustomers;
    }

    /*public void addMoneyToCustomer(int customer, int amountOfMoney){
        int index = 1;
        for(Customer c : descriptor.getCustomerList().values()){
            if(index == customer){
                c.addToBalance(amountOfMoney, descriptor.getCurrYaz());
            }
            index++;
        }
    }*/

    public void addMoneyToCustomer(String customer, int amountOfMoney) {
        for (Customer c : descriptor.getCustomerList().values()) {
            if (c.getName().equals(customer)) {

                c.addToBalance(amountOfMoney, descriptor.getCurrYaz());
            }
        }
    }

    public void minusMoneyToCustomer(String customer, int amountOfMoney) {
        for (Customer c : descriptor.getCustomerList().values()) {
            if (c.getName().equals(customer)) {
                c.minusFromBalance(amountOfMoney, descriptor.getCurrYaz());
            }
        }
    }

    /*public void minusMoneyToCustomer(int customer, int amountOfMoney){
        int index = 1;
        for(Customer c : descriptor.getCustomerList().values()){
            if(index == customer){
                c.minusFromBalance(amountOfMoney, descriptor.getCurrYaz());
            }
            index++;
        }
    }*/

    public boolean IsFileCategoriesAreCorrect(AbsDescriptor tmpDescriptor) {

        int Num = 0;
        for (String str : tmpDescriptor.getAbsCategories().getAbsCategory()) {
            if (!str.equals("Setup a business") && !str.equals("Overdraft cover") &&
                    !str.equals("Investment") && !str.equals("Happy Event") && !str.equals("Renovate")) {
                return false;
            } else
                Num++;
        }

        if (Num == 5) {
            return true;
        }
        return false;
    }

    public boolean IsFileDivideYazPaymentIsCorrect(AbsDescriptor tmpDescriptor) {
        boolean res = true;
        for (AbsLoan loan : tmpDescriptor.getAbsLoans().getAbsLoan()) {
            if (loan.getAbsTotalYazTime() % loan.getAbsPaysEveryYaz() != 0)
                res = false;
        }
        return res;
    }

    public boolean TwoNamesAreTheSame(AbsDescriptor tmpDescriptor) {
        int times;
        for (AbsCustomer c : tmpDescriptor.getAbsCustomers().getAbsCustomer()) {
            times = 0;
            for (AbsCustomer d : tmpDescriptor.getAbsCustomers().getAbsCustomer()) {
                if (c.getName().equals(d.getName())) {
                    times++;
                }
                if (times > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /* public  List <DTOLoan> filterMatchLoanByCustomer(int category, int interest, int minLoanYaz,
                                          String customer){
        DTOallLoans DTOLoans = new DTOallLoans(descriptor.getLoanList());
        List<DTOLoan> filteredList = new ArrayList<>();
        Predicate<DTOLoan> customerNamePredicate = (n) -> !(n.getLoanOwner().equals(customer));
        Predicate<DTOLoan> pendingPredicate = (p) -> p.getLoanStatus().getName().equals("pending");
        filteredList = DTOLoans.getDTOloanList().stream().filter(customerNamePredicate).collect(Collectors.toList());
        filteredList = filteredList.stream().filter(pendingPredicate).collect(Collectors.toList());
        if(category != 0) {
            LoanCategories c = LoanCategories.values()[category - 1];
            Predicate<DTOLoan> categoryPredicate = (l) -> l.getCategory().toString().equals(c.toString());
            filteredList = filteredList.stream().filter(categoryPredicate).collect(Collectors.toList());
        }
        if (interest != 0){
            Predicate<DTOLoan> interestPredicate = (o) -> o.getInterest() >= interest;
            filteredList = filteredList.stream().filter(interestPredicate).collect(Collectors.toList());
        }
        if(minLoanYaz != 0){
            Predicate<DTOLoan> minLoanYazPredicate = (z) -> z.getTotalYazTime() >= minLoanYaz;
            filteredList = filteredList.stream().filter(minLoanYazPredicate).collect(Collectors.toList());
        }
        return filteredList;
    }*/

    public ObservableList<DTOLoan> filterMatchLoan(ObservableList<String> categories, int interest, int minLoanYaz, int maxLoans, String customer) {
        int countCategoryFilter, countFilteredList = 0;
        DTOallLoans DTOLoans = new DTOallLoans(descriptor.getLoanList());
        ObservableList<DTOLoan> filteredList;
        ObservableList<DTOLoan> newFilteredList = FXCollections.observableArrayList();
        Predicate<DTOLoan> customerNamePredicate = (n) -> !(n.getLoanOwner().equals(customer));
        Predicate<DTOLoan> pendingPredicate = (p) -> p.getLoanStatus().getName().equals("pending");
        filteredList = FXCollections.observableArrayList(DTOLoans.getDTOloanList().stream().filter(customerNamePredicate).collect(Collectors.toList()));
        filteredList = FXCollections.observableArrayList(filteredList.stream().filter(pendingPredicate).collect(Collectors.toList()));
        if (categories != null) {
            while (countFilteredList < filteredList.size()) {
                countCategoryFilter = 0;
                while (countCategoryFilter < categories.size()) {
                    if (filteredList.get(countFilteredList).getCategory() == categories.get(countCategoryFilter)) {
                        newFilteredList.add(filteredList.get(countFilteredList));
                    }
                    countCategoryFilter++;
                }
                countFilteredList++;
            }
        }
        else {
            newFilteredList = filteredList;
        }
        if (interest != 0) {
            Predicate<DTOLoan> interestPredicate = (o) -> o.getInterest() >= interest;
            newFilteredList = FXCollections.observableArrayList(newFilteredList.stream().filter(interestPredicate).collect(Collectors.toList()));
        }
        if (minLoanYaz != 0) {
            Predicate<DTOLoan> minLoanYazPredicate = (z) -> z.getTotalYazTime() >= minLoanYaz;
            newFilteredList = FXCollections.observableArrayList(newFilteredList.stream().filter(minLoanYazPredicate).collect(Collectors.toList()));
        }
        if (maxLoans != 0){
            Predicate<DTOLoan> maxLoansPredicate = (c)-> (getCustomerByHisName(c.getLoanOwner())).customerOpenLoans() <= maxLoans;
            newFilteredList = FXCollections.observableArrayList(newFilteredList.stream().filter(maxLoansPredicate).collect(Collectors.toList()));
        }

        return newFilteredList;
    }


    public Boolean getIfFileIsGood() {
        return ifFileIsGood;
    }

    public String GetCustomerNameByIndex(int customer){
        int index = 1;
        String str = "";
        for(Customer c : descriptor.getCustomerList().values()){
            if(index == customer){
                str = c.getName();
            }
            index++;
        }
        return str;
    }

    public int GetCustomerAmountByIndex(int customer){
        int index = 1;
        int balance = 0;
        for(Customer c : descriptor.getCustomerList().values()){
            if(index == customer){
                balance = c.getBalance();
            }
            index++;
        }
        return balance;
    }
    public List<DTOLoan> createChosenList(Set<Integer> list, int amount, List<DTOLoan> filteredLoansList){

        int index = 1;
        List<DTOLoan> loanChoices = new ArrayList<>();
        for(Integer c : list){
            for(DTOLoan l : filteredLoansList){
                if (index == c){
                    loanChoices.add(l);
                }
                index++;
            }
            index=1;
        }
        return  loanChoices;
    }


    public boolean divideTheLoansMoney(List<DTOLoan> filteredLoansList, int moneyAmount, String customerName, int precent){

        int leftedMoney[] = new int[filteredLoansList.size()];
        int givenMoney[] = new int[filteredLoansList.size()];
        int sumLeftedMoney = 0;
        int currAmountToConvey, rest = 0;
        for(int i : givenMoney){
            i = 0;
        }
        for(int j = 0 ; j < filteredLoansList.size(); j++){

            leftedMoney[j] = filteredLoansList.get(j).getFundLeft();
            sumLeftedMoney += leftedMoney[j];
        }

        if(sumLeftedMoney < moneyAmount){
            moneyAmount = sumLeftedMoney;
        }
        while(moneyAmount != 0) {
            rest = 0;
            if (moneyAmount % filteredLoansList.size() != 0) {
                rest = moneyAmount % filteredLoansList.size();
            }
            currAmountToConvey = moneyAmount / filteredLoansList.size();
            for (int k = 0; k < filteredLoansList.size(); k++) {
                if (currAmountToConvey > leftedMoney[k]) {
                    givenMoney[k] += leftedMoney[k];
                    rest += (currAmountToConvey - leftedMoney[k]);
                    leftedMoney[k] = 0;
                } else {
                    givenMoney[k] += currAmountToConvey;
                    leftedMoney[k] -= currAmountToConvey;
                }
            }
            moneyAmount = rest;
            if (moneyAmount == 1) {
                for (int j = 0; j < filteredLoansList.size(); j++) {
                    if (leftedMoney[j] != 0) {
                        givenMoney[j] += moneyAmount;
                        leftedMoney[j] -= moneyAmount;
                        moneyAmount = 0;
                    }
                }
            }
    }

        updateLoansInvestments(customerName, filteredLoansList, givenMoney, precent);
        changeLoanStatusToActiveAndCheck();
        return true;
    }

    public void updateLoansInvestments(String customerName, List<DTOLoan> filteredLoansList, int givenMoney[], int precent){

        int index = 0;
        for(DTOLoan l : filteredLoansList) {
            if(precent!=0){
            if (((precent *l.getLoanAmount()) / 100) < givenMoney[index]){

                givenMoney[index] = ((precent *l.getLoanAmount()) / 100);
            }}
                //Minus from Blanace customer + add to actions bank

                this.descriptor.getCustomerList().get(customerName).minusFromBalance(givenMoney[index], descriptor.getCurrYaz());

                //Add lender to the list in the loan
                Customer temp = this.descriptor.getCustomerList().get(customerName);
                if (this.descriptor.getLoanList().get(l.getIdLoan()).getLenders().containsKey(temp) == true) {
                    this.descriptor.getLoanList().get(l.getIdLoan()).getLenders().get(temp).addMoneyToSameCustomer(givenMoney[index], l.getInterest());
                } else {
                    LoanPaymentInfo loanPaymentInfo = new LoanPaymentInfo(givenMoney[index], l.getInterest());
                    this.descriptor.getLoanList().get(l.getIdLoan()).addLender(descriptor.getCustomerList().get(customerName), loanPaymentInfo);
                }
                //add to the list lenders in customer
                this.descriptor.getCustomerList().get(customerName).addLoanLender(descriptor.getLoanList().get(l.getIdLoan()));

                //add to list borrower in customer (that the loan is own to him)
                this.descriptor.getCustomerList().get(l.getLoanOwner()).addLoanBorrower(descriptor.getLoanList().get(l.getIdLoan()));

                //update the loan values
                this.descriptor.getLoanList().get(l.getIdLoan()).updateTotalFundAndInterest();

                index++;
            }
        }
    public int countBorrowerLoansByStatus(String status, String name){
        DTOcustomer customer = getCustomerByHisName(name);
        int count = 0;
        for(DTOLoan l : customer.getBorrowers().getDTOloanList()){
            if (l.getLoanStatus().getName() == status){
                count++;
            }
        }
        return count;
    }


    public int countLendersLoansByStatus(String status, String name){
        DTOcustomer customer = getCustomerByHisName(name);
        int count = 0;
        for(DTOLoan l : customer.getLenders().getDTOloanList()){
            if (l.getLoanStatus().getName() == status){
                count++;
            }
        }
        return count;
    }

    public void payLoanByYazTime(){

        List<Loan> sortedLoans = descriptor.getLoanList().values().stream().filter((g) -> g.getLoanStatus().getName().equals("active")
                || g.getLoanStatus().equals("risk"))
                .sorted().collect(Collectors.toList());

        for(Loan i : sortedLoans){
            descriptor.getLoanList().get(i.getID()).updateLeftPulseToPay();
            //if is the time to pay
            if(this.descriptor.getCurrYaz() == descriptor.getLoanList().get(i.getID()).getLoanYaz().getNextPayment()){

                descriptor.getLoanList().get(i.getID()).getLoanYaz().setNextPayment(i.getLoanYaz().getPaysEveryYaz());
                //if you have the money, pay it
                if (descriptor.getCustomerList().get(descriptor.getLoanList().get(i.getID()).getLoanOwner()).getBalance() >= ((descriptor.getLoanList().get(i.getID()).getTotalAmountToPayOnce())*(descriptor.getLoanList().get(i.getID()).getLoanYaz().getNotPayPulseYaz()))){

                    descriptor.getLoanList().get(i.getID()).payEveryLender(descriptor.getLoanList().get(i.getID()).getLoanYaz().getNotPayPulseYaz());

                    descriptor.getCustomerList().get(descriptor.getLoanList().get(i.getID()).getLoanOwner()).minusFromBalance((descriptor.getLoanList().get(i.getID()).getTotalAmountToPayOnce()*(descriptor.getLoanList().get(i.getID()).getLoanYaz().getNotPayPulseYaz())), GeneralYazTime.getCurrYaz());

                    descriptor.getLoanList().get(i.getID()).getLoanYaz().setLastPayYaz(descriptor.getCurrYaz());
                }
                else
                {
                    descriptor.getLoanList().get(i.getID()).setLoanStatus(StatusENUM.RISK);
                }
            }
        }


    }

    public void PayLoansByAmount(int amount, List<DTOLoan> chosenLoans){

        for(DTOLoan DTOloan : chosenLoans){
            for(Loan loan : descriptor.getLoanList().values()){
                if(DTOloan.getIdLoan().equals(loan.getID())){

                }
            }
        }

    }

   /* public void divideTheLoansPaymentsToCustomers(int amount, List<DTOLoan> chosenLoans, String customerName){
        int leftedMoney[] = new int[chosenLoans.size()];
        int givenMoney[] = new int[chosenLoans.size()];
        int sumLeftedMoney = 0;
        int currAmountToConvey, rest = 0;
        for(int i : givenMoney){
            i = 0;
        }
        for(int j = 0 ; j < chosenLoans.size(); j++){

            leftedMoney[j] = chosenLoans.get(j).getFundLeft();
            sumLeftedMoney += leftedMoney[j];
        }
        if(sumLeftedMoney < moneyAmount){
            return false;
        }
        while(moneyAmount != 0) {
            rest = 0;
            if (moneyAmount % filteredLoansList.size() != 0) {
                rest = moneyAmount % filteredLoansList.size();
            }
            currAmountToConvey = moneyAmount / filteredLoansList.size();
            for (int k = 0; k < filteredLoansList.size(); k++) {
                if (currAmountToConvey > leftedMoney[k]) {
                    givenMoney[k] += leftedMoney[k];
                    rest += (currAmountToConvey - leftedMoney[k]);
                    leftedMoney[k] = 0;
                } else {
                    givenMoney[k] += currAmountToConvey;
                    leftedMoney[k] -= currAmountToConvey;
                }
            }
            moneyAmount = rest;
            if (moneyAmount == 1) {
                for (int j = 0; j < filteredLoansList.size(); j++) {
                    if (leftedMoney[j] != 0) {
                        givenMoney[j] += moneyAmount;
                        leftedMoney[j] -= moneyAmount;
                        moneyAmount = 0;
                    }
                }
            }
        }
        updateLoansInvestments(customerName, filteredLoansList, givenMoney);
        changeLoanStatusToActiveAndCheck();
        return true;
    }*/


    public void checkFinishedLoan(){
        for(Loan l : descriptor.getLoanList().values()) {
            if ((l.getLoanStatus().getName().equals("risk")) || (l.getLoanStatus().getName().equals("active"))) {
                if (l.getSumOfPaymentAlreadyPaid() == l.getTotalAmountReturn()) {
                    l.setLoanStatus(StatusENUM.FINISHED);
                    l.getLoanYaz().setEndingYaz(descriptor.getCurrYaz());
                }
            }
        }
    }

    public void changeLoanStatusToActiveAndCheck(){
        for(Loan l : descriptor.getLoanList().values()) {
            if ((l.getTotalFund() == l.getLoanAmount()) &&
                    (l.getLoanStatus().getName().equals("pending"))) {
                l.setLoanStatus(StatusENUM.ACTIVE);
                l.getLoanYaz().setStartActiveYaz(descriptor.getCurrYaz());
                l.getLoanYaz().setLastPayYaz(descriptor.getCurrYaz());
                l.getLoanYaz().setNextPayment(l.getLoanYaz().getLastPayYaz() + l.getLoanYaz().getPaysEveryYaz());
                descriptor.getCustomerList().get(l.getLoanOwner()).addToBalance(l.getLoanAmount(), descriptor.getCurrYaz());
            }
        }
    }

    public void addLoanYaz(){
        GeneralYazTime.addCurrYaz();
    }

    public int getLoanYaz(){
        return GeneralYazTime.getCurrYaz();
    }

    public boolean conveyMoneyToLoanLenders(DTOLoan loan) {

        for(Loan i: descriptor.getLoanList().values())
        {
            if(loan.getIdLoan() == i.getID())
            {
                //check if the owner has enouth money for one pulse
                if(descriptor.getCustomerList().get(descriptor.getLoanList().get(i.getID()).getLoanOwner()).getBalance() >= (descriptor.getLoanList().get(i.getID()).getTotalAmountToPayOnce()))
                {
                    i.getLoanYaz().setLastPayYaz(descriptor.getCurrYaz());
                    //pay to every lender his pulse
                    descriptor.getLoanList().get(i.getID()).payEveryLender(1);
                    //minus money to the owner
                    descriptor.getCustomerList().get(descriptor.getLoanList().get(i.getID()).getLoanOwner()).minusFromBalance((descriptor.getLoanList().get(i.getID()).getTotalAmountToPayOnce()), GeneralYazTime.getCurrYaz());
                    //update next payment - not sure if its working

                    i.getLoanYaz().setNextPayment(descriptor.getCurrYaz());
                    i.getLoanYaz().setBooleanPaymentToTrue();
                    //check if finish
                    checkIfLoanFinish(i.getID());
                    return true;
                }
            }
        }

        return false;
    }

    public void checkIfLoanIsRisk(){
        for(Loan l : descriptor.getLoanList().values()){
            if((l.getLoanYaz().getPayedOrNot().containsKey(descriptor.getCurrYaz()) == true)) {
                if((l.getLoanStatus().getName().equals("active")) || (l.getLoanStatus().getName().equals("risk"))) {
                    if (l.getLoanYaz().getPayedOrNot().get(descriptor.getCurrYaz()).booleanValue() == false) {
                        l.setLoanStatus(StatusENUM.RISK);
                        l.plusToDebt(l.getTotalAmountToPayOnce());
                        String str = "Your Loan ID: " + l.getID().toString() + " ,is on status: Risk! pay immediately!!";
                        descriptor.getCustomerList().get(l.getLoanOwner()).addToRiskNotifications(str);
                    }
                }
            }
        }
    }

    public boolean conveyAllMoneyToLoanLenders(DTOLoan loan){
        for(Loan i: descriptor.getLoanList().values())
        {
            if(loan.getIdLoan() == i.getID())
            {
                //check if the owner has enouth money
                if(descriptor.getCustomerList().get(descriptor.getLoanList().get(i.getID()).getLoanOwner()).getBalance() >= i.leftedMoneyToPay())
                {
                    i.getLoanYaz().setLastPayYaz(descriptor.getCurrYaz());
                    //minus money to the owner
                    descriptor.getCustomerList().get(descriptor.getLoanList().get(i.getID()).getLoanOwner()).minusFromBalance( i.leftedMoneyToPay(), GeneralYazTime.getCurrYaz());

                    //pay to every lender his pulse
                    descriptor.getLoanList().get(i.getID()).payAllRestMoneyEveryLender();

                    //check if finish
                    checkIfLoanFinish(i.getID());
                    //descriptor.getLoanList().get(i.getID()).setLoanStatus(StatusENUM.FINISHED);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean conveyMoneyToRiskLoan(int amount, DTOLoan loan){
        for(Loan i: descriptor.getLoanList().values())
        {
            if(loan.getIdLoan() == i.getID())
            {
                //check if the owner has enouth money for one pulse
                if(descriptor.getCustomerList().get(descriptor.getLoanList().get(i.getID()).getLoanOwner()).getBalance() >= amount)
                {
                    //pay to every lender his pulse
                    int toMinus = descriptor.getLoanList().get(i.getID()).payEveryLenderOnRisk(amount);
                    //minus money to the owner
                    descriptor.getCustomerList().get(descriptor.getLoanList().get(i.getID()).getLoanOwner()).minusFromBalance(toMinus, GeneralYazTime.getCurrYaz());
                    i.minusFromDebt(toMinus);
                    //update next payment - not sure if its working
                    if(i.getDebt() == 0) {
                        i.setLoanStatus(StatusENUM.ACTIVE);
                        i.getLoanYaz().setNextPayment(descriptor.getCurrYaz());
                        i.getLoanYaz().setBooleanPaymentToTrue();
                    }
                    //check if finish
                    checkIfLoanFinish(i.getID());
                    return true;
                }
            }
        }

        return false;
    }


    public void checkIfLoanFinish(String ID)
    {
        //if the amount to return is the same as amount that paid
        if(descriptor.getLoanList().get(ID).getSumOfPaymentAlreadyPaid() == descriptor.getLoanList().get(ID).getTotalAmountReturn()){
            descriptor.getLoanList().get(ID).setLoanStatus(StatusENUM.FINISHED);
            descriptor.getLoanList().get(ID).getLoanYaz().setEndingYaz(descriptor.getCurrYaz());
            String str = "Your Loan ID: " + ID + " ,is on status: Finish! Mazal Tov!!!";
            descriptor.getCustomerList().get(descriptor.getLoanList().get(ID).getLoanOwner()).addToRiskNotifications(str);
        }
    }



}




