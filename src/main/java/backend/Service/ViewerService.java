package backend.Service;

import backend.Repository.AccountRepo;
import backend.Repository.RecordsRepo;
import backend.Repository.UserDataRepo;
import backend.database.Account;
import backend.database.Records;
import backend.database.UsersData;
import backend.database.enums.Category;
import backend.database.enums.TransactionType;
import backend.dto.CategoryInsight;
import backend.dto.DashboardInsight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ViewerService {

    @Autowired
    private UserDataRepo userDataRepo;

    @Autowired
    private RecordsRepo recordsRepo;

    @Autowired
    private AccountRepo accountRepo;


    private UsersData resolveAdmin(String username) {
        UsersData user = userDataRepo.findUsersDataByName(username);
        UsersData creator = user.getCreator();
        if (creator == null) throw new RuntimeException("Viewer has no associated admin");
        return creator;
    }

    private List<Records> fetchAdminRecords(String username) {
        UsersData admin = resolveAdmin(username);
        return recordsRepo.findTransactionOfAdmin(admin.getUserId());
    }


    public DashboardInsight getDashboard(String username, int recentLimit) {
        List<Records> records = fetchAdminRecords(username);

        float totalIncome = sumByType(records, TransactionType.INCOME);
        float totalExpenses = sumByType(records, TransactionType.EXPENSE);
        float netBalance = totalIncome - totalExpenses;

        List<Records> recentActivity = records.stream()
                .sorted(Comparator.comparing(Records::getCreatedAt).reversed())
                .limit(recentLimit)
                .collect(Collectors.toList());

        return new DashboardInsight(totalIncome, totalExpenses, netBalance, recentActivity);
    }

    public Object getSummary(String username) {
        List<Records> records = fetchAdminRecords(username);

        float totalIncome   = sumByType(records, TransactionType.INCOME);
        float totalExpenses = sumByType(records, TransactionType.EXPENSE);
        float netBalance    = totalIncome - totalExpenses;

        return Map.of(
                "totalIncome",   totalIncome,
                "totalExpenses", totalExpenses,
                "netBalance",    netBalance,
                "totalRecords",  records.size()
        );
    }

    public List<Records> getRecentActivity(String username, int limit) {
        return fetchAdminRecords(username).stream()
                .sorted(Comparator.comparing(Records::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }


    public Object getNetBalance(String username) {
        UsersData admin = resolveAdmin(username);

        List<String> accountNos = recordsRepo
                .findTransactionOfAdmin(admin.getUserId())
                .stream()
                .map(Records::getAccountNo)
                .distinct()
                .collect(Collectors.toList());

        float totalBalance = 0f;
        for (String accountNo : accountNos) {
            Account account = accountRepo.findAccountByAccountId(accountNo);
            if (account != null) totalBalance += account.getBalance();
        }

        return Map.of(
                "accountCount", accountNos.size(),
                "netBalance",   totalBalance
        );
    }

    private float sumByType(List<Records> records, TransactionType type) {
        return (float) records.stream()
                .filter(r -> r.getTransactionType() == type)
                .mapToDouble(Records::getAmount)
                .sum();
    }

}
