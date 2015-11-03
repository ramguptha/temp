/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.webapi.controllers;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.codehaus.enunciate.jaxrs.*;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.dal.ColumnConstants;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IPolicyHandler;
import com.absolute.am.dal.model.iOsPolicies;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.model.MetaData;
import com.absolute.am.model.Result;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.model.exception.WebAPIException;
import com.absolute.am.model.policy.Policy;
import com.absolute.am.model.policy.PolicyList;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;


/**
 * <h3>Policies API</h3>
 * 
 * @author klavin
 *
 */
//@WebServlet("/api/policies/*")

@Path ("/policies")
public class Policies {
	
    private static Logger m_logger = LoggerFactory.getLogger(Policies.class.getName()); 
	private static final String VIEW_NAME_ONE_MOBILE_POLICY = "onemobilepolicy";
	private static final String VIEW_NAME_ONE_MOBILE_SMART_POLICY = "onemobilesmartpolicy";
	private static final String VIEW_NAME_ALL_MOBILE_POLICIES = "allmobilepolicies";
	private static final String VIEW_NAME_SMART_MOBILE_POLICIES = "smartmobilepolicies";
	private static final String VIEW_NAME_STANDARD_MOBILE_POLICIES= "standardmobilepolicies";
	private static final String VIEW_NAME_SMART_MOBILE_POLICIES_SHORT = "smart";
	private static final String VIEW_NAME_STANDARD_MOBILE_POLICIES_SHORT= "standard";
	private static final String VIEW_NAME_DEVICES_FOR_NON_SMART_POLICY = "devicesfornonsmartpolicy";
	private static final String VIEW_NAME_DEVICES_FOR_SMART_POLICY = "devicesforsmartpolicy";
	private static final String VIEW_NAME_CONTENT_FOR_POLICY = "contentforpolicy";
	private static final String VIEW_NAME_CONFIG_PROFILES_FOR_POLICY = "configprofilesforpolicy";
	private static final String VIEW_NAME_INHOUSE_APPS_FOR_POLICY = "inhouseappsforpolicy";
	private static final String VIEW_NAME_THIRDPARTY_APPS_FOR_POLICY = "thirdpartyappsforpolicy";
	private static final String VIEW_NAME_ACTIONS_FOR_POLICY = "actionsforpolicy";
	
	private static final String RESULT_MD_COLUMN_DATA_TYPE = "ColumnDataType";
	private static final String RESULT_MD_COLUMN_DATA_TYPE_PLIST = "propertyList";
	
	private static final String SMART_POLICY_USER_EDITABLE_FILTER = "SmartPolicyUserEditableFilter";
	
	private static final String FILTER_OPERATOR = "Operator";
	private static final String FILTER_OPERATOR_EQUALS = "==";
	private static final String FILTER_OPERATOR_NOT_IN = "NOT IN";
	private static final String FILTER_OPERATOR_AND = "AND";
	@SuppressWarnings("unused")
	private static final String FILTER_CRITERIA_FIELD_TYPE = "CriteriaFieldType";
	private static final String FILTER_CONTAINMENT_OPERATOR = "ContainmentOperator";
	private static final String FILTER_COMPARE_VALUE = "CompareValue";
	private static final String FILTER_INFO_ITEM_ID = "InfoItemID";
	private static final String FILTER_INFO_ITEM_ID_26B03C68 = "26B03C68-0BF5-41ED-AD06-85903D5FBDFE";
	private static final String FILTER_USE_NATIVE_TYPE = "UseNativeType";
	private static final String FILTER_DB_TABLE_NAME = "DBTable_Name";
	private static final String FILTER_DB_TABLE_NAME_IPHONE_INFO = "iphone_info";
	private static final String FILTER_DB_TABLE_COLUMN = "DBTable_Column";
	private static final String FILTER_DB_TABLE_COLUMN_IPHONE_INFO_RECORD_ID = "iphone_info_record_id";
	private static final String FILTER_DB_TABLE_COLUMN_ID = "id";

	private static final String FILTER_COMPARE_SUB_FILTER = "CompareSubFilter";
	private static final String FILTER_COMPARE_SUB_QUERY_DISTINCT_VALUES = "CompareSubQueryDistinctValues";
	private static final String FILTER_OPERATOR_INTERSECT = "&&";
	
	private static final int FILTER_TYPE_SMART_POLICY = 1;
	private static final int FILTER_TYPE_SMART_POLICY_BY_INSTALLED_APPS = 2;
	private static final int FILTER_TYPE_SMART_POLICY_BY_INSTALLED_PROFILES = 3;
	private static final Integer FILTER_TYPE_SMART_POLICY_UNMANAGED_DEVICES = 255;
	private static final String FILTER_TABLE_IPHONE_INSTALLED_SOFTWARE_INFO = "iphone_installed_software_info";
	private static final String FILTER_TABLE_IPHONE_INSTALLED_PROFILE_INFO = "iphone_installed_profile_info";
	
	private static final String SQL_GET_UNMANAGED_DEVICES_IDS = "SELECT id FROM "
											+ FILTER_DB_TABLE_NAME_IPHONE_INFO
											+ " WHERE MDMManagedDevice = 0";

	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;
	
