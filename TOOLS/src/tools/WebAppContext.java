package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Application Lifecycle Listener implementation
 * 
 * @author Maksim Stepanov
 */
public class WebAppContext implements ServletContextListener {

	private static final Logger logger = LogManager
			.getLogger(WebAppContext.class.getName());

	public static MultiThreadedHttpConnectionManager connManager;

	public static Properties properties = null;

	// Under arrays
	private static Vector<String> arrayUnderSbolDoverie;

	private static Vector<String> arrayUnderSurrogat;

	private static Vector<String> arrayUnderOferta;

	private static Vector<String> arrayUnderDoverie;

	private static Vector<String> arrayUnderCRM;

	private static Vector<String> arrayUnderPSO;

	private static Vector<String> arrayUnderPSOOferta;

	private static Vector<String> arrayUnderAuto;

	private static Vector<String> arrayUnderCard;

	private static Vector<String> arrayUnderVIP;

	private static Vector<String> arrayUnderDeb;

	private static Vector<String> arrayUnderMortgage;

	private static Vector<String> arrayUnderMortgage2D;

	private static Vector<String> arrayUnderPBO;

	private static Vector<String> arrayUnderPBOSbol;

	private static Vector<String> arrayUnderPBOSbolOferta;
	// KI arrays
	private static Vector<String> arrayKIPBO;

	private static Vector<String> arrayKIPBOOferta;

	private static Vector<String> arrayKIPSO;

	private static Vector<String> arrayKIPSOOferta;

	private static Vector<String> arrayKIAuto;

	private static Vector<String> arrayKICard;

	private static Vector<String> arrayKIPBODoverie;

	private static Vector<String> arrayKIMortgage2D;

	private static Vector<String> arrayKIVIP;

	private static Vector<String> arrayKIDeb;

	private static AtomicInteger callCounterSbolUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterSurrogatUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterOfertaUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterDoverieUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterCRMUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterKIPBO = new AtomicInteger(0);

	private static AtomicInteger callCounterKIPBOOferta = new AtomicInteger(0);

	private static AtomicInteger callCounterKIPBODoverie = new AtomicInteger(0);

	private static AtomicInteger callCounterKIPSO = new AtomicInteger(0);

	private static AtomicInteger callCounterKIPSOOferta = new AtomicInteger(0);

	private static AtomicInteger callCounterKIAuto = new AtomicInteger(0);

	private static AtomicInteger callCounterKICard = new AtomicInteger(0);

	private static AtomicInteger callCounterKIMortgage2D = new AtomicInteger(0);

	private static AtomicInteger callCounterAutoUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterVIPUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterDebUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterCardUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterMortgageUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterMortgage2DUnder = new AtomicInteger(
			0);

	private static AtomicInteger callCounterKIVIP = new AtomicInteger(0);

	private static AtomicInteger callCounterKIDEB = new AtomicInteger(0);

	private static AtomicInteger callCounterPSOUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterPSOOfertaUnder = new AtomicInteger(
			0);

	private static AtomicInteger callCounterPBOUnder = new AtomicInteger(0);

	private static AtomicInteger callCounterPBOSbolOfertaUnder = new AtomicInteger(
			0);

	private static AtomicInteger callCounterPBOSbolUnder = new AtomicInteger(0);

	private ScheduledExecutorService propsChecker;

	public class PropsChecker implements Runnable {

		private long previosModification;

		private File configFile;

		public PropsChecker() {

			properties = new Properties();

			String propertiesFname = System.getProperty("user.dir")
					+ "\\conf\\TSM.properties";

			// String properties_fname =
			// "C:\\Documents and Settings\\tester\\Рабочий стол\\tools.properties";

			configFile = new File(propertiesFname);

			previosModification = configFile.lastModified();

			readProps();

		}

		public void run() {

			checkProps();

		}

		private void checkProps() {

			long lastModification = configFile.lastModified();

			if (lastModification > previosModification) {

				logger.info(">>>TSM_TOOLS config file was modified! Reload properties...");

				previosModification = lastModification;

				readProps();
			}
		}

