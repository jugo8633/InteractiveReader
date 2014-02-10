package interactive.reader;

import interactive.common.BitmapHandler;
import interactive.common.Device;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.ObjectFactory;
import interactive.common.Share;
import interactive.common.SqliteHandler;
import interactive.common.Type;
import interactive.view.data.PageData;
import interactive.view.flip.FlipperView;
import interactive.view.gallery.GalleryView;
import interactive.view.gallery.GalleryView.onScrollStopListner;
import interactive.view.global.Global;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

public class OptionHandler
{

	private FlipperView			flipperView				= null;
	private Header				theHeader				= null;
	private ObjectFactory		objectFactory			= null;
	private int					mnHeaderCurrentSelected	= Type.INVALID;
	private CategoryListAdapter	theCategoryAdapter		= null;
	private FavoriteListAdapter	theFavoriteAdapter		= null;
	private ListView			listViewCategory		= null;
	private ListView			listViewFavorite		= null;
	private int					mnFavoriteItemId		= Type.INVALID;
	private int					mnCategoryItemId		= Type.INVALID;
	private Share				share					= null;
	private int					mnFavoriteAddId			= Type.INVALID;
	private int					mnFavoriteDeleteId		= Type.INVALID;

	public class Header
	{
		public int	mnBackId		= Type.INVALID;
		public int	mnCategoryId	= Type.INVALID;
		public int	mnChapId		= Type.INVALID;
		public int	mnShareId		= Type.INVALID;
		public int	mnFavoriteId	= Type.INVALID;

		public int	mnCategoryIndex	= Type.INVALID;
		public int	mnChapIndex		= Type.INVALID;
		public int	mnShareIndex	= Type.INVALID;
		public int	mnFavoriteIndex	= Type.INVALID;
	}

	public OptionHandler(Activity activity)
	{
		super();
		init(activity);
	}

	private void init(Activity activity)
	{
		int nResId = Global.getResourceId(activity, "fliperViewReaderOption", "id");
		flipperView = (FlipperView) activity.findViewById(nResId);
		if (null == flipperView)
		{
			Logs.showTrace("Flipper view is invalid");
			return;
		}

		objectFactory = new ObjectFactory(activity);
		theHeader = new Header();
		theHeader.mnBackId = Global.getResourceId(activity, "imageViewBack", "id");
		theHeader.mnCategoryId = Global.getResourceId(activity, "imageViewCategory", "id");
		theHeader.mnChapId = Global.getResourceId(activity, "imageViewChap", "id");
		theHeader.mnShareId = Global.getResourceId(activity, "imageViewShare", "id");
		theHeader.mnFavoriteId = Global.getResourceId(activity, "imageViewFavorite", "id");

		setOptionTouch(
				activity,
				objectFactory.addImageButton(theHeader.mnBackId,
						Global.getResourceId(activity, "backpage_normal", "drawable"),
						Global.getResourceId(activity, "backpage_rollover", "drawable"),
						Global.getResourceId(activity, "backpage_normal", "drawable")));

		setOptionTouch(
				activity,
				objectFactory.addImageButton(theHeader.mnCategoryId,
						Global.getResourceId(activity, "category_normal", "drawable"),
						Global.getResourceId(activity, "category_rollover", "drawable"),
						Global.getResourceId(activity, "category_click", "drawable")));

		setOptionTouch(
				activity,
				objectFactory.addImageButton(theHeader.mnChapId,
						Global.getResourceId(activity, "chap_normal", "drawable"),
						Global.getResourceId(activity, "chap_rollover", "drawable"),
						Global.getResourceId(activity, "chap_click", "drawable")));

		setOptionTouch(
				activity,
				objectFactory.addImageButton(theHeader.mnShareId,
						Global.getResourceId(activity, "share_normal", "drawable"),
						Global.getResourceId(activity, "share_rollover", "drawable"),
						Global.getResourceId(activity, "share_click", "drawable")));

		setOptionTouch(
				activity,
				objectFactory.addImageButton(theHeader.mnFavoriteId,
						Global.getResourceId(activity, "love_normal", "drawable"),
						Global.getResourceId(activity, "love_rollover", "drawable"),
						Global.getResourceId(activity, "love_click", "drawable")));

		flipperView.setNotifyHandler(Global.handlerActivity);

		initShare(activity);
		initFavorite(activity);
	}

	public void initCategory(Activity activity)
	{
		theHeader.mnCategoryIndex = flipperView.addChild(Global.getResourceId(activity, "reader_category", "layout"));
		theCategoryAdapter = new CategoryListAdapter(activity);
		listViewCategory = (ListView) activity.findViewById(Global.getResourceId(activity, "listViewCategory", "id"));
		listViewCategory.setOnItemClickListener(onItemClickListener);

		mnCategoryItemId = Global.getResourceId(activity, "relativeLayoutCategoryListMain", "id");
	}

