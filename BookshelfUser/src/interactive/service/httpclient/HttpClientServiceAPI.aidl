package interactive.service.httpclient;

import interactive.service.httpclient.HttpClientResponseAPI;
import interactive.service.httpclient.HttpClientResponseData;

interface HttpClientServiceAPI
{
    void addResponse(HttpClientResponseAPI response);
	void Login(String strAccount, String strPassword);
	void getBook(int nBookId);
	HttpClientResponseData getHttpClientResult();
}
