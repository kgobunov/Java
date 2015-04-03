package tools;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

/**
 * 
 * Return avaliable url for login user
 * 
 * @author Maksim Stepanov
 * 
 */
public class ReturnURL {

	private final int type;

	private static Lock lockKI = new ReentrantLock();

	private static Lock lockUW = new ReentrantLock();

	public static AtomicBoolean clearList = new AtomicBoolean(false);

	private ReturnURL(int type) {

		this.type = type;

	}

	public static ReturnURL getInstance(int type) {

		return new ReturnURL(type);

	}

	@SuppressWarnings("static-access")
	public final OMElement getUrl() {

		long time = System.currentTimeMillis();

		boolean flag = true;

		OMElement urlFinal = null;

		String url = "";

		OMFactory factory = OMAbstractFactory.getOMFactory();

		switch (this.type) {

		case 0:

			int countTryKI = 1;

			while (flag) {

				if (WebAppContext.ready.get()) {

					this.lockKI.lock();

					try {

						url = WebAppContext.getNextUrlKI();

					} finally {

						this.lockKI.unlock();

					}

					if (null != url && !(clearList.get())
							&& !(Staff.errorServers.containsKey(url))) {

						Vector<String> vector = Staff.parsingUrl(url);

						if (Staff.checkSocket(vector)) {

							flag = false;

							urlFinal = factory.createOMElement(new QName(
									"replyInfo"));

							OMElement urlReq = factory
									.createOMElement(new QName("url"));

							urlReq.setText(url);

							urlFinal.addChild(urlReq);

						}

					} else {

						if (countTryKI == WebAppContext.sizeKI) {

							flag = false;

							urlFinal = factory.createOMElement(new QName(
									"replyInfo"));

							OMElement urlReq = factory
									.createOMElement(new QName("url"));

							urlReq.setText("all urls ki in error list");

							urlFinal.addChild(urlReq);

							WebAppContext.loggerSevere
									.severe("All urls ki in error list!");

						} else {

							if (Staff.errorServers.containsKey(url)) {

								countTryKI++;

							}
						}
					}

				}

				if ((System.currentTimeMillis() - time) > 100000) {

					flag = false;

					urlFinal = factory.createOMElement(new QName("replyInfo"));

					OMElement urlReq = factory
							.createOMElement(new QName("url"));

					urlReq.setText("Step download timeout error!");

					urlFinal.addChild(urlReq);

					WebAppContext.loggerSevere
							.severe("Step download timeout error!");

				}

			}

			break;

		case 1:

			int countTryUW = 1;

			while (flag) {

				if (WebAppContext.ready.get()) {

					this.lockUW.lock();

					try {

						url = WebAppContext.getNextUrlUND();

					} finally {

						this.lockUW.unlock();

					}

					if (null != url && !(clearList.get())
							&& !(Staff.errorServers.containsKey(url))) {

						Vector<String> vector = Staff.parsingUrl(url);

						if (Staff.checkSocket(vector)) {

							flag = false;

							urlFinal = factory.createOMElement(new QName(
									"replyInfo"));

							OMElement urlReq = factory
									.createOMElement(new QName("url"));

							urlReq.setText(url);

							urlFinal.addChild(urlReq);

						}

					} else {

						if (countTryUW == WebAppContext.sizeUND) {

							flag = false;

							urlFinal = factory.createOMElement(new QName(
									"replyInfo"));

							OMElement urlReq = factory
									.createOMElement(new QName("url"));

							urlReq.setText("all urls uw in error list");

							urlFinal.addChild(urlReq);

							WebAppContext.loggerSevere
									.severe("All urls uw in error list!");

						} else {

							if (Staff.errorServers.containsKey(url)) {

								countTryUW++;

							}

						}
					}

				}

				if ((System.currentTimeMillis() - time) > 100000) {

					flag = false;

					urlFinal = factory.createOMElement(new QName("replyInfo"));

					OMElement urlReq = factory
							.createOMElement(new QName("url"));

					urlReq.setText("Step download timeout error!");

					urlFinal.addChild(urlReq);

					WebAppContext.loggerSevere
							.severe("Step download timeout error!");

				}

			}

			break;

		}

		if (WebAppContext.debug) {

			WebAppContext.loggerInfo.info("Avaliable url: " + urlFinal);
		}

		return urlFinal;

	}

}
