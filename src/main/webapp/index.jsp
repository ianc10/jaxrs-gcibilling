<html>
<body>
    <h2>GCI Billing Demo App</h2>
    <p><b>Application links:</b>
    <p><a href="webapi/databroker/getcontracts">Get set of Contract POJO's as JSON (it uses @JsonTypeInfo annotations to automatically output the appropriate subclass properties): webapi/databroker/getcontracts</a>
    <p><a href="webapi/databroker/getinvoices">Get set of Invoice POJO's as JSON: webapi/databroker/getinvoices</a>
    <p><a href="webapi/databroker/getcontractsandinvoices">Get contracts and invoices from in-memory contructed POJO's as JSON (this is an alternative to using @JsonTypeInfo annotations, as it manually checks each Contract object to determine which subclass of Contract, to output the appropriate object's properties): webapi/databroker/getcontractsandinvoices</a>
    <!-- <p><a href="webapi/databroker/couchbase">Access Couchbase server: webapi/databroker/couchbase</a> -->
    <p><a href="webapi/databroker/calculatebadgenumber">Calculate badge number (the number of contracts that are expiring PLUS the number of invoices that are past due): webapi/databroker/calculatebadgenumber</a>
    <p><a href="webapi/databroker/search/dayOfWeek">Search all in-memory POJO-to-JSON data for keys "dayOfWeek": webapi/databroker/search/dayOfWeek</a>
	<p><a href="webapi/application.wadl">Show API calls by getting the Web Application Description Language: webapi/application.wadl</a>
    <p><b>*note*  I installed Couchbase Enteprise Server 4.5.0 64-bit for Linux along with client
    <br>Java and .NET SDK's (via Maven in Eclipse and NuGet Package Manager in Visual Studio), and
    <br>made RESTful calls to it:</b>
    <p><a href="webapi/databroker/couchbasewrite">Write POJO Contracts (and subclasses) and Invoices data to Couchbase as JSON: webapi/databroker/couchbasewrite</a>
    <p><a href="webapi/databroker/couchbasegetinvoices">Query Couchbase for all Invoices: webapi/databroker/couchbasegetinvoices</a>
    <p><a href="webapi/databroker/couchbasegetcontracts">Query Couchbase for all Contracts: webapi/databroker/couchbasegetcontracts</a>
	<p>Enter search text to instantly query (in-memory POJO's) via AJAX: <input id="searchText" type="text" value="dayOfWeek" oninput="callSearch()">
	<p><em><b>Search results:</b></em>
	<blockquote>
		<div id="searchResultsDiv" style="color:#00FF00">
		</div>
	</blockquote>
	<!-- see www.developer.com/tech/article.php/3841046/Real-World-REST-Using-Jersey-AJAX-and-JSON.htm for reference  -->
	<script>
	function callSearch() {
	    if (window.XMLHttpRequest)
	       {
	        // code for IE7+, Firefox, Chrome, Opera, Safari
	        xmlhttp=new XMLHttpRequest();
	       }
	     else if (window.ActiveXObject)
	       {
	         // code for IE6, IE5
	         xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	        }
	     else
	        {
	          alert("Your browser does not support XMLHTTP!");
	        }
	     //get the text to search:
	     var searchText = document.getElementById("searchText").value;
	     //Prepare the REST Url (don't send if the string is empty, to
	     //avoid a Tomcat "Not Found" error:
	     if (searchText.length > 0) {
		     var url = "webapi/databroker/search/" + searchText;
		     xmlhttp.open("GET",url,true);
		     xmlhttp.onreadystatechange=callback;
		     xmlhttp.send(null);
	     }
	}
	/**
	 * 
	 * function for processing the AJAX callback
	 */
	  function callback()
	  {
		if(xmlhttp.readyState==4)
	    {
		  searchResultsDiv.innerHTML = xmlhttp.responseText;	
	      //Create the card Json object
	      //var card = eval( "(" + xmlhttp.responseText + ")" );
	      //Get the <img> element
		  //var img = document.getElementById("cardViewer");
	      //Apply the Base64 string, telling the image element about the nature of
	      //the data
		  //img.src="data:image/" + card.fileFormat + ";base64," + card.image;
	      //Put the card code in the image alt text
		  //img.alt = card.code;
	      //Get the hidden element and apply the shoeId value
		  //var shoeId = document.getElementById("shoeId");
	      //shoeId.value = card.shoeId;
	      //Also, display the shoeId value on the page	  
		  //var shoeIdDiv = document.getElementById("shoeIdDiv");
	      //shoeIdDiv.innerHTML = card.shoeId;
	      //Display the card code on the page	  
		  //var cardCodeDiv = document.getElementById("cardCodeDiv");
	      //cardCodeDiv.innerHTML = card.code;
	    }
	  }
	callSearch();
	</script>
	    
    <br><br><p>Original Template Contents:
    <h2>Jersey RESTful Web Application!</h2>
    <p><a href="webapi/myresource">Sample Jersey resource</a>
    <p>Visit <a href="http://jersey.java.net">Project Jersey website</a>
    for more information on Jersey!</p>
  </body>
</html>
