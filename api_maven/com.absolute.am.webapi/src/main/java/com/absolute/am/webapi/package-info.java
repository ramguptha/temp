/**
 * <p>This documentation is confidential and is property of Absolute Software Corporation.</p>
 *
 * <h2>Purpose</h2>
 * <p>This documentation defines and describes the Absolute Manage (AM) Web Application Programming Interface (API). The AM Web API provides programmatic access to all AM Web Admin functionality without the need to use the application's user interface, allowing you to integrate AM Web Admin functionality with third-party systems. This API is a set of web services that are based on the RESTful architecture.</p>
 * 
 * <h2>Accessing the API</h2>
 * <p>The AM Web API is accessible in the following ways:</p>
 * <ul>
 * <li>Locally from the machine on which AM Web Admin is installed using port 8080 (for example, <u>http://localhost:8080/</u>)</li>
 * <li>Remotely using port 443 (for example, <u>https://&lt;your-fully-qualified-domain-name&gt;/</u>)</li>
 * <p>All API resources referenced in this documentation must use <u>com.absolute.am.webapi/api</u> in the base URL for requests. For example, to request a list of all mobile devices using the Views resource, use the following URL if you are accessing the API from your local machine:<br/>
 * <u>http://localhost:8080/com.absolute.am.webapi/api/views/allmobiledevices</u>
 * </p>
 * <p>To begin an API session, you must validate your credentials using the Login resource.</p>
 *
 * <h2>Terminology</h2>
 * <p>The following table summarizes various terms and acronyms used in this document.</p>
 * <p><strong>Table 1 - Terms used in this Document</strong></p>
 * <table>
 * <tr><td>AM</td><td>Absolute Manage</td></tr>
 * <tr><td>IF</td><td>Interface</td></tr>
 * <tr><td>MCM</td><td>Mobile Content Management</td></tr>
 * </table>
 *
 * <h1>Common Definitions</h1>
 *
 * <h2>Common Query Strings</h2>
 * <p>Many of the APIs in this document will return a list of objects. To help with ordering, paging and filtering, each of these APIs will accept the following common query strings:</p>
 * <table>
 * <tr><th><strong>Query String</th><th>Description</th></tr>
 * <tr><td><strong>$top=N</strong></td><td>Returns the top N rows of the view being queried. If this query parameter is not specified, then the top 50 rows are returned.</td></tr>
 * <tr><td><strong>$skip=N</strong></td><td>Skip the top N rows that would normally be returned by the view, and return rows from N+1 onwards. If this parameter is not specified, then no rows are skipped.</td></tr>
 * <tr><td><strong>$inlinecount=allpages|none</strong></td><td>This parameter can have 2 values.<br/> 
 *   <strong>allpages</strong>: include a count in the returned Result of the total number of rows this view returns. The count is not limited by the $top parameter, but is a count of the total number of rows as if the $top were not present.<br/>
 *   <strong>none</strong>: do not include a count in the returned Result.
 * </td></tr>
 * <tr><td><strong>$search{:column_guid}=search_string</strong></td><td>This parameter has 2 forms:<br/> 
 *   <strong>$search=search_string</strong><br/> 
 *   This form searches all string columns for the provided search_string.<br/> 
 *   <strong>$search:column_guid =search_string</strong><br/> 
 *   This form only searches the column identified by column_guid for the search_string. The column identified by column_guid must be a String column.
 * </td></tr>
 * <tr><td><strong>$orderby=column1,column2,...</strong></td><td>This parameter specifies how the returned Result should be ordered. 
 * It is first ordered by column1 and then column2 and so on.
 * The column parameter must take one of the following forms:<br/>
 *   <strong>column_guid</strong>: perform an ascending sort on the column identified by this guid<br/>
 *   <strong>column_guid ASC</strong>: perform an ascending sort on the column identified by this guid<br/>
 *   <strong>column_guid DESC</strong>: perform a descending sort on the column identified by this guid
 * </td></tr>
 * </table>
 *
 * <h2>Common  Return Type: Result</h2>
 * <p>Many APIs return lists of objects to the caller. These are returned in a type called Result. The Result type also contain some meta-data for the list.</p> 
 * <p>The meta-data includes the following:</p>
 * <ul>
 * <li>total number of rows</li>
 * <li>localized titles for the columns of the rows</li>
 * <li>column types/display types</li>
 * </ul>
 * <p>The column specific meta data is provided in an array. The size of this array is determined by the number of columns in the view definition.</p>
 * <p>The data for the rows immediately follows the column meta data.</p>
 * <p>The following is an example of the common Result Type serialized with JSON:</p>
 * 
 * <pre>
 * {
 * &emsp;"metaData":
 * &emsp;{
 * &emsp;	"totalRows":2,
 * &emsp;	"columnMetaData":[
 * &emsp;		{
 * &emsp;			"MaxWidth":100,
 * &emsp;			"ShortDisplayName":"Id",
 * &emsp;			"Description":"The pk of the table.",
 * &emsp;			"MinWidth":30,
 * &emsp;			"DisplayName":"Id",
 * &emsp;			"Truncation":3,
 * &emsp;			"ColumnDataType":"Number",
 * &emsp;			"InfoItemID":"A78A37B9-86B7-4118-84C6-25A15C6F68C8",
 * &emsp;			"Alignment":1,
 * &emsp;			"Width":150
 * &emsp;		},
 * &emsp;		{
 * &emsp;			"ShortDisplayName":"Name",
 * &emsp;			"MaxWidth":1000,
 * &emsp;			"Description":"The name of the mobile device policy.",
 * &emsp;			"MinWidth":50,
 * &emsp;			"DisplayName":"Policy Name",
 * &emsp;			"Truncation":3,
 * &emsp;			"ColumnDataType":"String",
 * &emsp;	 		"InfoItemID":"426FBD79-BE65-4FC0-A27F-BAC810C15C6E",
 * &emsp;	 		"Alignment":1,
 * &emsp;	 		"Width":150
 * &emsp;		},
 * &emsp;		... etc.
 * &emsp;	]
 * },
 * "rows":
 * &emsp;[
 * &emsp;	[25,"webAPIUnitTest1","0BCCC470-561B-4E01-A44A-5BFEBBC692A7","On-demand, Auto-remove","Always",null],
 * &emsp;	[26,"webAPIUnitTest2","F4ACC40C-E1B9-4A5F-939A-A4C1249ED2A1","On-demand","Daily interval","13:01"]
 * &emsp;]
 * }
 * </pre>
 * 
 * <h2>Authentication</h2>
 * <p>The web API should only allow access to users who have credentials that match those in the AM Server database. That right to access expires after a configurable idle period.</p> 
 * <p>Browser cookies will be used to store an authentication token. The token will be returned by the Web API when valid credentials are provided to the server using a proprietary encrypted protocol.</p>
 * <p>See <strong>Login API</strong> for further details</p>
 * <p>Subsequent requests will include the token and the Web API will check and update the last access time associated with the token. When the idle time has been exceeded, the server will respond with HTTP result code 401 (Unauthorized). For the client to proceed, it must re-authenticate with the server and obtain a new authentication token.</p>
 * <p>Note: this approach requires that the client browser has cookie support enabled.</p>
 *
 * <h2>Authorization</h2>
 * <p>All API requests are checked for appropriate authorization at the Web API. Only data that the user is permitted to view will be returned from the Web API. The authorization is bound to the commands and rights assigned in the Administrator editor of the Admin Console. The command/right required is documented with each endpoint.</p>
 *
 * <h2>HTTP Status Codes</h2>
 * <p>The standard HTTP status codes will be used. Please refer to section 10 of RFC2616 for further details (http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html).</p>
 * <p>The following rules will also be applied:</p>
 * <ul>
 * <li>When a resource identifier is part of the URI, and the resource is not found, <strong>404 Not Found</strong> will be returned. Unless otherwise noted, in all other cases when the resource is not found, <strong>400 Bad Request</strong> will be returned.</li>
 * <li>For status codes <strong>400 Bad Request</strong> and <strong>500 Internal Server Error</strong> additional error information will may be provided in the body of the response. See below.</li>
 * </ul>
 * <p>When error information is provided in the body, it will use the following JSON format:</p>
 * <pre>{
 * &emsp;   "message":"Could not add content to policy",
 * &emsp;   "errorDescription":"Policy not found.",
 * &emsp;   "errorCode":12345568,
 * &emsp;   "server":"qaams2:3971"
 * }
 * </pre>
 *
 * <p><strong>message</strong>:<br/>
 * This is a required value. It is a string.
 * It is a textual description of the operation that failed.
 * </p>
 * <p><strong>errorDescription</strong>:
 * This is a required value. It is a string.
 * It is a textual description of the reason for the failure.
 * </p>
 * <p><strong>errorCode</strong>:
 * This is an optional value. It is a number.
 * The AM Server has its own error range, starting at 0x20000000. If we receive an error back from the AM server then that number will be provided here.
 * </p>
 * <p><strong>server</strong>:
 * This is an optional field. It is a string.
 * </p>
 * <p>If the error was reported by the AM Server, this field will contain the hostname or IP address of the server, together with the port number.</p>
 *
 * <p></p>
 *
 * 
 */
@XmlSchema (
  namespace = "com.absolute.am.webapi"
)
package com.absolute.am.webapi;

import javax.xml.bind.annotation.XmlSchema;