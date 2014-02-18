package interactive.view.handler;

import interactive.common.Type;
import android.view.ViewGroup.LayoutParams;

public class InteractiveGoogleMapData
{
	public String	mstrTag				= null;
	public int		mnMapType			= Type.INVALID;
	public double	mdLatitude			= 0f;
	public double	mdLongitude			= 0f;
	public int		mnZoomLevel			= Type.INVALID;
	public String	mstrMarker			= null;
	public int		mnX					= 0;
	public int		mnY					= 0;
	public int		mnWidth				= LayoutParams.MATCH_PARENT;
	public int		mnHeight			= LayoutParams.MATCH_PARENT;
	public String	mstrBackgroundImage	= null;

	public InteractiveGoogleMapData(String strTag, int nMapType, double dLatitude, double dLongitude, int nZoomLevel,
			String strMarker, int nX, int nY, int nWidth, int nHeight, String strBackgroundImage)
	{
		mstrTag = strTag;
		mnMapType = nMapType;
		mdLatitude = dLatitude;
		mdLongitude = dLongitude;
		mnZoomLevel = nZoomLevel;
		mstrMarker = strMarker;
		mnX = nX;
		mnY = nY;
		mnWidth = nWidth;
		mnHeight = nHeight;
		mstrBackgroundImage = strBackgroundImage;
	}
}
