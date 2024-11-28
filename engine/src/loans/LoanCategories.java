package loans;

public enum LoanCategories {

    SETUP_A_BUSINESS("Setup a business"),
    OVERDRAFT_COVER("Overdraft cover"),
    INVESTMENT("Investment"),
    HAPPY_EVENT("Happy Event"),
    RENOVATE("Renovate");

    private String name;
   LoanCategories(String Name){
        this.name = Name;
    }

    public String toString() {
        return name;
    }

}

