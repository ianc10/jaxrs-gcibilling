package net.mandelmania.rest.jaxrs_gcibilling;

import net.mandelmania.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;

import com.couchbase.client.java.*;
import com.couchbase.client.java.document.*;
import com.couchbase.client.java.document.json.*;
import com.couchbase.client.java.query.*;

//The jaunt libraries are not used:
//import com.jaunt.JNode;
//import com.jaunt.JauntException;
//import com.jaunt.UserAgent;

//GitHub locations for jackson-databind and jackson-core
//https://fasterxml.github.io/jackson-databind/
//https://fasterxml.github.io/jackson-core/

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
	
	//Get Invoices from Couchbase
	@GET
	@Path("/couchbasegetinvoices")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInvoicesFromCouchbase() {
        // Initialize the Connection
        Cluster cluster = CouchbaseCluster.create("localhost");
        Bucket bucket = cluster.openBucket("default");

        N1qlQueryResult result = bucket.query(
        		N1qlQuery.simple("SELECT default.* FROM default WHERE type='net.mandelmania.Invoice'"));
        
        String resultString = result.allRows().toString();
        
        //These should be reused in a normal application, across threads, not closed after every use:
        bucket.close();
        cluster.disconnect();
        return resultString;			
	}
	
	//Get Contracts from Couchbase
	@GET
	@Path("/couchbasegetcontracts")
	@Produces(MediaType.APPLICATION_JSON)
	public String getContractsFromCouchbase() {
        // Initialize the Connection
        Cluster cluster = CouchbaseCluster.create("localhost");
        Bucket bucket = cluster.openBucket("default");

        N1qlQueryResult result = bucket.query(
        		N1qlQuery.simple("SELECT default.* FROM default WHERE type='net.mandelmania.LineItemContract' OR type='net.mandelmania.ServiceOrderContract' OR type='net.mandelmania.ServiceAgreementContract'"));
        
        /*
        // Print each found Row
        for (N1qlQueryRow row : result) {
            // Prints {"name":"Arthur"}
            System.out.println(row);
            resultString = row.toString();
        }*/
        
        String resultString = result.allRows().toString();
        
        //These should be reused in a normal application, across threads, not closed after every use:
        bucket.close();
        cluster.disconnect();
        return resultString;		
	}
	
	//Connect to Couchbase database
	@GET
	@Path("/couchbase")
	@Produces(MediaType.APPLICATION_JSON)
	public String connectToCouchbase() {
		String resultString = null;
		
        // Initialize the Connection
        Cluster cluster = CouchbaseCluster.create("localhost");
        Bucket bucket = cluster.openBucket("default");

        // Create a JSON Document
        JsonObject arthur = JsonObject.create()
            .put("name", "Arthur")
            .put("email", "kingarthur@couchbase.com")
            .put("interests", JsonArray.from("Holy Grail", "African Swallows"));

        // Store the Document
        bucket.upsert(JsonDocument.create("u:king_arthur", arthur));

        // Load the Document and print it
        // Prints Content and Metadata of the stored Document
        //System.out.println(bucket.get("u:king_arthur"));
        resultString = bucket.get("u:king_arthur").toString();

        /*
        // Create a N1QL Primary Index (but ignore if it exists)
        bucket.bucketManager().createN1qlPrimaryIndex(true, false);

        // Perform a N1QL Query
        N1qlQueryResult result = bucket.query(
            N1qlQuery.parameterized("SELECT name FROM default WHERE $1 IN interests",
            JsonArray.from("African Swallows"))
        );

        // Print each found Row
        for (N1qlQueryRow row : result) {
            // Prints {"name":"Arthur"}
            System.out.println(row);
            resultString = row.toString();
        }*/
        
        //These should be reused in a normal application, across threads, not closed after every use:
        bucket.close();
        cluster.disconnect();
        return resultString;
	}

	//Write out JSON documents to Couchbase
	@GET
	@Path("/couchbasewrite")
	@Produces(MediaType.TEXT_PLAIN)
	public String writeToCouchbase() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		//This will cause the node to be a subnode of the root node Contracts (and Invoices, later):
		//objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,  true);
        Cluster cluster = CouchbaseCluster.create("localhost");
        Bucket bucket = cluster.openBucket("default");
		try {
			for (Contract contract : contracts) {
				if (contract instanceof LineItemContract) {
					bucket.upsert(RawJsonDocument.create(Integer.toString(contract.contractID), objectMapper.writeValueAsString((LineItemContract)contract)));
				} else if (contract instanceof ServiceAgreementContract) {
					bucket.upsert(RawJsonDocument.create(Integer.toString(contract.contractID), objectMapper.writeValueAsString((ServiceAgreementContract)contract)));
				} else if (contract instanceof ServiceOrderContract)	{
					bucket.upsert(RawJsonDocument.create(Integer.toString(contract.contractID), objectMapper.writeValueAsString((ServiceOrderContract)contract)));
				}
			}
			for (Invoice invoice : invoices) {
				bucket.upsert(RawJsonDocument.create(Integer.toString(invoice.invoiceID),objectMapper.writeValueAsString(invoice)));
			}
		} catch (JsonProcessingException e)	{
			e.printStackTrace();
			return e.toString();
		} finally {
			bucket.close();
			cluster.disconnect();
			//System.out.println("*finally block complete*");
		}
		return "OK";		
	}
	
	//This doesn't produce subclass-specific properties:
	@GET
	@Path("/getsuperclasscontracts")
	@Produces(MediaType.APPLICATION_JSON)
	public Vector<Contract> getContractsSuperclassInfoInJSON() {
		return contracts;
	}

	//This takes advantage of the @JsonProperty annotations in the subclasses of the
	//Contract class to automatically deserialize the appropriate subclass's properties.
	@GET
	@Path("/getcontracts")
	@Produces(MediaType.APPLICATION_JSON)
	public String getContractsInJSON() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		String output = null;
		try
		{
			//output = objectMapper.writeValueAsString(contracts);
			//Include the type information (for some reason, it does get included in the
			//getContractsAndInvoicesInJSON() method):
			JavaType vectorJavaType = objectMapper.getTypeFactory().constructCollectionType(Vector.class, Contract.class);
			//output = objectMapper.writerWithType(vectorJavaType).writeValueAsString(contracts);
			//writerWithType() is deprecated, so use writerFor() instead:
			output = objectMapper.writerFor(vectorJavaType).writeValueAsString(contracts);
		} catch (JsonProcessingException e)
		{
			e.printStackTrace();
		}
		return output;
	}
	
	//This takes advantage of the @JsonProperty annotations in the subclasses of the
	//Contract class to automatically deserialize the appropriate subclass's properties.
	@GET
	@Path("/getinvoices")
	@Produces(MediaType.APPLICATION_JSON)
	public String getInvoicesInJSON() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		String output = null;
		try
		{
			//output = objectMapper.writeValueAsString(contracts);
			//Include the type information (for some reason, it does get included in the
			//getContractsAndInvoicesInJSON() method):
			JavaType vectorJavaType = objectMapper.getTypeFactory().constructCollectionType(Vector.class, Invoice.class);
			//output = objectMapper.writerWithType(vectorJavaType).writeValueAsString(invoices);
			//writerWithType() is deprecated, so use writerFor() instead:			
			output = objectMapper.writerFor(vectorJavaType).writeValueAsString(invoices);
		} catch (JsonProcessingException e)
		{
			e.printStackTrace();
		}
		return output;
	}	
	
	//This is an alternative to using the @JsonProperty annotations that will (manually)
	//detect each particular subclass and output accordingly.
	@GET
	@Path("/getcontractsandinvoices")
	@Produces(MediaType.APPLICATION_JSON)
	public String getContractsAndInvoicesInJSON() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		//This will cause the node to be a subnode of the root node Contracts (and Invoices, later):
		//objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE,  true);
		StringBuffer output = new StringBuffer();
		output.append("[");
		try {
			for (Contract contract : contracts) {
				if (output.length() > 1)
					output.append(",");
				//String rootName = Contract.class.getAnnotation(JsonRootName.class).value();
				if (contract instanceof LineItemContract)
					output.append(objectMapper.writeValueAsString((LineItemContract)contract));
					//output.append(objectMapper.writer().withRootName(rootName).writeValueAsString((LineItemContract)contract));
				else if (contract instanceof ServiceAgreementContract)
					output.append(objectMapper.writeValueAsString((ServiceAgreementContract)contract));
					//output.append(objectMapper.writer().withRootName(rootName).writeValueAsString((ServiceAgreementContract)contract));
				else if (contract instanceof ServiceOrderContract)
					output.append(objectMapper.writeValueAsString((ServiceOrderContract)contract));
					//output.append(objectMapper.writer().withRootName(rootName).writeValueAsString((ServiceOrderContract)contract));
			}
			for (Invoice invoice : invoices) {
				output.append(",");
				output.append(objectMapper.writeValueAsString(invoice));
			}
			output.append("]");
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
		
		searchFor = searchFor.toLowerCase();
		//Search all lower case version of the JSON object, but construct the output strings
		//from the original mixed-case text.
		String jsonToSearch = getContractsAndInvoicesInJSON();
		//String jsonToSearchLowerCase = getContractsAndInvoicesInJSON().toLowerCase();
		String searchResultsString = "";

		//Read the array of JSON objects and cycle through every object -- THIS WORKS, YAY!!!
		StringBuffer searchResultsStringBuffer = new StringBuffer();
		final ObjectMapper mapper = new ObjectMapper();
		JsonNode rootArray = null; 
		try
		{
			rootArray = mapper.readTree(jsonToSearch);
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		for (JsonNode root: rootArray) {
			//String jsonNodeText = root.toString();
			final MapType type = mapper.getTypeFactory().constructMapType(
				    Map.class, String.class, Object.class);
			ObjectMapper innerMapper = new ObjectMapper();
			//Use type instead of Map.class:
			Map<String, Object> data = innerMapper.convertValue(root, type);
			for (Map.Entry<String, Object> entry : data.entrySet()) {
			    //String key = entry.getKey();
			    //Object value = entry.getValue();
				//For values, we are dealing with these classes:  String, Integer, Double, Boolean, ArrayList, LinkedHashMap
				//searchResultsStringBuffer.append("   " + entry.getValue() + " " + entry.getValue().getClass().getName() + "!!!!!");
				//Just use Object's toString()!  Simplifies things.  Could also use Java 8's new Stream API
				//to query objects similarly to C# and .NET's LINQ.
				if (entry.getKey().toString().toLowerCase().contains(searchFor.toLowerCase()) || 
						entry.getValue().toString().toLowerCase().contains(searchFor.toLowerCase())) {
				    if (searchResultsStringBuffer.length() > 1)
				    	searchResultsStringBuffer.append(",");
				    //We can get the values for the JSON object's corresponding keys with root.path("keyname")
				    searchResultsStringBuffer.append("[ID: " + root.path("contractIDPrefix") + root.path("contractID") +
				    		", Matching Key: " + entry.getKey() + ", Value: " + entry.getValue().toString() + "]<BR><BR>");
				}
			}
			//Could have an input parameter specifying whether to return the whole node text, then
			//if the flag is set, do something like the following:
			//searchResultsStringBuffer.append(jsonNodeText + "<BR><BR>");
		}
		searchResultsString = searchResultsStringBuffer.toString();
		
		/*
		//Read the array of JSON objects and cycle through every object -- THIS WORKS, YAY!!!
		StringBuffer searchResultsStringBuffer = new StringBuffer();
		final ObjectMapper mapper = new ObjectMapper();
		JsonNode rootArray = null; 
		try
		{
			rootArray = mapper.readTree(jsonToSearch);
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		for (JsonNode root: rootArray) {
			String jsonNodeText = root.toString();
			final MapType type = mapper.getTypeFactory().constructMapType(
				    Map.class, String.class, Object.class);
			ObjectMapper innerMapper = new ObjectMapper();
			//Use type instead of Map.class:
			Map<String, Object> data = innerMapper.convertValue(root, type);
			for (Map.Entry<String, Object> entry : data.entrySet()) {
			    //String key = entry.getKey();
			    //Object value = entry.getValue();
				//if (entry.getKey().toLowerCase().contains(searchFor.toLowerCase()) || 
				//		((String)entry.getValue()).toLowerCase().contains(searchFor.toLowerCase())) {
				    if (searchResultsStringBuffer.length() > 1)
				    	searchResultsStringBuffer.append(",");
				    searchResultsStringBuffer.append("[Key: " + entry.getKey() + "\tValue: " + entry.getValue() + "]");
				//}
			}
			searchResultsStringBuffer.append(jsonNodeText + "**************<BR><BR>");
		}
		searchResultsString = searchResultsStringBuffer.toString();
		*/
		
		/*
		 * This will convert the first JSON object to key/value pairs
		 */
		/*
		final MapType type = mapper.getTypeFactory().constructMapType(
		    Map.class, String.class, Object.class);
		Map<String, Object> data = null;
		try
		{
			data = mapper.readValue(jsonToSearch, type);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		for (Map.Entry<String, Object> entry : data.entrySet()) {
		    //String key = entry.getKey();
		    //Object value = entry.getValue();
			//if (entry.getKey().toLowerCase().contains(searchFor.toLowerCase()) || 
			//		((String)entry.getValue()).toLowerCase().contains(searchFor.toLowerCase())) {
			    if (searchResultsStringBuffer.length() > 1)
			    	searchResultsStringBuffer.append(",");
			    searchResultsStringBuffer.append("[Key: " + entry.getKey() + "\tValue: " + entry.getValue() + "]");
			//}
		}
		*/
		
	    //searchResultsString = searchResultsStringBuffer.toString();
		/*
		try {
			//The following text parsing algorithm will find key/value pairs that contain
			 * text matching the search string.  It is not entirely finished, as it doesn't
			 * properly handle values that don't have double quotes surrounding them.
			 * 
			int beginIndex = jsonToSearchLowerCase.indexOf(searchFor);
			while (beginIndex != -1) {
				String newString = "";
				int newStartIndex = 0;
					int traverseIndex = beginIndex;
					while (jsonToSearchLowerCase.charAt(traverseIndex) != '"')
						traverseIndex--;
					int endIndex = beginIndex;
					beginIndex = traverseIndex;
					traverseIndex = endIndex;
					while (jsonToSearchLowerCase.charAt(traverseIndex) != '"')
						traverseIndex++;
					endIndex = traverseIndex + 1;
					newString = jsonToSearch.substring(beginIndex, endIndex);
					if (jsonToSearch.charAt(endIndex) == ' ') {
						//string is a key, so get the corresponding value
						beginIndex = endIndex + 3;
						traverseIndex = beginIndex + 1;
						while (jsonToSearchLowerCase.charAt(traverseIndex) != '"')
							traverseIndex++;
						endIndex = traverseIndex + 1;
						newString = newString + " : " + jsonToSearch.substring(beginIndex, endIndex);
						newStartIndex = endIndex;
					} else {
						//that should instead be a comma, so:
						//string is a value, so get the corresponding key
						newStartIndex = endIndex;
						endIndex = beginIndex - 4;
						traverseIndex = endIndex - 1;
						while (jsonToSearchLowerCase.charAt(traverseIndex) != '"')
							traverseIndex--;
						beginIndex = traverseIndex - 1;
						newString = jsonToSearch.substring(beginIndex, endIndex) + " : " + newString;
					}
				searchResultsString = searchResultsString + newString + "<BR>";
				beginIndex = jsonToSearchLowerCase.indexOf(searchFor, newStartIndex);
			}
		} catch (StringIndexOutOfBoundsException ex) {
			//Do nothing at this point but break out of the loop
		}
		
		
		 /* This was an attempt to use Jaunt (which provides JNode and UserAgent) to query
		  * the JSON.  This seems to only work when querying keys, not values too.
		  *
		JNode searchResults = null;
		try {
			UserAgent userAgent = new UserAgent();
			userAgent.openJSON(jsonToSearch);
			searchResults = userAgent.json.findEvery(searchFor);
			searchResultsString = searchResults.toString();
			//This was attempt at querying values.
			//searchResults = userAgent.json.findEvery("?:{?:"+searchFor+"}");
			//searchResultsString += searchResults.toString();
		} catch(JauntException e) {
			System.err.println(e);
		}
		*/		
		
		
		/*  This is a properly formed hard-coded array of JSON objects.
		 *  JSON can be verified for validity at jsonlint.com. 
		 * 
		String jsonToSearch = 
				"[{" + 
					"\"type\": \"net.mandelmania.LineItemContract\"," + 
					"\"customerCode\": \"A Customer\"," +
					"\"contractIDPrefix\": \"HB\"," +
					"\"contractID\": 134," +
					"\"startDate\": {" +
						"\"year\": 2014," +
						"\"month\": \"JULY\"," +
						"\"dayOfYear\": 182," +
						"\"leapYear\": false," +
						"\"monthValue\": 7," +
						"\"chronology\": {" +
							"\"calendarType\": \"iso8601\"," +
							"\"id\": \"ISO\"" +
						"}," +
						"\"dayOfMonth\": 1," +
						"\"dayOfWeek\": \"TUESDAY\"," +
						"\"era\": \"CE\"" +
					"}," +
					"\"termPeriod\": \"Yearly\"," +
					"\"numberOfTerms\": 2," +
					"\"billingPeriod\": \"Monthly\"," +
					"\"endDate\": {" +
						"\"year\": 2016," +
						"\"month\": \"JUNE\"," +
						"\"dayOfYear\": 182," +
						"\"leapYear\": true," +
						"\"monthValue\": 6," +
						"\"chronology\": {" +
							"\"calendarType\": \"iso8601\"," +
							"\"id\": \"ISO\"" +
						"}," +
						"\"dayOfMonth\": 30," +
						"\"dayOfWeek\": \"THURSDAY\"," +
						"\"era\": \"CE\"" +
					"}," +
					"\"isExpiring\": true," +
					"\"billingPeriodTotal\": 18703.0," +
					"\"numberOfServices\": 5," +
					"\"lineItems\": [{" +
						"\"siteName\": \"Site 1\"," +
						"\"serviceDescription\": \"100 Mbps MPLS\"," +
						"\"monthlyPrice\": 7422.0" +
					"}, {" +
						"\"siteName\": \"Site 2\"," +
						"\"serviceDescription\": \"100 Mbps MPLS\"," +
						"\"monthlyPrice\": 245.0" +
					"}, {" +
						"\"siteName\": \"Site 3\"," +
						"\"serviceDescription\": \"100 Mbps MPLS\"," +
						"\"monthlyPrice\": 3325.0" +
					"}, {" +
						"\"siteName\": \"Site 4\"," +
						"\"serviceDescription\": \"100 Mbps MPLS\"," +
						"\"monthlyPrice\": 3618.0" +
					"}, {" +
						"\"siteName\": \"Site 5\"," +
						"\"serviceDescription\": \"100 Mbps MPLS\"," +
						"\"monthlyPrice\": 4093.0" +
					"}]" +
				"}, {" +
					"\"type\": \"net.mandelmania.ServiceOrderContract\"," +
					"\"customerCode\": \"A Customer\"," +
					"\"contractIDPrefix\": \"WT\"," +
					"\"contractID\": 239," +
					"\"startDate\": {" +
						"\"year\": 2015," +
						"\"month\": \"NOVEMBER\"," +
						"\"dayOfYear\": 328," +
						"\"leapYear\": false," +
						"\"monthValue\": 11," +
						"\"chronology\": {" +
							"\"calendarType\": \"iso8601\"," +
							"\"id\": \"ISO\"" +
						"}," +
						"\"dayOfMonth\": 24," +
						"\"dayOfWeek\": \"TUESDAY\"," +
						"\"era\": \"CE\"" +
					"}," +
					"\"termPeriod\": \"Yearly\"," +
					"\"numberOfTerms\": 3," +
					"\"billingPeriod\": \"Monthly\"," +
					"\"endDate\": {" +
						"\"year\": 2018," +
						"\"month\": \"NOVEMBER\"," +
						"\"dayOfYear\": 327," +
						"\"leapYear\": false," +
						"\"monthValue\": 11," +
						"\"chronology\": {" +
							"\"calendarType\": \"iso8601\"," +
							"\"id\": \"ISO\"" +
						"}," +
						"\"dayOfMonth\": 23," +
						"\"dayOfWeek\": \"FRIDAY\"," +
						"\"era\": \"CE\"" +
					"}," +
					"\"isExpiring\": false," +
					"\"billingPeriodTotal\": 275.0," +
					"\"services\": [{" +
						"\"description\": \"Configuration and Testing of Equipment\"," +
						"\"price\": 3485.0," +
						"\"isRecurring\": false" +
					"}, {" +
						"\"description\": \"Remote Site VPN\"," +
						"\"price\": 275.0," +
						"\"isRecurring\": true" +
					"}]" +
				"}, {" +
					"\"type\": \"net.mandelmania.ServiceAgreementContract\"," +
					"\"customerCode\": \"A Customer\"," +
					"\"contractIDPrefix\": \"SA\"," +
					"\"contractID\": 432," +
					"\"startDate\": {" +
						"\"year\": 2015," +
						"\"month\": \"JUNE\"," +
						"\"dayOfYear\": 169," +
						"\"leapYear\": false," +
						"\"monthValue\": 6," +
						"\"chronology\": {" +
							"\"calendarType\": \"iso8601\"," +
							"\"id\": \"ISO\"" +
						"}," +
						"\"dayOfMonth\": 18," +
						"\"dayOfWeek\": \"THURSDAY\"," +
						"\"era\": \"CE\"" +
					"}," +
					"\"termPeriod\": \"Yearly\"," +
					"\"numberOfTerms\": 1," +
					"\"billingPeriod\": \"Hourly\"," +
					"\"pricePerBillingPeriod\": 125.0," +
					"\"projectSummary\": \"This SOW covers the discovery and documentation of ABC Health Corporation's (ABCHC) wired and wireless local area networks (LANs).\"," +
					"\"scopeOfWork\": \"Document and evaluate IP Address allocation and usage\nDocument physical layout of all LANs\"," +
					"\"upToHours\": 160," +
					"\"endDate\": {" +
						"\"year\": 2016," +
						"\"month\": \"JUNE\"," +
						"\"dayOfYear\": 169," +
						"\"leapYear\": true," +
						"\"monthValue\": 6," +
						"\"chronology\": {" +
							"\"calendarType\": \"iso8601\"," +
							"\"id\": \"ISO\"" +
						"}," +
						"\"dayOfMonth\": 17," +
						"\"dayOfWeek\": \"FRIDAY\"," +
						"\"era\": \"CE\"" +
					"}," +
					"\"isExpiring\": true" +
				"}]";
				//*/
		
		//StringBuffer searchResultsStringBuffer = new StringBuffer();
		/* The following code could be used as a starting point to import JSON
		 * into POJOs (Plain Old Java Objects). 
		 *
		 searchResultsStringBuffer.append("[");
		JsonFactory factory = new JsonFactory();
		factory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
		JsonParser jp= null;
		try
		{
			jp = factory.createParser(jsonToSearch.getBytes());
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();
		//TypeFactory typeFactory = mapper.getTypeFactory();
		//MapType type = mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class);
		TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};		
		Map<String, String> result = null;
		//Iterator <Map<String, String>> it = new ObjectMapper().reader(typeRef).readValues(jp);
		
		Contract[] contracts = null;
		try
		{
			contracts = mapper.readValue(jsonToSearch, Contract[].class);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		/*
		Iterator <Map<String, String>> it = null;
		try
		{
			//result =  /*(Map<String, String>) mapper.s(jp, typeRef);
			//it = mapper.readValues(jp, typeRef);
			/*
			it = mapper.reader(typeRef).readValues(jp);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		while (it.hasNext()) {
			Map<String, String> map = it.next();
			searchResultsStringBuffer.append(map);
		}
		*/
		
		/*
		ObjectMapper mapper = new ObjectMapper();
		List<Contract> contracts = null;
		try
		{
			contracts = mapper.readValue(jsonToSearch,  mapper.getTypeFactory().constructCollectionType(List.class, Contract.class));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		for (Contract contract : contracts) {
			
		}
		*/

		//These are attempts at pulling JSON objects out via Java's Regex Matcher.  This is
		//not a suitable task for it, though, since the pattern matching for parsing via regular
		//expressions requires recursion, which the Java regex library doesn't support. 
		//String[] jsonObjects = jsonToSearch.split("\\{([^}]*.?)\\}");
		//String[] jsonObjects = jsonToSearch.split("\\{([^}]*?)\\}");
		//String[] jsonObjects = jsonToSearch.split("\\{([^\\}]*.?)\\}");
		/*
		List<String> matchList = new ArrayList<String>();
		//Pattern jsonPattern = Pattern.compile("\\{(.*?)\\}");
		//Pattern jsonPattern = Pattern.compile("\\{([^}]*)\\}");
		Pattern jsonPattern = Pattern.compile("\\{(.*?)\\}(?!\\s*\\})\\s*", Pattern.DOTALL);
		Matcher regexMatcher = jsonPattern.matcher(jsonToSearch);
		while (regexMatcher.find())
			matchList.add(regexMatcher.group());
		
		//Iterate through the list of JSON objects
		for (String match : matchList) {
		//for (String match : jsonObjects) {
			searchResultsStringBuffer.append(match);
			/*
			//From http://stackoverflow.com/questions/13916086/jackson-recursive-parsing-into-mapstring-object/13926850#13926850
			//final String json = "{}";
			final ObjectMapper mapper = new ObjectMapper();
			final MapType type = mapper.getTypeFactory().constructMapType(
			    Map.class, String.class, Object.class);
			Map<String, Object> data = null;
			try
			{
				data = mapper.readValue(match, type);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			for (Map.Entry<String, Object> entry : data.entrySet()) {
			    //String key = entry.getKey();
			    //Object value = entry.getValue();
				if (entry.getKey().toLowerCase().contains(searchFor.toLowerCase()) || 
						((String)entry.getValue()).toLowerCase().contains(searchFor.toLowerCase())) {
				    if (searchResultsStringBuffer.length() > 1)
				    	searchResultsStringBuffer.append(",");
				    searchResultsStringBuffer.append("[Key: " + entry.getKey() + "\tValue: " + entry.getValue() + "]");
				}
			}
			*/
			/*
			//searchResultsStringBuffer.append("]");			
			searchResultsStringBuffer.append("]       **********************");			
		}
		*/
		
		/*
		//searchResultsStringBuffer.append("]");			
		searchResultsStringBuffer.append("]       **********************");	
		
		/*
		//For reference: http://stackoverflow.com/questions/19760138/parsing-json-in-java-without-knowing-json-format
		JsonFactory factory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);
		JsonNode rootNode = null;
		try
		{
			rootNode = mapper.readTree(jsonToSearch);
		} catch (IOException e)
		{
			e.printStackTrace();
		}  
		Iterator<Map.Entry<String,JsonNode>> fieldsIterator = rootNode.fields();
		while (fieldsIterator.hasNext()) {
		    Map.Entry<String,JsonNode> field = fieldsIterator.next();
		    //System.out.println("Key: " + field.getKey() + "\tValue:" + field.getValue());
		    if (searchResultsStringBuffer.length() > 1)
		    	searchResultsStringBuffer.append(",");
		    searchResultsStringBuffer.append("[Key: " + field.getKey() + "\tValue: " + field.getValue() + "]");
		}
		searchResultsStringBuffer.append("]");
	       
		/*
		 * From: http://stackoverflow.com/questions/9151619/java-iterate-over-jsonobject
		 *  
		jObject = new JSONObject(contents.trim());
		Iterator<?> keys = jObject.keys();
		while( keys.hasNext() ) {
		    String key = (String)keys.next();
		    if ( jObject.get(key) instanceof JSONObject ) {

		    }
		}
		*/
		
		return Response
				.status(200)
				.entity(searchResultsString.toString())
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
