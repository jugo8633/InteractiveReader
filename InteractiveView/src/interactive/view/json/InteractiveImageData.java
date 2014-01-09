package interactive.view.json;

public class InteractiveImageData
{

	public String	mstrName	= null;
	public int		mnWidth		= 0;
	public int		mnHeight	= 0;
	public int		mnX			= 0;
	public int		mnY			= 0;
	public String	mstrSrc		= null;
	public String	mstrGroupId	= null;

	public InteractiveImageData()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public InteractiveImageData(String strName, int nWidth, int nHeight, int nX, int nY, String strSrc,
			String strGroupId)
	{
		super();
		mstrName = strName;
		mnWidth = nWidth;
		mnHeight = nHeight;
		mnX = nX;
		mnY = nY;
		mstrSrc = strSrc;
		mstrGroupId = strGroupId;
	}
}
