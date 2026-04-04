package backend.dto;

public class MonthlyInsight {

    private String yearMonth;
    private float  totalIncome;
    private float  totalExpenses;
    private float  netBalance;

    public MonthlyInsight(String yearMonth, float totalIncome, float totalExpenses) {
        this.yearMonth     = yearMonth;
        this.totalIncome   = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netBalance    = totalIncome - totalExpenses;
    }

    public String getYearMonth()     { return yearMonth; }
    public float  getTotalIncome()   { return totalIncome; }
    public float  getTotalExpenses() { return totalExpenses; }
    public float  getNetBalance()    { return netBalance; }
}
