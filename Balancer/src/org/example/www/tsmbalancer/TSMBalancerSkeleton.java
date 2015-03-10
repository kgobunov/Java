/**
 * TSMBalancerSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */
package org.example.www.tsmbalancer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;

import tools.RequestData;
import tools.RequestErrorData;
import tools.ReturnURL;
import tools.Staff;
import tools.WebAppContext;

/**
 * TSMBalancerSkeleton java skeleton for the axisService
 */
public class TSMBalancerSkeleton {

	/**
	 * Add bad url to black list
	 * 
	 * @param setBadUrl
	 *            - request
	 * @return - status
	 */
	public OMElement setBadUrl(OMElement setBadUrl) {

		RequestErrorData errorData = new RequestErrorData(setBadUrl);

		String server = errorData.getErrorServer();

		int type = errorData.getType();

		if (WebAppContext.debug) {

			WebAppContext.loggerInfoBlack.info("Date: " + new Date()
					+ ". Bad server: " + server + "; Error code: " + type);

		}

		Staff.errorServers.put(server, type);
		
		SimpleDateFormat sdf = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm");
		
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

	public OMElement getUrl(OMElement getUrl) throws OMException, IOException {

		int type = new RequestData(getUrl).getType();

		return new ReturnURL(type).getUrl();
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
