package backend.dto;

public class CategoryInsight {

    private String category;
    private float  totalIncome;
    private float  totalExpenses;
    private long   recordCount;

    public CategoryInsight(String category, float totalIncome, float totalExpenses, long recordCount) {
        this.category      = category;
        this.totalIncome   = totalIncome;
        this.totalExpenses = totalExpenses;
        this.recordCount   = recordCount;
    }

    public String getCategory()      { return category; }
    public float  getTotalIncome()   { return totalIncome; }
    public float  getTotalExpenses() { return totalExpenses; }
    public long   getRecordCount()   { return recordCount; }
}
