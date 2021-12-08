package org.annoscheme.application.device.exception;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.annotation.Conditional;
import org.apache.log4j.Logger;

public class DeviceNotFoundException extends RuntimeException {

	@Action(message = "Log failed attempt to create Restock order", actionType = ActionType.END, parentMessage = "Find device by ID",
			diagramIdentifiers = {"1"})
	@Conditional(type = BranchingType.ALTERNATIVE, condition = "Found device?", diagramIdentifiers = {"1"})
	public DeviceNotFoundException(String message) {
		Logger.getLogger(DeviceNotFoundException.class).error(message);
	}

}
