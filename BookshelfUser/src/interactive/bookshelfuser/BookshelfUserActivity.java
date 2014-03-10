package interactive.bookshelfuser;

import interactive.common.Device;
import interactive.common.Logs;
import interactive.view.flip.AnimationType;
import interactive.view.global.Global;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import android.app.Activity;

public class BookshelfUserActivity extends Activity
{

	private FootbarHandler	footbar					= null;
	private ViewFlipper		flipper					= null;
	private int				mnCurrentFootbarItem	= 0;
	private AnimationType	animationType			= null;
	Animation				slide_in_left, slide_out_right, slide_in_right, slide_out_left;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/** show device information */
		getDeviceInfo(this);

		/** init global*/
		Global.theActivity = this;
		Global.handlerActivity = selfHandler;

		/** load book layout */
		int nResId = Global.getResourceId(this, "activity_main", "layout");
		this.setContentView(nResId);
		flipper = (ViewFlipper) this.findViewById(Global.getResourceId(this, "viewflipper", "id"));
		animationType = new AnimationType();
		flipper.setOutAnimation(animationType.outToLeftAnimation(100));
		flipper.setInAnimation(animationType.inFromRightAnimation(500));

		/** init footbar handler */
		footbar = new FootbarHandler(this);
		footbar.setDefaultSelected(Global.getResourceId(this, "bookCityBtn", "id"));
		footbar.setOnItemSelectedListener(new FootbarHandler.OnItemSelectedListener()
		{
			@Override
			public void OnItemSelected(int nIndexSelected)
			{
				switchMode(nIndexSelected);
				Logs.showTrace("Footbar item selected index=" + nIndexSelected);
			}
		});
	}

	private void getDeviceInfo(Activity activity)
	{
		Device device = new Device(activity);
		Logs.showTrace("==== Device Information ====");
		Logs.showTrace("Display width = " + device.getDisplayWidth() + " Display height = " + device.getDisplayHeight());
		Logs.showTrace("Device width = " + device.getDeviceWidth() + " Device height = " + device.getDeviceHeight());
		Logs.showTrace("Display scale size = " + device.getScaleSize());
		Logs.showTrace("Device orientation = " + device.getOrientation());
		Logs.showTrace("Device inches = " + device.getDisplayIncheSize());
		Logs.showTrace("Device type = " + device.getDeviceType()); // 0:phone 1:tablet
		device = null;
	}

	private void switchMode(int nIndex)
	{
		if (mnCurrentFootbarItem == nIndex)
		{
			return;
		}

		mnCurrentFootbarItem = nIndex;
		flipper.setDisplayedChild(mnCurrentFootbarItem);

		Logs.showTrace("Flipper show child index=" + nIndex);
	}

	private Handler	selfHandler	= new Handler()
								{
									@Override
									public void handleMessage(Message msg)
									{
										super.handleMessage(msg);
									}

								};

}
