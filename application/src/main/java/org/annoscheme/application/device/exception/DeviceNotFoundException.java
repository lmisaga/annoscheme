package org.annoscheme.application.device.exception;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.annotation.Conditional;
import org.apache.log4j.Logger;

public class DeviceNotFoundException extends RuntimeException {

	@Action(message = "d1.logFailed", actionType = ActionType.END, parentMessage = "d1.findById",
			diagramIdentifiers = {"d1.id"})
	@Conditional(type = BranchingType.ALTERNATIVE, condition = "d1.deviceCond", diagramIdentifiers = {"d1.id"}, joining = true)
	public DeviceNotFoundException(String message) {
		Logger.getLogger(DeviceNotFoundException.class).error(message);
	}

}
