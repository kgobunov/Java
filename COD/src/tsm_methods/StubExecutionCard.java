package tsm_methods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.axiom.om.OMElement;

/**
 * Sending async response to MQ
 * 
 * @author Maksim Stepanov
 * 
 */
public class StubExecutionCard implements Runnable {

	private boolean stopped = true;

	private ArrayList<String> dataArray;

	private String name = null;

	private String tsmId = null;

	public StubExecutionCard(ArrayList<String> dataArray) {

		this.dataArray = dataArray;

		if (WebAppContext.debug) {

			String info = "DEBUG [INFO]: COD_dataArray: ";

			for (String s : this.dataArray) {

				info += s + " ";

			}

			System.out.println(info);
		}

		if (WebAppContext.debug) {

			System.out.println("DEBUG [INFO]: COD_CARD_delay: "
					+ WebAppContext.delay);

			System.out.println("DEBUG [INFO]: COD_CARD_sys_name: "
					+ WebAppContext.sysName);

		}

		this.name = String.valueOf(Thread.currentThread().getId());

		this.tsmId = this.dataArray.get(0);

		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
				.format(new Date())
				+ "["
				+ WebAppContext.sysName
				+ "Stub] Thread{" + this.name + "}" + " is created.");

	}

	public void run() {

		WebAppContext.currentExecutableThreadCounter.getAndIncrement();

		Runtime r = Runtime.getRuntime();

		while (stopped) {

			Date startTime = new Date();

			System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
					.format(startTime)
					+ "["
					+ WebAppContext.sysName
					+ "Stub] Thread{"
					+ this.name
					+ "}"
					+ " is started. MsgId: "
					+ this.tsmId
					+ " Threads: "
					+ WebAppContext.currentExecutableThreadCounter.get()
					+ " Free memory: " + r.freeMemory());

			// генерируем по dataArray (необходимые данные из запроса) item1
			String busResponse = new AsyncResponseCard(this.dataArray).get();

			// создаем для item1 - обертку (формат запроса OUG)
			OMElement ougRequest = OUGFormat.createElement(
					WebAppContext.sysName, busResponse);

			// delay.

			try {
				Thread.sleep(WebAppContext.delay);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			try {

				try {

					MQTools.sendMessage(ougRequest.toString());

					System.out
							.println(new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss.SSS")
									.format(new Date())
									+ ". Message put successfully. TSMId: "
									+ this.tsmId
									+ ". System: "
									+ WebAppContext.sysName);

					if (WebAppContext.debug) {

						System.out.println(new Date() + ". Message: "
								+ busResponse);

					}

				} catch (NumberFormatException e2) {

					System.err.println("NumberFormatException: "
							+ e2.getMessage());

					e2.printStackTrace();

				}

			} catch (Exception e2) {

				System.err.println("Exception: " + e2.getMessage());

				e2.printStackTrace();
			}

			shutdown();

		}

		if (WebAppContext.currentExecutableThreadCounter.get() > 500) {

			r.gc();

			WebAppContext.currentExecutableThreadCounter.set(0);
		}

	}

	private final void shutdown() {

		stopped = false;
	}

}
