package interactive.view.slideshow;

import java.io.Serializable;

/**
 * 
 * @author jugo
 * 利用 Serializable 傳遞物件給其他Activity
 */
public class SlideshowViewItem implements Serializable
{
	private static final long	serialVersionUID		= 0L;
	public static final int		TYPE_IMAGE				= 0;
	public static final int		TYPE_VIDEO				= 1;

	private int					mnType					= -1;
	private String				mstrTypeName			= null;
	private String				mstrTitle				= null;
	private String				mstrDescription			= null;
	private String				mstrTargetId			= null;
	private String				mstrSourceImage			= null;

	// image data
	private String				mstrImageName			= null;
	private String				mstrImageSrc			= null;
	private String				mstrImageGroupId		= null;

	// video data
	private String				mstrVideoName			= null;
	private String				mstrVideoSrc			= null;
	private int					mnVideoType				= 1;
	private String				mstrVideoId				= null;
	private int					mnVideoStart			= 0;
	private int					mnVideoEnd				= 0;
	private boolean				mbVideoAutoplay			= false;
	private boolean				mbVideoLoop				= false;
	private boolean				mbVideoPlayerControls	= true;

	public SlideshowViewItem()
	{

	}

	public SlideshowViewItem(int nType, String strTypeName, String strTitle, String strDescription, String strTargetId,
			String strSourceImage)
	{
		super();
		// TODO Auto-generated constructor stub
		initViewItem(nType, strTypeName, strTitle, strDescription, strTargetId, strSourceImage);
	}

	public void initViewItem(int nType, String strTypeName, String strTitle, String strDescription, String strTargetId,
			String strSourceImage)
	{
		mnType = nType;
		mstrTypeName = strTypeName;
		mstrTitle = strTitle;
		mstrDescription = strDescription;
		mstrTargetId = strTargetId;
		mstrSourceImage = strSourceImage;
	}

	@Override
	protected void finalize() throws Throwable
	{
		// TODO Auto-generated method stub
		super.finalize();
	}

	public void setSlideImage(String strName, String strSrc, String strGroupId)
	{
		mstrImageName = strName;
		mstrImageSrc = strSrc;
		mstrImageGroupId = strGroupId;
	}

	public void setSlideVideo(String strName, String strSrc, String strVideoSrc, int nVideoType, int nStart, int nEnd,
			boolean bAutoplay, boolean bLoop, boolean bPlayerControls)
	{
		mstrVideoName = strName;
		mstrVideoSrc = strSrc;
		mnVideoType = nVideoType;
		mstrVideoId = strVideoSrc;
		mnVideoStart = nStart;
		mnVideoEnd = nEnd;
		mbVideoAutoplay = bAutoplay;
		mbVideoLoop = bLoop;
		mbVideoPlayerControls = bPlayerControls;
	}

	public int getType()
	{
		return mnType;
	}

	public String getTypeName()
	{
		return mstrTypeName;
	}

	public String getTitle()
	{
		return mstrTitle;
	}

	public String getDescription()
	{
		return mstrDescription;
	}

	public String getTargetId()
	{
		return mstrTargetId;
	}

	public String getSourceImage()
	{
		return mstrSourceImage;
	}

	// image data
	public String getImageName()
	{
		return mstrImageName;
	}

	public String getImageSrc()
	{
		return mstrImageSrc;
	}

	public String getImageGroupId()
	{
		return mstrImageGroupId;
	}

	// video data
	public String getVideoName()
	{
		return mstrVideoName;
	}

	public String getVideoSrc()
	{
		return mstrVideoSrc;
	}

	public int getVideoType()
	{
		return mnVideoType;
	}

	public String getVideoId()
	{
		return mstrVideoId;
	}

	public int getVideoStart()
	{
		return mnVideoStart;
	}

	public int getVideoEnd()
	{
		return mnVideoEnd;
	}

	public boolean getVideoAutoplay()
	{
		return mbVideoAutoplay;
	}

	public boolean getVideoLoop()
	{
		return mbVideoLoop;
	}

	public boolean getVideoPlayerControls()
	{
		return mbVideoPlayerControls;
	}
}
