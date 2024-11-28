Loan Management System
Project Overview
This is a Loan Management System that serves as a platform to connect lenders with individual borrowers. In this system, borrowers can submit requests for loans, while lenders can specify the amount they wish to invest. The system uses a special matchmaking algorithm to minimize the risk of losing money by distributing the investment across multiple loans. If one loan is not repaid on time, the risk is reduced due to the diversified investment.

The system provides detailed information about the progress of all loans, allowing both borrowers and lenders to track their loan activities.

Key Features
User Interface (UI) Skin Toggle: Users can toggle between light mode and dark mode by using a button in the top-left corner of the interface.
File Loading: When an admin logs in, no actions can be taken until an XML file is loaded with the necessary data.
Admin Mode: Admins can advance the system's timeline and view detailed data about all users and loans.
Customer Mode: Customers (both borrowers and lenders) can view detailed information about their loans and perform various financial operations.
Starting the System
Upon launching the application, the admin user will be prompted to load an XML file containing all necessary data. The system will not allow further actions until this file is loaded using the "Load File" button.

Customer Mode Features:
Loan Information: The "Information" screen shows the details of all loans related to the logged-in customer, whether they are a borrower or a lender. On the right side of the screen, users can view their account activity and perform actions like depositing or withdrawing money.
Loan Scramble: The "Scramble" screen allows users to choose which loan to invest in and the specific amount they wish to invest. Users can filter loans based on various criteria.
Loan Payment: Customers can view their active loans and make payments. They can pay off full loans or make partial payments if they have sufficient funds.
Risk Mode: If a loan is at risk, customers can choose which portion of the debt they want to pay off.
Active Mode: Customers can choose to either pay the entire loan or just one payment.
Loan Matching Algorithm
In the "Scramble" section, users can filter loans and select the amount they wish to invest. The system checks if the entered amount is valid (i.e., the customer has enough funds). If the customer enters an invalid amount, an error message will be displayed.

Once the customer enters a valid amount, the system will display a list of loans that match the selected criteria. The customer can choose which loans to invest in.

The system then runs a matching algorithm:

It checks if the total amount can be evenly distributed across the selected loans.
If the amount exceeds a loan's outstanding debt, the system accumulates the surplus in a separate variable.
Once the total money is divided across all loans as evenly as possible, loans with fully funded amounts will change their status to "active."
The system continues to distribute the surplus money among remaining loans until no surplus is left.

Loan Payment System
In the "Payment" section, customers can view their active loans and make payments. The system shows loans that are still active.

Payment Process:
Risk Mode: Customers can select a loan in risk status and input the amount they wish to pay. If the loan is fully paid, its status will change to "active" or "finished."
Active Mode: Customers can either pay the entire loan or make a partial payment. The payment will be divided proportionally among all investors based on their contribution.
Class Overview
The system includes several classes to manage and handle data related to customers, loans, and transactions. Below is an overview of the key classes:

ABS Classes: These classes represent data imported from the XML schema, containing essential information.
Customer: A class representing customer data, including name, account status, and associated loans.
Customer Bank Action: This class tracks actions taken by the customer on their account.
Customer Bank Action List: A list of all customer bank actions.
Customer List: A collection of all customers in the system.
Exceptions: Classes that handle error scenarios and provide relevant messages.
Loan: Represents loan data, including the owner, category, ID, status, etc.
Loan Categories: A list of categories available for loans.
Loan Payment Info: Contains information about payments made by lenders toward loans.
Loan List: A list of all loans in the system.
Loan Yaz: Contains timing data for loans (start time, end time, total time, etc.).
Status ENUM: An enum containing all loan statuses.
Descriptor: A class that maps loans to customers and their relationships.
General Yaz Time: The current time for all system actions and related tasks.
Transport: Handles communication between the UI and backend, processing information accordingly.
DTO Classes: These classes provide limited access to data for the UI, only exposing getters to fetch information.
UI Main: The main UI class that communicates with the user, passing data to and from the transport layer, and displaying relevant information.
FXML Controllers: For each FXML file, a corresponding controller class handles button actions and loads the relevant data. The AppController is the main controller class that integrates intermediate controllers.
