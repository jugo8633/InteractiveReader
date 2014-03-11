package interactive.bookshelfuser;

import interactive.view.global.Global;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;

public class WelcomePage extends Dialog
{
	private final int	SHOW_DURATION	= 3000;
	private Handler		mHandlerTime	= new Handler();

	public WelcomePage(Context context)
	{
		super(context, Global.getResourceId(context, "full_screen_dialog", "style"));
		this.setContentView(Global.getResourceId(context, "welcome", "layout"));
	}

	@Override
	public void show()
	{
		super.show();
		mHandlerTime.postDelayed(timerRun, SHOW_DURATION);
	}

	private final Runnable	timerRun	= new Runnable()
										{
											public void run()
											{
												WelcomePage.this.dismiss();
											}
										};

}
