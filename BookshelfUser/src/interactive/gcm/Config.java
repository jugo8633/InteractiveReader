package interactive.gcm;

public interface Config
{
	// used to share GCM regId with application server - using php app server
	//static final String	APP_SERVER_URL		= "http://192.168.1.17/gcm/gcm.php?shareRegId=1";

	// GCM server using java
	// static final String APP_SERVER_URL =
	// "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

	// Google Project Number
	static final String	GOOGLE_PROJECT_ID	= "15722213180";
	static final String	MESSAGE_KEY			= "message";
}

/*
 * How to test
 * Service Provider 發訊息給指定的 Mobile device：
 * 
 * $ curl --header "Authorization: key=API_Key" --header
 * Content-Type:"application/json" https://android.googleapis.com/gcm/send -d
 * '{"to":"MobileDeviceToken","data":{"message":"hello
 * world","action":"com.example.gcm"}}'
 * 
 * 
 * example: curl --header "Authorization: key=AIzaSyBJtaoytiKuOSiqieXoYjPuDEC4BK05Ehs" --header Content-Type:"application/json" http://android.googleapis.com/gcm/send  -d '{"to":"APA91bEcdzTLcnNOk8mjIN-FbVp9itAjIGR_1ncoMRZqurcqFAheIppEM3poZBHmu3SQ3zL9iXdu95-EP62whGhiN0j4J_DB7MkrIunpGLYZ0GBhJN-FWvamzNHtMC-hIgqdcMnFoMJDixPUBxPUKIkSEgzqRoCLiA","data":{"message":"hello world","action":"com.example.gcm"}}'
 */
