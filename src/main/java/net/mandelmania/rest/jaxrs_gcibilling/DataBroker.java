package net.mandelmania.rest.jaxrs_gcibilling;

import net.mandelmania.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.Vector;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jaunt.JNode;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;

/**
 * Root resource (exposed at "databroker" path)
 */
@Path("databroker")
public class DataBroker {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
	
	static Vector<Contract> contracts = new Vector<Contract>();
	static Vector<Invoice> invoices = new Vector<Invoice>();
	
	//Populate our in-memory data:
	static {
		//Create three Contract objects:
		//First Contract object
		LineItemContract aLineItemContract = new LineItemContract("A Customer", "HB", 134, LocalDate.of(2014, Month.JULY, 1),
			TermPeriod.Yearly, 2, BillingPeriod.Monthly);
		LineItem lineItem1 = new LineItem("Site 1", "100 Mbps MPLS", 7422.0);
		LineItem lineItem2 = new LineItem("Site 2", "100 Mbps MPLS", 245.0);
		LineItem lineItem3 = new LineItem("Site 3", "100 Mbps MPLS", 3325.0);
		LineItem lineItem4 = new LineItem("Site 4", "100 Mbps MPLS", 3618.0);
		LineItem lineItem5 = new LineItem("Site 5", "100 Mbps MPLS", 4093.0);
		aLineItemContract.AddLineItem(lineItem1);
		aLineItemContract.AddLineItem(lineItem2);
		aLineItemContract.AddLineItem(lineItem3);
		aLineItemContract.AddLineItem(lineItem4);
		aLineItemContract.AddLineItem(lineItem5);
		contracts.add(aLineItemContract);
		
		//Second Contract object
		ServiceOrderContract aServiceOrderContract = new ServiceOrderContract("A Customer", "WT", 239,
				LocalDate.of(2015, Month.NOVEMBER, 24), TermPeriod.Yearly, 3, BillingPeriod.Monthly);
		Service service1 = new Service("Configuration and Testing of Equipment", 3485.0, false);
		aServiceOrderContract.AddService(service1);
		Service service2 = new Service("Remote Site VPN", 275.0, true);
		aServiceOrderContract.AddService(service2);
		contracts.add(aServiceOrderContract);
		
		//Third Contract object
		ServiceAgreementContract aServiceAgreementContract = new ServiceAgreementContract(
				"A Customer", 
				"SA",
				432,
				LocalDate.of(2015, Month.JUNE, 18),
				TermPeriod.Yearly,
				1,
				BillingPeriod.Hourly,
				125,
				"This SOW covers the discovery and documentation of ABC Health Corporation's " +
				"(ABCHC) wired and wireless local area networks (LANs).",
				"Document and evaluate IP Address allocation and usage\nDocument physical layout of all LANs",
				160
		);
		contracts.add(aServiceAgreementContract);
		
		//Create eight Invoice objects:
		Invoice invoice1 = new Invoice(239, 335783, LocalDate.of(2016, Month.JULY, 31), 275, false);
		Invoice invoice2 = new Invoice(134, 312236, LocalDate.of(2015, Month.DECEMBER, 30), 51155.0, false);
		Invoice invoice3 = new Invoice(239, 301389, LocalDate.of(2015, Month.NOVEMBER, 26), 275.0, true);
		Invoice invoice4 = new Invoice(134, 288193, LocalDate.of(2015, Month.NOVEMBER, 25), 51155.0, true);
		Invoice invoice5 = new Invoice(134, 279192, LocalDate.of(2015, Month.AUGUST, 27), 49390.0, true);
		Invoice invoice6 = new Invoice(134, 268381, LocalDate.of(2015, Month.JULY, 28), 51155.0, true);
		Invoice invoice7 = new Invoice(134, 257181, LocalDate.of(2015, Month.JUNE, 24), 51155.0, true);
		Invoice invoice8 = new Invoice(134, 248184, LocalDate.of(2015, Month.MAY, 21), 51155.0, true);
		invoices.add(invoice1);
		invoices.add(invoice2);
		invoices.add(invoice3);
		invoices.add(invoice4);
		invoices.add(invoice5);
		invoices.add(invoice6);
		invoices.add(invoice7);
		invoices.add(invoice8);
	}

	//With the @Json annotation (see first couple of subclasses) I was
	//trying to get automatic output of subclass-particular info.
	@GET
	@Path("/getsuperclasscontracts")
	@Produces(MediaType.APPLICATION_JSON)
	public Vector<Contract> getContractsSuperclassInfoInJSON() {
		return contracts;
	}
	 
