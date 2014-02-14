package interactive.view.animation.flipcard;

import interactive.common.Type;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

public class Rotate3d
{

	public static final int						ROTATE_LEFT				= 0;
	public static final int						ROTATE_RIGHT			= 1;
	private int									mnDirection				= Type.INVALID;
	private SparseArray<OnRotateEndListener>	listOnRotateEndListener	= null;

	public interface OnRotateEndListener
	{
		public void onRotateEnd();
	}

	public Rotate3d()
	{
		super();
		listOnRotateEndListener = new SparseArray<OnRotateEndListener>();
	}

	public void setlistOnRotateEndListener(Rotate3d.OnRotateEndListener listener)
	{
		if (null != listOnRotateEndListener && null != listener)
		{
			listOnRotateEndListener.put(listOnRotateEndListener.size(), listener);
		}
	}

	private void notifyRotateEnd()
	{
		if (null != listOnRotateEndListener)
		{
			for (int i = 0; i < listOnRotateEndListener.size(); ++i)
			{
				listOnRotateEndListener.get(i).onRotateEnd();
			}
		}
	}

	public void applyRotation(View view, float start, float end, int nDirection)
	{
		mnDirection = nDirection;

		// 計算中心點
		final float centerX = view.getX() + (view.getWidth() / 2.0f);
		final float centerY = view.getY() + (view.getHeight() / 2.0f);
		Rotate3dAnimation rotation = new Rotate3dAnimation(start, end, centerX, centerY, (view.getWidth() / 2.0f), true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		// 設置監聽
		rotation.setAnimationListener(new DisplayNextView(view));

		view.startAnimation(rotation);
		rotation = null;
	}

	private final class DisplayNextView implements Animation.AnimationListener
	{
		private View	mView	= null;

		public DisplayNextView(View view)
		{
			mView = view;
		}

		@Override
		public void onAnimationEnd(Animation animation)
		{
			notifyRotateEnd();
			mView.post(new SwapViews(mView));
		}

		@Override
		public void onAnimationRepeat(Animation animation)
		{

		}

		@Override
		public void onAnimationStart(Animation animation)
		{

		}
	}

	private final class SwapViews implements Runnable
	{
		private View	mView	= null;

		public SwapViews(View view)
		{
			mView = view;
		}

		@Override
		public void run()
		{
			final float centerX = mView.getX() + (mView.getWidth() / 2.0f);
			final float centerY = mView.getY() + (mView.getHeight() / 2.0f);
			Rotate3dAnimation rotation = null;

			mView.requestFocus();

			switch (mnDirection)
			{
			case ROTATE_LEFT:
				rotation = new Rotate3dAnimation(90, 0, centerX, centerY, (mView.getWidth() / 2.0f), false);
				break;
			case ROTATE_RIGHT:
				rotation = new Rotate3dAnimation(-90, 0, centerX, centerY, (mView.getWidth() / 2.0f), false);
				break;
			}

			rotation.setDuration(300);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());
			// 開始動畫
			mView.startAnimation(rotation);
			rotation = null;
		}
	}
}