		private void readProps() {

			FileReader fr = null;

			try {

				fr = new FileReader(configFile);

				synchronized (properties) {

					properties.clear();

					properties.load(fr);
				}

				synchronized (connManager) {

					int connections = Integer.parseInt(properties
							.getProperty("MaxConnections"));

					connManager.getParams().setMaxTotalConnections(connections);

					connManager.getParams().setDefaultMaxConnectionsPerHost(
							connections);

				}

			} catch (FileNotFoundException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			} finally {

				try {

					if (null != fr) {

						fr.close();

					}

				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * Default constructor.
	 */
	public WebAppContext() {

	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {

		connManager = new MultiThreadedHttpConnectionManager();

		propsChecker = Executors.newSingleThreadScheduledExecutor();

		propsChecker.scheduleAtFixedRate(new PropsChecker(), 0, 30,
				TimeUnit.SECONDS);

		arrayKIPBO = getVector(properties.getProperty("01_KI_PBO"), 0);

		arrayKIPBOOferta = getVector(
				properties.getProperty("01_KI_PBO_oferta"), 0);

		arrayKIPBODoverie = getVector(
				properties.getProperty("05_KI_PBO_doverie"), 0);

		arrayKIPSO = getVector(properties.getProperty("02_KI_PSO"), 0);

		arrayKIPSOOferta = getVector(
				properties.getProperty("02_KI_PSO_oferta"), 0);

		arrayKIAuto = getVector(properties.getProperty("03_KI_AUTO"), 0);

		arrayKICard = getVector(properties.getProperty("04_KI_CARD"), 0);

		arrayKIMortgage2D = getVector(properties.getProperty("11_SGK_2D"), 0);

		arrayKIVIP = getVector(properties.getProperty("42_KI_VIP"), 0);

		arrayKIDeb = getVector(properties.getProperty("44_KI_DebCard"), 0);

		arrayUnderSbolDoverie = getVector(
				properties.getProperty("15_Und0_PBO_Doverie_SBOL"), 1);

		arrayUnderSurrogat = getVector(
				properties.getProperty("15_Und0_PBO_surrogat"), 1);

		arrayUnderOferta = getVector(
				properties.getProperty("15_Und0_PBO_oferta"), 1);

		arrayUnderDoverie = getVector(
				properties.getProperty("15_Und0_PBO_Doverie_TSM"), 1);

		arrayUnderCRM = getVector(properties.getProperty("15_Und0_PBO_CRM"), 1);

		arrayUnderAuto = getVector(properties.getProperty("18_Und0_AUTO"), 1);

		arrayUnderCard = getVector(properties.getProperty("21_Und0_CARD"), 1);

		arrayUnderMortgage = getVector(
				properties.getProperty("24_Und0_MORTGAGE_SC2"), 1);

		arrayUnderMortgage2D = getVector(
				properties.getProperty("27_Und0_MORTGAGE_2D"), 1);

		arrayUnderPBO = getVector(properties.getProperty("15_Und0_PBO"), 1);

		arrayUnderPBOSbol = getVector(
				properties.getProperty("15_Und0_PBO_SBOL"), 1);

		arrayUnderPBOSbolOferta = getVector(
				properties.getProperty("15_Und0_PBO_SBOL_oferta"), 1);

		arrayUnderPSO = getVector(properties.getProperty("15_Und0_PSO"), 1);

		arrayUnderPSOOferta = getVector(
				properties.getProperty("15_Und0_PSO_oferta"), 1);

		arrayUnderVIP = getVector(properties.getProperty("21_Und0_VIP"), 1);

		arrayUnderDeb = getVector(properties.getProperty("45_Und0_Deb_Card"), 1);

		System.out.println(">>>TSM_Tools Started!!!!");

	}

	/**
	 * 
	 * Dispensation
	 * 
	 * @param seq
	 * @param type
	 *            - script type: 0- ki, 1 - under
	 * @return
	 */
	private Vector<String> getVector(String seq, int type) {

		System.out.println("Sequence: " + seq);

		Vector<String> vector = null;

		String[] dispensation = null;

		int size = 0;

		switch (type) {
		case 1:

			dispensation = seq.split(",");

			int confirm = 0;

			int toUnder = 0;

			int cancel = 0;

			for (int i = 0; i < dispensation.length; i++) {

				switch (i) {
				case 0:
					confirm = Integer.parseInt(dispensation[i]);
					break;
				case 1:
					toUnder = Integer.parseInt(dispensation[i]);
					break;
				case 2:
					cancel = Integer.parseInt(dispensation[i]);
					break;
				default:
					break;
				}

				size += Integer.parseInt(dispensation[i]);

			}

			System.out.println("Size: " + size + "; Confirm: " + confirm
					+ "; To under: " + toUnder + "; Cancel: " + cancel);

			vector = new Vector<String>(size);

			for (int i = 0; i < size; i++) {

				if (confirm != 0) {

					vector.add("1");

					confirm--;
				}

				if (toUnder != 0) {

					vector.add("0");

					toUnder--;

				}

				if (cancel != 0) {

					vector.add("-1");

					cancel--;
				}

			}

			break;

		case 0:

			dispensation = seq.split(",");

			size = Integer.parseInt(dispensation[0]);

			int toUnderKI = Integer.parseInt(dispensation[1]);

			int cancelKI = size - toUnderKI;

			System.out.println("Size: " + size + "; toUnder: " + toUnderKI
					+ "; CancelKi: " + cancelKI);

			vector = new Vector<String>(size);

			for (int i = 0; i < size; i++) {

				if (toUnderKI != 0) {

					vector.add("1");

					toUnderKI--;

				}

				if (cancelKI != 0) {

					vector.add("0");

					cancelKI--;

				}

			}

			break;
		}

		String result = "";

		for (int i = 0; i < vector.size(); i++) {

			result += vector.get(i);

			result += ",";

		}

		System.out.println(result);

		return vector;

	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {

		propsChecker.shutdownNow();

		System.out.println(">>>TSM_Tools Stopped!!!!");
	}

	public static String getNext(int action) {

		String number = null;

		switch (action) {
		case 1:
			number = arrayUnderSbolDoverie.get(Math.abs(callCounterSbolUnder
					.getAndIncrement() % arrayUnderSbolDoverie.size()));
			break;
		case 2:
			number = arrayUnderSurrogat.get(Math.abs(callCounterSurrogatUnder
					.getAndIncrement() % arrayUnderSurrogat.size()));
			break;
		case 3:
			number = arrayUnderOferta.get(Math.abs(callCounterOfertaUnder
					.getAndIncrement() % arrayUnderOferta.size()));
			break;
		case 4:
			number = arrayUnderDoverie.get(Math.abs(callCounterDoverieUnder
					.getAndIncrement() % arrayUnderDoverie.size()));
			break;
		case 5:
			number = arrayUnderCRM.get(Math.abs(callCounterCRMUnder
					.getAndIncrement() % arrayUnderCRM.size()));
			break;
		case 6:
			number = arrayKIPBO.get(Math.abs(callCounterKIPBO.getAndIncrement()
					% arrayKIPBO.size()));
			break;
		case 7:
			number = arrayKIPBOOferta.get(Math.abs(callCounterKIPBOOferta
					.getAndIncrement() % arrayKIPBOOferta.size()));
			break;
		case 8:
			number = arrayKIPSO.get(Math.abs(callCounterKIPSO.getAndIncrement()
					% arrayKIPSO.size()));
			break;
		case 9:
			number = arrayKIPSOOferta.get(Math.abs(callCounterKIPSOOferta
					.getAndIncrement() % arrayKIPSOOferta.size()));
			break;
		case 10:
			number = arrayKIAuto.get(Math.abs(callCounterKIAuto
					.getAndIncrement() % arrayKIAuto.size()));
			break;
		case 11:
			number = arrayKICard.get(Math.abs(callCounterKICard
					.getAndIncrement() % arrayKICard.size()));
			break;
		case 12:
			number = arrayKIPBODoverie.get(Math.abs(callCounterKIPBODoverie
					.getAndIncrement() % arrayKIPBODoverie.size()));
			break;
		case 13:
			number = arrayKIMortgage2D.get(Math.abs(callCounterKIMortgage2D
					.getAndIncrement() % arrayKIMortgage2D.size()));
			break;
		case 14:
			number = arrayUnderAuto.get(Math.abs(callCounterAutoUnder
					.getAndIncrement() % arrayUnderAuto.size()));
			break;
		case 15:
			number = arrayUnderVIP.get(Math.abs(callCounterVIPUnder
					.getAndIncrement() % arrayUnderVIP.size()));
			break;
		case 16:
			number = arrayUnderCard.get(Math.abs(callCounterCardUnder
					.getAndIncrement() % arrayUnderCard.size()));
			break;
		case 17:
			number = arrayUnderMortgage.get(Math.abs(callCounterMortgageUnder
					.getAndIncrement() % arrayUnderMortgage.size()));
			break;
		case 18:
			number = arrayUnderMortgage2D.get(Math
					.abs(callCounterMortgage2DUnder.getAndIncrement()
							% arrayUnderMortgage2D.size()));
			break;
		case 19:
			number = arrayKIVIP.get(Math.abs(callCounterKIVIP.getAndIncrement()
					% arrayKIVIP.size()));
			break;
		case 20:
			number = arrayUnderPSO.get(Math.abs(callCounterPSOUnder
					.getAndIncrement() % arrayUnderPSO.size()));
			break;
		case 21:
			number = arrayUnderPSOOferta.get(Math.abs(callCounterPSOOfertaUnder
					.getAndIncrement() % arrayUnderPSOOferta.size()));
			break;
		case 22:
			number = arrayUnderPBO.get(Math.abs(callCounterPBOUnder
					.getAndIncrement() % arrayUnderPBO.size()));
			break;
		case 23:
			number = arrayUnderPBOSbolOferta.get(Math
					.abs(callCounterPBOSbolOfertaUnder.getAndIncrement()
							% arrayUnderPBOSbolOferta.size()));
			break;
		case 24:
			number = arrayUnderPBOSbol.get(Math.abs(callCounterPBOSbolUnder
					.getAndIncrement() % arrayUnderPBOSbol.size()));
			break;
		case 25:
			number = arrayKIDeb.get(Math.abs(callCounterKIDEB.getAndIncrement()
					% arrayKIDeb.size()));
			break;
		case 26:
			number = arrayUnderDeb.get(Math.abs(callCounterDebUnder
					.getAndIncrement() % arrayUnderDeb.size()));
			break;
		default:
			break;
		}

		return number;
	}

}
