Here are the main application URLs--one using SSL, and the other not:
https://www.mandelmania.net:8443/jaxrs-gcibilling
http://www.mandelmania.net:8080/jaxrs-gcibilling


A basic UML diagram of my class hierarchy (thanks to the ObjectAid UML Diagram Eclipse plugin, found at www.objectaid.com) is the file ClassDiagram.png in the Github repo.

Here is the Github URL to the source code:  https://github.com/ianc10/jaxrs-gcibilling

Here are some JAX-RS URLs to API calls:
Get contracts and invoices from in-memory contructed POJO's as JSON:
https://www.mandelmania.net:8443/jaxrs-gcibilling/webapi/databroker/getcontractsandinvoices

Write POJO Contracts (and subclasses) and Invoices data to Couchbase as JSON: https://www.mandelmania.net:8443/jaxrs-gcibilling/webapi/databroker/couchbasewrite

Query Couchbase for all Invoices:
https://www.mandelmania.net:8443/jaxrs-gcibilling/webapi/databroker/couchbasegetinvoices

Query Couchbase for all Contracts:
https://www.mandelmania.net:8443/jaxrs-gcibilling/webapi/databroker/couchbasegetcontracts

Calculate badge number (the number of contracts that are expiring PLUS the number of invoices that are past due):
https://www.mandelmania.net:8443/jaxrs-gcibilling/webapi/databroker/calculatebadgenumber

Search all in-memory POJO-to-JSON data for keys "dayOfWeek" :
https://www.mandelmania.net:8443/jaxrs-gcibilling/webapi/databroker/search/dayOfWeek

Show API calls by getting the Web Application Description Language:
https://www.mandelmania.net:8443/jaxrs-gcibilling/webapi/application.wadl


