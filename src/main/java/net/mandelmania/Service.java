package net.mandelmania;

public class Service
{
	public String description;
	public double price;
	public boolean isRecurring;
	
	public Service(String aDescription, double aPrice, boolean aIsRecurring) {
		description = aDescription;
		price = aPrice;
		isRecurring = aIsRecurring; 
	}
}
