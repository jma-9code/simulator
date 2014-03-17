package fr.ensicaen.simulator_ep.utils;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;

public class ISOTools {

	public static void logISOMsg(ISOMsg msg, ISOPackager pack) {
		System.out.println("----ISO MESSAGE-----");
		try {
			System.out.println("  MTI : " + msg.getMTI());
			for (int i = 1; i <= msg.getMaxField(); i++) {
				if (msg.hasField(i)) {
					System.out.println("    Field-" + i + " : " + msg.getString(i) + " "
							+ pack.getFieldDescription(msg, i));
				}
			}
		}
		catch (ISOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("--------------------");
		}

	}
}
