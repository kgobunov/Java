package tools;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Vector;

/**
 * Checks available server by socket
 * 
 * @author Maksim Stepanov
 *
 */
public class CheckConn {

	public static final boolean checkSocket(Vector<String> data) {

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

			WebAppContext.loggerSevere.severe("Can't connect to " + ip + ":" + port);
			
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
