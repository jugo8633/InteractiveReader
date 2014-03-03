package interactive.view.image;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Type;
import interactive.view.global.Global;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

public class ImageViewHandler
{
	private SparseArray<ImageViewData>	listImageView	= null;
	private boolean						mbIsRelease		= false;
	private Runnable					runInitImage	= null;
	private Handler						theHandler		= null;
	private Handler						notifyHandler	= null;

	public ImageViewHandler(Handler handler)
	{
		super();
		notifyHandler = handler;
		theHandler = new Handler();
		listImageView = new SparseArray<ImageViewData>();

		runInitImage = new Runnable()
		{
			@Override
			public void run()
			{
				if (isRelease())
				{
					theHandler.postDelayed(runInitImage, 500);
					return;
				}
				initImageView();
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_PAGE, 0, 0, null);
				EventHandler.notify(notifyHandler, EventMessage.MSG_VIEW_INITED, 0, 0, null);
			}
		};
	}

	@Override
	protected void finalize() throws Throwable
	{
		releaseBitmap();
		super.finalize();
	}

	public void addImageView(ImageView imageView, String strBitmapPath, int nWidth, int nHeight)
	{
		releaseBitmap(imageView.getId());
		listImageView.put(imageView.getId(), new ImageViewData(imageView, null, strBitmapPath, nWidth, nHeight));
	}

	public void runInitImageView()
	{
		EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_PAGE, 0, 0, null);
		theHandler.postDelayed(runInitImage, 500);
	}

	public void initImageView()
	{
		int nKey = Type.INVALID;
		String strBitmapPath = null;
		int nWidth = Type.INVALID;
		int nHeight = Type.INVALID;

		for (int i = 0; i < listImageView.size(); ++i)
		{
			nKey = listImageView.keyAt(i);
			releaseBitmap(nKey);
			strBitmapPath = listImageView.get(nKey).mstrBitmapPath;
			nWidth = listImageView.get(nKey).mnWidth;
			nHeight = listImageView.get(nKey).mnHeight;
			if (null != strBitmapPath && 0 < nWidth && 0 < nHeight)
			{
				Bitmap bitmap = BitmapHandler.readBitmap(strBitmapPath, nWidth, nHeight, false);
				if (null != bitmap)
				{
					listImageView.get(nKey).mImageView.setImageBitmap(bitmap);
					listImageView.get(nKey).mBitmap = bitmap;
				}
			}
			if (null != listImageView.get(nKey).mImageView)
			{
				listImageView.get(nKey).mImageView.setVisibility(View.VISIBLE);
				listImageView.get(nKey).mImageView.invalidate();
			}
		}
	}

	public void releaseBitmap(int nKey)
	{
		if (null != listImageView.get(nKey))
		{
			if (null != listImageView.get(nKey).mBitmap)
			{
				if (!listImageView.get(nKey).mBitmap.isRecycled())
				{
					listImageView.get(nKey).mBitmap.recycle();
				}
				listImageView.get(nKey).mBitmap = null;
			}
		}
	}

	public void releaseBitmap()
	{
		mbIsRelease = true;
		int nKey = Type.INVALID;
		for (int i = 0; i < listImageView.size(); ++i)
		{
			nKey = listImageView.keyAt(i);
			if (null != listImageView.get(nKey).mImageView)
			{
				listImageView.get(nKey).mImageView.setVisibility(View.GONE);
			}
			if (null != listImageView.get(nKey).mBitmap)
			{
				if (!listImageView.get(nKey).mBitmap.isRecycled())
				{
					listImageView.get(nKey).mBitmap.recycle();
				}
				listImageView.get(nKey).mBitmap = null;
			}
		}
		mbIsRelease = false;
	}

	public boolean isRelease()
	{
		return mbIsRelease;
	}

}
