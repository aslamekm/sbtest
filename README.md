Activity Diagram of TransactionServiceApplication.java
------------------------------------------------------
Run as spring boot application with help of maven - spring boot and rest api dependencies.
1.  @SpringBootApplication: This simplifies configuration for a Spring Boot application.
	@RestController: This annotation marks the class as a RESTful web service controller.

2.  TransactionServiceApplication ->> loadTransactionsFromFile(file path) - Tries to load the file path content.

3.  1 line of 6 element data from the readed document is considered as a page.

3.	With help of below 
	Collections and Strings.
	ConcurrentHashMap<Integer, Transaction> transactions -  updated the map value with transaction id count as key and value.
	ConcurrentHashMap <Integer, Transaction> "Stores transactions in memory (key: transaction ID, value: Transaction object)"
  	AtomicInteger "Generates unique transaction IDs"  transactionIdCounter
  	String "Represents search parameters"  customerId, accountNumber, description
  	ResponseEntity<List<Transaction>> "HTTP response containing list of transactions"
  	ResponseEntity<String> "HTTP response containing success/error message"

4.  @GetMapping("/transactions") -  
			public ResponseEntity<List<Transaction>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String description)
            {
            ......
            }

            - is used for fetching the data from the Transaction list item.
            - data can be fetched with customerid, account number, transaction description and with pagination.
    @PutMapping("/transactions/{id}")
    		public ResponseEntity<String> updateTransaction(@PathVariable int id, @RequestBody String newDescription) {
      		-----
    		}
    		- used to update the tranaction description based on the transaction list index/page	

5. Transaction class - helps declare and define the getters and setters.



Design patterm: Singleton Pattern

Singleton: The transactionIdCounter is a single instance. The Spring container manages the TransactionServiceApplication as a Singleton bean by default.


Proposed Database Structure:
--------------------------------
Given the data structure from the datasource.txt file and the requirements of the assignment (handling concurrent updates, searching, and pagination),
a relational database structure is the most appropriate

Mentioned below a sample database schema:

Table Name: transactions

Columns:

Column 				Name			Data Type						Constraints	Description
id					BIGINT			PRIMARY KEY, AUTO_INCREMENT		Unique identifier for each transaction.
account_number		VARCHAR(255)	NOT NULL, INDEX					Account number associated with the transaction. Indexed for efficient searching.
transaction_amount	DECIMAL(19, 2)	NOT NULL						Transaction amount. DECIMAL(19, 2) allows for large numbers with two decimal places.
description			VARCHAR(255)	INDEX							Description of the transaction. Indexed for efficient searching.
transaction_date	DATE			NOT NULL						Date of the transaction.
transaction_time	TIME			NOT NULL						Time of the transaction.
customer_id			INT				NOT NULL, INDEX					ID of the customer who made the transaction. Indexed for efficient searching.
