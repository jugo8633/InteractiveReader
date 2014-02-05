package interactive.view.scrollable;

import interactive.common.BitmapHandler;
import interactive.common.EventMessage;
import interactive.common.Type;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class HorizonScrollableView extends HorizontalScrollView
{
	private int		mnOffsetX	= 0;
	private int		mnChapter	= Type.INVALID;
	private int		mnPage		= Type.INVALID;
	private int		mnWidth		= Type.INVALID;
	private Context	theContext	= null;

	public HorizonScrollableView(Context context)
	{
		super(context);
		init(context);
	}

	public HorizonScrollableView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public HorizonScrollableView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		theContext = context;
		setHorizontalScrollBarEnabled(false);
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

		if ((nWidth - nOffsetX) < mnWidth)
		{
			Bitmap bitmapBack = Bitmap
					.createBitmap(nWidth + (mnWidth - (nWidth - nOffsetX)), nHeight, Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(strImagePath, nWidth, nHeight);
			bmp = BitmapHandler.combineBitmap(bitmapBack, bitmapFront);
			bitmapBack.recycle();
			bitmapFront.recycle();
		}
		else
		{
			bmp = BitmapHandler.readBitmap(strImagePath, nWidth, nHeight);
		}

		ImageView imageView = new ImageView(theContext);
		imageView.setScaleType(ScaleType.MATRIX);
		imageView.setAdjustViewBounds(true);
		imageView.setLayoutParams(new LayoutParams(nWidth, nHeight));
		imageView.setImageBitmap(bmp);
		bmp = null;

		removeAllViewsInLayout();
		addView(imageView);

		if (0 < nOffsetX)
		{
			setOffset(nOffsetX);
		}

		if (0 > nOffsetY)
		{
			setPadding(0 - nOffsetY);
		}
	}

	private void setPadding(int nTop)
	{
		this.setPadding(0, nTop, 0, 0);
	}

	private void setOffset(int nX)
	{
		mnOffsetX = nX;
	}

	public void setPosition(int nChapter, int nPage)
	{
		mnChapter = nChapter;
		mnPage = nPage;
		Global.addActiveNotify(mnChapter, mnPage, notifyHandler);
	}

	private void initOffset()
	{
		this.scrollTo(mnOffsetX, 0);
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
