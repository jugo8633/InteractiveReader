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
import android.widget.ScrollView;
import android.widget.ImageView.ScaleType;

public class AutoScrollableView extends HorizontalScrollView
{
	private ScrollView	verticalScrollView	= null;
	private int			mnOffsetX			= 0;
	private int			mnOffsetY			= 0;
	private int			mnChapter			= Type.INVALID;
	private int			mnPage				= Type.INVALID;
	private int			mnWidth				= Type.INVALID;
	private Context		theContext			= null;

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
	}

	public void setDisplay(int nX, int nY, int nWidth, int nHeight)
	{
		setX(nX);
		setY(nY);
		setLayoutParams(new LayoutParams(nWidth, nHeight));
		mnWidth = nWidth;
	}

	private ImageView getImageView(Bitmap bitmap, int nWidth, int nHeight)
	{
		ImageView imageView = new ImageView(theContext);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setAdjustViewBounds(true);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		imageView.setImageBitmap(bitmap);
		return imageView;
	}

	public void setImage(String strImagePath, int nWidth, int nHeight, int nOffsetX, int nOffsetY)
	{
		Bitmap bmp = null;

		if ((nWidth - nOffsetX) < mnWidth)
		{
			Bitmap bitmapBack = Bitmap
					.createBitmap(nWidth + (mnWidth - (nWidth - nOffsetX)), nHeight, Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(strImagePath, nWidth, nHeight);
			bmp = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, 0f, 0f);
			bitmapBack.recycle();
			bitmapFront.recycle();
		}
		else if (0 > nOffsetX)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(nWidth + (0 - nOffsetX), nHeight, Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(theContext, strImagePath, nWidth, nHeight);
			bmp = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, (0 - nOffsetX), 0f);
			bitmapBack.recycle();
			bitmapFront.recycle();
		}
		else
		{
			bmp = BitmapHandler.readBitmap(strImagePath, nWidth, nHeight);
		}

		if (0 > nOffsetY)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(bmp.getWidth(), nHeight + (0 - nOffsetY), Config.ARGB_8888);
			bmp = BitmapHandler.combineBitmap(bitmapBack, bmp, 0f, (0 - nOffsetY));
			bitmapBack.recycle();
		}

		ImageView imageView = getImageView(bmp, nWidth, nHeight);
		bmp = null;

		if (0 < nOffsetX)
		{
			setOffsetX(nOffsetX);
		}

		verticalScrollView.removeAllViewsInLayout();
		verticalScrollView.addView(imageView);
	}

	private void setOffsetX(int nX)
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
		verticalScrollView.scrollTo(0, mnOffsetY);
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
