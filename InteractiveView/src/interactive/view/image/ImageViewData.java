package interactive.view.image;

import interactive.common.Type;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageViewData
{
	public ImageView	mImageView		= null;
	public Bitmap		mBitmap			= null;
	public String		mstrBitmapPath	= null;
	public int			mnWidth			= Type.INVALID;
	public int			mnHeight		= Type.INVALID;

	public ImageViewData(ImageView imageView, Bitmap bitmap, String strBitmapPath, int nWidth, int nHeight)
	{
		mImageView = imageView;
		mBitmap = bitmap;
		mstrBitmapPath = strBitmapPath;
		mnWidth = nWidth;
		mnHeight = nHeight;
	}
}
