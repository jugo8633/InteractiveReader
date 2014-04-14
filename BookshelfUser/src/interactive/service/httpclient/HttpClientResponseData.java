package interactive.service.httpclient;

import android.os.Parcel;
import android.os.Parcelable;

public final class HttpClientResponseData implements Parcelable
{
	private String										mstrResult		= null;
	private int											mnResultCode	= 0;

	public static final Creator<HttpClientResponseData>	CREATOR			= new Creator<HttpClientResponseData>()
																		{

																			@Override
																			public HttpClientResponseData createFromParcel(
																					Parcel source)
																			{
																				return new HttpClientResponseData(
																						source);
																			}

																			@Override
																			public HttpClientResponseData[] newArray(
																					int size)
																			{
																				return new HttpClientResponseData[size];
																			}

																		};

	public HttpClientResponseData(String strResult, int nResultCode)
	{
		this.mstrResult = strResult;
		this.mnResultCode = nResultCode;
	}

	private HttpClientResponseData(Parcel source)
	{
		mstrResult = source.readString();
		mnResultCode = source.readInt();
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(mstrResult);
		dest.writeInt(mnResultCode);
	}

	public void setResult(String strResult)
	{
		mstrResult = strResult;
	}

	public void setHttpReturnCode(int nCode)
	{
		mnResultCode = nCode;
	}

	public int getHttpReturnCode()
	{
		return mnResultCode;
	}

	public String getResult()
	{
		return mstrResult;
	}
}
