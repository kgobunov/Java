package tools;

import java.io.IOException;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;

/**
 * 
 * Class return good url for login user
 * 
 * @author Maksim Stepanov
 * 
 */
public class ReturnURL {

	private boolean flag = true;

	private OMElement urlFinal = null;

	private String url = null;

	public ReturnURL(int type) throws OMException, IOException {

		switch (type) {

		case 0:

			while (this.flag) {

				if (WebAppContext.ready) {

					this.url = WebAppContext.getNextUrlKI();

					if (null != this.url
							&& !(Staff.errorServers.containsKey(this.url))) {

						Vector<String> vector = Staff.parsingUrl(this.url);

						if (CheckConn.checkSocket(vector)) {

							this.flag = false;

							OMFactory factory = OMAbstractFactory
									.getOMFactory();

							this.urlFinal = factory.createOMElement(new QName(
									"replyInfo"));

							OMElement urlReq = factory
									.createOMElement(new QName("url"));

							urlReq.setText(this.url);

							this.urlFinal.addChild(urlReq);
						}

					}

				}
			}

			break;

		case 1:

			while (this.flag) {

				if (WebAppContext.ready) {

					this.url = WebAppContext.getNextUrlUND();

					if (null != this.url
							&& !(Staff.errorServers.containsKey(this.url))) {

						Vector<String> vector = Staff.parsingUrl(this.url);

						if (CheckConn.checkSocket(vector)) {

							this.flag = false;

							OMFactory factory = OMAbstractFactory
									.getOMFactory();

							this.urlFinal = factory.createOMElement(new QName(
									"replyInfo"));

							OMElement urlReq = factory
									.createOMElement(new QName("url"));

							urlReq.setText(url);

							this.urlFinal.addChild(urlReq);
						}

					}

				}

			}

			break;

		}

		if (WebAppContext.debug) {

			WebAppContext.loggerInfo.info("Avaliable url: " + urlFinal);
		}

	}

	public final OMElement getUrl() {

		return this.urlFinal;
	}

}
