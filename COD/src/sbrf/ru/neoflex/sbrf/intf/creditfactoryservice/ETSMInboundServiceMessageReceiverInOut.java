/**
 * ETSMInboundServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */
package sbrf.ru.neoflex.sbrf.intf.creditfactoryservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ETSMInboundServiceMessageReceiverInOut message receiver
 */

public class ETSMInboundServiceMessageReceiverInOut extends
		org.apache.axis2.receivers.AbstractInOutMessageReceiver {

	private static final Logger logger = LogManager
			.getLogger(ETSMInboundServiceMessageReceiverInOut.class.getName());

	public void invokeBusinessLogic(
			org.apache.axis2.context.MessageContext msgContext,
			org.apache.axis2.context.MessageContext newMsgContext)
			throws org.apache.axis2.AxisFault {

		try {

			// get the implementation class for the Web Service
			Object obj = getTheImplementationObject(msgContext);

			ETSMInboundServiceSkeleton skel = (ETSMInboundServiceSkeleton) obj;
			// Out Envelop
			org.apache.axiom.soap.SOAPEnvelope envelope = null;
			// Find the axisOperation that has been set by the Dispatch phase.
			org.apache.axis2.description.AxisOperation op = msgContext
					.getOperationContext().getAxisOperation();
			if (op == null) {
				throw new org.apache.axis2.AxisFault(
						"Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
			}

			java.lang.String methodName;
			if ((op.getName() != null)
					&& ((methodName = org.apache.axis2.util.JavaUtils
							.xmlNameToJavaIdentifier(op.getName()
									.getLocalPart())) != null)) {

				if ("requestSalaryForCard".equals(methodName)) {

					org.apache.axiom.om.OMElement requestSalaryForCardResponse1 = null;
					org.apache.axiom.om.OMElement wrappedParam = (org.apache.axiom.om.OMElement) fromOM(
							msgContext.getEnvelope().getBody()
									.getFirstElement(),
							org.apache.axiom.om.OMElement.class,
							getEnvelopeNamespaces(msgContext.getEnvelope()));

					requestSalaryForCardResponse1 =

					skel.requestSalaryForCard(wrappedParam);

					logger.trace("SalaryForCardResponse: "
							+ requestSalaryForCardResponse1.toString());

					envelope = toEnvelope(
							getSOAPFactory(msgContext),
							requestSalaryForCardResponse1,
							false,
							new javax.xml.namespace.QName(
									"http://sbrf/ru/neoflex/sbrf/intf/CreditFactoryService",
									"requestSalaryForCard"));
				} else

				if ("requestSalaryForAccount".equals(methodName)) {

					org.apache.axiom.om.OMElement requestSalaryForAccountResponse3 = null;
					org.apache.axiom.om.OMElement wrappedParam = (org.apache.axiom.om.OMElement) fromOM(
							msgContext.getEnvelope().getBody()
									.getFirstElement(),
							org.apache.axiom.om.OMElement.class,
							getEnvelopeNamespaces(msgContext.getEnvelope()));

					requestSalaryForAccountResponse3 =

					skel.requestSalaryForAccount(wrappedParam);

					logger.trace("SalaryForAccountResponse: "
							+ requestSalaryForAccountResponse3.toString());

					envelope = toEnvelope(
							getSOAPFactory(msgContext),
							requestSalaryForAccountResponse3,
							false,
							new javax.xml.namespace.QName(
									"http://sbrf/ru/neoflex/sbrf/intf/CreditFactoryService",
									"requestSalaryForAccount"));

				} else {
					throw new java.lang.RuntimeException("method not found");
				}

				newMsgContext.setEnvelope(envelope);
			}
		} catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	//
	private org.apache.axiom.om.OMElement fromOM(
			org.apache.axiom.om.OMElement param, java.lang.Class type,
			java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault {
		return param;
	}

	private org.apache.axiom.om.OMElement toOM(
			org.apache.axiom.om.OMElement param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault {
		return param;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
			org.apache.axiom.soap.SOAPFactory factory,
			org.apache.axiom.om.OMElement param, boolean optimizeContent,
			javax.xml.namespace.QName methodQName)
			throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory
				.getDefaultEnvelope();
		envelope.getBody().addChild(param);
		return envelope;
	}

	/**
	 * get the default envelope
	 */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
			org.apache.axiom.soap.SOAPFactory factory)
			throws org.apache.axis2.AxisFault {
		return factory.getDefaultEnvelope();
	}

	/**
	 * A utility method that copies the namepaces from the SOAPEnvelope
	 */
	private java.util.Map getEnvelopeNamespaces(
			org.apache.axiom.soap.SOAPEnvelope env) {
		java.util.Map returnMap = new java.util.HashMap();
		java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
		while (namespaceIterator.hasNext()) {
			org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator
					.next();
			returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
		}
		return returnMap;
	}

	private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
		org.apache.axis2.AxisFault f;
		Throwable cause = e.getCause();
		if (cause != null) {
			f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
		} else {
			f = new org.apache.axis2.AxisFault(e.getMessage());
		}

		return f;
	}

}// end of class
