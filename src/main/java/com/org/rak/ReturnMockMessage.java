package com.org.rak;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * @author Ruman Khan 
 * Load the responsed based on the fileId 
 * On first request, it looks for the files in http.request.path and if not found throws FileNotFoundException and calls same flow again
 * If the flow is returning from Exception, it looks for file in the root folder(mockresponse) 
 * 		and if still not found, it will throw another FileNotFoundException   
*/
public class ReturnMockMessage implements Callable {

	private String mockResponseFolder;
	public void setMockResponseFolder(String mockResponseFolder)
	{
		this.mockResponseFolder = mockResponseFolder; 
	}
	
	private String fileId;
	public void setFileId(String fileId)
	{
		this.fileId = fileId; 
	}
	
	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		MuleMessage message = eventContext.getMessage();
		
		String routedViaException = message.getOutboundProperty("routedviaexception") != null ? message.getOutboundProperty("routedviaexception").toString() : "false";
		//get the request path to find folder where to look for file
		String path = message.getInboundProperty("http.request.path"); 
		String fileNamePrefix = message.getProperty(fileId,PropertyScope.INBOUND);
		String headersFilePath =mockResponseFolder+ path+"/"+fileNamePrefix +"_headers.xml";
		String payloadFilePath = mockResponseFolder+ path+"/"+fileNamePrefix +"_message.xml";
		FileInputStream headersStream = getContentsAsInputStream(headersFilePath);
		if(headersStream == null && routedViaException == "true")
		{
			//If file not found look in base folder path
			headersFilePath = mockResponseFolder+"/"+fileNamePrefix +"_headers.xml";
			headersStream = getContentsAsInputStream(headersFilePath);
		}
		FileInputStream payloadStream = getContentsAsInputStream(payloadFilePath);
		if(payloadStream == null && routedViaException == "true")
		{
				//If file not found look in basepath
				payloadFilePath = mockResponseFolder+"/"+fileNamePrefix +"_message.xml";
				payloadStream = getContentsAsInputStream(payloadFilePath);
		}

		if(headersStream == null)
			throw new FileNotFoundException("headers file couldnt be found. Path:"+headersFilePath);
		if(payloadStream == null)
			throw new FileNotFoundException("Payload file couldnt be found. Path:"+payloadFilePath);

		String line;
		BufferedReader headersReader = new BufferedReader(new InputStreamReader(headersStream));
		while((line = headersReader.readLine()) != null)
		{
			String[] values = line.split("=");
		  message.setOutboundProperty(values[0],values[1]);
		}
		headersReader.close();
		message.setPayload(payloadStream);
		return message;
	}
	
	private FileInputStream getContentsAsInputStream(String filePath)
	{
		FileInputStream fileStream;
		try {
			File file = new File(filePath);
			fileStream = new FileInputStream(file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return fileStream;
	}
}
