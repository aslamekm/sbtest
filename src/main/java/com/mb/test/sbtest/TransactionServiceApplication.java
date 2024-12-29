package com.mb.test.sbtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
@SpringBootApplication
@RestController
public class TransactionServiceApplication {

    private static final ConcurrentHashMap<Integer, Transaction> transactions = new ConcurrentHashMap<>();
    private static final AtomicInteger transactionIdCounter = new AtomicInteger(1);

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
        loadTransactionsFromFile("datasource.txt");
    }

    private static void loadTransactionsFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header row
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    Transaction transaction = new Transaction();
                    transaction.setAccountNumber(parts[0]);
                    transaction.setTransactionAmount(new BigDecimal(parts[1]));
                    transaction.setDescription(parts[2]);
                    transaction.setTransactionDate(LocalDate.parse(parts[3], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    transaction.setTransactionTime(LocalTime.parse(parts[4], DateTimeFormatter.ofPattern("HH:mm:ss")));
                    transaction.setCustomerId(Integer.parseInt(parts[5]));
                    int transactionId = transactionIdCounter.getAndIncrement();
                    transactions.put(transactionId, transaction);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle appropriately in real application
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String description) {

        List<Transaction> filteredTransactions = new ArrayList<>(transactions.values());

        if (customerId != null) {
            filteredTransactions.removeIf(t -> !String.valueOf(t.getCustomerId()).equals(customerId));
        }

        if (accountNumber != null) {
            filteredTransactions.removeIf(t -> !t.getAccountNumber().equals(accountNumber));
        }

        if (description != null) {
            filteredTransactions.removeIf(t -> !t.getDescription().toLowerCase().contains(description.toLowerCase()));
        }

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, filteredTransactions.size());

        if (startIndex >= filteredTransactions.size()) {
            return ResponseEntity.ok(new ArrayList<>()); // return empty list if page is out of bounds
        }
        return ResponseEntity.ok(filteredTransactions.subList(startIndex, endIndex));
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable int id, @RequestBody String newDescription) {
        if (transactions.containsKey(id)) {
            transactions.get(id).setDescription(newDescription);
            return ResponseEntity.ok("Transaction updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
        }
    }


    public static class Transaction {
        private String accountNumber;
        private BigDecimal transactionAmount;
        private String description;
        private LocalDate transactionDate;
        private LocalTime transactionTime;
        private int customerId;

        // Getters and setters
        public String getAccountNumber() { return accountNumber;}
        public void setAccountNumber(String accountNumber) {this.accountNumber = accountNumber;}

        public BigDecimal getTransactionAmount() {return transactionAmount;}
        public void setTransactionAmount(BigDecimal transactionAmount){this.transactionAmount= transactionAmount;}

        public String getDescription() {return description;}
        public void setDescription(String description){this.description = description;}

        public LocalDate getTransactionDate() {return transactionDate;}
        public void setTransactionDate(LocalDate transactionDate){this.transactionDate = transactionDate;}

        public LocalTime getTransactionTime() {return transactionTime;}
        public void setTransactionTime(LocalTime transactionTime){this.transactionTime = transactionTime;}

        public int getCustomerId() {return customerId;}
        public void setCustomerId(int customerId){this.customerId = customerId;}
    }
}