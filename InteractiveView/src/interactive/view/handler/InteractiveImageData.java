package interactive.view.handler;

public class InteractiveImageData
{

	public String	mstrName	= null;
	public int		mnWidth		= 0;
	public int		mnHeight	= 0;
	public int		mnX			= 0;
	public int		mnY			= 0;
	public String	mstrSrc		= null;
	public String	mstrGroupId	= null;
	public boolean	mbIsVisible	= true;

	public InteractiveImageData()
	{
		super();
	}

	public InteractiveImageData(String strName, int nWidth, int nHeight, int nX, int nY, String strSrc,
			String strGroupId, boolean bIsVisible)
	{
		super();
		mstrName = strName;
		mnWidth = nWidth;
		mnHeight = nHeight;
		mnX = nX;
		mnY = nY;
		mstrSrc = strSrc;
		mstrGroupId = strGroupId;
		mbIsVisible = bIsVisible;
	}
}
