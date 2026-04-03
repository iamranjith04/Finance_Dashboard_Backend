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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AdminService {

    @Autowired
    private UserDataRepo userDataRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private RecordsRepo recordsRepo;

    public String AddNewUser(AddUserRequest newUserDetails, String AdminUserName){
        UsersData Admin=userDataRepo.findUsersDataByName(AdminUserName);
        UserRoles role;
        try {
            role = UserRoles.valueOf(newUserDetails.Role);
        } catch (IllegalArgumentException e) {
            return "Invalid role provided: "+newUserDetails.Role;
        }
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        String encodedPassword=encoder.encode(newUserDetails.Password);
        UsersData newUser=new UsersData(newUserDetails.Username, encodedPassword, role, Admin);
        userDataRepo.save(newUser);
        return "User Added successfully";
    }

    private void updateAccountBalance(String accountNo, TransactionType type, float amount) {
        if (amount <= 0) throw new RuntimeException("Invalid Amount. Try again");

        Account act = accountRepo.findAccountByAccountId(accountNo);
        if (act == null) throw new RuntimeException("Account not found");

        if (type == TransactionType.EXPENSE) {
            if (amount > act.getBalance()) {
                throw new RuntimeException("Insufficient balance!");
            }
            act.setBalance(act.getBalance() - amount);
        } else {
            act.setBalance(act.getBalance() + amount);
        }

        accountRepo.save(act);
    }

    @Transactional
    public String addNewRecord(AddRecordRequest request, String adminName) {
        try {
            Category category = Category.valueOf(request.Category);
            TransactionType type = TransactionType.valueOf(request.Type);

            UsersData admin = userDataRepo.findUsersDataByName(adminName);

            updateAccountBalance(request.AccountNo, type, request.Amount);

            Records newRecord = new Records();
            newRecord.setAdminId(admin.getUserId());
            newRecord.setAmount(request.Amount);
            newRecord.setCategory(category);
            newRecord.setCreatedAt(request.getTimestamp());
            newRecord.setTransactionType(type);
            newRecord.setDescription(request.Description);

            recordsRepo.save(newRecord);
            return "Record added successfully";

        } catch (IllegalArgumentException e) {
            return "Invalid Category or Type";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public List<Records> getRecordsHistory(String AdminName){
        UsersData admin=userDataRepo.findUsersDataByName(AdminName);
        return recordsRepo.findTransactionOfAdmin(admin.getUserId());
    }


}
