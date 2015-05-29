/**
 * ETSMInboundServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */
package sbrf.ru.neoflex.sbrf.intf.creditfactoryservice;

import java.util.ArrayList;
import java.util.Date;

import org.apache.axiom.om.OMElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tsm_methods.WebAppContext;

/**
 * ETSMInboundServiceSkeleton java skeleton for the axisService
 */
public class ETSMInboundServiceSkeleton {

	private static final Logger logger = LogManager
			.getLogger(ETSMInboundServiceSkeleton.class.getName());

	/**
	 * Auto generated method signature Запрос поступлений по
	 * номеру карты
	 * 
	 * @param requestSalaryForCard
	 * @return requestSalaryForCardResponse
	 */

	public OMElement requestSalaryForCard(OMElement requestSalaryForCard) {

		logger.trace("SalaryForCardRequest: " + requestSalaryForCard.toString());

		Date syncRequest = new Date();

		ArrayList<String> dataArray = new tsm_methods.RequestDataCard(
				requestSalaryForCard).getArrayList();

		init(dataArray);

		Date syncResponse = new Date();

		long syncDuration = syncResponse.getTime() - syncRequest.getTime();

		logger.debug("Number note: " + dataArray.get(0) + "; Duration: "
				+ syncDuration);

		return new tsm_methods.SyncResponseCard(dataArray).get();

	}

	/**
	 * Auto generated method signature Запрос поступлений по
	 * номеру счета
	 * 
	 * @param requestSalaryForAccount
	 * @return requestSalaryForAccountResponse
	 */

	public OMElement requestSalaryForAccount(OMElement requestSalaryForAccount) {

		logger.trace("SalaryForAccount: " + requestSalaryForAccount.toString());

		return null;
	}

	private void init(ArrayList<String> dataArray) {

		WebAppContext.ex.execute(new tsm_methods.StubExecutionCard(dataArray));

	}

}
