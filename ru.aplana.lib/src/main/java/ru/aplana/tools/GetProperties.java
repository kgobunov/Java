package ru.aplana.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Read file with properties
 * 
 * @author Maksim Stepanov
 * 
 */
public class GetProperties {

	private final File config;

	private final Properties properties;

	/**
	 * Properties initialization
	 * 
	 * @param fileName
	 *            with properties.
	 * @param type
	 *            by user - need set absolute path to file, null - location
	 *            default by user.dir
	 * @author Maksim Stepanov
	 */
	private GetProperties(String fileName, String type) {

		this.properties = new Properties();

		String propertiesFileName = null;

		if (null == type) {

			String system = System.getProperty("os.name").toLowerCase();

			if (system.startsWith("windows")) {

				propertiesFileName = System.getProperty("user.dir") + "\\"
						+ fileName;

			} else {

				propertiesFileName = System.getProperty("user.dir") + "/"
						+ fileName;
			}

		} else {

			propertiesFileName = fileName;
		}

		this.config = new File(propertiesFileName);

		readProps();
	}

	public static GetProperties getInstance(String fileName, String type) {

		return new GetProperties(fileName, type);

	}

	/**
	 * Reading file properties
	 * 
	 * @author Maksim Stepanov
	 */
	private void readProps() {

		try {

			FileReader fr = new FileReader(this.config);

			this.properties.clear();

			this.properties.load(fr);

			fr.close();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	/**
	 * Get properties
	 * 
	 * @return properties
	 * @author Maksim Stepanov
	 */
	public Properties getProperties() {

		return this.properties;

	}

	public File getConfig() {

		return this.config;

	}

}
