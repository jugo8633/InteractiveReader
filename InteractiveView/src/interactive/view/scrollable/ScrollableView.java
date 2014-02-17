package interactive.view.scrollable;

import interactive.common.Type;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ScrollableView extends RelativeLayout
{

	public static final int	SCROLL_TYPE_AUTO		= 0;
	public static final int	SCROLL_TYPE_VERTICAL	= 1;
	public static final int	SCROLL_TYPE_HORIZONTAL	= 2;

	private int				mnDisplayWidth			= Type.INVALID;
	private int				mnDisplayHeight			= Type.INVALID;
	private int				mnDisplayX				= 0;
	private int				mnDisplayY				= 0;
	private int				mnChapter				= Type.INVALID;
	private int				mnPage					= Type.INVALID;

	public ScrollableView(Context context)
	{
		super(context);
	}

	public ScrollableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ScrollableView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		this.setX(nX);
		this.setY(nY);
		this.setLayoutParams(new LayoutParams(nWidth, nHeight));
		mnDisplayX = nX;
		mnDisplayY = nY;
		mnDisplayWidth = nWidth;
		mnDisplayHeight = nHeight;
	}

	private void createHorizonScrollView(String strTag, String strPath, int nWidth, int nHeight, int nScrollType,
			int nOffsetX, int nOffsetY, ViewGroup container)
	{
		HorizonScrollableView hview = new HorizonScrollableView(getContext());
		hview.setPosition(mnChapter, mnPage);
		hview.setTag(strTag);
		hview.setDisplay(mnDisplayX, mnDisplayY, mnDisplayWidth, mnDisplayHeight);
		hview.setImage(strPath, nWidth, nHeight, nOffsetX, nOffsetY);
		container.addView(hview);
	}

	private void createVerticalScrollView(String strTag, String strPath, int nWidth, int nHeight, int nScrollType,
			int nOffsetX, int nOffsetY, ViewGroup container)
	{
		VerticalScrollableView vView = new VerticalScrollableView(getContext());
		vView.setPosition(mnChapter, mnPage);
		vView.setTag(strTag);
		vView.setDisplay(mnDisplayX, mnDisplayY, mnDisplayWidth, mnDisplayHeight);
		vView.setImage(strPath, nWidth, nHeight, nOffsetX, nOffsetY);
		container.addView(vView);
	}

	private void createAutoScrollView(String strTag, String strPath, int nWidth, int nHeight, int nScrollType,
			int nOffsetX, int nOffsetY, ViewGroup container)
	{
		if (mnDisplayWidth < nWidth && mnDisplayHeight >= nHeight)
		{
			createHorizonScrollView(strTag, strPath, nWidth, nHeight, nScrollType, nOffsetX, nOffsetY, container);
			return;
		}
		if (mnDisplayHeight < nHeight && mnDisplayWidth >= nWidth)
		{
			createVerticalScrollView(strTag, strPath, nWidth, nHeight, nScrollType, nOffsetX, nOffsetY, container);
			return;
		}

		AutoScrollableView autoView = new AutoScrollableView(getContext());
		autoView.setPosition(mnChapter, mnPage);
		autoView.setTag(strTag);
		autoView.setDisplay(mnDisplayX, mnDisplayY, mnDisplayWidth, mnDisplayHeight);
		autoView.setImage(strPath, nWidth, nHeight, nOffsetX, nOffsetY);
		container.addView(autoView);
	}

	public void setImage(String strTag, String strPath, int nWidth, int nHeight, int nScrollType, int nOffsetX,
			int nOffsetY, ViewGroup container)
	{
		switch (nScrollType)
		{
		case SCROLL_TYPE_HORIZONTAL:
			createHorizonScrollView(strTag, strPath, nWidth, nHeight, nScrollType, nOffsetX, nOffsetY, container);
			break;
		case SCROLL_TYPE_VERTICAL:
			createVerticalScrollView(strTag, strPath, nWidth, nHeight, nScrollType, nOffsetX, nOffsetY, container);
			break;
		case SCROLL_TYPE_AUTO:
			createAutoScrollView(strTag, strPath, nWidth, nHeight, nScrollType, nOffsetX, nOffsetY, container);
			break;
		}
	}

	public void setPosition(int nChapter, int nPage)
	{
		mnChapter = nChapter;
		mnPage = nPage;
	}
}
