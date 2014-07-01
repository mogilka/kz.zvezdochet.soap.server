package kz.zvezdochet.soap.server;
import java.util.Iterator;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SAAJResult;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;

import kz.zvezdochet.soap.server.jaxws.Calculate;

public class CalcHandler {

	private static final String NAMESPACE_URI = "http://server.soap.zvezdochet.kz/";
	private static final QName CALCULATE_QNAME = new QName(NAMESPACE_URI, "calculate");

	private MessageFactory messageFactory;
	private CalcAdapter adapter;

	public CalcHandler() throws SOAPException {
		messageFactory = MessageFactory.newInstance();
		adapter = new CalcAdapter();
	}

	public SOAPMessage handleSOAPRequest(SOAPMessage request) throws SOAPException {
		SOAPMessage soapResponse = null;
		try {
			SOAPBody soapBody = request.getSOAPBody();
			Iterator iterator = soapBody.getChildElements();
			Object responsePojo = null;
			while (iterator.hasNext()) {
				Object next = iterator.next();
				if (next instanceof SOAPElement) {
					SOAPElement soapElement = (SOAPElement)next;
					QName qname = soapElement.getElementQName();
					if (CALCULATE_QNAME.equals(qname)) {
						responsePojo = handleRequest(soapElement);
						break;
					}
				}
			}
			soapResponse = messageFactory.createMessage();
			soapBody = soapResponse.getSOAPBody();
			if (responsePojo != null) {
				JAXB.marshal(responsePojo, new SAAJResult(soapBody));
			} else {
				SOAPFault fault = soapBody.addFault();
				fault.setFaultString("Unrecognized SOAP request.");
			}
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
		return soapResponse;
	}

	private Object handleRequest(SOAPElement soapElement) {
		try {
			Calculate request = JAXB.unmarshal(new DOMSource(soapElement), Calculate.class);
			return adapter.calculate(request);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
		return null;
	}
}