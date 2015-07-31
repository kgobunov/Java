/**
 * TSMBalancerSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */
package org.example.www.tsmbalancer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.axiom.om.OMElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tools.RequestData;
import tools.RequestErrorData;
import tools.ReturnURL;
import tools.Staff;

/**
 * TSMBalancerSkeleton java skeleton for the axisService
 */
public class TSMBalancerSkeleton {

	private static final Logger logger = LogManager
			.getLogger(TSMBalancerSkeleton.class.getName());

	/**
	 * Add bad url to black list
	 * 
	 * @param setBadUrl
	 *            - request
	 * @return - status
	 */
	public OMElement setBadUrl(OMElement setBadUrl) {

		RequestErrorData errorData = RequestErrorData.getInstance(setBadUrl);

		String server = errorData.getErrorServer();

		int type = errorData.getType();

		logger.info("Date: " + new Date() + ". Bad server: " + server
				+ "; Error code: " + type);

		Staff.errorServers.put(server, type);

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

		String serverWithDate = server + " - " + sdf.format(new Date());

		Staff.blackList.put(serverWithDate, type);

		return Staff.info(server, type);
	}

	/**
	 * Method returns url to server
	 * 
	 * @param getUrl
	 *            - request
	 * @return getUrlResponse - response
	 */

	public OMElement getUrl(OMElement getUrl) {

		int type = RequestData.getInstance(getUrl).getType();

		return ReturnURL.getInstance(type).getUrl();
	}

	/**
	 * Show list server in blacklist
	 * 
	 * @return showBlackListResponse
	 */

	public OMElement showBlackList() {

		return Staff.showBlackList();

	}

	/**
	 * Clearing black list
	 * 
	 * @author Maksim Stepanov
	 * 
	 * 
	 */

	public void clearBlackList() {

		Staff.clearBadUrl();

	}

}
