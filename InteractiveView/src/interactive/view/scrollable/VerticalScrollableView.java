package interactive.view.scrollable;

import interactive.common.BitmapHandler;
import interactive.common.EventMessage;
import interactive.common.Type;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;

public class VerticalScrollableView extends ScrollView
{

	private int		mnOffsetY	= 0;
	private int		mnChapter	= Type.INVALID;
	private int		mnPage		= Type.INVALID;
	private int		mnWidth		= Type.INVALID;
	private Context	theContext	= null;

	public VerticalScrollableView(Context context)
	{
		super(context);
		init(context);
	}

	public VerticalScrollableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public VerticalScrollableView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		theContext = context;
		setVerticalScrollBarEnabled(false);
		this.setVerticalFadingEdgeEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);
		this.setHorizontalScrollBarEnabled(false);
		this.setPadding(0, 0, 0, 0);
	}

	public void setPosition(int nChapter, int nPage)
	{
		mnChapter = nChapter;
		mnPage = nPage;
		Global.addActiveNotify(mnChapter, mnPage, notifyHandler);
	}

	private void initOffset()
	{
		this.scrollTo(0, mnOffsetY);
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new LayoutParams(nWidth, nHeight));
		mnWidth = nWidth;
	}

	public void setImage(String strImagePath, int nWidth, int nHeight, int nOffsetX, int nOffsetY)
	{
		Bitmap bmp = null;

		bmp = BitmapHandler.readBitmap(strImagePath, nWidth, nHeight);

		ImageView imageView = new ImageView(theContext);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setAdjustViewBounds(false);
		imageView.setLayoutParams(new LayoutParams(nWidth, nHeight));
		imageView.setImageBitmap(bmp);
		bmp = null;

		if ((nWidth - nOffsetX) < mnWidth)
		{
			setPadding(0, 0, mnWidth - (nWidth - nOffsetX), 0);
			if (0 < nOffsetX)
			{
				imageView.setX(0 - nOffsetX);
			}
		}

		removeAllViewsInLayout();
		addView(imageView);

		if (0 < nOffsetY)
		{
			setOffset(nOffsetY);
		}

		if (0 > nOffsetX)
		{
			setPadding(0 - nOffsetX);
		}
	}

	private void setPadding(int nLeft)
	{
		this.setPadding(nLeft, 0, 0, 0);
	}

	private void setOffset(int nY)
	{
		mnOffsetY = nY;
	}

	private Handler	notifyHandler	= new Handler()
									{
										@Override
										public void handleMessage(Message msg)
										{
											switch (msg.what)
											{
											case EventMessage.MSG_CURRENT_ACTIVE:
												initOffset();
												break;
											}
											super.handleMessage(msg);
										}
									};
}
