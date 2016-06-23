package net.mandelmania;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class ServiceAgreementContract extends Contract
{
	public double pricePerBillingPeriod;
	public String projectSummary;
	public String scopeOfWork;
	//This field is optional; if in use, set it to something other than 0.
	public int upToHours = 0;
	
	public ServiceAgreementContract(String aCustomerCode, String aContractIDPrefix, int aContractID,
			LocalDate aStartDate, TermPeriod aTermPeriod, int aNumberOfTerms, BillingPeriod aBillingPeriod)
	{
		super(aCustomerCode, aContractIDPrefix, aContractID, aStartDate, aTermPeriod, aNumberOfTerms, aBillingPeriod);
	}

	//@JSonCreator and @JsonProperty:
	//attempt at automatic JSON subclass info output (see DataBroker.getContractsSuperclassInfoInJSON())
	@JsonCreator
	public ServiceAgreementContract(
			@JsonProperty("customerCode") String aCustomerCode,
			@JsonProperty("contractIDPrefix") String aContractIDPrefix,
			@JsonProperty("contractID") int aContractID,
			@JsonProperty("startDate") LocalDate aStartDate,
			@JsonProperty("termPeriod") TermPeriod aTermPeriod,
			@JsonProperty("numberOfTerms") int aNumberOfTerms,
			@JsonProperty("billingPeriod") BillingPeriod aBillingPeriod,
			@JsonProperty("pricePerBillingPeriod") double aPricePerBillingPeriod,
			@JsonProperty("projectSummary") String aProjectSummary,
			@JsonProperty("scopeOfWork") String aScopeOfWork
	) {
		this(aCustomerCode, aContractIDPrefix, aContractID, aStartDate, aTermPeriod, aNumberOfTerms, aBillingPeriod);
		pricePerBillingPeriod = aPricePerBillingPeriod;
		projectSummary = aProjectSummary;
		scopeOfWork = aScopeOfWork;
	}
	
	@JsonCreator
	public ServiceAgreementContract(
			@JsonProperty("customerCode") String aCustomerCode,
			@JsonProperty("contractIDPrefix") String aContractIDPrefix,
			@JsonProperty("contractID") int aContractID,
			@JsonProperty("startDate") LocalDate aStartDate,
			@JsonProperty("termPeriod") TermPeriod aTermPeriod,
			@JsonProperty("numberOfTerms") int aNumberOfTerms,
			@JsonProperty("billingPeriod") BillingPeriod aBillingPeriod,
			@JsonProperty("pricePerBillingPeriod") double aPricePerBillingPeriod,
			@JsonProperty("projectSummary") String aProjectSummary,
			@JsonProperty("scopeOfWork") String aScopeOfWork,
			@JsonProperty("upToHours") int anUpToHours
		) {
		this(aCustomerCode, aContractIDPrefix, aContractID, aStartDate, aTermPeriod, aNumberOfTerms, aBillingPeriod,
				 aPricePerBillingPeriod, aProjectSummary, aScopeOfWork);
		upToHours = anUpToHours;
	}	
	
}
