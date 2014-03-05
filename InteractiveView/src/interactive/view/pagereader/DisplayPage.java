package interactive.view.pagereader;

import interactive.common.Device;
import interactive.common.FileHandler;
import interactive.common.Logs;
import interactive.common.Type;
import interactive.view.data.PageData;
import interactive.view.global.Global;
import interactive.view.json.InteractiveAudio;
import interactive.view.json.InteractiveButton;
import interactive.view.json.InteractiveDoodle;
import interactive.view.json.InteractiveIframe;
import interactive.view.json.InteractiveImage;
import interactive.view.json.InteractiveMap;
import interactive.view.json.InteractiveObject;
import interactive.view.json.InteractivePostcard;
import interactive.view.json.InteractivePuzzle;
import interactive.view.json.InteractiveScrollable;
import interactive.view.json.InteractiveSlideshow;
import interactive.view.json.InteractiveVideo;
import interactive.view.json.InteractiveWebPage;
import interactive.view.webview.InteractiveWebView;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class DisplayPage extends RelativeLayout
{
	private PageContainer		pageContainer	= null;
	private InteractiveWebView	exdWebView		= null;
	private String				mstrBookPath	= null;
	private JSONObject			jsonAll			= null;

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
		this.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.setGravity(Gravity.CENTER);
		pageContainer = new PageContainer(context);
	}

	public void setBookPath(String strPath)
	{
		mstrBookPath = strPath;
	}

	public void setPageData(PageData.Data pageData)
	{
		Device device = new Device(getContext());
		int nDisplayWidth = device.getDeviceWidth();
		int nDisplayHeight = device.getDeviceHeight();
		device = null;

		int nWidth = pageData.nWidth;
		int nHeight = pageData.nHeight;

		if (Type.INVALID != nDisplayWidth && nDisplayWidth < pageData.nWidth)
		{
			nWidth = nDisplayWidth;
		}

		if (Type.INVALID != nDisplayHeight && nDisplayHeight < pageData.nHeight)
		{
			nHeight = nDisplayHeight;
		}

		//		pageContainer.setPosition(pageData.nChapter, pageData.nPage);
		//		pageContainer.setTag(pageData.strName);
		//		pageContainer.setDisplay(0, 0, Global.ScaleSize(nWidth), Global.ScaleSize(nHeight));
		//		pageContainer.setBackground(pageData.strShapLarge, Global.ScaleSize(nWidth), Global.ScaleSize(nHeight));
		//		addView(pageContainer);

		exdWebView = new InteractiveWebView(getContext());
		exdWebView.setPosition(pageData.nChapter, pageData.nPage);
		exdWebView.setTag(pageData.strName);
		exdWebView.setDisplay(0, 0, Global.ScaleSize(nWidth), Global.ScaleSize(nHeight));
		exdWebView.loadUrl("file://" + pageData.strPath);
		Logs.showTrace("Webview load file:" + pageData.strPath);

		String jsonData = pageData.strPath.substring(0, pageData.strPath.lastIndexOf(".")) + ".json";
		setJson(jsonData, pageData.nChapter, pageData.nPage);

		pageData.container = pageContainer;
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

			ViewGroup container = exdWebView;
			if (!jsonAll.isNull(InteractiveObject.JSON_WEB_PAGE))
			{
				InteractiveWebPage interactiveWebPage = new InteractiveWebPage(getContext());
				interactiveWebPage.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveWebPage = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_PUZZLE))
			{
				InteractivePuzzle interactivePuzzle = new InteractivePuzzle(getContext());
				interactivePuzzle.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactivePuzzle = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_DOODLE))
			{
				InteractiveDoodle interactiveDoodle = new InteractiveDoodle(getContext());
				interactiveDoodle.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveDoodle = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_IFRAME))
			{
				InteractiveIframe interactiveIframe = new InteractiveIframe(getContext());
				interactiveIframe.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveIframe = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_POSTCARD))
			{
				InteractivePostcard interactivePostcard = new InteractivePostcard(getContext());
				interactivePostcard.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactivePostcard = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_AUDIO))
			{
				InteractiveAudio interactiveAudio = new InteractiveAudio(getContext());
				interactiveAudio.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveAudio = null;
			}

			/**  note: vide 要放在slideshow前面*/
			if (!jsonAll.isNull(InteractiveObject.JSON_VIDEO))
			{
				InteractiveVideo interactiveVideo = new InteractiveVideo(getContext());
				interactiveVideo.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveVideo = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_SLIDESHOW))
			{
				InteractiveSlideshow interactiveSlideshow = new InteractiveSlideshow(getContext());
				interactiveSlideshow.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveSlideshow = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_MAP))
			{
				InteractiveMap interactiveMap = new InteractiveMap(getContext());
				interactiveMap.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveMap = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_IMAGE))
			{
				InteractiveImage interactiveImage = new InteractiveImage(getContext());
				interactiveImage.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveImage = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_SCROLLABLE))
			{
				InteractiveScrollable interactiveScrollable = new InteractiveScrollable(getContext());
				interactiveScrollable.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveScrollable = null;
			}

			if (!jsonAll.isNull(InteractiveObject.JSON_BUTTON))
			{
				InteractiveButton interactiveButton = new InteractiveButton(getContext());
				interactiveButton.createInteractive(container, mstrBookPath, jsonAll, nChapter, nPage);
				interactiveButton = null;
			}

			jsonAll = null;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

}
