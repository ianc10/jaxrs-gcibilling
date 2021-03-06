package net.mandelmania;

import java.time.LocalDate;
import java.util.Vector;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class ServiceOrderContract extends Contract
{
	//@JsonProperty enables ObjectMapper to output subclass-specific properties (but isn't necessary
	//when using the @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
	//annotation above for the superclass and each subclass):
	public double billingPeriodTotal = 0.0;

	//@JsonProperty("services")
	public Vector<Service> services = new Vector<Service>();
	
	//@JsonCreator and @JsonProperty:
	//attempt at automatic JSON subclass info output (see DataBroker.getContractsSuperclassInfoInJSON())
	//They don't seem to have any effect here:
	//@JsonCreator
	public ServiceOrderContract(
			/*@JsonProperty("customerCode")*/ String aCustomerCode,
			/*@JsonProperty("contractIDPrefix")*/ String aContractIDPrefix,
			/*@JsonProperty("contractID")*/ int aContractID,
			/*@JsonProperty("startDate")*/ LocalDate aStartDate,
			/*@JsonProperty("termPeriod")*/ TermPeriod aTermPeriod,
			/*@JsonProperty("numberOfTerms")*/ int aNumberOfTerms,
			/*@JsonProperty("billingPeriod")*/ BillingPeriod aBillingPeriod)
	{
		super(aCustomerCode, aContractIDPrefix, aContractID, aStartDate, aTermPeriod, aNumberOfTerms, aBillingPeriod);
		BillingPeriodTotal();
	}
	
	public void AddService(Service aService) {
		services.add(aService);
		BillingPeriodTotal();
	}
	
	public double BillingPeriodTotal() {
		billingPeriodTotal = 0.0;
		for (Service service : services) {
			if (service.isRecurring)
				billingPeriodTotal += service.price;
			//Could add some logic to detect if a one-time billing item is within the current billing period
			//and add possible one-time prices to the total here.
		}
		return billingPeriodTotal;		
	}

}