	//Since I haven't gotten the @Json approach (using Jackson) to work yet,
	//I completed this alternative way to return in JSON the correct subclass-
	//particular information for the three subclasses of Contract.
	@GET
	@Path("/getcontractsandinvoices")
	@Produces(MediaType.APPLICATION_JSON)
	public String getContractsAndInvoicesInJSON() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		StringBuffer output = new StringBuffer();
		try {
			for (Contract contract : contracts) {
				if (contract instanceof LineItemContract)
					output.append(objectMapper.writeValueAsString((LineItemContract)contract));
				else if (contract instanceof ServiceAgreementContract)
					output.append(objectMapper.writeValueAsString((ServiceAgreementContract)contract));
				else if (contract instanceof ServiceOrderContract)
					output.append(objectMapper.writeValueAsString((ServiceOrderContract)contract));
			}
			for (Invoice invoice : invoices)
				output.append(objectMapper.writeValueAsString(invoice));
		} catch (JsonProcessingException e)
		{
			e.printStackTrace();
		}
		return output.toString();
	}
	
	@GET
	@Path("/calculatebadgenumber")
	public String getBadgeNumber() {
		int total = 0;
		for (Contract contract : contracts)
			if (contract.isExpiring)
				total++;
		for (Invoice invoice : invoices)
			if (invoice.isPastDue)
				total++;
		return Integer.toString(total);
	}
	
	@GET
	@Path("/search/{searchstring}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveSomething(@PathParam("searchstring") String searchFor) {
		
		String searchThrough = getContractsAndInvoicesInJSON();

		/*
		 * This JSON iterator is probably the best bet--maybe scratch the two methods, below.
		 * From: http://stackoverflow.com/questions/9151619/java-iterate-over-jsonobject
		jObject = new JSONObject(contents.trim());
		Iterator<?> keys = jObject.keys();
		while( keys.hasNext() ) {
		    String key = (String)keys.next();
		    if ( jObject.get(key) instanceof JSONObject ) {

		    }
		}
		*/
		
		/*
		 * This isn't working.
		Map<String,String> resultMap = new HashMap<String,String>();  //or can this be set to null?
		ObjectMapper mapperObject = new ObjectMapper();
		try
		{
			//From http://www.java2novice.com/java-json/jackson/json-to-map-object/
			resultMap = mapperObject.readValue(searchThrough, new TypeReference<HashMap<String,String>>(){});
		} catch (JsonParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuffer searchResultsStringBuffer = new StringBuffer();
		if (resultMap.containsKey(searchFor))
			searchResultsStringBuffer.append("[" + searchFor + "," + resultMap.get(searchFor) + "]\n");
	    Set<String> keys = new HashSet<String>();
	    for (Entry<String, String> entry : resultMap.entrySet()) {
	        if (searchFor.equals(entry.getValue())) {
	            keys.add(entry.getKey());
	        }
	    }
	    for (String key : keys) {
	    	searchResultsStringBuffer.append("[" + key + "," + resultMap.get(key) + "]\n");
		}
	    String searchResultsString = searchResultsStringBuffer.toString();
	    */
	    
		/*
		 * These were attempts to use Jaunt (which provides JNode and UserAgent) to query
		 * the JSON.  This seems to only work when querying keys, not values too.
		 */
		JNode searchResults = null;
		String searchResultsString = null;
		try {
			UserAgent userAgent = new UserAgent();
			userAgent.openJSON(searchThrough);
			searchResults = userAgent.json.findEvery(searchFor);
			searchResultsString = searchResults.toString();
			//This was attempt at querying values.
			//searchResults = userAgent.json.findEvery("?:{?:"+searchFor+"}");
			//searchResultsString += searchResults.toString();
		} catch(JauntException e) {
			System.err.println(e);
		}
		
		return Response
				.status(200)
				.entity(searchResultsString)
				.build();
	}	
	
	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Track getTrackInJSON() {
		Track track = new Track();
		track.setTitle("Enter Sandman");
		track.setSinger("Metallica");
		return track;
	}

	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTrackInJSON(Track track) {
		String result = "Track saved : " + track;
		return Response.status(201).entity(result).build();
	}	
    
	/*
	 * To retrieve with specified parameters, and return a particular Response.    
	@GET
	@Path("retrieve/{uuid}")
	public Response retrieveSomething(@PathParam("uuid") String uuid) {
	    if(uuid == null || uuid.trim().length() == 0) {
	        return Response.serverError().entity("UUID cannot be blank").build();
	    }
	    Entity entity = service.getById(uuid);
	    if(entity == null) {
	        return Response.status(Response.Status.NOT_FOUND).entity("Entity not found for UUID: " + uuid).build();
	    }
	    String json = //convert entity to json
	    return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}
	 */
    
}
