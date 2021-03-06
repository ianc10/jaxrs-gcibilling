package net.mandelmania;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

//@JsonRootName("Contracts")
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    //@JsonSubTypes.Type(value = LineItemContract.class, name = "LineItemContract"),
    //@JsonSubTypes.Type(value = ServiceAgreementContract.class, name = "ServiceAgreementContract"),
    //@JsonSubTypes.Type(value = ServiceOrderContract.class, name = "ServiceOrderContract")})	
	@Type(value = LineItemContract.class),
	@Type(value = ServiceAgreementContract.class),
	@Type(value = ServiceOrderContract.class)
})
public abstract class Contract
{
	public String customerCode;
	public String contractIDPrefix;
	public int contractID;
	public LocalDate startDate;
	public TermPeriod termPeriod;
	public int numberOfTerms;
	public BillingPeriod billingPeriod;
	//Automatically computed:
	public LocalDate endDate;
	public boolean isExpiring = false;
	
	public Contract (String aCustomerCode, String aContractIDPrefix, int aContractID, LocalDate aStartDate,
			TermPeriod aTermPeriod, int aNumberOfTerms, BillingPeriod aBillingPeriod) {
		customerCode = aCustomerCode;
		contractIDPrefix = aContractIDPrefix;
		contractID = aContractID;
		startDate = aStartDate;
		termPeriod = aTermPeriod;
		numberOfTerms = aNumberOfTerms;
		billingPeriod = aBillingPeriod;
		//Calculate the automatic fields:
		EndDate();
		IsExpiring();
	}
	
	public LocalDate EndDate() {
		//Make the default term period Yearly
		LocalDate aDate = startDate.plusYears(numberOfTerms).minusDays(1);
		if (termPeriod == TermPeriod.Monthly)
			aDate = startDate.plusMonths(numberOfTerms).minusDays(1);
		endDate = aDate;
		return endDate;
	}
	
	public boolean IsExpiring() {
		long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), EndDate());
		isExpiring = (daysBetween <= 90);
		return isExpiring;
	}
	
}
