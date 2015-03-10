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

import tsm_methods.WebAppContext;

/**
 * ETSMInboundServiceSkeleton java skeleton for the axisService
 */
public class ETSMInboundServiceSkeleton {

	/**
	 * Auto generated method signature Запрос поступлений по
	 * номеру карты
	 * 
	 * @param requestSalaryForCard
	 * @return requestSalaryForCardResponse
	 */

	public OMElement requestSalaryForCard(OMElement requestSalaryForCard,
			long thread_id) {

		if (WebAppContext.debug && WebAppContext.loggerIncome != null) {

			WebAppContext.loggerIncome.fine("Thread[" + thread_id + "] - "
					+ "requestSalaryForCard_COD: " + requestSalaryForCard);

		}

		Date syncRequest = new Date();

		ArrayList<String> dataArray = new tsm_methods.RequestDataCard(
				requestSalaryForCard).getArrayList();

		init(dataArray);

		Date syncResponse = new Date();

		long syncDuration = syncResponse.getTime() - syncRequest.getTime();

		System.out.println("Number note: " + dataArray.get(0) + "; "
				+ "Duration:" + syncDuration);

		return new tsm_methods.SyncResponseCard(dataArray).get();

	}

	/**
	 * Auto generated method signature Запрос поступлений по
	 * номеру счета
	 * 
	 * @param requestSalaryForAccount
	 * @return requestSalaryForAccountResponse
	 */

	public OMElement requestSalaryForAccount(OMElement requestSalaryForAccount,
			long thread_id) {

		if (WebAppContext.debug && WebAppContext.loggerIncome != null) {

			WebAppContext.loggerIncome
					.fine("Thread[" + thread_id + "] - "
							+ "requestSalaryForAccount_COD: "
							+ requestSalaryForAccount);

		}

		return null;
	}

	public void init(ArrayList<String> dataArray) {

		WebAppContext.ex.submit(new tsm_methods.StubExecutionCard(dataArray));

	}

}
