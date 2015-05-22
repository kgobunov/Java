package ru.aplana.app;

import static ru.aplana.tools.MQTools.getConnection;
import static ru.aplana.tools.MQTools.getConsumer;
import static ru.aplana.tools.MQTools.getSession;
import static tools.PropCheck.ckpit;
import static tools.PropCheck.common;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import listener.Listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import products.Auto;
import products.Card;
import products.PotrebBez;
import products.PotrebBezNT;
import tools.CheckConn;
import tools.MQConn;
import tools.PropCheck;
import tools.Queues;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueSession;

import db.DatabaseOperation;

/**
 * Main Class - enter point. This bundle emulation CKPIT system.
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class Main implements Runnable {

	public static AtomicInteger count = new AtomicInteger(101);

	public static AtomicBoolean flag = new AtomicBoolean(false);

	public static AtomicInteger countSetListener = new AtomicInteger(0);

	public static AtomicInteger countAddProduct = null;

	public static AtomicInteger saveCountAddProduct = new AtomicInteger(0);

	public static ScheduledExecutorService exec = null;

	public static ScheduledExecutorService sc = null;

	private boolean flagReconnect = false;

	private static final Logger logger = LogManager
			.getFormatterLogger(Main.class.getName());

	private static int countThread;

	// time delay between times
	private static int timeIter;

	// time when need start (if 0 start immediate)
	private static int timeStart;

	private MQQueueConnectionFactory factory;

	private MQQueueConnection connection;

	private static final Lock lock = new ReentrantLock();

	public Main() {

		// connect to mq
		try {

			this.factory = MQConn.getFactory();

			this.factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

		} catch (NumberFormatException | JMSException e) {

			logger.error(e.getMessage(), e);

		}

	}

	/**
	 * @param args
	 */

	public void run() {

		if (flag.get() == false) {

			flag.set(true);
		}

		logger.debug("Send to TSM from CKPIT");

		if (countSetListener.get() == 0) {

			try {

				this.connection = getConnection(this.factory, null, null);

				// exception Listener
				this.connection.setExceptionListener(new ExceptionListener() {

					@Override
					public void onException(JMSException e) {

						String errorCode = e.getErrorCode();

						logger.error("Error code: %s", errorCode);

						// JMSWMQ1107 - closed connection
						if (errorCode.equalsIgnoreCase("JMSWMQ1107")) {

							logger.error("Error msg: %s", e.getMessage(), e);

							flagReconnect = true;

							flag.set(false);

							logger.info("Connection to MQ closed.");

							long delay = Long.parseLong(common
									.getChildText("delayReconnect")) * 1000;

							while (flagReconnect) {

								try {

									lock.lock();

									try {
										if (CheckConn.checkConn()) {

											countSetListener.set(0);

											flagReconnect = false;
										}

									} finally {

										lock.unlock();

									}

								} catch (Exception e1) {

									logger.error("Can't reconecting to MQ: %s",
											e1.getMessage());
								}

								// Delay between retry get connect
								try {

									long start = System.currentTimeMillis();

									Thread.sleep(delay);

									long stop = System.currentTimeMillis();

									long diff = stop - start;

									logger.info("Time wait: %s", diff);

									if (diff < delay) {

										logger.info("Wake up early! Wait: %s",
												(delay - diff));

										Thread.sleep(delay - diff);
									}

								} catch (InterruptedException e1) {

									logger.error(e1.getMessage(), e1);
								}

							}
						}
					}
				});

				this.connection.start();

				flagReconnect = false;

				flag.set(true);

				logger.debug(
						"Thread is connected to MQ server with parameters: HostName: %s; Port: %s; QueueManager: %s; Channel: %s",
						this.factory.getHostName(), this.factory.getPort(),
						this.factory.getQueueManager(),
						this.factory.getChannel());

				MessageConsumer consumerERIB = getConsumer(this.connection,
						Queues.CKPIT_IN);

				consumerERIB.setMessageListener(new Listener());

				logger.info("Listener is set to queue %s", Queues.CKPIT_IN);

			} catch (JMSException e) {

				logger.error("Set listener: %s", e.getMessage(), e);

				if (!flagReconnect) {

					exec.shutdownNow();

					sc.shutdownNow();

				}

			}

			countSetListener.getAndIncrement();

		}

		// send request

		MQQueueSession session = null;

		MessageProducer producer = null;

		try {

			session = getSession(this.connection, false,
					MQQueueSession.AUTO_ACKNOWLEDGE);

			MQQueue queueSend = (MQQueue) session.createQueue(Queues.CKPIT_OUT);

			queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

			String request = null;

			producer = session.createProducer(queueSend);

			TextMessage outputMsg = null;

			ArrayList<String> data = new ArrayList<String>();

			while (flag.get()) {

				if ((count.get() > 453) && (count.get() > 679)) {

					Auto auto = new Auto(count.getAndIncrement(), "update");

					request = auto.getXml();

					data = auto.getdata();

				} else if (count.get() > 453) {

					Card card = new Card(count.getAndIncrement(), "update");

					request = card.getXml();

					data = card.getdata();

				} else {

					// < 453
					PotrebBez potreb = new PotrebBez(count.getAndIncrement(),
							"update");

					request = potreb.getXml();

					data = potreb.getdata();
				}

				logger.debug("Request: %s", request);

				outputMsg = session.createTextMessage(request);

				try {

					Thread.sleep(500);

				} catch (InterruptedException e) {

					logger.error(e.getMessage(), e);
				}

				producer.send(outputMsg);

				DatabaseOperation.getInstance().evalOperation(1, data);

				logger.debug("Count: %s", count);

				if (count.get() > 1144) {

					flag.set(false);

					count.getAndSet(101);

					logger.info("Update NT product");

					PotrebBezNT addPotreb_NT = new PotrebBezNT();

					outputMsg = session
							.createTextMessage(addPotreb_NT.getXml());

					producer.send(outputMsg);

					// insert nt product

					DatabaseOperation.getInstance().evalOperation(1,
							addPotreb_NT.getdata());

					ArrayList<String> dataAdd = new ArrayList<String>();

					while (saveCountAddProduct.get() < 55) {

						if (countAddProduct.get() > countAddProduct.get() + 18 - 1
								&& countAddProduct.get() > countAddProduct
										.get() + 36 - 1) {

							Card addCard = new Card(
									countAddProduct.getAndIncrement(), "insert");

							outputMsg = session.createTextMessage(addCard
									.getXml());

							dataAdd = addCard.getdata();

						} else if (countAddProduct.get() > countAddProduct
								.get() + 18 - 1) {

							Auto addAuto = new Auto(
									countAddProduct.getAndIncrement(), "insert");

							outputMsg = session.createTextMessage(addAuto
									.getXml());

							dataAdd = addAuto.getdata();

						} else {

							// < 2018

							PotrebBez addPotreb = new PotrebBez(
									countAddProduct.getAndIncrement(), "insert");

							outputMsg = session.createTextMessage(addPotreb
									.getXml());

							dataAdd = addPotreb.getdata();

						}

						producer.send(outputMsg);

						saveCountAddProduct.getAndIncrement();

						logger.debug("saveCountAddProduct: %s",
								saveCountAddProduct);

						// insert to db new products

						DatabaseOperation.getInstance().evalOperation(1,
								dataAdd);

					}

					saveCountAddProduct.getAndSet(0);

				}

			}

		} catch (JMSException e) {

			logger.error("Can't send xml's %s", e.getMessage(), e);

		} finally {

			try {

				if (null != producer) {

					producer.close();

				}

			} catch (JMSException e) {

				logger.error(e.getMessage(), e);
			}

			try {

				if (null != session) {

					session.close();

				}

			} catch (JMSException e) {

				logger.error(e.getMessage(), e);
			}

		}

	}

	public static void main(String[] args) throws NumberFormatException,
			JMSException {

		sc = Executors.newSingleThreadScheduledExecutor();

		sc.scheduleAtFixedRate(new PropCheck(), 0, 10, TimeUnit.SECONDS);

		countThread = Integer.parseInt(ckpit.getChildText("threads"));

		timeIter = Integer.parseInt(ckpit.getChildText("timeIter"));

		timeStart = Integer.parseInt(ckpit.getChildText("timeStart"));

		int startAddProduct = Integer.parseInt(ckpit
				.getChildText("startProductUniqCode"));

		countAddProduct = new AtomicInteger(startAddProduct);

		exec = Executors.newScheduledThreadPool(countThread);

		for (int i = 0; i < countThread; i++) {

			exec.scheduleAtFixedRate(new Main(), timeStart, timeIter,
					TimeUnit.MINUTES);
		}

	}

}
