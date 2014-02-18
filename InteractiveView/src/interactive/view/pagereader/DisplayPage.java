package interactive.view.pagereader;

import interactive.common.Device;
import interactive.common.FileHandler;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.data.PageData;
import interactive.view.json.InteractiveButton;
import interactive.view.json.InteractiveImage;
import interactive.view.json.InteractiveMap;
import interactive.view.json.InteractiveObject;
import interactive.view.json.InteractivePostcard;
import interactive.view.json.InteractiveScrollable;
import interactive.view.json.InteractiveSlideshow;
import interactive.view.json.InteractiveVideo;
import interactive.view.json.InteractiveWebPage;
import interactive.view.webview.InteractiveWebView;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.RelativeLayout;

public class DisplayPage extends RelativeLayout
{
	private Context				theContext		= null;
	private InteractiveWebView	exdWebView		= null;
	private String				mstrBookPath	= null;
	private JSONObject			jsonAll			= null;
	private DisplayMetrics		metrics			= null;
	private int					mnChapter		= Type.INVALID;
	private int					mnPage			= Type.INVALID;
	private String				mstrBackground	= null;

	public DisplayPage(Context context)
	{
		super(context);
		init(context);
	}

	public DisplayPage(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public DisplayPage(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context)
	{
		theContext = context;
		metrics = context.getResources().getDisplayMetrics();
		this.setGravity(Gravity.CENTER);
	}

	public void setBookPath(String strPath)
	{
		mstrBookPath = strPath;
	}

	public void setPageData(final Handler handler, PageData.Data pageData, int nChapter, int nPage)
	{
		Device device = new Device(theContext);
		int nDisplayWidth = device.getDeviceWidth();
		int nDisplayHeight = device.getDeviceHeight();
		device = null;

		int nWebWidth = pageData.nWidth;
		int nWebHeight = pageData.nHeight;
		mstrBackground = pageData.strShapLarge;

		if (Type.INVALID != nDisplayWidth && nDisplayWidth < pageData.nWidth)
		{
			nWebWidth = nDisplayWidth;
		}

		if (Type.INVALID != nDisplayHeight && nDisplayHeight < pageData.nHeight)
		{
			nWebHeight = nDisplayHeight;
		}

		exdWebView = new InteractiveWebView(theContext);
		exdWebView.setPosition(pageData.nChapter, pageData.nPage);
		exdWebView.setTag(pageData.strName);
		exdWebView.initPageReaderHandler(handler);
		exdWebView.setDisplaySize(getScaleUnit(nWebWidth), getScaleUnit(nWebHeight));
		exdWebView.loadUrl("file://" + pageData.strPath);
		Logs.showTrace("Webview load file:" + pageData.strPath);

		String jsonData = pageData.strPath.substring(0, pageData.strPath.lastIndexOf(".")) + ".json";
		setJson(jsonData, nChapter, nPage);

		pageData.extWebView = exdWebView;

		addView(exdWebView);
	}

	public InteractiveWebView getWebView()
	{
		return exdWebView;
	}

	public void setJson(String strJsonPath, int nChapter, int nPage)
	{
		if (!FileHandler.isFileExist(strJsonPath))
		{
			Logs.showTrace("JSON File not exist :" + strJsonPath);
			return;
		}

		StringBuffer jsonData = null;
		try
		{
			jsonData = FileHandler.getFileContent(strJsonPath);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
			return;
		}

		Logs.showTrace("Load JSON file:" + strJsonPath);
		try
		{
			/** 互動元件顯示階層
			1.	NativeButton
			2.	Scrollable
			3.	EventImage、DragToFav
			4.	Video
			5.	Map
			6.	Slideshow
			7.	PostCard
			8.	Doodle
			9.	Puzzle
			10.	Web view
			 */
			jsonAll = new JSONObject(jsonData.toString());
			if (!jsonAll.isNull(InteractiveObject.JSON_WEB_PAGE))
			{
				InteractiveWebPage interactiveWebPage = new InteractiveWebPage(theContext);
				interactiveWebPage.createInteractive(exdWebView, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveWebPage = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_POSTCARD))
			{
				InteractivePostcard interactivePostcard = new InteractivePostcard(theContext);
				interactivePostcard.createInteractive(exdWebView, mstrBookPath, jsonAll, nChapter, nPage);
				interactivePostcard = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_SLIDESHOW))
			{
				InteractiveSlideshow interactiveSlideshow = new InteractiveSlideshow(theContext);
				interactiveSlideshow.createInteractive(exdWebView, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveSlideshow = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_MAP))
			{
				InteractiveMap interactiveMap = new InteractiveMap(theContext);
				interactiveMap.setBackground(mstrBackground);
				interactiveMap.createInteractive(exdWebView, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveMap = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_VIDEO))
			{
				InteractiveVideo interactiveVideo = new InteractiveVideo(theContext);
				interactiveVideo.createInteractive(exdWebView, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveVideo = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_IMAGE))
			{
				InteractiveImage interactiveImage = new InteractiveImage(theContext);
				interactiveImage.createInteractive(exdWebView, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveImage = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_SCROLLABLE))
			{
				InteractiveScrollable interactiveScrollable = new InteractiveScrollable(theContext);
				interactiveScrollable.createInteractive(exdWebView, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveScrollable = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_BUTTON))
			{
				InteractiveButton interactiveButton = new InteractiveButton(theContext);
				interactiveButton.setBackground(mstrBackground);
				interactiveButton.createInteractive(exdWebView, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveButton = null;
			}

			jsonAll = null;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	private int getScaleUnit(int original)
	{
		if (metrics.densityDpi > 160)
		{
			return (int) (original * metrics.densityDpi / 160);// metrics.density
		}
		else
		{
			return original;
		}
	}
}
