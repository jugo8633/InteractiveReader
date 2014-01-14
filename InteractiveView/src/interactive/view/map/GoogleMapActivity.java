package interactive.view.map;

import interactive.common.Logs;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GoogleMapActivity extends Activity
{

	public static final String	EXTRA_TAG				= "Tag";
	public static final String	EXTRA_MAP_TYPE			= "MapType";
	public static final String	EXTRA_LATITUDE			= "Latitude";
	public static final String	EXTRA_LONGITUDE			= "Longitude";
	public static final String	EXTRA_ZOOM_LEVEL		= "ZoomLevel";
	public static final String	EXTRA_MARKER			= "Marker";
	public static final String	EXTRA_X					= "X";
	public static final String	EXTRA_Y					= "Y";
	public static final String	EXTRA_WIDTH				= "Width";
	public static final String	EXTRA_HEIGHT			= "Height";
	public static final String	EXTRA_BACKGROUND_IMAGE	= "Background";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		RelativeLayout rlMain = new RelativeLayout(this);
		rlMain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		rlMain.setAlpha(0.80f);
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
		String strBackgroundImage = intent.getStringExtra(EXTRA_BACKGROUND_IMAGE);

		Logs.showTrace("google map activity tag=" + strTag + " map type=" + nMapType + " latitude=" + dLatitude
				+ " longitude=" + dLongitude + " zoom level=" + nZoomLevel + " marker=" + strMarker + " backImage="
				+ strBackgroundImage);

		// add background image
		if (null != strBackgroundImage)
		{
			ImageView backImage = new ImageView(this);
			backImage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			backImage.setImageURI(Uri.parse(strBackgroundImage));
			backImage.setAlpha(0.30f);
			rlMain.addView(backImage);
		}

		// add google map view
		GoogleMapView googleMapView = new GoogleMapView(this);
		googleMapView.setCloseButton(true);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(nWidth, nHeight);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		googleMapView.setLayoutParams(layoutParams);
		googleMapView.init(strTag, this, nMapType, dLatitude, dLongitude, nZoomLevel, strMarker);
		rlMain.addView(googleMapView);

		googleMapView.setOnViewCloseListner(new GoogleMapView.onViewCloseListner()
		{
			public void onViewClosed()
			{
				// TODO Auto-generated method stub
				GoogleMapActivity.this.finish();
			}
		});
	}
}