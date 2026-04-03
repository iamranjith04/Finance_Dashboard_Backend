package backend.Service;

import backend.Repository.AccountRepo;
import backend.Repository.RecordsRepo;
import backend.Repository.UserDataRepo;
import backend.database.Account;
import backend.database.Records;
import backend.database.enums.Category;
import backend.database.enums.TransactionType;
import backend.database.enums.UserRoles;
import backend.database.UsersData;
import backend.dto.AddRecordRequest;
import backend.dto.AddUserRequest;
import backend.dto.UpdateRecordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class AdminService {

    @Autowired
    private UserDataRepo userDataRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private RecordsRepo recordsRepo;

    public String addNewUser(AddUserRequest newUserDetails, String adminUserName) {
        UsersData admin = userDataRepo.findUsersDataByName(adminUserName);
        UserRoles role;
        try {
            role = UserRoles.valueOf(newUserDetails.Role);
        } catch (IllegalArgumentException e) {
            return "Invalid role provided: " + newUserDetails.Role;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(newUserDetails.Password);
        UsersData newUser = new UsersData(newUserDetails.Username, encodedPassword, role, admin);
        userDataRepo.save(newUser);
        return "User Added successfully";
    }

    public String deleteUser(int userId, String requestingAdminName) {
        Optional<UsersData> targetOpt = userDataRepo.findById(userId);
        if (targetOpt.isEmpty()) {
            return "User not found";
        }

        UsersData target = targetOpt.get();
        UsersData requestingAdmin = userDataRepo.findUsersDataByName(requestingAdminName);

        if (requestingAdmin.getUserId() == target.getUserId()) {
            return "Cannot delete yourself";
        }
        if ("Admin".equals(target.getRole())) {
            return "Cannot delete another Admin";
        }

        userDataRepo.delete(target);
        return "User deleted successfully";
    }

    @Transactional
    public String deleteRecord(long recordId, String adminName) {
        Optional<Records> recordOpt = recordsRepo.findById((int) recordId);
        if (recordOpt.isEmpty()) {
            return "Record not found";
        }

        Records record = recordOpt.get();
        UsersData admin = userDataRepo.findUsersDataByName(adminName);

        if (record.getAdminId() != admin.getUserId()) {
            return "Access denied: record belongs to another admin";
        }

        reverseAccountBalance(record.getAccountNo(), record.getTransactionType(), record.getAmount());

        recordsRepo.delete(record);
        return "Record deleted successfully";
    }


    @Transactional
    public String updateRecord(long recordId, UpdateRecordRequest request, String adminName) {
        Optional<Records> recordOpt = recordsRepo.findById((int) recordId);
        if (recordOpt.isEmpty()) {
            return "Record not found";
        }

        Records record = recordOpt.get();
        UsersData admin = userDataRepo.findUsersDataByName(adminName);

        if (record.getAdminId() != admin.getUserId()) {
            return "Access denied: record belongs to another admin";
        }

        try {
            Category newCategory  = (request.Category != null) ? Category.valueOf(request.Category) : record.getCategory();
            TransactionType newType = (request.Type != null) ? TransactionType.valueOf(request.Type) : record.getTransactionType();
            float newAmount = (request.Amount > 0) ? request.Amount : record.getAmount();

            boolean financialChange = newType != record.getTransactionType() || newAmount != record.getAmount();

            if (financialChange) {
                reverseAccountBalance(record.getAccountNo(), record.getTransactionType(), record.getAmount());
                updateAccountBalance(record.getAccountNo(), newType, newAmount);
            }

            record.setCategory(newCategory);
            record.setTransactionType(newType);
            record.setAmount(newAmount);

            if (request.Description != null) {
                record.setDescription(request.Description);
            }

            recordsRepo.save(record);
            return "Record updated successfully";

        } catch (IllegalArgumentException e) {
            return "Invalid Category or Type";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    @Transactional
    public String addNewRecord(AddRecordRequest request, String adminName) {
        try {
            Category category     = Category.valueOf(request.Category);
            TransactionType type  = TransactionType.valueOf(request.Type);

            UsersData admin = userDataRepo.findUsersDataByName(adminName);

            updateAccountBalance(request.AccountNo, type, request.Amount);

            Records newRecord = new Records();
            newRecord.setAdminId(admin.getUserId());
            newRecord.setAmount(request.Amount);
            newRecord.setCategory(category);
            newRecord.setCreatedAt(request.getTimestamp());
            newRecord.setTransactionType(type);
            newRecord.setDescription(request.Description);
            newRecord.setAccountNo(request.AccountNo);

            recordsRepo.save(newRecord);
            return "Record added successfully";

        } catch (IllegalArgumentException e) {
            return "Invalid Category or Type";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public List<Records> getRecordsHistory(String adminName) {
        UsersData admin = userDataRepo.findUsersDataByName(adminName);
        return recordsRepo.findTransactionOfAdmin(admin.getUserId());
    }

    private void updateAccountBalance(String accountNo, TransactionType type, float amount) {
        if (amount <= 0) throw new RuntimeException("Invalid Amount. Try again");

        Account act = accountRepo.findAccountByAccountId(accountNo);
        if (act == null) throw new RuntimeException("Account not found");

        if (type == TransactionType.EXPENSE) {
            if (amount > act.getBalance()) throw new RuntimeException("Insufficient balance!");
            act.setBalance(act.getBalance() - amount);
        } else {
            act.setBalance(act.getBalance() + amount);
        }
        accountRepo.save(act);
    }

    private void reverseAccountBalance(String accountNo, TransactionType type, float amount) {
        TransactionType reverseType = (type == TransactionType.EXPENSE) ? TransactionType.INCOME : TransactionType.EXPENSE;
        updateAccountBalance(accountNo, reverseType, amount);
    }
}