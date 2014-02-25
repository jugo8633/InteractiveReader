package interactive.view.slideshow;

import interactive.common.Device;
import interactive.common.Logs;
import interactive.view.global.Global;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class SlideshowViewActivity extends Activity
{

	private SlideshowView	gallery	= null;

	public SlideshowViewActivity()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Global.theActivity = this;

		gallery = new SlideshowView(this);
		gallery.setSlideViewActivity(this);
		gallery.setDisplay(0, 0, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		gallery.setIndicatorShow(true);
		gallery.setShowThumbnail(false);

		RelativeLayout rlMain = new RelativeLayout(this);
		rlMain.addView(gallery);

		this.setContentView(rlMain);

		Intent intent = getIntent();
		ArrayList<SlideshowViewItem> listItem = (ArrayList<SlideshowViewItem>) intent
				.getSerializableExtra(SlideshowView.EXTRA_GALLERY_ITEM);

		int nOrientation = intent.getIntExtra(SlideshowView.EXTRA_ORIENTATION, Configuration.ORIENTATION_PORTRAIT);
		this.setRequestedOrientation(nOrientation);
		int nCurrentItem = intent.getIntExtra(SlideshowView.EXTRA_CURRENT_ITEM, 0);
		Logs.showTrace("nCurrentItem = " + nCurrentItem);
		SparseArray<SlideshowViewItem> slist = new SparseArray<SlideshowViewItem>();

		if (null != listItem && 0 < listItem.size())
		{
			for (int i = 0; i < listItem.size(); ++i)
			{
				SlideshowViewItem viewItem = new SlideshowViewItem(listItem.get(i).getType(), listItem.get(i)
						.getTypeName(), listItem.get(i).getTitle(), listItem.get(i).getDescription(), listItem.get(i)
						.getTargetId(), listItem.get(i).getSourceImage());
				switch (listItem.get(i).getType())
				{
				case SlideshowViewItem.TYPE_IMAGE:
					viewItem.setSlideImage(listItem.get(i).getImageName(), listItem.get(i).getImageSrc(),
							listItem.get(i).getImageGroupId());
					break;
				case SlideshowViewItem.TYPE_VIDEO:
					viewItem.setSlideVideo(listItem.get(i).getVideoName(), listItem.get(i).getVideoSrc(),
							listItem.get(i).getVideoId(), listItem.get(i).getVideoType(), listItem.get(i)
									.getVideoStart(), listItem.get(i).getVideoEnd(),
							listItem.get(i).getVideoAutoplay(), listItem.get(i).getVideoLoop(), listItem.get(i)
									.getVideoPlayerControls());
					break;
				}

				slist.put(slist.size(), viewItem);
			}
		}

		Global.interactiveHandler.initMediaView(this);
		if (0 < slist.size())
		{
			Device device = new Device(this);
			int nDisplayWidth = device.getDeviceWidth();
			gallery.setItem(slist, Global.ScaleSize(nDisplayWidth), Global.ScaleSize(600), true);
			gallery.setCurrentItem(nCurrentItem);
			gallery.initImageView();
		}
	}

	@Override
	protected void onDestroy()
	{
		gallery = null;
		super.onDestroy();
	}

}
