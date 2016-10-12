package CommonLib;

/**
 * Created by Cuongph on 16/06/2016.
 */
public class BI_ReportCompare
{
    public String sDate;
    public BI_ReportDaily Tagert;
    public BI_ReportDaily Working;
    public BI_ReportCompare()
    {
        Tagert = new BI_ReportDaily();
        Working = new BI_ReportDaily();
    }
}
