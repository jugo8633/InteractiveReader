package interactive.view.pagereader;

import interactive.common.BitmapHandler;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.animation.fade.FadeHandler;
import interactive.view.data.PageData;
import interactive.view.global.Global;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class PageReader extends RelativeLayout
{
	private HorizonPageView						viewPager					= null;
	private ChaptersAdapter						chaptersAdapter				= null;
	private int									mnTotalPage					= Type.INVALID;
	private SparseArray<ViewHistory>			listViewHistory				= null;
	private boolean								mbIsGoHistory				= false;
	private SparseArray<OnPageSwitchedListener>	listOnPageSwitchedListener	= null;
	private Bitmap								mShowBitmap					= null;
	private Bitmap								mHideBitmap					= null;
	private ImageView							mShowImage					= null;
	private ImageView							mHideImage					= null;
	FadeHandler									fade						= null;

	public interface OnPageSwitchedListener
	{
		public void onPageSwitched();
	}

	class ViewHistory
	{
		public int	mnChapter	= Type.INVALID;
		public int	mnPage		= Type.INVALID;

		public ViewHistory(int nChapter, int nPage)
		{
			mnChapter = nChapter;
			mnPage = nPage;
		}
	}

	public PageReader(Context context)
	{
		super(context);
		init(context);
	}

	public PageReader(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public PageReader(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context)
	{
		initPageReader(context);
		fade = new FadeHandler();
	}

	@Override
	protected void finalize() throws Throwable
	{
		listViewHistory.clear();
		listViewHistory = null;
		chaptersAdapter = null;
		super.finalize();
	}

	public void initPageReader(Context context)
	{
		viewPager = new HorizonPageView(context);
		viewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		viewPager.setOnPageChangeListener(new ChapterChangeListener(readerHandler));
		chaptersAdapter = new ChaptersAdapter();
		listViewHistory = new SparseArray<ViewHistory>();
		addHistory(0, 0);
		this.addView(viewPager);
		listOnPageSwitchedListener = new SparseArray<OnPageSwitchedListener>();
	}

	public void setOnPageSwitchedListener(PageReader.OnPageSwitchedListener listener)
	{
		if (null != listOnPageSwitchedListener)
		{
			listOnPageSwitchedListener.put(listOnPageSwitchedListener.size(), listener);
		}
	}

	private void notifyPageSwitched()
	{
		if (null != listOnPageSwitchedListener)
		{
			for (int i = 0; i < listOnPageSwitchedListener.size(); ++i)
			{
				listOnPageSwitchedListener.get(i).onPageSwitched();
			}
		}
	}

	public void updatePageReader()
	{
		viewPager.removeAllViewsInLayout();
		viewPager.setAdapter(chaptersAdapter);
	}

	public void createBook(SparseArray<SparseArray<DisplayPage>> book)
	{
		chaptersAdapter.clear();
		mnTotalPage = 0;
		for (int nChapter = 0; nChapter < book.size(); ++nChapter)
		{
			SparseArray<DisplayPage> listPage = book.get(nChapter);
			PagesAdapter pagesAdapter = new PagesAdapter();
			for (int nPage = 0; nPage < listPage.size(); ++nPage)
			{
				pagesAdapter.addPageView(listPage.get(nPage));
				++mnTotalPage;
			}
			VerticalPageView vvp = new VerticalPageView(getContext());
			vvp.setAdapter(pagesAdapter);
			vvp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			vvp.setOnPageChangeListener(new PageChangeListener(readerHandler));
			chaptersAdapter.addChapterView(vvp);
			vvp = null;
		}

		updatePageReader();
	}

	public void jumpPage(int nChapter, int nPage, boolean bFade)
	{
		if (Type.INVALID != nChapter)
		{
			if (Type.INVALID == nPage && bFade)
			{
				crossFade(nChapter, Global.currentPage);
			}
			viewPager.setCurrentItem(nChapter, false);
			if (Type.INVALID != nPage)
			{
				VerticalPageView vvp = (VerticalPageView) chaptersAdapter.getChildView(nChapter);
				if (null != vvp)
				{
					if (bFade)
					{
						crossFade(nChapter, nPage);
					}
					vvp.setCurrentItem(nPage, false);
				}
			}
			Logs.showTrace("jump to chapter=" + nChapter + " page=" + nPage);
		}
		else if (Type.INVALID != nPage)
		{
			VerticalPageView vvp = (VerticalPageView) chaptersAdapter.getChildView(getCurrentChapter());
			if (bFade)
			{
				crossFade(Global.currentChapter, nPage);
			}
			vvp.setCurrentItem(nPage);
		}
	}

	private void crossFade(int nChapter, int nPage)
	{
		removeCrossFade();
		if (null == PageData.listPageData.get(nChapter).get(nPage)
				|| null == PageData.listPageData.get(Global.currentChapter).get(Global.currentPage))
		{
			return;
		}
		String strImagePath = PageData.listPageData.get(Global.currentChapter).get(Global.currentPage).strShapLarge;
		int nWidth = Global.ScaleSize(PageData.listPageData.get(Global.currentChapter).get(Global.currentPage).nWidth);
		int nHeight = Global
				.ScaleSize(PageData.listPageData.get(Global.currentChapter).get(Global.currentPage).nHeight);
		mHideBitmap = BitmapHandler.readBitmap(getContext(), strImagePath, nWidth, nHeight, false);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(nWidth, nHeight);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mHideImage = new ImageView(getContext());
		mHideImage.setImageBitmap(mHideBitmap);
		mHideImage.setScaleType(ScaleType.FIT_XY);
		mHideImage.setLayoutParams(layoutParams);

		strImagePath = PageData.listPageData.get(nChapter).get(nPage).strShapLarge;
		nWidth = Global.ScaleSize(PageData.listPageData.get(nChapter).get(nPage).nWidth);
		nHeight = Global.ScaleSize(PageData.listPageData.get(nChapter).get(nPage).nHeight);
		mShowBitmap = BitmapHandler.readBitmap(getContext(), strImagePath, nWidth, nHeight, false);
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(nWidth, nHeight);
		layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
		mShowImage = new ImageView(getContext());
		mShowImage.setImageBitmap(mShowBitmap);
		mShowImage.setScaleType(ScaleType.FIT_XY);
		mShowImage.setLayoutParams(layoutParams2);

		this.addView(mShowImage);
		this.addView(mHideImage);

		fade.crossFade(mShowImage, mHideImage, selfHandler);
	}

	private void removeCrossFade()
	{
		if (null != mHideImage)
		{
			this.removeView(mHideImage);
			if (null != mHideBitmap)
			{
				if (!mHideBitmap.isRecycled())
				{
					mHideBitmap.recycle();
				}
				mHideBitmap = null;
			}
		}
		mHideImage = null;

		if (null != mShowImage)
		{
			this.removeView(mShowImage);
			if (null != mShowBitmap)
			{
				if (!mShowBitmap.isRecycled())
				{
					mShowBitmap.recycle();
				}
				mShowBitmap = null;
			}
		}
		mShowImage = null;
	}

	public void goForward()
	{
		if (1 < listViewHistory.size())
		{
			listViewHistory.removeAt(listViewHistory.size() - 1);
			int nKey = listViewHistory.keyAt(listViewHistory.size() - 1);
			ViewHistory history = listViewHistory.get(nKey);
			mbIsGoHistory = true;
			jumpPage(history.mnChapter, history.mnPage, true);
		}
	}

	private void addHistory(int nChapter, int nPage)
	{
		if (mbIsGoHistory)
		{
			mbIsGoHistory = false;
			return;
		}
		ViewHistory history = new ViewHistory(nChapter, nPage);
		listViewHistory.put(listViewHistory.size(), history);
		history = null;

	}

	public Handler getHandler()
	{
		return readerHandler;
	}

	private Handler	readerHandler	= new Handler()
									{
										@Override
										public void handleMessage(Message msg)
										{
											switch (msg.what)
											{
											case EventMessage.MSG_CHAPTER:
												chapterEvent(msg.arg1, msg.arg2);
												break;
											case EventMessage.MSG_PAGE:
												pageEvent(msg.arg1, msg.arg2);
												break;
											}
										}

									};

	private void chapterEvent(int nEvent, int nPosition)
	{
		switch (nEvent)
		{
		case EventMessage.MSG_VIEW_CHANGE:
			setCurrentPosition();
			break;
		}
	}

	private void pageEvent(int nEvent, int nPosition)
	{
		switch (nEvent)
		{
		case EventMessage.MSG_VIEW_CHANGE:
			setCurrentPosition();
			break;
		}
	}

	public void setCurrentPosition()
	{
		VerticalPageView vvp = (VerticalPageView) chaptersAdapter.getChildView(viewPager.getCurrentItem());
		if (null == vvp)
		{
			return;
		}

		Global.currentChapter = viewPager.getCurrentItem();
		Global.currentPage = vvp.getCurrentItem();

		addHistory(Global.currentChapter, Global.currentPage);
		Logs.showTrace("Current position: " + Global.currentChapter + " " + Global.currentPage);

		EventHandler.notify(Global.interactiveHandler.getNotifyHandler(), EventMessage.MSG_MEDIA_STOP, Type.INVALID,
				Type.INVALID, null);
		Global.notifyActive(Global.currentChapter, Global.currentPage);
		notifyPageSwitched();
		lockHorizonScroll(false);
		lockVerticalScroll(false);
	}

	public int getTotalPage()
	{
		return mnTotalPage;
	}

	public int getCurrentChapter()
	{
		return Global.currentChapter;
	}

	public int getCurrentPage()
	{
		return Global.currentPage;
	}

	public void lockScroll(boolean bLock)
	{
		lockHorizonScroll(bLock);
		lockVerticalScroll(bLock);
	}

	public void lockHorizonScroll(boolean bLock)
	{
		viewPager.setPagingEnabled(!bLock);
		Logs.showTrace("Page reader lock horizon page: " + bLock);
	}

	public void lockVerticalScroll(boolean bLock)
	{
		for (int i = 0; i < chaptersAdapter.getCount(); ++i)
		{
			((VerticalPageView) chaptersAdapter.getChildView(i)).setPagingEnabled(!bLock);
		}
		Logs.showTrace("Page reader lock vertical page: " + bLock);
	}

	private Handler	selfHandler	= new Handler()
								{
									@Override
									public void handleMessage(Message msg)
									{
										switch (msg.what)
										{
										case EventMessage.MSG_ANIMATION_END:
											removeCrossFade();
											break;
										}
									}
								};
}
