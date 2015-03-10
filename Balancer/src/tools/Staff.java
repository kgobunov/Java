package tools;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

/**
 * Class include staff.
 * 
 * @author Maksim Stepanov
 * 
 */
public class Staff {

	public static HashMap<String, Integer> errorServers = new HashMap<String, Integer>();

	public static HashMap<String, Integer> blackList = new HashMap<String, Integer>();

	/**
	 * Parsing url for two component ip and port
	 * 
	 * @param url
	 *            for parsing
	 * @return array with ip and port
	 */
	public static final Vector<String> parsingUrl(String url) {

		Vector<String> data = new Vector<String>(2);

		String port = new String(url.substring(url.length() - 3, url.length()));

		data.addElement(port);

		String ip = new String(url.substring(8, url.length() - 4));

		data.addElement(ip);

		return data;

	}

	/**
	 * Info about bad server for soap response
	 * 
	 * @param server
	 *            - bad server
	 * @param type
	 *            - reason code
	 * @return tag with description about error
	 */
	public static OMElement info(String server, int type) {

		OMFactory factory = OMAbstractFactory.getOMFactory();

		OMElement replyInfo = factory.createOMElement(new QName("replyInfo"));

		OMElement info = factory.createOMElement(new QName("info"));

		info.setText("Server: " + server + "; "
				+ "remove from balancing for reason code: " + type);

		replyInfo.addChild(info);

		return replyInfo;
	}

	/**
	 * Clear bad url from hash
	 * 
	 * @author Maksim Stepanov
	 */
	public static void clearBadUrl() {

		if (Staff.blackList.size() > 0) {

			WebAppContext.loggerInfoBlack.info("Hash size before clearing: "
					+ Staff.blackList.size());

			Staff.blackList.clear();

			WebAppContext.loggerInfoBlack.info("Black list cleared in "
					+ new Date());

			WebAppContext.loggerInfoBlack.info("Hash size after clearing: "
					+ Staff.blackList.size());

		}
	}

	/**
	 * 
	 * Show blackList
	 * 
	 * @return blacklist servers
	 */
	public static OMElement showBlackList() {

		OMFactory factory = OMAbstractFactory.getOMFactory();

		OMElement servers = factory.createOMElement(new QName("Servers"));

		if (blackList.size() != 0) {

			factory = OMAbstractFactory.getOMFactory();

			servers = factory.createOMElement(new QName("Servers"));

			Set<Map.Entry<String, Integer>> values = blackList.entrySet();

			for (Map.Entry<String, Integer> entry : values) {

				OMElement server = factory.createOMElement(new QName("server"));

				OMElement serverName = factory
						.createOMElement(new QName("name"));

				serverName.setText(entry.getKey());

				OMElement errorCode = factory.createOMElement(new QName(
						"errorCode"));

				errorCode.setText(String.valueOf(entry.getValue()));

				server.addChild(serverName);

				server.addChild(errorCode);

				servers.addChild(server);

			}

		} else {

			OMElement notFound = factory.createOMElement(new QName("notFound"));

			notFound.setText("Blacklist is empty!");

			servers.addChild(notFound);

		}

		return servers;

	}

}
