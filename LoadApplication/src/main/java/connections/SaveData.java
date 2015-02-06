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
 * Copyright: OOO Aplana
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

		this.request = req;

		this.flagCheck = flag;

		this.systemName = system;

	}

	@Override
	public void run() {

		if (null != Initialization.helper) {

			Initialization.helper.saveResponseStatus(this.request,
					this.flagCheck, this.systemName);
			
		}

	}

}
