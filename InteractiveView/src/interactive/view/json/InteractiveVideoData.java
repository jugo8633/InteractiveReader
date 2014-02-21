package interactive.view.json;

import android.view.ViewGroup.LayoutParams;

public class InteractiveVideoData
{

	public String	mstrName			= null;
	public int		mnWidth				= LayoutParams.MATCH_PARENT;
	public int		mnHeight			= LayoutParams.MATCH_PARENT;
	public int		mnX					= 0;
	public int		mnY					= 0;
	public String	mstrSrc				= null;
	public int		mnMediaType			= 0;
	public String	mstrMediaSrc		= null;
	public int		mnStart				= 0;
	public int		mnEnd				= 0;
	public boolean	mbAutoplay			= false;
	public boolean	mbLoop				= false;
	public boolean	mbPlayerControls	= false;
	public boolean	mbIsVisible			= true;

	public InteractiveVideoData()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public InteractiveVideoData(String strName, int nWidth, int nHeight, int nX, int nY, String strSrc, int nMediaType,
			String strMediaSrc, int nStart, int nEnd, boolean bAutoplay, boolean bLoop, boolean bPlayerControls)
	{
		super();
		mstrName = strName;
		mnWidth = nWidth;
		mnHeight = nHeight;
		mnX = nX;
		mnY = nY;
		mstrSrc = strSrc;
		mnMediaType = nMediaType;
		mstrMediaSrc = strMediaSrc;
		mnStart = nStart;
		mnEnd = nEnd;
		mbAutoplay = bAutoplay;
		mbLoop = bLoop;
		mbPlayerControls = bPlayerControls;
	}

}
