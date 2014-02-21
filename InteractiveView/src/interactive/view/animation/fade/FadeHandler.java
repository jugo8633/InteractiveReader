package interactive.view.animation.fade;

import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Type;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class FadeHandler
{
	public FadeHandler()
	{
		super();
	}

	public void fade(final View view)
	{
		Animation anim = new AlphaAnimation(1.00f, 0.5f);

		anim.setDuration(1000);
		anim.setAnimationListener(new AnimationListener()
		{

			public void onAnimationStart(Animation animation)
			{

			}

			public void onAnimationRepeat(Animation animation)
			{

			}

			public void onAnimationEnd(Animation animation)
			{

			}
		});

		view.startAnimation(anim);
	}

	public void crossFade(final View showView, final View hideView, final Handler handler)
	{
		showView.setAlpha(0f);
		showView.setVisibility(View.VISIBLE);

		showView.animate().alpha(1f).setDuration(1000).setListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				EventHandler.notify(handler, EventMessage.MSG_ANIMATION_END, Type.INVALID, Type.INVALID, null);
			}
		});

		hideView.animate().alpha(0f).setDuration(1000).setListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				hideView.setVisibility(View.GONE);
			}
		});
	}
}
