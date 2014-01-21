package interactive.view.pagereader;

import interactive.common.EventMessage;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.data.PageData;
import interactive.view.global.Global;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.RelativeLayout;

public class PageReader extends RelativeLayout
{

	private Context								theContext					= null;
	private HorizonPageView						viewPager					= null;
	private ChaptersAdapter						chaptersAdapter				= null;
	private int									mnTotalPage					= Type.INVALID;
	private SparseArray<ViewHistory>			listViewHistory				= null;
	private boolean								mbIsGoHistory				= false;
	private SparseArray<OnPageSwitchedListener>	listOnPageSwitchedListener	= null;

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
		theContext = context;
		initPageReader(context);
	}

	public PageReader(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		theContext = context;
		initPageReader(context);
	}

	public PageReader(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		theContext = context;
		initPageReader(context);
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
			VerticalPageView vvp = new VerticalPageView(theContext);
			vvp.setAdapter(pagesAdapter);
			vvp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			vvp.setOnPageChangeListener(new PageChangeListener(readerHandler));
			chaptersAdapter.addChapterView(vvp);
			vvp = null;
		}

		updatePageReader();
	}

	public void jumpPage(int nChapter, int nPage)
	{
		if (Type.INVALID != nChapter)
		{
			viewPager.setCurrentItem(nChapter);
			if (Type.INVALID != nPage)
			{
				VerticalPageView vvp = (VerticalPageView) chaptersAdapter.getChildView(nChapter);
				if (null != vvp)
				{
					vvp.setCurrentItem(nPage);
				}
			}
			Logs.showTrace("jump to chapter=" + nChapter + " page=" + nPage);
		}
		else if (Type.INVALID != nPage)
		{

			VerticalPageView vvp = (VerticalPageView) chaptersAdapter.getChildView(getCurrentChapter());
			vvp.setCurrentItem(nPage);

		}
	}

	public void goForward()
	{
		if (1 < listViewHistory.size())
		{
			listViewHistory.removeAt(listViewHistory.size() - 1);
			int nKey = listViewHistory.keyAt(listViewHistory.size() - 1);
			ViewHistory history = listViewHistory.get(nKey);
			mbIsGoHistory = true;
			jumpPage(history.mnChapter, history.mnPage);
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
											case EventMessage.MSG_WEB:
												WebEvent(msg.arg1, msg.arg2, msg.obj);
												break;
											}
										}

									};

	private void WebEvent(int nEvent, int nPosition, Object object)
	{
		switch (nEvent)
		{
		case EventMessage.MSG_JUMP:
			jumpPage(nPosition, (Integer) object);
			break;
		}
	}

	private void chapterEvent(int nEvent, int nPosition)
	{
		switch (nEvent)
		{
		case EventMessage.MSG_VIEW_INIT:
			break;
		case EventMessage.MSG_VIEW_CHANGE:
			setCurrentPosition();
			break;
		}
	}

	private void pageEvent(int nEvent, int nPosition)
	{
		switch (nEvent)
		{
		case EventMessage.MSG_VIEW_INIT:
			break;
		case EventMessage.MSG_VIEW_CHANGE:
			setCurrentPosition();
			break;
		}
	}

	private void setCurrentPosition()
	{
		int nOldChapter = Global.currentChapter;
		int nOldPage = Global.currentPage;
		Global.currentChapter = viewPager.getCurrentItem();
		VerticalPageView vvp = (VerticalPageView) chaptersAdapter.getChildView(Global.currentChapter);
		Global.currentPage = vvp.getCurrentItem();
		addHistory(Global.currentChapter, Global.currentPage);
		Logs.showTrace("Current position: " + Global.currentChapter + " " + Global.currentPage);

		if (null != PageData.listPageData)
		{
			PageData.listPageData.get(Global.currentChapter).get(Global.currentPage).extWebView.setCurrentView(true);
			PageData.listPageData.get(nOldChapter).get(nOldPage).extWebView.setCurrentView(false);
		}
		Global.interactiveHandler.removeAllMedia();
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
}
