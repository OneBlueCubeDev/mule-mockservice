package com.org.rak;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

/**
 * Simulates delay before sending response back 
 * @author Ruman Khan
 *
 */
public class SimulateDelayedResponse implements Callable {

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		// TODO Auto-generated method stub
	try{
		String delayResponse = eventContext.getMessage().getOutboundProperty("delay");
		if(delayResponse !=null)
			Thread.sleep(Long.parseLong(delayResponse));
	}catch(Exception ex)
	{
		ex.printStackTrace();
	}
		return eventContext.getMessage();
	}

}
