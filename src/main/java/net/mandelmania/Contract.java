package net.mandelmania;

import java.time.LocalDate;
//import java.time.temporal.ChronoUnit.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonRootName("Contracts")
//Use JsonTypeInfo.Id.NAME instead of .CLASS to get the class name without the package prefix.
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@Type(value = LineItemContract.class),
	@Type(value = ServiceAgreementContract.class),
	@Type(value = ServiceOrderContract.class)
	//To override the Id.NAME values:
	//@Type(value = LineItemContract.class, name = "LineItemContract"),
	//@Type(value = ServiceAgreementContract.class, name = "ServiceAgreementContract"),
	//@Type(value = ServiceOrderContract.class, name = "ServiceOrderContract")
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
