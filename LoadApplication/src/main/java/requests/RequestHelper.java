package requests;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;

import ru.aplana.app.Initialization;

/**
 * Classname: RequestHelper
 * 
 * Version: 1.1
 * 
 * 
 * Requests initialization
 * 
 * @author Maksim Stepanov
 * 
 */
public class RequestHelper {

	private static ConcurrentHashMap<String, String> requests = new ConcurrentHashMap<String, String>();

	/**
	 * Return request message
	 * 
	 * @param path
	 *            to file with response
	 * @throws IOException
	 */

	public static String getRequest(String path) {

		String request = null;

		request = requests.get(path);

		if (null == request) {

			request = "BAD MESSAGE!";

		}

		return request;

	}

	/**
	 * 
	 * Read request message from file/s
	 * 
	 * @param sysname
	 * @param files
	 */
	public static void addRequest(String sysname, String[] files) {

		for (int i = 0; i < files.length; i++) {

			try {

				String request = FileUtils.readFileToString(new File("emuls\\"
						+ sysname + "\\" + files[i]), "UTF-8");

				requests.put(files[i], request);

				Initialization.info.info("Key: " + files[i] + "; Value: "
						+ request);

			} catch (IOException e) {

				Initialization.severe.severe("Can't read file! Error: "
						+ e.getMessage());

				e.printStackTrace();
			}

		}

	}

}
