package net.mandelmania;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

//@JsonRootName("Invoices")
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Invoice
{
	public int contractID;
	public int invoiceID;
	public LocalDate dueDate;
	public double amount;
	public boolean isPaid;
	//Automatically calculated:
	public boolean isPastDue = false;

	public Invoice(int aContractID, int anInvoiceID, LocalDate aDueDate, double anAmount, boolean anIsPaid) {
		contractID = aContractID;
		invoiceID = anInvoiceID;
		dueDate = aDueDate;
		amount = anAmount;
		isPaid = anIsPaid;
		IsPastDue();
	}
	
	public boolean IsPastDue() {
		isPastDue = !isPaid && LocalDate.now().isAfter(dueDate);
		return isPastDue;
	}
	
}
