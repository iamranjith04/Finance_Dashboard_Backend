package backend.dto;

public class SummaryInsight {

    private float totalIncome;
    private float totalExpenses;
    private float netBalance;
    private long recordCount;

    public SummaryInsight(float totalIncome, float totalExpenses, long recordCount) {
        this.totalIncome   = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netBalance    = totalIncome - totalExpenses;
        this.recordCount   = recordCount;
    }

    public float getTotalIncome()   { return totalIncome; }
    public float getTotalExpenses() { return totalExpenses; }
    public float getNetBalance()    { return netBalance; }
    public long  getRecordCount()   { return recordCount; }
}