	//"version number", always 1 for a new object
	private static final int SEED_FOR_NEW_OBJECT = 1;

	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	
	private static final ViewDescription[] m_viewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_NAME_ALL, "All Mobile Policies", 0),
		new ViewDescription(VIEW_NAME_SMART_MOBILE_POLICIES_SHORT, "Smart Mobile Policies", 1),
		new ViewDescription(VIEW_NAME_STANDARD_MOBILE_POLICIES_SHORT, "Standard Mobile Policies", 2)
	};

	/**
	 * <p>Get a list of named views available for policies. Please refer to /api/mobiledevices/views – GET for an example of the response to this request.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *
	 * @return Returns all list of available views for this end-point.
	 * @throws Exception 
	 */
	@GET @Path("/views")
	@Produces({ MediaType.APPLICATION_JSON })
	public ViewDescriptionList getViewsForPolicies() throws Exception  {

		ViewDescriptionList result = new ViewDescriptionList();	
		result.setViewDescriptions(m_viewDescriptions);
		
		return result;
	}
	
	/**
	 * <p>Returns the data for a given view.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *
	 * @param viewname The given <strong>viewname</strong>
	 * @return Get a list of named views available for policies. 
	 * @throws Exception 
	 */
	@GET @Path("/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		@ResponseCode ( code = 404, condition = "The <strong>viewName</strong> is not found.")
	})
	public Result getView(@PathParam("viewname") String viewname,
			@Context UriInfo ui) throws Exception {
		
		MDC.put("viewname", viewname);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		if (viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VIEW_NAME_ALL_MOBILE_POLICIES;
		} else if (viewname.compareToIgnoreCase(VIEW_NAME_SMART_MOBILE_POLICIES_SHORT) == 0){
			viewname = VIEW_NAME_SMART_MOBILE_POLICIES;
		} else if (viewname.compareToIgnoreCase(VIEW_NAME_STANDARD_MOBILE_POLICIES_SHORT) == 0){
			viewname = VIEW_NAME_STANDARD_MOBILE_POLICIES;
		} else {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base, "viewname", viewname);
		}

		Result result = null;
		IDal dal = Application.getDal(session);

		result = ViewHelper.getViewDetails(
					dal,
					viewname, 
					ui.getQueryParameters(),
					null,
					dbLocaleSuffix);

		MDC.remove("viewname");
		
		return result;
	}

	/**
	 * <p>Get the smart policy identified by {id}.<br/>
	 *    The response is a single row ResultSet with meta data. It will include the attributes:</p>
	 * 
	 * <pre>
	 * Id
	 * Policy Name
	 * IsSmartPolicy 
	 * Policy Seed
	 * FilterType
	 * </pre>   
	 * 
	 * <p>FilterType is a numeric value corresponding with the type of the smart policy:
	 *    1 – by device attribute, 2 – by application, 3 – by configuration profile</p>
	 * <p>Example of result:</p>
	 * <pre>
	 *{
	 * &emsp;metaData: 
	 * &emsp;{
	 * &emsp;      totalRows: 0,
	 * &emsp;      columnMetaData:
	 * &emsp;      [
	 * &emsp;           {
	 * &emsp;           MaxWidth: 100,
	 * &emsp;           ShortDisplayName: "Id",
	 * &emsp;           Description: "The pk of the table.",
	 * &emsp;           MinWidth: 30,
	 * &emsp;           DisplayName: "Id",
	 * &emsp;           Truncation: 3,
	 * &emsp;           ColumnDataType: "Number",
	 * &emsp;           InfoItemID: "A78A37B9-86B7-4118-84C6-25A15C6F68C8",
	 * &emsp;           Alignment: 1,
	 * &emsp;           Width: 150
	 * &emsp;           },
	 * &emsp;           {
	 * &emsp;           ShortDisplayName: "Name",
	 * &emsp;           MaxWidth: 1000,
	 * &emsp;           Description: "The name of the mobile device policy.",
	 * &emsp;           MinWidth: 50,
	 * &emsp;           DisplayName: "Policy Name",
	 * &emsp;           Truncation: 3,
	 * &emsp;           ColumnDataType: "String",
	 * &emsp;           InfoItemID: "426FBD79-BE65-4FC0-A27F-BAC810C15C6E",
	 * &emsp;           Alignment: 1,
	 * &emsp;           Width: 150
	 * &emsp;           },
	 * &emsp;           {
	 * &emsp;           ShortDisplayName: "Smart policy",
	 * &emsp;           MaxWidth: 1000,
	 * &emsp;           Description: "Is the mobile device policy a smart policy?",
	 * &emsp;           MinWidth: 50,
	 * &emsp;           DisplayName: "Is Smart Policy",
	 * &emsp;           Truncation: 3,
	 * &emsp;           ColumnDataType: "Blob",
	 * &emsp;           InfoItemID: "F6917EE8-9F39-43F3-B8C9-D3C461170FE5",
	 * &emsp;           Alignment: 3,
	 * &emsp;           Width: 150,
	 * &emsp;           DisplayType: "FormatBoolean"
	 * &emsp;           },
	 * &emsp;           {
	 * &emsp;           MaxWidth: 100,
	 * &emsp;           ShortDisplayName: "Seed",
	 * &emsp;           Description: "The seed of the iOS Policy",
	 * &emsp;           MinWidth: 30,
	 * &emsp;           DisplayName: "Seed",
	 * &emsp;           Truncation: 3,
	 * &emsp;           ColumnDataType: "Number",
	 * &emsp;           InfoItemID: "65A7E9DC-2026-4178-9950-B0E26A3A8B0A",
	 * &emsp;           Alignment: 1,
	 * &emsp;           Width: 150
	 * &emsp;           },
	 * &emsp;           {
	 * &emsp;           MaxWidth: 100,
	 * &emsp;           ShortDisplayName: "Filter Type",
	 * &emsp;           Description: "Filter Type.",
	 * &emsp;           MinWidth: 20,
	 * &emsp;           DisplayName: "Filter Type",
	 * &emsp;           Truncation: 3,
	 * &emsp;           ColumnDataType: "Number",
	 * &emsp;           InfoItemID: "618ee8ca-6e1a-4c47-a761-c43f5b7f5e48",
	 * &emsp;           Alignment: 1,
	 * &emsp;           Width: 20,
	 * &emsp;           DisplayType: "Number"
	 * &emsp;           },
	 * &emsp;           {}
	 * &emsp;      ]
	 * &emsp;},
	 * &emsp;rows: 
	 * &emsp;[
	 * &emsp;           [
	 * &emsp;           28,
	 * &emsp;           "Smart Policy KL",
	 * &emsp;           1,
	 * &emsp;           1,
	 * &emsp;           1,
	 * &emsp;           {
	 * &emsp;               CompareValue: 
	 * &emsp;               [
	 * &emsp;                    {
	 * &emsp;                    CachedInfoItemName: "Mobile Device Manufacturer",
	 * &emsp;                    CompareValue: "Samsung",
	 * &emsp;                    CompareValue2: "",
	 * &emsp;                    CompareValueUnits: "Minutes",
	 * &emsp;                    InfoItemID: "408A8D10-D908-4A9E-A00C-3FFB27E7EA81",
	 * &emsp;                    IsCustomField: false,
	 * &emsp;                    Operator: "==",
	 * &emsp;                    UseNativeType: false
	 * &emsp;                    }
	 * &emsp;              ],
	 * &emsp;              CriteriaFieldType: 0,
	 * &emsp;              Operator: "AND"
	 * &emsp;          }
	 * &emsp;          ]
	 * &emsp;]
	 *}
	 * </pre>   
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param policyId The given Smart Policy Id
	 * @return Returns the attributes of a smart policy.
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	@GET @Path("/smart/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no such policy.")
		})
	public Result getSmartPolicyForId(
			@Context UriInfo ui,
			@PathParam("id") String policyId) throws Exception  {
	
		MDC.put("smartPolicyId", policyId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null; 
		IDal dal = Application.getDal(session);

		iOsPolicies policy = dal.getPolicyHandler().getPolicy(
				Long.parseLong(policyId));

		if (null == policy) {
			throw new NotFoundException("NO_POLICY_FOUND_FOR_ID",
					new Object[] { policyId }, locale, m_Base);
		}			
		
		ArrayList<String> viewParams = new ArrayList<String>();
		viewParams.add(policyId);
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_ONE_MOBILE_SMART_POLICY,
				null,
				viewParams,
				dbLocaleSuffix);
		
		MetaData metaData = result.getMetaData();
		ArrayList<Map<String, Object>> colMetaData = metaData.getColumnMetaData();
		for (int i=0; i < colMetaData.size(); i++) {
			Map<String, Object> thisColumn = colMetaData.get(i);
			if (thisColumn.containsKey(RESULT_MD_COLUMN_DATA_TYPE)) {
				String colType = (String)thisColumn.get(RESULT_MD_COLUMN_DATA_TYPE);
				if (colType.compareToIgnoreCase(RESULT_MD_COLUMN_DATA_TYPE_PLIST) == 0) {
					Object[] rows = result.getRows();
					Object[] row = (Object[]) rows[0];
					byte[] blob2 = (byte[])row[i];
					PropertyList pList = PropertyList.fromByteArray(blob2);
					if (!PropertyList.elementExists(pList, SMART_POLICY_USER_EDITABLE_FILTER)) {
						throw new NotFoundException("SmartPolicyUserEditableFilter not found for idxxx: ", new Object[]{policyId}, locale, m_Base);
					}
					Map<String, Object> filter = PropertyList.getElementAsMap(pList, SMART_POLICY_USER_EDITABLE_FILTER);
					row[i] = filter;
				}
			}
		}

		MDC.remove("smartPolicyId");
		
		if (result.getRows().length < 1) {
			throw new NotFoundException("NO_SMART_POLICY_FOUND_FOR_ID", new Object[]{policyId}, locale, m_Base);
		}
		return result;

	}

	/**
	 * <p>This end point is used to create a smart policy. This end point can be used to create 3 different types of smart policies:</p>
	 * <ul>
	 * <li>smart policies for mobile devices</li>
	 * <li>smart policies for mobile devices by installed applications</li>
	 * <li>smart policies for mobile devices by installed configuration profiles</li>
	 * </ul>
	 * 
	 * <p>Example – create a new standard policy for Mobile Devices:</p>
	 * <pre>
	 *{
	 *&emsp;"filterType" : 1,
	 *&emsp;"name" : "bynone",
	 *&emsp;"smartPolicyUserEditableFilter" : { "CompareValue" : [ { "CachedInfoItemName" : "Account Enabled",
	 *&emsp;            "CompareValue" : "",
	 *&emsp;            "CompareValue2" : "",
	 *&emsp;            "CompareValueUnits" : "",
 	 *&emsp;            "InfoItemID" : "8DB6230A-CD33-11D9-86E2-000D93B66ADA",
 	 *&emsp;            "IsCustomField" : false,
	 *&emsp;            "Operator" : "<>",
	 *&emsp;            "UseNativeType" : false
	 *&emsp;          },
	 *&emsp;          { "CachedInfoItemName" : "App Assignment Rule",
	 *&emsp;            "CompareValue" : "2",
   	 *&emsp;            "CompareValue2" : "",
   	 *&emsp;            "CompareValueUnits" : "",
 	 *&emsp;            "InfoItemID" : "3CDDA1CB-D62E-4F5B-A29D-5CCFE1CDA013",
 	 *&emsp;            "IsCustomField" : false,
	 *&emsp;            "Operator" : "BeginsWith",
 	 *&emsp;            "UseNativeType" : false
 	 *&emsp;         },
 	 *&emsp;         { "CachedInfoItemName" : "Administrator Name",
	 *&emsp;            "CompareValue" : "abc",
	 *&emsp;            "CompareValue2" : "",
 	 *&emsp;            "CompareValueUnits" : "",
	 *&emsp;            "InfoItemID" : "8DB8C146-CD33-11D9-86E2-000D93B66ADA",
	 *&emsp;            "IsCustomField" : false,
	 *&emsp;            "Operator" : "EndsWith",
	 *&emsp;            "UseNativeType" : false
	 *&emsp;          }
	 *&emsp;        ],
	 *&emsp;      "CriteriaFieldType" : 0,
	 *&emsp;      "Operator" : "AND"
	 *&emsp;    }
	 *}
	 * </pre>
	 * 
	 * <p>Note: The smartPolicyUserEditableFilter above is used to generate a filter criteria and an SQL query which are needed by AM to create a smart policy.
	 *    The end point will generate the filter criteria from the smartPolicyUserEditableFilter. The AM Server will generate the SQL query.</p>
	 * 
	 * <p>Example - create a new smart policy for Mobile Devices by installed applications:</p>
	 * <p>The below example had "Some" and "Missing" selected in the pull downs when creating the smart policy.</p>
	 * <pre>
	 *{
	 *&emsp;"filterType" : 2,
	 *&emsp;  "name" : "byapp",
	 *&emsp;  "smartPolicyUserEditableFilter" : { "CompareValue" : [ { "CachedInfoItemName" : "Mobile App Name",
	 *&emsp;            "CompareValue" : "abc",
	 *&emsp;            "CompareValue2" : "",
	 *&emsp;            "CompareValueUnits" : "",
	 *&emsp;            "InfoItemID" : "89450ED3-7B11-41F8-AF11-AEE369CD26B8",
	 *&emsp;            "IsCustomField" : false,
	 *&emsp;            "Operator" : "==",
	 *&emsp;            "UseNativeType" : false
 	 *&emsp;         },
	 *&emsp;         {  "CachedInfoItemName" : "Mobile App Bundle Identifier",
	 *&emsp;            "CompareValue" : "",
	 *&emsp;            "CompareValue2" : "",
	 *&emsp;            "CompareValueUnits" : "",
 	 *&emsp;            "InfoItemID" : "3234134A-E61B-4BD4-AA88-3A50CD07C2AB",
 	 *&emsp;            "IsCustomField" : false,
 	 *&emsp;            "Operator" : "IsEmpty",
 	 *&emsp;            "UseNativeType" : false
 	 *&emsp;         }
	 *&emsp;        ],
	 *&emsp;      "ContainmentOperator" : "NOT IN",
	 *&emsp;      "Operator" : "AND"
	 *&emsp;    }
	 *}
	 * </pre>
	 * 
	 * <p>When you creates a smart policy and you have some/installed selected, 
	 *    the ContainmentOperator changes to IN. The Operator below it changes to OR.</p>
	 * <p>When you create a smart policy and you have all/missing selected, the ContainmentOperator changes to NOT IN. 
	 *    The Operator below it changes to OR.</p>
	 * <p>When you create a smart policy and you have all/installed selected, the ContainmentOperator changes to IN. 
	 *    The Operator below it changes to AND.</p>
	 * <p>Example - create a new smart policy for Mobile Devices by installed configuration profile:</p>
	 * 
	 * <pre>
	 *{
	 *&emsp;"filterType" : 3,
	 *&emsp;"name" : "byprofile",
	 *&emsp;"smartPolicyUserEditableFilter" : { "CompareValue" : [ { "CachedInfoItemName" : "Mobile Device Installed Profile Name",
	 *&emsp;       "CompareValue" : "hi",
	 *&emsp;       "CompareValue2" : "",
	 *&emsp;       "CompareValueUnits" : "",
	 *&emsp;       "InfoItemID" : "B78AAB04-4384-431F-A473-C555DDC649DD",
	 *&emsp;       "IsCustomField" : false,
	 *&emsp;       "Operator" : "<>",
	 *&emsp;       "UseNativeType" : false
	 *&emsp;     },
	 *&emsp;     { "CachedInfoItemName" : "Mobile Device Installed Profile Encrypted",
	 *&emsp;       "CompareValue" : "",
	 *&emsp;       "CompareValue2" : "",
	 *&emsp;       "CompareValueUnits" : "",
	 *&emsp;       "InfoItemID" : "4543AD1C-A764-4288-B672-110EE7A9A548",
	 *&emsp;       "IsCustomField" : false,
	 *&emsp;       "Operator" : "IsNULL",
	 *&emsp;       "UseNativeType" : false
	 *&emsp;     }
	 *&emsp;   ],
	 *&emsp;   "ContainmentOperator" : "NOT IN",
	 *&emsp;   "Operator" : "AND"
	 *&emsp;}
	 *}
	 * </pre>
	 * 
	 *<p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 * 
	 * @param policy Policy
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/smart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "The policy name is empty."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 409, condition = "A policy with the same name already exists.")
		})
	public void createSmartPolicy(
			@Context HttpServletRequest req,
			Policy policy) throws Exception  {
		
		String policyName = policy.getName().trim();		
		PropertyList filterCriteria = createFilterCriteria(policy);
	
		MDC.put("policyName", policyName);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (null == policyName || policyName.isEmpty()) {
			throw new BadRequestException("POLICIES_NO_POLICY_NAME_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		IDal dal = Application.getDal(session);
		if (0 != ResourceUtilities.getNonUniqueDataItemCount(SessionState.getLocaleDbSuffix(session), dal,
				policyName, VIEW_NAME_ALL_MOBILE_POLICIES, ColumnConstants.COLUMN_INFO_ITEM_ID_IOS_POLICIES_NAME)){
			throw new WebAPIException(Response.Status.CONFLICT, "POLICIES_POLICY_NAME_ALREADY_EXISTS",
					"DUPLICATE_NAME", null, locale, m_Base, "PolicyName", policyName);
		}
		int schemaVersion = dal.getGlobalDataHandler().getDatabaseSchemaVersion();

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			CobraAdminMiscDatabaseCommand createSmartPolicyCommand = CommandFactory.createSmartPolicyCommand(
				SessionState.getAdminUUID(session),
				policyName,
				SEED_FOR_NEW_OBJECT,
				schemaVersion,
				policy.getFilterType(),
				filterCriteria,
				UUID.randomUUID()
			);
			String contextMessage = ResourceUtilities.getLocalizedFormattedString(
						"SMART_POLICIES_CREATE_FAILED", 
						null, 
						locale, m_Base);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(
						createSmartPolicyCommand, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
						SessionState.getSyncServiceSession(session), 
						response);				


		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/webapi/Webapi");
			throw ex;
		} finally {
		    amServerProtocol.close();
		}
		MDC.remove("policyName");
	}
	
	/**
	 * <p>This end point is used to update an existing smart policy for a mobile device only.</p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 *    
	 * @param policyId The given Smart Policy Id
	 * @param newPolicy Policy
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/smart/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "Incomplete data is supplied (missing oldPolicy or newPolicy or any field in newPolicy)."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "The policy with specified id does not exist"),
		  @ResponseCode ( code = 409, condition = "A policy with the same name as in newPolicy already exists."),
		  @ResponseCode ( code = 409, condition = "The policy has been changed on the server since the client retrieved it.")
		})
	public void updateSmartPolicy(
			@Context HttpServletRequest req,
			@PathParam("id") long policyId,
			Policy newPolicy) throws Exception  {
		
		MDC.put("policyId", "" + policyId);
		m_logger.debug("Policies.updateSmartPolicy called");
		HttpSession session = m_servletRequest.getSession();
		IDal dal = Application.getDal(session);
		String locale = SessionState.getLocale(session);
		
		MDC.put("policyId", "" + policyId);

		//id in the URI does not match id in request body
		if (newPolicy.getId() != policyId) {
			throw new WebAPIException(Response.Status.CONFLICT, "POLICIES_POLICY_ID_MISMATCH",
					"ID_MISMATCH", null, locale, m_Base, "PolicyId", policyId);
		}
		
		String newPolicyName = newPolicy.getName();
		if (null == newPolicyName || newPolicyName.isEmpty()) {
			throw new BadRequestException("SMART_POLICIES_NO_NEW_POLICY_NAME_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
			
		// Need to check whether there's only a single policy with the same name ( only changing the filters for smart policy )
		// or no policies with the same name ( changing the name of the smart policy )
		int uniqueCount = ResourceUtilities.getNonUniqueDataItemCount(SessionState.getLocaleDbSuffix(session), dal, 
				newPolicyName, VIEW_NAME_ALL_MOBILE_POLICIES, ColumnConstants.COLUMN_INFO_ITEM_ID_IOS_POLICIES_NAME);
		if (1 != uniqueCount && 0 != uniqueCount ) {
			throw new WebAPIException(Response.Status.CONFLICT, "POLICIES_POLICY_NAME_ALREADY_EXISTS",
					"DUPLICATE_NAME", null, SessionState.getLocale(session), m_Base, "PolicyName", newPolicyName);
		}
		
		int newSchemaVersion = dal.getGlobalDataHandler().getDatabaseSchemaVersion();
		
		IPolicyHandler ph = dal.getPolicyHandler();
		iOsPolicies oldPolicyiOs = ph.getPolicy(policyId);

		if (null == oldPolicyiOs) {
			throw new NotFoundException("POLICIES_CANNOT_FIND_POLICY_FOR_UPDATE_POLICY", null, locale, m_Base, "PolicyId", policyId);
		}
		
		String oldPolicyName = oldPolicyiOs.getName();
		
		MDC.put("newPolicyName", newPolicyName);
		MDC.put("oldPolicyName", oldPolicyName);
		
		int newSeed = newPolicy.getSeed();
		int oldSeed = oldPolicyiOs.getSeed();
		if (oldSeed != newSeed) {
			throw new WebAPIException(Response.Status.CONFLICT, "POLICIES_THIS_POLICY_HAS_BEEN_MODIFIED_BY_ANOTHER_USER",
					"SEED_MISMATCH", null, locale, m_Base, "PolicyId", policyId);
		}
		int oldFilterType = oldPolicyiOs.getFilterType();
		int newFilterType = newPolicy.getFilterType();
		if (newFilterType != oldFilterType) {
			throw new BadRequestException("SMART_POLICIES_FILTER_TYPE_MISMATCH",
					new Object[]{policyId, oldFilterType, newFilterType}, 
					locale, m_Base);
		}
		if (null == oldPolicyiOs.getUniqueId()) {
			throw new NotFoundException("SMART_POLICIES_UPDATE_CANNOT_FIND_UNIQUE_ID", 
					new Object[] {oldPolicyiOs.getId()}, locale, m_Base);
		}
		
		PropertyList newFilterCriteria = createFilterCriteria(newPolicy);

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			CobraAdminMiscDatabaseCommand renameSmartPolicyCommand = CommandFactory.updateSmartPolicyCommand(
					SessionState.getAdminUUID(session),
					newFilterCriteria, 
					newFilterType, 
					newPolicyName, 
					newSchemaVersion, 
					newSeed, 
					PropertyList.fromByteArray(oldPolicyiOs.getFilterCriteria()),
					oldPolicyiOs.getFilterType(), 
					oldPolicyName, 
					oldPolicyiOs.getSchemaVersion(), 
					oldPolicyiOs.getSeed(),
					UUID.fromString(oldPolicyiOs.getUniqueId()));

			String contextMessage = ResourceUtilities.getLocalizedFormattedString(
				"SMART_POLICIES_UPDATE_FAILED", 
				null, 
				locale, m_Base);
			PropertyList response = amServerProtocol.sendCommandAndValidateResponse(
				renameSmartPolicyCommand, contextMessage);

				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
			Application.getSyncService().prioritySync( 
				SessionState.getSyncServiceSession(session), 
				response);				
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/webapi/Webapi");
			throw ex;
		} finally {
		    amServerProtocol.close();
		}
		
		MDC.remove("newPolicyName");
		MDC.remove("oldPolicyName");
		MDC.remove("policyId");
	}

	private PropertyList createFilterForMobileDevicesByInstalled(PropertyList originalFilter, String filterTable) {
		PropertyList synthesizedFilter = new PropertyList();
		ArrayList<Map<String, Object>> topLevelFilterArray = new ArrayList<Map<String, Object>>();
			
		// fetch containment operator (pre 5.5 filters were "missing XYZ" filters that did not contain this property, so default to "NOT IN")
		String containmentOperator = FILTER_OPERATOR_NOT_IN;
		if (originalFilter.containsKey(FILTER_CONTAINMENT_OPERATOR)) {
			containmentOperator = PropertyList.getElementAsString(originalFilter, FILTER_CONTAINMENT_OPERATOR);
		}
			
		// create subfilter filtering for agent IDs according to user editable filter (using subquery filter)
		PropertyList subfilter = new PropertyList();
		PropertyList subQueryFilter = new PropertyList();
		if (isNonTrivialAndFilter(originalFilter)) {
			// for non-trivial AND filters (i.e. AND filters with more than one entry), 
			// we have to construct an INTERSECT query to intersect the devices having each entry
			ArrayList<Map<String, Object>> filterElements = PropertyList.getElementAsArrayListMap(originalFilter, FILTER_COMPARE_VALUE);
			if (filterElements != null) {
				ArrayList<Map<String, Object>> intersectSubFilters = new ArrayList<Map<String, Object>>();
				for (int index = 0; index < filterElements.size(); index++) {
					Map<String, Object> element = filterElements.get(index);  
					if (element != null) {
						PropertyList intersectElement = new PropertyList();
						intersectElement.put(FILTER_DB_TABLE_NAME, filterTable);
						intersectElement.put(FILTER_DB_TABLE_COLUMN, FILTER_DB_TABLE_COLUMN_IPHONE_INFO_RECORD_ID);
						intersectElement.put(FILTER_COMPARE_VALUE, element);
						intersectElement.put(FILTER_COMPARE_SUB_QUERY_DISTINCT_VALUES, true);
						intersectSubFilters.add(intersectElement);
					}
				}
				subQueryFilter.put(FILTER_OPERATOR, FILTER_OPERATOR_INTERSECT);
				subQueryFilter.put(FILTER_COMPARE_VALUE, intersectSubFilters);
			}
		} else {
			subQueryFilter.put(FILTER_DB_TABLE_NAME, filterTable);
			subQueryFilter.put(FILTER_DB_TABLE_COLUMN, FILTER_DB_TABLE_COLUMN_IPHONE_INFO_RECORD_ID);
			subQueryFilter.put(FILTER_COMPARE_VALUE, originalFilter);
			subQueryFilter.put(FILTER_COMPARE_SUB_QUERY_DISTINCT_VALUES, true);
		}
		subfilter.put(FILTER_DB_TABLE_NAME, FILTER_DB_TABLE_NAME_IPHONE_INFO);
		subfilter.put(FILTER_DB_TABLE_COLUMN, FILTER_DB_TABLE_COLUMN_ID);
		subfilter.put(FILTER_OPERATOR, containmentOperator);
		subfilter.put(FILTER_COMPARE_SUB_FILTER, subQueryFilter);
		topLevelFilterArray.add(subfilter);
		
		synthesizedFilter.put(FILTER_OPERATOR, FILTER_OPERATOR_AND);
		synthesizedFilter.put(FILTER_COMPARE_VALUE, topLevelFilterArray);
		return synthesizedFilter;
	}

	
	private boolean isNonTrivialAndFilter(PropertyList inFilter) {
		boolean isNonTrivial = false;

		String operator = PropertyList.getElementAsString(inFilter, FILTER_OPERATOR);
		if (operator.compareToIgnoreCase(FILTER_OPERATOR_AND) == 0) {
			ArrayList<Map<String, Object>> compareValuesArray = PropertyList.getElementAsArrayListMap(inFilter, FILTER_COMPARE_VALUE);
			if (compareValuesArray != null && compareValuesArray.size() > 1) {
				isNonTrivial = true;
			}
		} 
		return isNonTrivial;
	}

	/**
	 * <p>Get the policy identified by {id}.<br/>
	 *    The response is a single row ResultSet with meta data. It will include the attributes Id, Policy Name, IsSmartPolicy and Policy Seed.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param policyId The given Policy Id
	 * @return Returns the attributes of a policy.
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	@GET @Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no such policy.")
		})
	public Result getPolicyForId(
			@Context UriInfo ui,
			@PathParam("id") String policyId) throws Exception  {

		MDC.put("policyId", policyId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null; 
		IDal dal = Application.getDal(session);

		ArrayList<String> viewParams = new ArrayList<String>();
		viewParams.add(policyId);
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_ONE_MOBILE_POLICY,
				null,
				viewParams,
				dbLocaleSuffix);
		
		if( result.getRows().length == 0){
			throw new NotFoundException("NO_POLICY_FOUND_FOR_ID", new Object[]{policyId}, locale, m_Base);
		}
		
		MDC.remove("policyId");
		
		return result;
	}
	
	/**
	 * <p>Get a list of actions associated with the policy identified by <strong>{id}</strong>. 
	 *    The response is a multi-row ResultSet with meta data. The following attributes are returned: 
	 *    Action Name, Action Type, Description, Supported Platforms, Initial Delay Seconds, Repeat Interval Seconds, 
	 *    and Repeat Count Number.
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param policyId The given Policy Id
	 * @return Returns the list of actions that are associated with this policy.
	 * @throws Exception
	 */
	@GET @Path("/{id}/actions")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no such policy.")
		})

	public Result getActionsForPolicyId(
			@Context UriInfo ui,
			@PathParam("id") long policyId) throws Exception {
		
		MDC.put("policyId", "getActionsForPolicyId:" + policyId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);

		Result result = null;
		IDal dal = Application.getDal(session);
		iOsPolicies policy = dal.getPolicyHandler().getPolicy(policyId);
		if (null == policy) {
			throw new NotFoundException("NO_POLICY_FOUND_FOR_ID",
					new Object[] { policyId }, locale, m_Base);
		}
		
		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(String.valueOf(policyId));
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_ACTIONS_FOR_POLICY, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);

		MDC.remove("policyId");
		return result;
	}
	
	/**
	 * <p>Get a list of devices associated with the policy identified by <strong>{id}</strong>. 
	 *    The response is a multi-row ResultSet with meta data. The following attributes are returned: 
	 *    Mobile Device Name, Mobile Device Model, Mobile Device OS Version, Mobile Device Serial Number, Mobile Device Last Contact Time,
	 *    Mobile Device OS Build Number, Mobile Device SIM ICC Identifier, Mobile Device IMEI.</p>
	 *    
	 *  <p>This only works for standard policies, smart polices are not supported yet.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param policyId The given Policy Id
	 * @return Returns the list of devices that are associated with this policy.
	 * @throws Exception
	 */
	@GET @Path("/{id}/devices")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "The policy does not exist.")
		})
	public Result getDevicesForPolicyId(
			@Context UriInfo ui,
			@PathParam("id") String policyId) throws Exception {

		//TODO: This only works for standard policies, smart polices are not supported yet.

		MDC.put("policyId", policyId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		iOsPolicies policy = dal.getPolicyHandler().getPolicy(Long.parseLong(policyId));
		
		if (null == policy) {
			throw new NotFoundException("NO_POLICY_FOUND_FOR_ID", new Object[]{policyId}, locale, m_Base);
		}
		
		ArrayList<String> viewParams = new ArrayList<String>();
		StringBuilder filterQuery = new StringBuilder();
		String view = VIEW_NAME_DEVICES_FOR_SMART_POLICY;
		
		// Check if the policy is of special type Unmanaged devices
		// or a standard policy
		// or a smart policy 
		if (policy.getFilterQuery() == null || policy.getFilterQuery().isEmpty()) {
			if (FILTER_TYPE_SMART_POLICY_UNMANAGED_DEVICES.equals(policy.getFilterType())) {
				//SPECIAL SMART POLICY "Unmanaged devices"
				filterQuery.append(SQL_GET_UNMANAGED_DEVICES_IDS);
				viewParams.add(filterQuery.toString());
			} else {
				//STANDARD POLICY
				viewParams.add(policyId);
				view = VIEW_NAME_DEVICES_FOR_NON_SMART_POLICY;
			}
			
			result = ViewHelper.getViewDetails(
						dal,
						view,
						ViewHelper.getQueryParameters(ui, session),
						viewParams,
						dbLocaleSuffix);
		} else {
			//SMART POLICY
			
			// TODO When we fully support smart policies this SQL will hopefully 
			// disappear. Don't use this example as a blueprint for a way forward.
			String policyQuery = policy.getFilterQuery();
			if (!policyQuery.contains("__iphone_info_id")) {
				throw new RuntimeException("policy.filterQuery does not contain __iphone_info_id.");
			}
			filterQuery.append("select __iphone_info_id as id from (");
			filterQuery.append(policy.getFilterQuery());
			filterQuery.append(")");
			viewParams.add(filterQuery.toString());
			result = ViewHelper.getViewDetails(
					dal,
					view,
					ViewHelper.getQueryParameters(ui, session),
					viewParams,
					dbLocaleSuffix);
		}
		
		MDC.remove("policyId");
		
		return result;
	}
		
	/**
	 * <p>Get a list of media files associated with this policy. The response is a multi-row ResultSet with meta data. 
	 *    The following attributes will be included in the result: Media Name, Assignment Rule, Size, Can Leave AbsoluteSafe, 
	 *    Media Type, Media Category, Media File Last Modified, Media Availability, Media Availability Start Time, Media Availability End Time.</p>
	 *    
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param policyId The given Policy Id
	 * @return Returns a list of media files associated with this policy.
	 * @throws Exception
	 */
	@GET @Path("/{id}/content")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "The policy does not exist.")
		})
	public Result getContentForPolicyId(
			@Context UriInfo ui,
			@PathParam("id") String policyId) throws Exception {
		
		MDC.put("policyId", policyId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);

		iOsPolicies policy = dal.getPolicyHandler().getPolicy(
				Long.parseLong(policyId));

		if (null == policy) {
			throw new NotFoundException("NO_POLICY_FOUND_FOR_ID",
					new Object[] { policyId }, locale, m_Base);
		}
		
		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(policyId);
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_CONTENT_FOR_POLICY, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);

		MDC.remove("policyId");
		
		return result;
	}
	
	/**
	 * <p>Get a list of configuration profiles associated with this policy. 
	 *    The response is a multi-row ResultSet with meta data. The following attributes will be included in the result: </p>
	 *    
	 * <pre>
	 * Profile Id
	 * Profile Name     
	 * Profile Description         
	 * Profile Organization       
	 * Profile Identifier  
	 * Profile Type             
	 * Profile Platform Type        
	 * Profile Platform Type Numeric
	 * Profile UUID      
	 * Allow Removal
	 * Assignment Rule
	 * Availability
	 * Availability Start Time
	 * Availability End Time.
	 * </pre>   
	 *    
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param policyId The given Policy Id
	 * @return Returns the configuration profiles that are associated with this policy.
	 * @throws Exception
	 */
	@GET @Path("/{id}/configurationprofiles")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "The policy does not exist.")
		})
	public Result getConfigProfilesForPolicyId(
			@Context UriInfo ui,
			@PathParam("id") String policyId) throws Exception {
		
		MDC.put("policyId", policyId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);
		
		iOsPolicies policy = dal.getPolicyHandler().getPolicy(Long.parseLong(policyId));
		
		if (null == policy) {
			throw new NotFoundException("NO_POLICY_FOUND_FOR_ID", new Object[]{policyId}, locale, m_Base);
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(policyId);
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_CONFIG_PROFILES_FOR_POLICY, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("policyId");
		
		return result;
	}

	
	/**
	 * <p>Get a list of in-house applications associated with this policy. 
	 *    The response is a multi-row ResultSet with meta data. The result set should include the same columns as in the Admin Console:</p>
	 *    
	 * <pre>
	 * App Id
	 * Mobile App Name
	 * Assignment Rule
	 * Platform  Type
	 * Platform Type Numeric
	 * Version
	 * Build Number 
	 * Size
	 * Short Description
	 * Bundle Identifier
	 * Min OS Version
	 * Universal
	 * Supported Devices
	 * iOS Provisioning Profile Name
	 * iOS Provisioning Profile Expiry Date
	 * Unique Id
	 * Seed
	 * Profile Unique Id
	 * Original File Name
	 * Display Name
	 * Binary Package MD5
	 * Binary Package Name
	 * Long Description
	 * Update Description
	 * Category
	 * Encryption Key
	 * Remove When MDM Is Removed
	 * Prevent App Data Backup
	 * </pre>   
	 *    
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param policyId The given Policy Id
	 * @return Returns the in-house applications that are associated with this policy.
	 * @throws Exception
	 */
	@GET @Path("/{id}/inhouseapps")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "The policy does not exist.")
		})
	public Result getInHouseAppsForPolicyId(
			@Context UriInfo ui,
			@PathParam("id") String policyId) throws Exception {
		
		MDC.put("policyId", policyId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);
		
		iOsPolicies policy = dal.getPolicyHandler().getPolicy(Long.parseLong(policyId));
		
		if (null == policy) {
			throw new NotFoundException("NO_POLICY_FOUND_FOR_ID", new Object[]{policyId}, locale, m_Base);
		}
		
		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(policyId);
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_INHOUSE_APPS_FOR_POLICY, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);

		MDC.remove("policyId");
		
		return result;
	}
	
	
	/**
	 * <p>Get a list of 3rd-party applications associated with this policy. 
	 *    The response is a multi-row ResultSet with meta data. The result set should include the following columns:</p>
	 *    
	 * <pre>
	 * App Id
	 * App Name
	 * Assignment Rule
	 * OS Platform  
	 * Platform Type
	 * Platform Type Numeric
	 * Category 
	 * Min OS Version
	 * Universal
	 * Supported Devices
	 * Short Description
	 * Prevent Data Backup
	 * Remove When MDM is Removed
	 * VPP Codes Purchased
	 * VPP Codes Redeemed
	 * VPP Codes Remaining
	 * Unique Id
	 * Seed
	 * VPP Order Number
	 * App Store Country
	 * App Store URL
	 * Long Description
	 * App Store Id
	 * </pre>   
	 *    
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param policyId The given Policy Id
	 * @return Returns the 3rd-party applications that are associated with this policy.
	 * @throws Exception
	 */
	@GET @Path("/{id}/thirdpartyapps")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "The policy does not exist.")
		})
	public Result getThirdPartyAppsForPolicyId(
			@Context UriInfo ui,
			@PathParam("id") String policyId) throws Exception {
		
		MDC.put("policyId", policyId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		Result result = null;
		IDal dal = Application.getDal(session);
		
		iOsPolicies policy = dal.getPolicyHandler().getPolicy(Long.parseLong(policyId));
		
		if (null == policy) {
			throw new NotFoundException("NO_POLICY_FOUND_FOR_ID", new Object[]{policyId}, locale, m_Base);
		}
		
		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(policyId);
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_THIRDPARTY_APPS_FOR_POLICY, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("policyId");
		
		return result;
	}

	/**
	 * <p>This request is used to create a new standard (i.e., not smart) policy.</p>
	 * 
	 * <p>Example – create a new standard policy:</p>
	 * <pre>
	 *{
	 * &emsp;"name":"My Policy"
	 *}
	 * </pre>
	 * 
	 *<p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 * 
	 * @param policy Policy
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/standard")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "The policy name is empty."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 409, condition = "A policy with the same name already exists. ")
		})
	public void createStandardPolicy(
			@Context HttpServletRequest req,
			Policy policy) throws Exception  {
		
		String policyName = policy.getName().trim();

		MDC.put("policyName", policyName);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (null == policyName || policyName.isEmpty()) {
			throw new BadRequestException("POLICIES_NO_POLICY_NAME_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		IDal dal = Application.getDal(session);
		if (0 != ResourceUtilities.getNonUniqueDataItemCount(SessionState.getLocaleDbSuffix(session), dal,
				policyName, VIEW_NAME_ALL_MOBILE_POLICIES, ColumnConstants.COLUMN_INFO_ITEM_ID_IOS_POLICIES_NAME)) {
			throw new WebAPIException(Response.Status.CONFLICT, "POLICIES_POLICY_NAME_ALREADY_EXISTS",
					"DUPLICATE_NAME", null, locale, m_Base);
		}
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			CobraAdminMiscDatabaseCommand createStandardPolicyCommand = CommandFactory.createStandardPolicyCommand(
				SessionState.getAdminUUID(session),
				policyName,
				SEED_FOR_NEW_OBJECT,
				UUID.randomUUID()
			);
				
			String contextMessage = ResourceUtilities.getLocalizedFormattedString(
						"POLICIES_CREATE_STANDARD_POLICY_FAILED", 
						null, 
						locale, m_Base);
				PropertyList response = amServerProtocol.sendCommandAndValidateResponse(
						createStandardPolicyCommand, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				Application.getSyncService().prioritySync( 
						SessionState.getSyncServiceSession(session), 
						response);				


		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/webapi/Webapi");
			throw ex;
		} finally {
		    amServerProtocol.close();
		}
		
		MDC.remove("policyName");
	}
	
	/**
	 * <p>Delete the policy identified by {id}.</p>
	 * 
	 *<p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 * 
	 * @param policyId The given Policy Id
	 * @return
	 * @throws Exception 
	 */
	@DELETE @Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 204, condition = "The policy was deleted, there is no content/body in the response."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "There is no policy with this id.")
		})
	public void deletePolicyForId(
			@Context UriInfo ui,
			@PathParam("id") String policyId) throws Exception {
		
		MDC.put("policyId", policyId);
		m_logger.debug("deletePolicyForId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		if (policyId.length() <= 0) {
			throw new BadRequestException("POLICIES_MUST_SPECIFY_POLICY_ID_TO_DELETE", null, locale, m_Base, "policyId", policyId);
		}
		IDal dal = Application.getDal(session);
		AMServerProtocol amServerProtocol = null;
		ArrayList<UUID> policyUuidArrLst = new ArrayList<UUID>();		
		
		try {
			//Convert int[] into UUID[]
			IPolicyHandler ph = dal.getPolicyHandler();
			iOsPolicies pol = ph.getPolicy(Long.valueOf(policyId).longValue());
			if (null == pol) {
				throw new NotFoundException("POLICIES_CANNOT_FIND_POLICY_FOR_DELETEPOLICY", 
					new Object[] {policyId}, locale, m_Base);
			}
			UUID policyUuid = UUID.fromString(pol.getUniqueId());
			policyUuidArrLst.add(policyUuid);
			
			amServerProtocol = deletePolicy(policyUuidArrLst, locale);
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/webapi/Webapi");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}
		
		MDC.remove("policyId");
	}
	
	/**
	 * <p>Delete a list of policies. The body of the DELETE is a list of ids. Here is an example:</p>
	 * 
	 * <pre>
	 *{
	 * &emsp;"policyIds":[1,2,3,4]
	 *}
	 * </pre>
	 * 
	 *<p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 * 
	 * @param policyId The given Policy Id
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "Policy Ids is empty or one or more of the policies do not exist (the existing policies do not get deleted)"),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void deletePolicyForIdList(
			PolicyList policyList) throws Exception {
		
		MDC.put("policyList", policyList.toString());		
		m_logger.debug("deletePolicyForIdList called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		int[] policyIds = policyList.getPolicyIds();
		if (policyIds.length <= 0) {
			throw new BadRequestException("POLICIES_MUST_SPECIFY_POLICY_IDS_TO_DELETE", null, locale, m_Base, "policyList", policyList);
		}
		IDal dal = Application.getDal(session);
		ArrayList<UUID> policyUuidArrLst = new ArrayList<UUID>();		
		AMServerProtocol amServerProtocol = null;
		
		try {
			//Convert int[] into UUID[]
			IPolicyHandler ph = dal.getPolicyHandler();
			for (int i = 0; i < policyIds.length; i++) {
				iOsPolicies pol = ph.getPolicy(policyIds[i]);
				if (null == pol) {
					throw new BadRequestException("POLICIES_CANNOT_FIND_POLICY_FOR_DELETEPOLICY", 
						new Object[] {policyIds[i]}, locale, m_Base);
				}
				UUID policyUuid = UUID.fromString(pol.getUniqueId());
				policyUuidArrLst.add(policyUuid);
			}
			
			amServerProtocol = deletePolicy(policyUuidArrLst, locale);
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/webapi/Webapi");
			throw ex;
		} finally {
			if (amServerProtocol != null) {
				amServerProtocol.close();
			}
		}
		
		MDC.remove("policyList");
	}
	
	/**
	 * <p>Update the name of the standard policy identified by {id}. 
	 *    The body of the POST is a JSON representation of the old (existing) and new policy attributes.</p>
	 * <p>Example body of POST:</p>
	 * <pre>
	 *{
	 * &emsp;"name":"MyNewPolicy",
	 * &emsp;"seed":1,
	 * &emsp;"id":123
	 *}
	 * </pre>
	 * 
	 * <p>The <strong>seed</strong> value is a version number for the object. If the object has been changed on the server since the client retrieved it,
	 *    the POST will fail with HTTP Status 409 Conflict.</p>
	 * 
	 *<p>Rights required:</br>
	 *    AllowModifyiOSPolicies </p>
	 * 
	 * @param policyId The given standard policy id
	 * @param newPolicy Policy
	 * @return
	 * @throws Exception 
	 */
	@POST @Path("/standard/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyiOSPolicies)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "The id in request body does not match the id in the URI."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "A policy with this {id} could not be found."),
		  @ResponseCode ( code = 409, condition = "A policy with the same name as in newPolicy already exists."),
		  @ResponseCode ( code = 409, condition = "The policy has been changed on the server since the client retrieved it.")
		})
	public void renameStandardPolicy(
			@Context HttpServletRequest req,
			@PathParam("id") long policyId,
			Policy newPolicy) throws Exception  {
		
		MDC.put("policyId", "" + policyId);
		m_logger.debug("Policies.renameStandardPolicy called");
		HttpSession session = m_servletRequest.getSession();
		IDal dal = Application.getDal(session);
		String locale = SessionState.getLocale(session);

		//id in the URI does not match id in request body
		if (newPolicy.getId() != policyId) {
			throw new WebAPIException(Response.Status.CONFLICT, "POLICIES_POLICY_ID_MISMATCH",
					"ID_MISMATCH", null, locale, m_Base, "PolicyId", policyId);
		}
		
		String newPolicyName = newPolicy.getName();
		if (null == newPolicyName || newPolicyName.isEmpty()) {
			throw new BadRequestException("POLICIES_NO_POLICY_NAME_SUPPLIED_WITH_REQUEST", null, locale, m_Base);
		}
		
		IPolicyHandler ph = dal.getPolicyHandler();
		if (0 != ResourceUtilities.getNonUniqueDataItemCount(SessionState.getLocaleDbSuffix(session), dal, 
				newPolicyName, VIEW_NAME_ALL_MOBILE_POLICIES, ColumnConstants.COLUMN_INFO_ITEM_ID_IOS_POLICIES_NAME)) {
			throw new WebAPIException(Response.Status.CONFLICT, "POLICIES_POLICY_NAME_ALREADY_EXISTS",
					"DUPLICATE_NAME", null, SessionState.getLocale(session), m_Base, "PolicyName", newPolicyName);
		}
		
		iOsPolicies oldPolicy = null;
		
		try { 
			oldPolicy = ph.getPolicy(policyId);
		} finally {	}

		if (null == oldPolicy) {
			throw new NotFoundException("POLICIES_CANNOT_FIND_POLICY_FOR_UPDATE_POLICY", null, locale, m_Base, "PolicyId", policyId);
		}
		
		int newSeed = newPolicy.getSeed();
		int oldSeed = oldPolicy.getSeed();
		if (oldSeed != newSeed) {
			throw new WebAPIException(Response.Status.CONFLICT, "POLICIES_THIS_POLICY_HAS_BEEN_MODIFIED_BY_ANOTHER_USER",
					"SEED_MISMATCH", null, locale, m_Base, "PolicyId", policyId);
		}
		
		String oldPolicyName = oldPolicy.getName();
		UUID policyUuid = UUID.fromString(oldPolicy.getUniqueId());

		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(m_servletRequest.getSession()));
		try {
			CobraAdminMiscDatabaseCommand renameStandardPolicyCommand = CommandFactory.renameStandardPolicyCommand(
					SessionState.getAdminUUID(session),
				    oldPolicyName,
				    oldSeed,
				    policyUuid,
				    newPolicyName,
				    newSeed,
				    policyUuid
			);
				
			String contextMessage = ResourceUtilities.getLocalizedFormattedString(
				"POLICIES_RENAME_STANDARD_POLICY_FAILED", 
				null, 
				locale, m_Base);
			PropertyList response = amServerProtocol.sendCommandAndValidateResponse(
				renameStandardPolicyCommand, contextMessage);
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
			Application.getSyncService().prioritySync( 
				SessionState.getSyncServiceSession(session), 
				response);				


		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/webapi/Webapi");
			throw ex;
		} finally {
		    amServerProtocol.close();
		}
		
		MDC.remove("policyId");
	}
	
	/**
	 * 3.7.103.7.9	/api/policies/{id}/books – GET
	 * TBD: This is not implemented.
	 * 
	 * Get a list of bookstore books associated with this policy. The response is a multi-row ResultSet with meta data. The result set should include the same columns as in the Admin Console:
	 * Book Title
	 * Category
	 * Short Description
	 * 
	 * Rights required:	
	 * AllowManageiOSDevices
	 * 
	 * Status Codes:
	 * 403 Forbidden: The user is not authorized to access this endpoint.
	 * 404 Not Found: the policy does not exist.

	 */
	
	/*
	 * Performs the actual deleting of a policy for deletePolicyForIdList() and deletePolicyForId
	 */
	private AMServerProtocol deletePolicy(
			ArrayList<UUID> policyUuidArrLst, 
			String locale)throws Exception {
		AMServerProtocol amServerProtocol = null;
		UUID[] policyUuids =  new UUID[policyUuidArrLst.size()];
		policyUuids = policyUuidArrLst.toArray(policyUuids);
		HttpSession session = m_servletRequest.getSession();
		
		CobraAdminMiscDatabaseCommand createDeletePoliciesCommand = CommandFactory.createDeletePoliciesCommand(
				SessionState.getAdminUUID(session),
				policyUuids);
		
		amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		
		String contextMessage = ResourceUtilities.getResourceStringForLocale("POLICIES_DELETE_POLICY_FAILED", m_Base, locale);
		PropertyList response = amServerProtocol.sendCommandAndValidateResponse(createDeletePoliciesCommand, contextMessage);

		// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
		Application.getSyncService().prioritySync( 
				SessionState.getSyncServiceSession(session), 
				response );
		
		return amServerProtocol;
	}
	
	private PropertyList createFilterCriteria(Policy policy) throws ParseException {
		PropertyList actualFilter = new PropertyList();
		
		// A passed in user editable filter may have native types ( ex. dates ) passed in as strings
		// We need to turn them into java objects so that they get transformed to XML correctly via PropertyList.objectToXML()
		policy.correctUserEditableFilterDates();
		PropertyList userEditableFilter = policy.getSmartPolicyUserEditableFilter();

		if (policy.getFilterType() == FILTER_TYPE_SMART_POLICY) {
			actualFilter = userEditableFilter;
		} else if (policy.getFilterType() == FILTER_TYPE_SMART_POLICY_BY_INSTALLED_APPS) {
			actualFilter = createFilterForMobileDevicesByInstalled(userEditableFilter, FILTER_TABLE_IPHONE_INSTALLED_SOFTWARE_INFO);
		} else if (policy.getFilterType() == FILTER_TYPE_SMART_POLICY_BY_INSTALLED_PROFILES) {
			actualFilter = createFilterForMobileDevicesByInstalled(userEditableFilter, FILTER_TABLE_IPHONE_INSTALLED_PROFILE_INFO);
		} else {
			throw new IllegalArgumentException("SmartPolicyUserEditableFilter missing CriteriaFieldType or ContainmentOperator");
		}

		PropertyList filterCriteria = new PropertyList();
		PropertyList mdmManagedDevice = new PropertyList();
		mdmManagedDevice.put(FILTER_COMPARE_VALUE, true);
		mdmManagedDevice.put(FILTER_INFO_ITEM_ID, FILTER_INFO_ITEM_ID_26B03C68);
		mdmManagedDevice.put(FILTER_OPERATOR, FILTER_OPERATOR_EQUALS);
		mdmManagedDevice.put(FILTER_USE_NATIVE_TYPE, true);

		ArrayList<Map<String, Object>> outterCompare = new ArrayList<Map<String, Object>>();
		outterCompare.add(actualFilter);
		outterCompare.add(mdmManagedDevice);

		filterCriteria.put(FILTER_COMPARE_VALUE, outterCompare);
		filterCriteria.put(FILTER_OPERATOR, "AND");
		filterCriteria.put(SMART_POLICY_USER_EDITABLE_FILTER, userEditableFilter);
		
		return filterCriteria;
	}
}
