package interactive.view.scrollable;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Type;
import interactive.view.global.Global;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

public class ScrollableImageHandler
{
	private int			mnType			= Type.INVALID;
	private ImageSet	imageSet		= null;
	private ImageView	mImageView		= null;
	private Bitmap		mBmpImage		= null;
	private int			mnWidth			= Type.INVALID;
	private int			mnHeight		= Type.INVALID;
	private Runnable	runInitImage	= null;
	private Handler		notifyHandler	= null;

	public ScrollableImageHandler(final Handler handler)
	{
		super();
		notifyHandler = handler;
		runInitImage = new Runnable()
		{
			@Override
			public void run()
			{
				switch (mnType)
				{
				case ScrollableView.SCROLL_TYPE_AUTO:
					initAutoImage();
					break;
				case ScrollableView.SCROLL_TYPE_HORIZONTAL:
					initHorizonImage();
					break;
				case ScrollableView.SCROLL_TYPE_VERTICAL:
					initVerticalImage();
					break;
				}
				EventHandler.notify(notifyHandler, EventMessage.MSG_VIEW_INITED, 0, 0, null);
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
			}
		};
	}

	private class ImageSet
	{
		public String	mstrImagePath;
		public int		mnWidth;
		public int		mnHeight;
		public int		mnOffsetX;
		public int		mnOffsetY;

		public ImageSet(String strImagePath, int nWidth, int nHeight, int nOffsetX, int nOffsetY)
		{
			mstrImagePath = strImagePath;
			mnWidth = nWidth;
			mnHeight = nHeight;
			mnOffsetX = nOffsetX;
			mnOffsetY = nOffsetY;
		}
	}

	public void setDisplay(int nWidth, int nHeight)
	{
		mnWidth = nWidth;
		mnHeight = nHeight;
	}

	public void setImage(ImageView imageView, String strImagePath, int nWidth, int nHeight, int nOffsetX, int nOffsetY)
	{
		if (null != imageSet)
		{
			imageSet = null;
		}

		mImageView = imageView;
		mImageView.setVisibility(View.GONE);
		imageSet = new ImageSet(strImagePath, nWidth, nHeight, nOffsetX, nOffsetY);
	}

	public void runInitHorizonImage()
	{
		EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
		mnType = ScrollableView.SCROLL_TYPE_HORIZONTAL;
		notifyHandler.postDelayed(runInitImage, 500);
	}

	public void initHorizonImage()
	{
		releaseBitmap();

		if ((imageSet.mnWidth - imageSet.mnOffsetX) < mnWidth)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(imageSet.mnWidth
					+ (mnWidth - (imageSet.mnWidth - imageSet.mnOffsetX)), imageSet.mnHeight, Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(imageSet.mstrImagePath, imageSet.mnWidth, imageSet.mnHeight,
					true);
			mBmpImage = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, 0f, 0f);
			bitmapBack.recycle();
			bitmapFront.recycle();
		}
		else if (0 > imageSet.mnOffsetX)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(imageSet.mnWidth + (0 - imageSet.mnOffsetX), imageSet.mnHeight,
					Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(imageSet.mstrImagePath, imageSet.mnWidth, imageSet.mnHeight,
					true);
			mBmpImage = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, (0 - imageSet.mnOffsetX), 0f);
			bitmapBack.recycle();
			bitmapFront.recycle();
		}
		else
		{
			mBmpImage = BitmapHandler.readBitmap(imageSet.mstrImagePath, imageSet.mnWidth, imageSet.mnHeight, false);
		}
		mImageView.setImageBitmap(mBmpImage);
		mImageView.setVisibility(View.VISIBLE);
	}

	public void runInitVerticalImage()
	{
		EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
		mnType = ScrollableView.SCROLL_TYPE_VERTICAL;
		notifyHandler.postDelayed(runInitImage, 500);
	}

	public void initVerticalImage()
	{
		releaseBitmap();

		if (0 > imageSet.mnOffsetY)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(imageSet.mnWidth, imageSet.mnHeight + (0 - imageSet.mnOffsetY),
					Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(imageSet.mstrImagePath, imageSet.mnWidth, imageSet.mnHeight,
					true);
			mBmpImage = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, 0f, (0 - imageSet.mnOffsetY));
			bitmapBack.recycle();
			bitmapFront.recycle();
		}
		else if (0 < imageSet.mnOffsetY && (imageSet.mnHeight - imageSet.mnOffsetY) < mnHeight)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(imageSet.mnWidth, imageSet.mnHeight + imageSet.mnOffsetY,
					Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(imageSet.mstrImagePath, imageSet.mnWidth, imageSet.mnHeight,
					true);
			mBmpImage = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, 0f, 0f);
			bitmapBack.recycle();
			bitmapFront.recycle();
		}
		else
		{
			mBmpImage = BitmapHandler.readBitmap(imageSet.mstrImagePath, imageSet.mnWidth, imageSet.mnHeight, false);
		}

		mImageView.setImageBitmap(mBmpImage);
		mImageView.setVisibility(View.VISIBLE);
	}

	public void runInitAutoImage()
	{
		EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
		mnType = ScrollableView.SCROLL_TYPE_AUTO;
		notifyHandler.postDelayed(runInitImage, 500);
	}

	public void initAutoImage()
	{
		releaseBitmap();

		if ((imageSet.mnWidth - imageSet.mnOffsetX) < mnWidth)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(imageSet.mnWidth
					+ (mnWidth - (imageSet.mnWidth - imageSet.mnOffsetX)), imageSet.mnHeight, Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(imageSet.mstrImagePath, imageSet.mnWidth, imageSet.mnHeight,
					true);
			mBmpImage = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, 0f, 0f);
			bitmapBack.recycle();
			bitmapFront.recycle();
		}
		else if (0 > imageSet.mnOffsetX)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(imageSet.mnWidth + (0 - imageSet.mnOffsetX), imageSet.mnHeight,
					Config.ARGB_8888);
			Bitmap bitmapFront = BitmapHandler.readBitmap(imageSet.mstrImagePath, imageSet.mnWidth, imageSet.mnHeight,
					true);
			mBmpImage = BitmapHandler.combineBitmap(bitmapBack, bitmapFront, (0 - imageSet.mnOffsetX), 0f);
			bitmapBack.recycle();
			bitmapFront.recycle();
		}
		else
		{
			mBmpImage = BitmapHandler.readBitmap(imageSet.mstrImagePath, imageSet.mnWidth, imageSet.mnHeight, true);
		}

		if (0 > imageSet.mnOffsetY)
		{
			Bitmap bitmapBack = Bitmap.createBitmap(mBmpImage.getWidth(), imageSet.mnHeight + (0 - imageSet.mnOffsetY),
					Config.ARGB_8888);
			mBmpImage = BitmapHandler.combineBitmap(bitmapBack, mBmpImage, 0f, (0 - imageSet.mnOffsetY));
			bitmapBack.recycle();
		}

		mImageView.setImageBitmap(mBmpImage);
		mImageView.setVisibility(View.VISIBLE);
	}

	public void releaseImage()
	{
		mImageView.setVisibility(View.GONE);
		releaseBitmap();
	}

	public void releaseBitmap()
	{
		if (null != mBmpImage)
		{
			if (!mBmpImage.isRecycled())
			{
				mBmpImage.recycle();
			}
			mBmpImage = null;
		}
	}
}
