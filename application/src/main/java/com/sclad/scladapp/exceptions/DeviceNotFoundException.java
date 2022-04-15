package com.sclad.scladapp.exceptions;

import org.annoscheme.common.annotation.Action;
import org.annoscheme.common.annotation.ActionType;
import org.annoscheme.common.annotation.BranchingType;
import org.annoscheme.common.annotation.Conditional;

public class DeviceNotFoundException extends RuntimeException {

	@Action(actionType = ActionType.END, message = "resOr.create.deviceNotFound", parentMessage = "resOr.create.findDeviceById",
			diagramIdentifiers = {"resOr.create"})
	@Conditional(condition = "resOr.create.foundDevice?", type = BranchingType.ALTERNATIVE, diagramIdentifiers = {"resOr.create"})
	public DeviceNotFoundException(Long id) {
		super("Could not find device " + id);
	}
}