	public void initChapOption(final Activity activity)
	{

		Device device = new Device(activity);
		float nScale = device.getScaleSize();
		device = null;
		int nChapItemWidth = (int) Math.floor(145 * nScale);
		int nChapItemHeight = (int) Math.floor(194 * nScale);

		theHeader.mnChapIndex = flipperView.addChild(Global.getResourceId(activity, "reader_chap", "layout"));
		final GalleryView galleryView = (GalleryView) flipperView.findViewById(Global.getResourceId(activity,
				"galleryViewChap", "id"));
		galleryView.setActivity(activity);
		galleryView.setNotifyHandler(Global.handlerActivity);

		SparseArray<SparseArray<ImageView>> listImage = new SparseArray<SparseArray<ImageView>>();
		for (int nChapter = 0; nChapter < PageData.listPageData.size(); ++nChapter)
		{
			SparseArray<ImageView> imgs = new SparseArray<ImageView>();
			for (int nPage = 0; nPage < PageData.listPageData.get(nChapter).size(); ++nPage)
			{
				ImageView img = new ImageView(activity);

				Bitmap bmp = BitmapHandler.readBitmap(
						PageData.listPageData.get(nChapter).get(nPage).strShapTiny, nChapItemWidth, nChapItemHeight);
				img.setImageBitmap(bmp);
				img.setLayoutParams(new LayoutParams(nChapItemWidth, nChapItemHeight));
				//	img.setImageURI(Uri.parse(PageData.listPageData.get(nChapter).get(nPage).strShapTiny));
				img.setScaleType(ScaleType.FIT_XY);
				img.setAdjustViewBounds(true);
				imgs.put(nPage, img);
				img = null;
			}
			listImage.put(nChapter, imgs);
			imgs = null;
		}
		galleryView.addChild(listImage, nChapItemWidth, nChapItemHeight);
		listImage = null;
		galleryView.setOnScrollStopListner(new onScrollStopListner()
		{
			@Override
			public void onScrollStoped()
			{
				int nCurrentItem = galleryView.getCurrentItem();
				String strChapName = PageData.listPageData.get(nCurrentItem).get(0).strChapterName;
				String strDescript = PageData.listPageData.get(nCurrentItem).get(0).strDescript;
				TextView textview = (TextView) flipperView.findViewById(Global.getResourceId(activity,
						"textViewChapTitle", "id"));
				textview.setText(strChapName);
				textview = (TextView) flipperView.findViewById(Global.getResourceId(activity, "textViewChapDescript",
						"id"));
				textview.setText(strDescript);
				textview = null;
			}
		});
	}

	private void initShare(Activity activity)
	{
		share = new Share(activity);
	}

