package interactive.view.animation.zoom;

<<<<<<< HEAD
import interactive.common.Device;
import interactive.view.slideshow.SlideshowView;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
=======
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
>>>>>>> 204daa84b89a9ac70a47661bbbb93e9f8e9deb15

public class ZoomHandler
{

<<<<<<< HEAD
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
=======
	private Handler		notifyHandler		= null;
	private Animator	mCurrentAnimator	= null;
	/**
	 * The system "short" animation time duration, in milliseconds. This duration is ideal for
	 * subtle animations or animations that occur very frequently.
	 */
	private int			mShortAnimationDuration;

	public ZoomHandler(Context context)
	{
		super();
		// Retrieve and cache the system's default "short" animation time.
		mShortAnimationDuration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
	}

	public void zoomOut(View view, float fScaleSize)
	{
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1f);
		anim1.setDuration(mShortAnimationDuration);
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleX", fScaleSize);
		anim2.setDuration(mShortAnimationDuration);
		ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, "scaleY", 1f);
		anim3.setDuration(mShortAnimationDuration);
		ObjectAnimator anim4 = ObjectAnimator.ofFloat(view, "scaleY", fScaleSize);
		anim4.setDuration(mShortAnimationDuration);

		AnimatorSet set2 = new AnimatorSet();
		AnimatorSet set3 = new AnimatorSet();

		set2.play(anim2);
		set3.play(anim4);
		set.play(anim1).before(set2);
		set.play(anim3).before(set3);
		set.start();

		set.addListener(new AnimatorListener()
		{

			@Override
			public void onAnimationStart(Animator animation)
			{

			}

			@Override
			public void onAnimationRepeat(Animator animation)
			{

			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				EventHandler.notify(notifyHandler, EventMessage.MSG_ANIMATION_END, 0, 0, null);
			}

			@Override
			public void onAnimationCancel(Animator animation)
			{

			}
		});
	}

	public void zoomIn(View viewOut, View viewIn, ViewGroup container)
	{
		if (null == viewOut || null == viewIn)
		{
			return;
		}

		// If there's an animation in progress, cancel it immediately and proceed with this one.
		if (mCurrentAnimator != null)
		{
			mCurrentAnimator.cancel();
		}

		// Calculate the starting and ending bounds for the zoomed-in image. This step
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the zoom out view, and the
		// final bounds are the global visible rectangle of the container view. Also
		// set the container view's offset as the origin for the bounds, since that's
		// the origin for the positioning animation properties (X, Y).
		viewOut.getGlobalVisibleRect(startBounds);
		container.getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);

		// Adjust the start bounds to be the same aspect ratio as the final bounds using the
		// "center crop" technique. This prevents undesirable stretching during the animation.
		// Also calculate the start scaling factor (the end scaling factor is always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height())
		{
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		}
		else
		{
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		// Hide the thumbnail and show the zoomed-in view. When the animation begins,
		// it will position the zoomed-in view in the place of the thumbnail.
		viewOut.setAlpha(0f);
		viewIn.setVisibility(View.VISIBLE);

		// Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
		// the zoomed-in view (the default is the center of the view).
		viewIn.setPivotX(0f);
		viewIn.setPivotY(0f);

		// Construct and run the parallel animation of the four translation and scale properties
		// (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(ObjectAnimator.ofFloat(viewIn, View.X, startBounds.left, finalBounds.left))
				.with(ObjectAnimator.ofFloat(viewIn, View.Y, startBounds.top, finalBounds.top))
				.with(ObjectAnimator.ofFloat(viewIn, View.SCALE_X, startScale, 1f))
				.with(ObjectAnimator.ofFloat(viewIn, View.SCALE_Y, startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation)
			{
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;
	}

	public void zoomOut(final View viewOut, final View viewIn)
	{
		if (null == viewOut || null == viewIn)
		{
			return;
		}

		// Calculate the starting and ending bounds for the zoomed-in image. This step
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();

		// The start bounds are the global visible rectangle of the zoom out view, and the
		// final bounds are the global visible rectangle of the container view. Also
		// set the container view's offset as the origin for the bounds, since that's
		// the origin for the positioning animation properties (X, Y).
		viewOut.getGlobalVisibleRect(startBounds);
		viewIn.getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);

		// Adjust the start bounds to be the same aspect ratio as the final bounds using the
		// "center crop" technique. This prevents undesirable stretching during the animation.
		// Also calculate the start scaling factor (the end scaling factor is always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height())
		{
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		}
		else
		{
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}

		final float startScaleFinal = startScale;
		// Animate the four positioning/sizing properties in parallel, back to their
		// original values.
		AnimatorSet set = new AnimatorSet();
		set.play(ObjectAnimator.ofFloat(viewIn, View.X, startBounds.left))
				.with(ObjectAnimator.ofFloat(viewIn, View.Y, startBounds.top))
				.with(ObjectAnimator.ofFloat(viewIn, View.SCALE_X, startScaleFinal))
				.with(ObjectAnimator.ofFloat(viewIn, View.SCALE_Y, startScaleFinal));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				viewOut.setAlpha(1f);
				viewIn.setVisibility(View.GONE);
				mCurrentAnimator = null;
			}

			@Override
			public void onAnimationCancel(Animator animation)
			{
				viewOut.setAlpha(1f);
				viewIn.setVisibility(View.GONE);
				mCurrentAnimator = null;
			}
		});
		set.start();
	}

	public void setNotifyHandler(Handler handler)
	{
		notifyHandler = handler;
>>>>>>> 204daa84b89a9ac70a47661bbbb93e9f8e9deb15
	}
}
