package interactive.view.animation.move;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Animator.AnimatorListener;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

public class MoveHandler
{
	public MoveHandler()
	{
		super();
	}

	public static void move(final View view, int nX1, int nX2, int nY1, int nY2, int nDuration, final Handler notifyHandler)
	{
		AnimationSet animSet = new AnimationSet(false);

		// 動畫設定 (指定移動動畫) (x1, x2, y1, y2)
		Animation am = new TranslateAnimation(nX1, nX2, nY1, nY2);

		// 動畫開始到結束的執行時間 (1000 = 1 秒)
		am.setDuration(nDuration);

		//避免元件又回到初始位置
		am.setFillEnabled(true);
		am.setFillAfter(true);

		// 動畫重複次數 (-1 表示一直重複)
		am.setRepeatCount(0);

		am.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationEnd(Animation animation)
			{
				view.clearAnimation();
				EventHandler.notify(notifyHandler, EventMessage.MSG_ANIMATION_END, EventMessage.MSG_ANIMATION_MOVE, 0,
						null);
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{

			}

			@Override
			public void onAnimationStart(Animation animation)
			{

			}
		});

		// 動畫開始
		animSet.addAnimation(am);
		view.startAnimation(animSet);
	}

	public static void slideView(final View view, final int nX1, final int nX2, final int nY1, final int nY2,
			final int nDuration, final Handler notifyHandler)
	{
		TranslateAnimation animation = new TranslateAnimation(nX1, nX2, nY1, nY2);
		animation.setInterpolator(new OvershootInterpolator());
		animation.setDuration(nDuration);
		animation.setStartOffset(100);
		//		animation.setFillEnabled(true);
		//		animation.setFillAfter(true);
		animation.setAnimationListener(new Animation.AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				int left = view.getLeft() + (int) (nX2 - nX1);
				int top = view.getTop() + (int) (nY2 - nY2);
				int width = view.getWidth();
				int height = view.getHeight();
				view.clearAnimation();
				view.layout(left, top, left + width, top + height);
				EventHandler.notify(notifyHandler, EventMessage.MSG_ANIMATION_END, EventMessage.MSG_ANIMATION_MOVE, 0,
						null);
			}
		});
		view.startAnimation(animation);
	}
}
