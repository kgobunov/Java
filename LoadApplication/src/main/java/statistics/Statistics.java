package statistics;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

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

	private int countRequest;

	private int sizeFlush;

	private static Lock lock = new ReentrantLock();

	public Statistics(String sysName, int countReq, int flushTime) {

		this.sysName = sysName;

		this.countRequest = countReq;

		this.sizeFlush = this.countRequest / flushTime;

	}

	@Override
	public void run() {

		int size = Initialization.systems.get(this.sysName).size();

		if (size > this.sizeFlush) {

			lock.lock();

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

				Initialization.severe.log(Level.SEVERE, e.getMessage(), e);

			} finally {

				lock.unlock();
			}

		}

	}

}
