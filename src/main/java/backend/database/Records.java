package backend.database;

import backend.database.enums.Category;
import backend.database.enums.TransactionType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Records {

    @Id
    private long recordI;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private float amount;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;

    private int adminId;
    @Column(nullable = false, updatable = false)
    private String accountNo;

    public Records() {
    }

    public Records(float amount, TransactionType transactionType, Category category, String description){
        this.amount = amount;
        this.transactionType = transactionType;
        this.category = category;
        this.description = description;
    }

    public long getRecordI() {
        return recordI;
    }

    public void setRecordI(long recordI) {
        this.recordI = recordI;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
}
