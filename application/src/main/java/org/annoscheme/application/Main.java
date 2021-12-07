package org.annoscheme.application;

import org.annoscheme.application.restockOrder.RestockOrder;
import org.annoscheme.application.restockOrder.RestockOrderController;

public class Main {

	//1st arg -> deviceId, 2nd arg -> restock
	public static void main(String[] args) {
		RestockOrderController controller = new RestockOrderController();
		RestockOrder restockOrder = controller.createRestockOrder(args);
	}

}
