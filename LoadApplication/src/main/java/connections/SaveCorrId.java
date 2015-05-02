package connections;

import ru.aplana.app.Initialization;

/**
 * 
 * 
 * 
 * Classname: SaveCorrId
 * 
 * Version: 1.0
 * 
 * 
 * Save correlation ID to database
 * 
 * @author Maksim Stepanov
 * 
 */
public class SaveCorrId implements Runnable {

	private String corrID = null;

	private String systemName = null;

	public SaveCorrId(String corrId, String system) {

		this.systemName = system;

		this.corrID = corrId;

	}

	@Override
	public void run() {

		if (null != Initialization.helper) {

			Initialization.helper.insertCorrId(this.corrID, this.systemName);

		}

	}

}