	private void initFavorite(final Activity activity)
	{
		theHeader.mnFavoriteIndex = flipperView.addChild(Global.getResourceId(activity, "reader_favorite", "layout"));
		listViewFavorite = (ListView) activity.findViewById(Global.getResourceId(activity, "listViewFavorite", "id"));
		theFavoriteAdapter = new FavoriteListAdapter(activity);
		mnFavoriteAddId = Global.getResourceId(activity, "add_normal", "drawable");
		mnFavoriteDeleteId = Global.getResourceId(activity, "delete_normal", "drawable");
		mnFavoriteItemId = Global.getResourceId(activity, "relativeLayoutFavoriteItem", "id");
		int nResId = Global.getResourceId(activity, "imageViewModifyFavorite", "id");
		ImageView imgNodify = (ImageView) activity.findViewById(nResId);
		imgNodify.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				PageData.Data pageData = PageData.listPageData.get(Global.currentChapter).get(Global.currentPage);
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					pageData.bIsFavorite = pageData.bIsFavorite ? false : true;
					updateFavoriteDB(activity, pageData.bIsFavorite, Global.currentChapter, Global.currentPage);
					showFavorite(activity, Global.currentChapter, Global.currentPage);
					break;
				}
				return true;
			}
		});
		listViewFavorite.setOnItemClickListener(onItemClickListener);
	}

	private void updateFavoriteDB(Activity activity, boolean bIsAdd, int nChapter, int nPage)
	{
		SqliteHandler sqliteHandler = new SqliteHandler(activity);
		if (bIsAdd)
		{
			sqliteHandler.addFavorite(nChapter, nPage);
		}
		else
		{
			sqliteHandler.deleteFavorite(nChapter, nPage);
		}
		sqliteHandler.close();
		sqliteHandler = null;
	}

	private void showFavorite(Activity activity, int nCurrentChapter, int nCurrentPage)
	{
		theFavoriteAdapter.clear();

		int nResId = Global.getResourceId(activity, "imageViewModifyFavorite", "id");
		ImageView imgNodify = (ImageView) activity.findViewById(nResId);

		PageData.Data pageData = PageData.listPageData.get(nCurrentChapter).get(nCurrentPage);
		if (pageData.bIsFavorite)
		{
			imgNodify.setImageResource(mnFavoriteDeleteId);
		}
		else
		{
			imgNodify.setImageResource(mnFavoriteAddId);
		}

		for (int nChapter = 0; nChapter < PageData.listPageData.size(); ++nChapter)
		{
			for (int nPage = 0; nPage < PageData.listPageData.get(nChapter).size(); ++nPage)
			{
				pageData = PageData.listPageData.get(nChapter).get(nPage);
				if (pageData.bIsFavorite)
				{
					theFavoriteAdapter.addItemData(pageData.strShapTiny, nChapter, nPage, pageData.strChapterName,
							pageData.strDescript);
				}
			}
		}

		listViewFavorite.removeAllViewsInLayout();
		listViewFavorite.setAdapter(theFavoriteAdapter);
	}

	private void setOptionTouch(Activity activity, int nResId)
	{
		ImageView imageView = (ImageView) activity.findViewById(nResId);
		if (null != imageView)
		{
			imageView.setOnTouchListener(optionTouchListener);
		}
	}

	private void lunchOption(int nResId)
	{
		if (Type.INVALID != mnHeaderCurrentSelected)
		{
			objectFactory.setImgBtnNormal(mnHeaderCurrentSelected);
		}
		objectFactory.setImgBtnTouchUp(nResId);
		mnHeaderCurrentSelected = nResId;

		if (theHeader.mnBackId == nResId)
		{
			EventHandler.notify(Global.handlerActivity, EventMessage.MSG_GO_FORWARD, 0, 0, null);
		}
		else if (theHeader.mnCategoryId == nResId)
		{
			initCategory();
			flipperView.showView(theHeader.mnCategoryIndex);
		}
		else if (theHeader.mnChapId == nResId)
		{
			flipperView.showView(theHeader.mnChapIndex);
		}
		else if (theHeader.mnShareId == nResId)
		{
			PageData.Data pageData = PageData.listPageData.get(Global.currentChapter).get(Global.currentPage);
			SparseArray<String> listImage = new SparseArray<String>();
			listImage.put(listImage.size(), pageData.strShapTiny);
			share.shareAll(pageData.strChapterName, pageData.strChapterName, null, listImage);
			Logs.showTrace("Option run share");
		}
		else if (theHeader.mnFavoriteId == nResId)
		{
			showFavorite(Global.theActivity, Global.currentChapter, Global.currentPage);
			flipperView.showView(theHeader.mnFavoriteIndex);
			Logs.showTrace("Option run favorite");
		}
	}

	private void initCategory()
	{
		theCategoryAdapter.clear();

		for (int nChapter = 0; nChapter < PageData.listPageData.size(); ++nChapter)
		{
			PageData.Data pageData = PageData.listPageData.get(nChapter).get(0);
			theCategoryAdapter.addItemData(pageData.strShapTiny, pageData.strChapterName, pageData.strDescript);
		}
		listViewCategory.removeAllViewsInLayout();
		listViewCategory.setAdapter(theCategoryAdapter);
	}

	public void updateFavoriteHeaderIcon(int nCurrentChapter, int nCurrentPage)
	{
		PageData.Data pageDate = PageData.listPageData.get(nCurrentChapter).get(nCurrentPage);
		if (pageDate.bIsFavorite)
		{
			objectFactory.setImgBtnTouchDown(theHeader.mnFavoriteId);
		}
		else
		{
			objectFactory.setImgBtnNormal(theHeader.mnFavoriteId);
		}
	}

	public void clearHeaderSelected(int nCurrentChapter, int nCurrentPage)
	{
		if (Type.INVALID != mnHeaderCurrentSelected)
		{
			objectFactory.setImgBtnNormal(mnHeaderCurrentSelected);
			mnHeaderCurrentSelected = Type.INVALID;
			updateFavoriteHeaderIcon(nCurrentChapter, nCurrentPage);
		}
	}

	public void closeFlipView()
	{
		if (null != flipperView)
		{
			flipperView.close();
		}
	}

	OnTouchListener		optionTouchListener	= new OnTouchListener()
											{

												@Override
												public boolean onTouch(View v, MotionEvent event)
												{
													int nResId = v.getId();

													switch (event.getAction())
													{
													case MotionEvent.ACTION_DOWN:
														objectFactory.setImgBtnTouchDown(nResId);
														break;
													case MotionEvent.ACTION_UP:
														lunchOption(nResId);
														break;
													}
													return true;
												}
											};

	OnItemClickListener	onItemClickListener	= new OnItemClickListener()
											{
												@Override
												public void onItemClick(AdapterView<?> arg0, View view, int arg2,
														long arg3)
												{
													int nId = view.getId();

													if (mnCategoryItemId == nId)
													{
														EventHandler.notify(Global.handlerActivity,
																EventMessage.MSG_OPTION_ITEM_SELECTED, arg2, 0, null);
													}
													if (mnFavoriteItemId == nId)
													{
														EventHandler.notify(Global.handlerActivity,
																EventMessage.MSG_OPTION_ITEM_SELECTED,
																theFavoriteAdapter.getChapter(arg2),
																theFavoriteAdapter.getPage(arg2), null);
													}
												}
											};

}
