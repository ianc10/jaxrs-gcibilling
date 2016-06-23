package net.mandelmania;

public class LineItem
{
	public String siteName;
	public String serviceDescription;
	public double monthlyPrice;
	
	public LineItem(String aSiteName, String aServiceDescription, double aMonthlyPrice) {
		siteName = aSiteName;
		serviceDescription = aServiceDescription;
		monthlyPrice = aMonthlyPrice;
	}
	
}
