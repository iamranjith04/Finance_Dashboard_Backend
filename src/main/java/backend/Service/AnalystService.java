package backend.Service;

import backend.Repository.RecordsRepo;
import backend.Repository.UserDataRepo;
import backend.database.Records;
import backend.database.UsersData;
import backend.database.enums.Category;
import backend.database.enums.TransactionType;
import backend.dto.CategoryInsight;
import backend.dto.MonthlyInsight;
import backend.dto.SummaryInsight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AnalystService {

    @Autowired
    private UserDataRepo userDataRepo;

    @Autowired
    private RecordsRepo recordsRepo;


    private UsersData resolveAdmin(String username) {
        UsersData user = userDataRepo.findUsersDataByName(username);

        UsersData creator = user.getCreator();
        if (creator == null) throw new RuntimeException("Analyst has no associated admin");
        return creator;
    }

    private List<Records> fetchAdminRecords(String username) {
        UsersData admin = resolveAdmin(username);
        return recordsRepo.findTransactionOfAdmin(admin.getUserId());
    }


    public List<Records> getAllRecords(String username) {
        return fetchAdminRecords(username);
    }

    public Object getRecordById(int recordId, String username) {
        Optional<Records> recordOpt = recordsRepo.findById(recordId);
        if (recordOpt.isEmpty()) {
            return "Record not found";
        }

        Records record = recordOpt.get();
        UsersData admin = resolveAdmin(username);

        if (record.getAdminId() != admin.getUserId()) {
            return "Access denied: record belongs to another admin";
        }
        return record;
    }

    public Object getFilteredRecords(String username, String categoryStr, String typeStr) {
        List<Records> records = fetchAdminRecords(username);

        Category        category = null;
        TransactionType type     = null;

        if (categoryStr != null) {
            try { category = Category.valueOf(categoryStr); }
            catch (IllegalArgumentException e) { return "Invalid category: " + categoryStr; }
        }
        if (typeStr != null) {
            try { type = TransactionType.valueOf(typeStr); }
            catch (IllegalArgumentException e) { return "Invalid type: " + typeStr; }
        }

        final Category        finalCategory = category;
        final TransactionType finalType     = type;

        return records.stream()
                .filter(r -> finalCategory == null || r.getCategory()        == finalCategory)
                .filter(r -> finalType     == null || r.getTransactionType() == finalType)
                .collect(Collectors.toList());
    }


    public SummaryInsight getSummary(String username) {
        List<Records> records = fetchAdminRecords(username);

        float totalIncome = (float) records.stream()
                .filter(r -> r.getTransactionType() == TransactionType.INCOME)
                .mapToDouble(Records::getAmount)
                .sum();

        float totalExpenses = (float) records.stream()
                .filter(r -> r.getTransactionType() == TransactionType.EXPENSE)
                .mapToDouble(Records::getAmount)
                .sum();

        return new SummaryInsight(totalIncome, totalExpenses, records.size());
    }

    public List<CategoryInsight> getInsightsByCategory(String username) {
        List<Records> records = fetchAdminRecords(username);

        Map<Category, List<Records>> grouped = records.stream()
                .collect(Collectors.groupingBy(Records::getCategory));

        return grouped.entrySet().stream()
                .map(entry -> {
                    List<Records> group = entry.getValue();

                    float income = (float) group.stream()
                            .filter(r -> r.getTransactionType() == TransactionType.INCOME)
                            .mapToDouble(Records::getAmount).sum();

                    float expenses = (float) group.stream()
                            .filter(r -> r.getTransactionType() == TransactionType.EXPENSE)
                            .mapToDouble(Records::getAmount).sum();

                    return new CategoryInsight(
                            entry.getKey().name(), income, expenses, group.size());
                })
                .sorted(Comparator.comparing(CategoryInsight::getCategory))
                .collect(Collectors.toList());
    }


    public List<MonthlyInsight> getInsightsByMonth(String username) {
        List<Records> records = fetchAdminRecords(username);

        // Key: "YYYY-MM" string derived from createdAt
        Map<String, List<Records>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> {
                    var date = r.getCreatedAt();
                    return String.format("%d-%02d", date.getYear(), date.getMonthValue());
                }));

        return grouped.entrySet().stream()
                .map(entry -> {
                    List<Records> group = entry.getValue();

                    float income = (float) group.stream()
                            .filter(r -> r.getTransactionType() == TransactionType.INCOME)
                            .mapToDouble(Records::getAmount).sum();

                    float expenses = (float) group.stream()
                            .filter(r -> r.getTransactionType() == TransactionType.EXPENSE)
                            .mapToDouble(Records::getAmount).sum();

                    return new MonthlyInsight(entry.getKey(), income, expenses);
                })
                .sorted(Comparator.comparing(MonthlyInsight::getYearMonth).reversed())
                .collect(Collectors.toList());
    }

    public List<Records> getTopExpenses(String username, int limit) {
        return fetchAdminRecords(username).stream()
                .filter(r -> r.getTransactionType() == TransactionType.EXPENSE)
                .sorted(Comparator.comparingDouble(Records::getAmount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
