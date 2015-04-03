package tools;

import static tools.WebAppContext.loggerInfoBlack;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

/**
 * Staff functions.
 * 
 * @author Maksim Stepanov
 * 
 */
public class Staff {

	public static final ConcurrentHashMap<String, Integer> errorServers = new ConcurrentHashMap<String, Integer>();

	public static final ConcurrentHashMap<String, Integer> blackList = new ConcurrentHashMap<String, Integer>();

	private Staff() {
	}

	/**
	 * Parsing url for two component ip and port
	 * 
	 * @param url
	 *            for parsing
	 * @return array with ip and port
	 */
	public static Vector<String> parsingUrl(String url) {

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

		int blackListSize = blackList.size();

		int errorServersSize = errorServers.size();

		if (blackListSize > 0 && errorServersSize > 0) {

			ReturnURL.clearList.set(true);

			loggerInfoBlack.info("BlackList size before clearing: "
					+ blackListSize);

			loggerInfoBlack.info("ErrorServers size before clearing: "
					+ errorServersSize);

			blackList.clear();

			errorServers.clear();

			loggerInfoBlack.info("BlackList cleared in " + new Date());

			loggerInfoBlack.info("ErrorsServers cleared in " + new Date());
			
			blackListSize = blackList.size();

			errorServersSize = errorServers.size();

			loggerInfoBlack.info("BlackList size after clearing: "
					+ blackListSize);

			loggerInfoBlack.info("Hash size after clearing: "
					+ errorServersSize);

			ReturnURL.clearList.set(false);

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

	public static boolean checkSocket(Vector<String> data) {

		boolean avaliable = false;

		String ip = data.get(1);

		int port = Integer.parseInt(data.get(0));

		Socket sc = new Socket();

		try {

			sc.connect(new InetSocketAddress(ip, port), 500);

			avaliable = true;

			if (WebAppContext.debug) {

				WebAppContext.loggerInfo.info("Socket opened successfully");

			}

		} catch (IOException e) {

			WebAppContext.loggerSevere.severe("Can't connect to " + ip + ":"
					+ port);

			e.printStackTrace();

		} finally {

			try {

				sc.close();

				WebAppContext.loggerInfo.info("Socket closed successfully!");

			} catch (IOException e) {

				WebAppContext.loggerSevere.severe("Cann't closed socket!");

				e.printStackTrace();
			}

			sc = null;
		}

		return avaliable;

	}

}
