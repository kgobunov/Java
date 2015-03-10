
/**
 * TSMBalancerMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.0  Built on : May 17, 2011 (04:19:43 IST)
 */
        package org.example.www.tsmbalancer;

        /**
        *  TSMBalancerMessageReceiverInOut message receiver
        */

        public class TSMBalancerMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver{


        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(msgContext);

        TSMBalancerSkeleton skel = (TSMBalancerSkeleton)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        java.lang.String methodName;
        if((op.getName() != null) && ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(op.getName().getLocalPart())) != null)){


        

            if("setBadUrl".equals(methodName)){
                
                org.apache.axiom.om.OMElement setBadUrlResponse3 = null;
	                        org.apache.axiom.om.OMElement wrappedParam =
                                                             (org.apache.axiom.om.OMElement)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    org.apache.axiom.om.OMElement.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               setBadUrlResponse3 =
                                                   
                                                   
                                                         skel.setBadUrl(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), setBadUrlResponse3, false, new javax.xml.namespace.QName("http://www.example.org/TSMBalancer/",
                                                    "setBadUrl"));
                                    } else 

            if("getUrl".equals(methodName)){
                
                org.apache.axiom.om.OMElement getUrlResponse5 = null;
	                        org.apache.axiom.om.OMElement wrappedParam =
                                                             (org.apache.axiom.om.OMElement)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    org.apache.axiom.om.OMElement.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               getUrlResponse5 =
                                                   
                                                   
                                                         skel.getUrl(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), getUrlResponse5, false, new javax.xml.namespace.QName("http://www.example.org/TSMBalancer/",
                                                    "getUrl"));
                                    } else 

            if("showBlackList".equals(methodName)){
                
                org.apache.axiom.om.OMElement showBlackListResponse7 = null;
	                        showBlackListResponse7 =
                                                     
                                                 skel.showBlackList()
                                                ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), showBlackListResponse7, false, new javax.xml.namespace.QName("http://www.example.org/TSMBalancer/",
                                                    "showBlackList"));
                                    
            } else {
              throw new java.lang.RuntimeException("method not found");
            }
        

        newMsgContext.setEnvelope(envelope);
        }
        }
        catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
        }
        
        //
        private  org.apache.axiom.om.OMElement fromOM(
        org.apache.axiom.om.OMElement param,
        java.lang.Class type,
        java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault {
        return param;
        }

        private  org.apache.axiom.om.OMElement  toOM(org.apache.axiom.om.OMElement param, boolean optimizeContent) throws org.apache.axis2.AxisFault {
        return param;
        }

        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, org.apache.axiom.om.OMElement param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
        throws org.apache.axis2.AxisFault {
        org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
        envelope.getBody().addChild(param);
        return envelope;
        }

        /**
        *  get the default envelope
        */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory)
        throws org.apache.axis2.AxisFault {
        return factory.getDefaultEnvelope();
        }

    

        /**
        *  A utility method that copies the namepaces from the SOAPEnvelope
        */
        private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
        org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
        returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
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

        }//end of class
    