/**
 * TOOLSSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */
package com.aplana.ru.tools;

import java.io.UnsupportedEncodingException;

import org.apache.axiom.om.OMElement;

import tools.ActionParse;
import tools.ResponseAction;
import tools.ResponseInfoVIP;

/**
 * TOOLSSkeleton java skeleton for the axisService
 */
public class TOOLSSkeleton {

	/**
	 * Auto generated method signature
	 * 
	 * @param getInfoForCardVIP
	 * @return getInfoForCardVIPResponse
	 */

	public OMElement getInfoForCardVIP(OMElement getInfoForCardVIP)
			throws UnsupportedEncodingException {

		return new ResponseInfoVIP().getResponse();
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param getAction
	 * @return getActionResponse
	 * @throws UnsupportedEncodingException 
	 */

	public OMElement getAction(OMElement getAction) throws UnsupportedEncodingException {

		
		String product = ActionParse.getProduct(getAction);
		
		return ResponseAction.getResponse(product);
		
	}

}
