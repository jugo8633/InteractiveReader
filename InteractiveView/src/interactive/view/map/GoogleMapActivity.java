package interactive.view.map;

import interactive.view.global.Global;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GoogleMapActivity extends Activity
{

	public static final String	EXTRA_TAG			= "Tag";
	public static final String	EXTRA_MAP_TYPE		= "MapType";
	public static final String	EXTRA_LATITUDE		= "Latitude";
	public static final String	EXTRA_LONGITUDE		= "Longitude";
	public static final String	EXTRA_ZOOM_LEVEL	= "ZoomLevel";
	public static final String	EXTRA_MARKER		= "Marker";
	public static final String	EXTRA_X				= "X";
	public static final String	EXTRA_Y				= "Y";
	public static final String	EXTRA_WIDTH			= "Width";
	public static final String	EXTRA_HEIGHT		= "Height";
	private Bitmap				mBmpClose			= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Global.theActivity = this;

		RelativeLayout rlMain = new RelativeLayout(this);
		rlMain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		rlMain.setBackgroundColor(Color.parseColor("#80000000"));
		this.setContentView(rlMain);

		Intent intent = getIntent();
		String strTag = intent.getStringExtra(EXTRA_TAG);
		int nMapType = intent.getIntExtra(EXTRA_MAP_TYPE, 0);
		double dLatitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0);
		double dLongitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0);
		int nZoomLevel = intent.getIntExtra(EXTRA_ZOOM_LEVEL, 0);
		String strMarker = intent.getStringExtra(EXTRA_MARKER);
		int nWidth = intent.getIntExtra(EXTRA_WIDTH, LayoutParams.MATCH_PARENT);
		int nHeight = intent.getIntExtra(EXTRA_HEIGHT, LayoutParams.MATCH_PARENT);

		// add google map view
		GoogleMapView googleMapView = new GoogleMapView(this);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(nWidth, nHeight);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		googleMapView.setLayoutParams(layoutParams);
		googleMapView.init(strTag, this, nMapType, dLatitude, dLongitude, nZoomLevel, strMarker);
		rlMain.addView(googleMapView);

		/** add close button */
		addCloseButton(rlMain, googleMapView.getId());
	}

	private void addCloseButton(ViewGroup viewContainer, int nId)
	{
		int nResId = Global.getResourceId(this, "close_button", "drawable");
		mBmpClose = BitmapFactory.decodeResource(getResources(), nResId);
		ImageView imgCloseBtn = new ImageView(this);
		imgCloseBtn.setImageBitmap(mBmpClose);
		imgCloseBtn.setPadding(0, 0, 10, 0);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(60, 60);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.ABOVE, nId);

		imgCloseBtn.setLayoutParams(params);

		imgCloseBtn.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				GoogleMapActivity.this.finish();
				return true;
			}
		});
		viewContainer.addView(imgCloseBtn);
		imgCloseBtn.bringToFront();
	}

	@Override
	protected void onDestroy()
	{
		if (null != mBmpClose)
		{
			if (!mBmpClose.isRecycled())
			{
				mBmpClose.recycle();
			}
			mBmpClose = null;
		}
		super.onDestroy();
	}
}
