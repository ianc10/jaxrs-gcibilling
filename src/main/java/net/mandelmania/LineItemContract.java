package net.mandelmania;

import java.time.LocalDate;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class LineItemContract extends Contract
{
	//@JsonProperty enables ObjectMapper to output subclass-specific properties (but isn't necessary
	//when using the @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
	//annotation above for the superclass and each subclass):
	//@JsonProperty("billingPeriodTotal")
	public double billingPeriodTotal = 0.0;
	
	//@JsonProperty("numberOfServices")
	public int numberOfServices = 0;
	
	//If this Vector is made private, forcing use of the accessor method AddLineItem(LineItem), below,
	//it isn't output with the rest of the JSON in the data traversal.  Might be good to investigate
	//some other way to make this inaccessible to outside classes.  (Perhaps an event listener, listening
	//to add events.)  Currently, items can be added to the Vector directly, bypassing the required
	//update events BillingPeriodTotal() and NumberOfServices().
	//@JsonProperty("lineItems")
	public Vector<LineItem> lineItems = new Vector<LineItem>();
	
	public LineItemContract(String aCustomerCode, String aContractIDPrefix, int aContractID, LocalDate aStartDate,
			TermPeriod aTermPeriod, int aNumberOfTerms, BillingPeriod aBillingPeriod)
	{
		super(aCustomerCode, aContractIDPrefix, aContractID, aStartDate, aTermPeriod, aNumberOfTerms, aBillingPeriod);
	}

	public void AddLineItem(LineItem aLineItem) {
		lineItems.add(aLineItem);
		BillingPeriodTotal();
		NumberOfServices();
	}
	
	public int NumberOfServices() {
		numberOfServices = lineItems.size();
		return numberOfServices;
	}
	
	public double BillingPeriodTotal() {
		billingPeriodTotal = 0.0;
		for (LineItem item : lineItems) {
			billingPeriodTotal += item.monthlyPrice;
		}
		return billingPeriodTotal;
	}
	
}
