package interactive.view.image;

import interactive.common.BitmapHandler;
import interactive.common.Logs;
import interactive.common.Type;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

public class ImageViewHandler
{
	private SparseArray<ImageViewData>	listImageView	= null;
	private boolean						mbIsRelease		= false;

	public ImageViewHandler()
	{
		super();
		listImageView = new SparseArray<ImageViewData>();
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
				Bitmap bitmap = BitmapHandler.readBitmap(strBitmapPath, nWidth, nHeight);
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
					Logs.showTrace("Bitmap recycle id=" + nKey + " $$$$$$$$$$$$$$$$$");
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
