package interactive.view.handler;

import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.ViewGroup;

public class InteractiveButtonData
{
	public String									mstrTag			= null;
	public String									mstrGroupId		= null;
	public Handler									mHandlerButton	= null;
	public SparseArray<InteractiveImageData>		listImageData	= null;
	public SparseArray<InteractiveGoogleMapData>	listMapData		= null;
	public SparseArray<InteractiveEventData>		listEventData	= null;
	public SparseArray<InteractiveMediaData>		listMediaData	= null;
	private ViewGroup								container		= null;
	private Context									theContext		= null;

	public InteractiveButtonData(Context context, String strTag, String strGroupId, Handler handler)
	{
		mstrTag = strTag;
		mstrGroupId = strGroupId;
		mHandlerButton = handler;
		theContext = context;
	}

	@Override
	protected void finalize() throws Throwable
	{
		if (null != listImageData)
		{
			listImageData.clear();
			listImageData = null;
		}

		if (null != listMapData)
		{
			listMapData.clear();
			listMapData = null;
		}

		if (null != listEventData)
		{
			listEventData.clear();
			listEventData = null;
		}

		if (null != listMediaData)
		{
			listMediaData.clear();
			listMediaData = null;
		}
		super.finalize();
	}

	public void addImageData(String strName, int nWidth, int nHeight, int nX, int nY, String strSrc, String strGroupId,
			boolean bIsVisible)
	{
		if (null == listImageData)
		{
			listImageData = new SparseArray<InteractiveImageData>();
		}

		listImageData.put(listImageData.size(), new InteractiveImageData(strName, nWidth, nHeight, nX, nY, strSrc,
				strGroupId, bIsVisible));
	}

	public void addMapData(String strTag, int nMapType, double dLatitude, double dLongitude, int nZoomLevel,
			String strMarker, int nX, int nY, int nWidth, int nHeight, String strBackgroundImage, boolean bIsVisible)
	{
		if (null == listMapData)
		{
			listMapData = new SparseArray<InteractiveGoogleMapData>();
		}

		listMapData.put(listMapData.size(), new InteractiveGoogleMapData(strTag, nMapType, dLatitude, dLongitude,
				nZoomLevel, strMarker, nX, nY, nWidth, nHeight, strBackgroundImage, bIsVisible));
	}

	public void addEventData(int nType, String strTypeName, int nEvent, String strEventName, int nTargetType,
			String strTargetID, int nDisplay)
	{
		if (null == listEventData)
		{
			listEventData = new SparseArray<InteractiveEventData>();
		}

		listEventData.put(listEventData.size(), new InteractiveEventData(nType, strTypeName, nEvent, strEventName,
				nTargetType, strTargetID, nDisplay));
	}

	public void addMediaData(String strName, int nWidth, int nHeight, int nX, int nY, String strSrc, int nMediaType,
			String strMediaSrc, int nStart, int nEnd, boolean bAutoplay, boolean bLoop, boolean bPlayerControls,
			boolean bIsVisible, ViewGroup viewParent, boolean bIsCurrentPlayer)
	{
		if (null == listMediaData)
		{
			listMediaData = new SparseArray<InteractiveMediaData>();
		}
		listMediaData.put(listMediaData.size(), new InteractiveMediaData(strName, nWidth, nHeight, nX, nY, strSrc,
				nMediaType, strMediaSrc, nStart, nEnd, bAutoplay, bLoop, bPlayerControls, bIsVisible, viewParent,
				bIsCurrentPlayer));
	}

	public void setContainer(ViewGroup viewGroup)
	{
		container = viewGroup;
	}

	public ViewGroup getContainer()
	{
		return container;
	}

	public Context getContext()
	{
		return theContext;
	}
}
