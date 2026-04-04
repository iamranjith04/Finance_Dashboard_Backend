package backend.dto;

import backend.database.Records;

import java.util.List;

public class DashboardInsight {

    private float totalIncome;
    private float totalExpenses;
    private float netBalance;
    private List<Records> recentActivity;

    public DashboardInsight(float totalIncome, float totalExpenses, float netBalance, List<Records> recentActivity) {
        this.totalIncome    = totalIncome;
        this.totalExpenses  = totalExpenses;
        this.netBalance     = netBalance;
        this.recentActivity = recentActivity;
    }

    public float getTotalIncome()                    { return totalIncome; }
    public float getTotalExpenses()                  { return totalExpenses; }
    public float getNetBalance()                     { return netBalance; }
    public List<Records> getRecentActivity()         { return recentActivity; }
}
