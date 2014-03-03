package interactive.view.scrollable;

import interactive.common.EventMessage;
import interactive.view.global.Global;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;

public class AutoScrollableView extends HorizontalScrollView
{
	private ScrollView				verticalScrollView	= null;
	private int						mnOffsetX			= 0;
	private int						mnOffsetY			= 0;
	private Context					theContext			= null;
	private ScrollableImageHandler	imageHandler		= null;
	private boolean					mbCurrentActive		= false;

	public AutoScrollableView(Context context)
	{
		super(context);
		init(context);
	}

	public AutoScrollableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public AutoScrollableView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		theContext = context;
		this.setVerticalScrollBarEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);
		this.setHorizontalScrollBarEnabled(false);
		this.setOverScrollMode(OVER_SCROLL_NEVER);

		verticalScrollView = new ScrollView(context);
		verticalScrollView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		verticalScrollView.setHorizontalScrollBarEnabled(false);
		verticalScrollView.setVerticalScrollBarEnabled(false);
		verticalScrollView.setVerticalFadingEdgeEnabled(false);
		verticalScrollView.setHorizontalFadingEdgeEnabled(false);
		verticalScrollView.setOverScrollMode(OVER_SCROLL_NEVER);

		addView(verticalScrollView);

		imageHandler = new ScrollableImageHandler(selfHandler);
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new LayoutParams(nWidth, nHeight));
		imageHandler.setDisplay(nWidth, nHeight);
	}

	public void setImage(String strImagePath, int nWidth, int nHeight, int nOffsetX, int nOffsetY)
	{
		if (0 < nOffsetX)
		{
			setOffsetX(nOffsetX);
		}

		if (0 < nOffsetY)
		{
			setOffsetY(nOffsetY);
		}

		ImageView imageView = new ImageView(theContext);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setAdjustViewBounds(true);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		imageHandler.setImage(imageView, strImagePath, nWidth, nHeight, nOffsetX, nOffsetY);

		verticalScrollView.removeAllViewsInLayout();
		verticalScrollView.addView(imageView);
	}

	private void setOffsetX(int nX)
	{
		mnOffsetX = nX;
	}

	private void setOffsetY(int nY)
	{
		mnOffsetY = nY;
	}

	public void setPosition(int nChapter, int nPage)
	{
		Global.addActiveNotify(nChapter, nPage, selfHandler);
		Global.addUnActiveNotify(nChapter, nPage, selfHandler);
	}

	private void initOffset()
	{
		this.scrollTo(mnOffsetX, 0);
		verticalScrollView.scrollTo(0, mnOffsetY);
	}

	private Handler	selfHandler	= new Handler()
								{
									@Override
									public void handleMessage(Message msg)
									{
										switch (msg.what)
										{
										case EventMessage.MSG_CURRENT_ACTIVE:
											mbCurrentActive = true;
											imageHandler.runInitAutoImage();
											break;
										case EventMessage.MSG_NOT_CURRENT_ACTIVE:
											if (mbCurrentActive)
											{
												imageHandler.releaseImage();
											}
											mbCurrentActive = false;
											break;
										case EventMessage.MSG_VIEW_INITED:
											initOffset();
											break;
										}
									}
								};
}
