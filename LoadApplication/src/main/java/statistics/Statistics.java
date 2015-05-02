package statistics;

import java.sql.Timestamp;
import java.util.ArrayList;

import ru.aplana.app.Initialization;

/**
 * Classname: Statistics
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * @author Maksim Stepanov
 *
 */
public class Statistics implements Runnable {

	private String sysName; // system name

	private int countRequest; // intensity

	private int sizeFlush; // size for flush

	public Statistics(String sysName, int countReq, int flushTime) {

		this.sysName = sysName;

		this.countRequest = countReq;

		this.sizeFlush = this.countRequest / flushTime;

	}

	@Override
	public void run() {

		int size = Initialization.systems.get(this.sysName).size();

		if (size > this.sizeFlush) {


			try {

				ArrayList<Timestamp> time = new ArrayList<Timestamp>(
						this.sizeFlush + 1);

				for (int i = 0; i < size; i++) {

					time.add(Initialization.systems.get(this.sysName).get(0));

					Initialization.systems.get(this.sysName).remove(0);

				}

				Initialization.systems.get(this.sysName).trimToSize();

				if (null != Initialization.helper) {

					Initialization.helper.insertPut(time, this.sysName);

				}

				time = null;

			} catch (Exception e) {

				e.printStackTrace();

			} 

		}

	}

}
