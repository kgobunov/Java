package connections;

import ru.aplana.app.Initialization;

/**
 * 
 * 
 * 
 * Classname: SaveData
 * 
 * Version: 1.0
 * 
 * 
 * Save responses to database
 * 
 * @author Maksim Stepanov
 * 
 */
public class SaveData implements Runnable {

	private String request = null;

	private boolean flagCheck = false;

	private String systemName = null;

	public SaveData(String req, boolean flag, String system) {

		this.flagCheck = flag;

		if (this.flagCheck) {

			this.request = "";

		} else {

			this.request = req;

		}

		this.systemName = system;

	}

	@Override
	public void run() {

		if (null != Initialization.helper) {

			if (Initialization.saveMode.equalsIgnoreCase("batch")) {

				Initialization.helper.saveResponseStatus(this.request,
						this.flagCheck, this.systemName);

			} else {

				Initialization.helper.insert(this.request, this.flagCheck,
						this.systemName);

			}

		}

	}

}
