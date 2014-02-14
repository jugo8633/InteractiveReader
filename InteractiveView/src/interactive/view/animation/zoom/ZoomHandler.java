package interactive.view.animation.zoom;

import interactive.common.Device;
import interactive.view.slideshow.SlideshowView;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

public class ZoomHandler
{

	private Animator	mCurrentAnimator;

	public ZoomHandler()
	{
		super();
	}

	public void zoomOut(Context context, View view)
	{
		if (mCurrentAnimator != null)
		{
			mCurrentAnimator.cancel();
		}

		// Load the high-resolution "zoomed-in" image.
		final ImageView expandedImageView = new ImageView(context);
		Bitmap bitmap = loadBitmapFromView(context, view);
		expandedImageView.setImageBitmap(bitmap);
	}

	private Bitmap loadBitmapFromView(Context context, View v)
	{
		int nWidth = v.getLayoutParams().width;
		int nHeight = v.getLayoutParams().height;

		if (0 >= nWidth)
		{
			nWidth = getDisplayWidth(context);
		}

		if (0 >= nHeight)
		{
			nHeight = getDisplayHeight(context);
		}

		Bitmap b = Bitmap.createBitmap(nWidth, nHeight, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.measure(MeasureSpec.makeMeasureSpec(v.getLayoutParams().width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(v.getLayoutParams().height, MeasureSpec.EXACTLY));
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		v.draw(c);

		return b;
	}

	private int getDisplayWidth(Context context)
	{
		Device device = new Device(context);
		int nWidth = device.getDeviceWidth();
		device = null;
		return nWidth;
	}

	private int getDisplayHeight(Context context)
	{
		Device device = new Device(context);
		int nHeight = device.getDeviceHeight();
		device = null;
		return nHeight;
	}
}
