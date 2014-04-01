package interactive.bookshelfuser;

import interactive.common.Device;
import interactive.common.EventHandler;
import interactive.common.Logs;
import interactive.view.global.Global;
import interactive.widget.BookGallery;
import interactive.widget.ShapButton;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class BookListHandler
{
	private final int		EXTEND_HIGHT			= 1200;
	private BookGallery		bookCityAllGallery		= null;
	private BookGallery		bookCityFreeGallery		= null;
	private BookGallery		bookCitySpecialGallery	= null;
	private BookGallery		bookCityPreviousGallery	= null;
	private Handler			notifyHandler			= null;
	public static final int	MSG_SUBSCRIBT			= 1000;

	public BookListHandler()
	{
		super();
	}

	public void initAllBookList(Activity activity)
	{
		bookCityAllGallery = (BookGallery) activity.findViewById(Global.getResourceId(activity, "gallery_all_book",
				"id"));
		LayoutInflater layInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		RelativeLayout viewChild = (RelativeLayout) layInflater.inflate(
				Global.getResourceId(activity, "book_city_all_book_first", "layout"), null, false);
		bookCityAllGallery.addPageView(viewChild);
		RelativeLayout extendLayout = (RelativeLayout) viewChild.findViewById(Global.getResourceId(activity,
				"book_city_all_book_first_extend_layout", "id"));
		Device device = new Device(activity);
		if (EXTEND_HIGHT > device.getDeviceHeight())
		{
			extendLayout.setVisibility(View.GONE);
		}
		device = null;

		ShapButton subBtn = (ShapButton) viewChild.findViewById(Global.getResourceId(activity,
				"book_city_all_book_first_subscribt", "id"));
		subBtn.setOnButtonClickedListener(new ShapButton.OnButtonClickedListener()
		{
			@Override
			public void OnButtonClicked()
			{
				Logs.showTrace("billing........................");
				EventHandler.notify(notifyHandler, MSG_SUBSCRIBT, 0, 0, null);
			}
		});

		viewChild = (RelativeLayout) layInflater.inflate(
				Global.getResourceId(activity, "book_city_all_book_list", "layout"), null, false);
		bookCityAllGallery.addPageView(viewChild);
		bookCityAllGallery.updateGallery();
		initextendItem(activity, viewChild);

	}

	public void initFreeBookList(Activity activity)
	{
		bookCityFreeGallery = (BookGallery) activity.findViewById(Global.getResourceId(activity, "gallery_free_book",
				"id"));

		LayoutInflater layInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		RelativeLayout viewChild = (RelativeLayout) layInflater.inflate(
				Global.getResourceId(activity, "book_city_all_book_list", "layout"), null, false);

		bookCityFreeGallery.addPageView(viewChild);
		bookCityFreeGallery.updateGallery();
		initextendItem(activity, viewChild);
	}

	public void initSpecialBookList(Activity activity)
	{
		bookCitySpecialGallery = (BookGallery) activity.findViewById(Global.getResourceId(activity,
				"gallery_special_book", "id"));

		LayoutInflater layInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		RelativeLayout viewChild = (RelativeLayout) layInflater.inflate(
				Global.getResourceId(activity, "book_city_all_book_list", "layout"), null, false);

		bookCitySpecialGallery.addPageView(viewChild);
		bookCitySpecialGallery.updateGallery();
		initextendItem(activity, viewChild);
	}

	public void initPreviousBookList(Activity activity)
	{
		bookCityPreviousGallery = (BookGallery) activity.findViewById(Global.getResourceId(activity,
				"gallery_previous_book", "id"));

		LayoutInflater layInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		RelativeLayout viewChild = (RelativeLayout) layInflater.inflate(
				Global.getResourceId(activity, "book_city_all_book_list", "layout"), null, false);

		bookCityPreviousGallery.addPageView(viewChild);
		bookCityPreviousGallery.updateGallery();
		initextendItem(activity, viewChild);
	}

	private void initextendItem(Activity activity, ViewGroup viewGroup)
	{
		FrameLayout bookItemLayout = (FrameLayout) viewGroup.findViewById(Global.getResourceId(activity,
				"book_city_all_book_list_layout4", "id"));
		View seprarteView = viewGroup.findViewById(Global.getResourceId(activity, "separate_line3", "id"));

		Device device = new Device(activity);
		if (EXTEND_HIGHT > device.getDeviceHeight())
		{
			bookItemLayout.setVisibility(View.GONE);
			seprarteView.setVisibility(View.GONE);
		}
		device = null;
	}

	public void setNotifyHandler(Handler handler)
	{
		notifyHandler = handler;
	}

}
