Here is the main application URL:  http://www.mandelmania.net:8080/jaxrs-gcibilling/

A basic UML diagram of my class hierarchy (thanks to the ObjectAid UML Diagram Eclipse plugin, found at www.objectaid.com) is the file ClassDiagram.png in the Github repo.

Here is the Github URL to the source code:  https://github.com/ianc10/jaxrs-gcibilling

Here are some JAX-RS URLs to API calls:
Return formatted JSON object with all contracts and invoices:
http://www.mandelmania.net:8080/jaxrs-gcibilling/webapi/databroker/getcontractsandinvoices

Return the “badge” number (the number of contracts that are expiring PLUS the number of invoices that are past due):
http://www.mandelmania.net:8080/jaxrs-gcibilling/webapi/databroker/calculatebadgenumber

Do a search (currently it only matches on some names of keys—I have found a better way to search, but at this point because of time constraints at this moment it isn’t implemented) of JSON data containing all the contracts and invoices:
http://www.mandelmania.net:8080/jaxrs-gcibilling/webapi/databroker/search/dayOfWeek


