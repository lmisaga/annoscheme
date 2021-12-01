package org.annoscheme.application.device.exception;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.annotation.Conditional;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

public class DeviceNotFoundException extends RuntimeException {

	private final Logger logger = LoggerFactory.getLogger(DeviceNotFoundException.class);

//	@Action(message = "Log failed attempt to create Restock order", actionType = ActionType.END, parentMessage = "Find device by ID")
//	@Conditional(type = BranchingType.ALTERNATIVE, condition = "Found device?")
	public DeviceNotFoundException(String message) {
		logger.error(message);
	}

}
