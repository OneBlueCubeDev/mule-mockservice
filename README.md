# mule-mockservice
This solution is used to mock webservice response with a predefined response and headers.
Responses and headers can be configured for individual webservice calls or a default response can be sent to any webservice call. 

Response is picked by below logic: 

1.	Get the value of ResponseFileId from inbound property of the message  
2.	Get the value of http.request.path from inbound property of the message 
3.	Look for headers and message file based on below paths  
		headersFile: `<<path>>/<<mockresponse>>/<<ResponseFileId>>_headers.xml`  
		messageFile: `<<path>>/<<mockresponse>>/<<ResponseFileId>>_message.xml`  
4.	If files found, 
   Load the headers file contents as outbound properties of the message   
   Load the message file contents as the payload of the message   
5.	If files not found, look for files at base folder path 
   headersFile: `<<path>>/<<mockresponse>>/default_headers.xml`   
   messageFile: `<<path>>/<<mockresponse>>/default_message.xml`   
6.	If files still not found, throw FileNotFoundException 

### Configurations:
All configurable values are placed in app-default.properties  file located at src/main/resources/
##### http configuration
http.host=localhost `--> indicate host machine name where mockservice will be deployed`
http.port=7008 `--> port of mockservice`

http.path=/ `--> base path of mockservice`

##### mockresponse folder configuration
mockresponse.folder.path = mockresponse `--> folder containing the mockresponses`

##### Simulating Delay in mock service response 
Inorder to simulate a delay for a particular request, add delay=`<<value in ms>>` in the headers file. 
Forexample, to simulate a failure of 500 status with delay of 10 sec, headers file will look like below: 
   https.status=500 
   delay=10000 

### MockResponse folder
This folder holds the response of the services to be mocked.
You can place different subfolders(like someservice) and configure the consuming application to point to the mockservice path as the subfolder name (instead of just / you would point the path as /someservice) to pick response from the  subfolder.
#Testing
Run the mule application and open fiddler and do a GET/POST or any verb with one of the below configurations:

Run1: Pointing to the default path  

	Url= http://localhost:7008/    
	
	Headers= ResponseFileId: default   
  
Run2: Pointing to the specific folder  

	Url= http://localhost:7008/someservice    
	
	Headers= ResponseFileId: default   
  
Run2: Pointing to the specific folder with a failure response (the headers has a delay of 10 sec)  

	Url= http://localhost:7008/    
	
	Headers= ResponseFileId: 500   
  

### Compatibility
This solution was built with below configuration. However it should work fine with any other mule versions as well.

1.  Java 1.7  
2.  Mule 3.7.1   
  
  _Note_: If using mule < 3.7 you may have to modify the http connector to older configuration.

###
MockService blog: http://rumanblogs.com/mock-your-dependency/
  
### Contributing
All pull requests are welcome
