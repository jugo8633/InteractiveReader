package interactive.view.json;

import interactive.common.Type;
import interactive.view.global.Global;
import interactive.view.webview.InteractiveWebView;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.ViewGroup;

@SuppressWarnings("unused")
public abstract class InteractiveObject
{
	// main json key
	public static final String		JSON_BUTTON					= "Button";
	public static final String		JSON_DOODLE					= "Doodle";
	public static final String		JSON_GROUP					= "Group";
	public static final String		JSON_IFRAME					= "Iframe";
	public static final String		JSON_IMAGE					= "Image";
	public static final String		JSON_MAP					= "Map";
	public static final String		JSON_POSTCARD				= "Postcard";
	public static final String		JSON_PUZZLE					= "Puzzle";
	public static final String		JSON_SCROLLABLE				= "Scrollable";
	public static final String		JSON_SHADOWING				= "Shadowing";
	public static final String		JSON_SLIDESHOW				= "Slideshow";
	public static final String		JSON_TICKETBOOK				= "Ticketbook";
	public static final String		JSON_VIDEO					= "Video";
	public static final String		JSON_WEB_PAGE				= "WebPage";

	// sub json key
	private final String			JSON_DELETE_BUTTON			= "DeleteButton";
	private final String			JSON_DIARIES				= "Diaries";
	private final String			JSON_EVENT					= "Event";
	private final String			JSON_EVENT_INFO				= "EventInfo";
	private final String			JSON_ERASER_BTN				= "EraserBtn";
	private final String			JSON_GESTURE				= "Gesture";
	private final String			JSON_IMAGE_AREA				= "ImageArea";
	private final String			JSON_ITEM					= "Item";
	private final String			JSON_NEW_BUTTON				= "NewButton";
	private final String			JSON_OPEN_BUTTON			= "OpenButton";
	private final String			JSON_OPTIONS				= "Options";
	private final String			JSON_PALETTE_BTN			= "PaletteBtn";
	private final String			JSON_PEN_BTN				= "PenBtn";
	private final String			JSON_RESET_BTN				= "ResetBtn";
	private final String			JSON_SAVE_BTN				= "SaveBtn";
	private final String			JSON_SAVE_BUTTON			= "SaveButton";
	private final String			JSON_SHARE					= "Share";
	private final String			JSON_SOL_BTN				= "SolBtn";
	private final String			JSON_TEXT_AREA				= "TextArea";
	private final String			JSON_TIME_LABEL				= "TimeLabel";

	// content json key
	private final String			JSON_ADDED_ICON				= "AddedIcon";
	private final String			JSON_ADDED_ICON_POSITION	= "AddedIconPosition";
	private final String			JSON_ADDRESS				= "Address";
	private final String			JSON_ADD_MESSAGE			= "AddMessage";
	private final String			JSON_ANIMATION				= "Animation";
	private final String			JSON_APPEARANCE				= "Appearance";
	private final String			JSON_AUTOPLAY				= "Autoplay";
	private final String			JSON_BACKGROUND				= "Background";
	private final String			JSON_BRUSHES				= "Brushes";
	private final String			JSON_CAMERA					= "Camera";
	private final String			JSON_CONTAIN_ITEMS			= "ContainItems";
	private final String			JSON_CORRECT_ITEMS			= "CorrectItems";
	private final String			JSON_DESCRIPTION			= "Description";
	private final String			JSON_DISPLAY				= "Display";
	private final String			JSON_ELEMENT				= "Element";
	private final String			JSON_END					= "End";
	private final String			JSON_ERASER					= "Eraser";
	private final String			JSON_EVENT_NAME				= "EventName";
	private final String			JSON_FINISH_OBJ				= "FinishObj";
	private final String			JSON_FULLSCREEN				= "FullScreen";
	private final String			JSON_GROUP_ID				= "GroupID";
	private final String			JSON_HEIGHT					= "Height";
	private final String			JSON_HTML					= "Html";
	private final String			JSON_IMGBBOX				= "ImgBBox";
	private final String			JSON_IS_OPEN				= "IsOpen";
	private final String			JSON_ISVISIBLE				= "IsVisible";
	private final String			JSON_ITEM_COUNT				= "ItemCount";
	private final String			JSON_LATITUDE				= "Latitude";
	private final String			JSON_LONGITUDE				= "Longitude";
	private final String			JSON_LOOP					= "Loop";
	private final String			JSON_MAILBOX				= "Mailbox";
	private final String			JSON_MAP_TYPE				= "MapType";
	private final String			JSON_MAP_TYPE_NAME			= "MapTypeName";
	private final String			JSON_MARK_AS				= "MarkAs";
	private final String			JSON_MEDIA_TYPE				= "MediaType";
	private final String			JSON_MEDIA_SRC				= "MediaSrc";
	private final String			JSON_METHOD					= "Method";
	private final String			JSON_METHOD_NAME			= "MethodName";
	private final String			JSON_NAME					= "Name";
	private final String			JSON_NOTIFICATION			= "Notification";
	private final String			JSON_NOTI_INFO				= "NotiInfo";
	private final String			JSON_OFFSET					= "Offset";
	private final String			JSON_OPEN					= "Open";
	private final String			JSON_OVERFLOW				= "Overflow";
	private final String			JSON_PALETTE				= "Palette";
	private final String			JSON_PEN					= "Pen";
	private final String			JSON_PIECE					= "Piece";
	private final String			JSON_PLAYER_CONTROLS		= "PlayerControls";
	private final String			JSON_SMIL					= "Smil";
	private final String			JSON_SOURCE_IMAGE			= "SourceImage";
	private final String			JSON_SRC					= "Src";
	private final String			JSON_SRC_BACK				= "SrcBack";
	private final String			JSON_SRC_FRONT				= "SrcFront";
	private final String			JSON_START					= "Start";
	private final String			JSON_STREET_VIEW			= "StreetView";
	private final String			JSON_STYLE					= "Style";
	private final String			JSON_TARGET_PAGE			= "TargetPage";
	private final String			JSON_TARGET_ID				= "TargetID";
	private final String			JSON_TARGET_TYPE			= "TargetType";
	private final String			JSON_THUMBNAIL				= "Thumbnail";
	private final String			JSON_TITLE					= "Title";
	private final String			JSON_TOUCH_DOWN_SRC			= "TouchDownSrc";
	private final String			JSON_TOUCH_UP_SRC			= "TouchUpSrc";
	private final String			JSON_TYPE					= "Type";
	private final String			JSON_TYPE_NAME				= "TypeName";
	private final String			JSON_URL					= "Url";
	//private final String			JSON_VIDEO_SRC				= "VideoSrc";
	//	private final String			JSON_VIDEO_TYPE				= "VideoType";
	private final String			JSON_WIDTH					= "Width";
	private final String			JSON_X						= "X";
	private final String			JSON_Y						= "Y";
	private final String			JSON_ZOOM_LEVEL				= "ZoomLevel";

	private Context					theContext					= null;
	private DisplayMetrics			metrics						= null;

	private HashMap<String, String>	oldKeys;

	public class JsonHeader
	{
		public String	mstrName	= null;
		public int		mnWidth		= Type.INVALID;
		public int		mnHeight	= Type.INVALID;
		public int		mnX			= Type.INVALID;
		public int		mnY			= Type.INVALID;
		public String	mstrSrc		= null;
		public boolean	mbIsVisible	= true;
		public String	mstrGroupId	= null;

		public JsonHeader()
		{
		}
	}

	public class Event // for button
	{
		public int		mnType			= Type.INVALID;
		public String	mstrTypeName	= null;
		public int		mnEvent			= Type.INVALID;
		public String	mstrEventName	= null;
		public int		mnTargetType	= Type.INVALID;
		public String	mstrTargetId	= null;
		public int		mnDisplay		= Type.INVALID;

		public Event()
		{
		}
	}

	public class JsonGesture // for image
	{
		public int		mnType			= Type.INVALID;
		public String	mstrTypeName	= null;
		public int		mnEvent			= Type.INVALID;
		public String	mstrEventName	= null;
		public int		mnTargetType	= Type.INVALID;
		public String	mstrTargetId	= null;
		public int		mnDisplay		= Type.INVALID;

		public JsonGesture()
		{
		}
	}

	public class Item
	{
		public int		mnType			= 0;
		public String	mstrTypeName	= null;
		public String	mstrTargetID	= null;
		public String	mstrSourceImage	= null; // for fullscreen
		public String	mstrTitle		= null;
		public String	mstrDescription	= null;

		public Item(int nType, String strTypeName, String strTargetID, String strSourceImage, String strTitle,
				String strDescription)
		{
			mnType = nType;
			mstrTypeName = strTypeName;
			mstrTargetID = strTargetID;
			mstrSourceImage = strSourceImage;
			mstrTitle = strTitle;
			mstrDescription = strDescription;
		}
	}

	public class JsonWebPage
	{
		class Options
		{
			public boolean	mbAutoplay	= false;

			public Options()
			{

			}
		}

		public Options	options	= null;

		public JsonWebPage()
		{
			options = new Options();
		}

		@Override
		protected void finalize() throws Throwable
		{
			options = null;
			super.finalize();
		}

	}

	public class JsonIframe
	{
		public boolean	mbIsOpen	= false;
		public String	mstrUrl		= null;

		public JsonIframe()
		{

		}
	}

	public class JsonSlideshow
	{
		public String				mstrBackground	= null;
		public int					mnStyle			= 0;
		public boolean				mbFullScreen	= false;
		public int					mnItemCount		= Type.INVALID;
		public SparseArray<Item>	listItem		= null;

		public JsonSlideshow()
		{
			listItem = new SparseArray<Item>();
		}

		@Override
		protected void finalize() throws Throwable
		{
			listItem.clear();
			listItem = null;
			super.finalize();
		}

	}

	public class JsonScrollable
	{
		class Offset
		{
			public int	mnX	= Type.INVALID;
			public int	mnY	= Type.INVALID;

			public Offset()
			{
			}
		}

		class ImgBBox
		{
			public int	mnWidth		= Type.INVALID;
			public int	mnHeight	= Type.INVALID;

			public ImgBBox()
			{
			}
		}

		public int		mnOverflow	= Type.INVALID;
		public Offset	offSet		= null;
		public ImgBBox	imgBBox		= null;

		public JsonScrollable()
		{
			offSet = new Offset();
			imgBBox = new ImgBBox();
		}

		@Override
		protected void finalize() throws Throwable
		{
			offSet = null;
			imgBBox = null;
			super.finalize();
		}

	}

	public class JsonVideo
	{
		class Options
		{
			public int		mnStart		= Type.INVALID;
			public int		mnEnd		= Type.INVALID;
			public boolean	mbAutoPlay	= false;
			public boolean	mbLoop		= false;

			public Options()
			{
			}
		}

		class Appearance
		{
			public boolean	mbPlayerControls	= false;

			public Appearance()
			{
			}
		}

		public int			mnMediaType		= Type.INVALID;
		public String		mstrMediaSrc	= null;
		public String		mstrUrl			= null;
		public Options		options			= null;
		public Appearance	appearance		= null;

		public JsonVideo()
		{
			options = new Options();
			appearance = new Appearance();
		}

		@Override
		protected void finalize() throws Throwable
		{
			options = null;
			appearance = null;
		}
	}

	public class JsonMap
	{
		class Appearance
		{
			public int		mnZoomLevel		= Type.INVALID;
			public int		mnMapType		= Type.INVALID;
			public String	mstrMapTypeName	= null;
			public boolean	mbStreetView	= false;
			public String	mstrMarkAs		= null;

			public Appearance()
			{
			}
		}

		public String		mstrAddress	= null;
		public String		mstrUrl		= null;
		public double		mdLongitude	= 0;
		public double		mdlatitude	= 0;
		public Appearance	appearance	= null;

		public JsonMap()
		{
			appearance = new Appearance();
		}

		@Override
		protected void finalize() throws Throwable
		{
			appearance = null;
		}
	}

	public class JsonPostcard
	{
		class Pen
		{
			public int		mnWidth		= Type.INVALID;
			public int		mnHeight	= Type.INVALID;
			public int		mnX			= Type.INVALID;
			public int		mnY			= Type.INVALID;
			public String	mstrSrc		= null;

			public Pen()
			{
			}
		}

		class Eraser
		{
			public int		mnWidth		= Type.INVALID;
			public int		mnHeight	= Type.INVALID;
			public int		mnX			= Type.INVALID;
			public int		mnY			= Type.INVALID;
			public String	mstrSrc		= null;

			public Eraser()
			{
			}
		}

		class TextArea
		{
			public int		mnWidth		= Type.INVALID;
			public int		mnHeight	= Type.INVALID;
			public int		mnX			= Type.INVALID;
			public int		mnY			= Type.INVALID;
			public String	mstrSrc		= null;

			public TextArea()
			{
			}
		}

		class Camera
		{
			public int		mnWidth		= Type.INVALID;
			public int		mnHeight	= Type.INVALID;
			public int		mnX			= Type.INVALID;
			public int		mnY			= Type.INVALID;
			public String	mstrSrc		= null;

			public Camera()
			{
			}
		}

		class OpenButton
		{
			public int		mnWidth		= Type.INVALID;
			public int		mnHeight	= Type.INVALID;
			public int		mnX			= Type.INVALID;
			public int		mnY			= Type.INVALID;
			public String	mstrSrc		= null;

			public OpenButton()
			{
			}
		}

		class MailBox
		{
			public int		mnWidth		= Type.INVALID;
			public int		mnHeight	= Type.INVALID;
			public int		mnX			= Type.INVALID;
			public int		mnY			= Type.INVALID;
			public String	mstrSrc		= null;

			public MailBox()
			{
			}
		}

		public String		mstrSrcFront	= null;
		public String		mstrSrcBack		= null;
		public Pen			pen				= null;
		public Eraser		eraser			= null;
		public TextArea		textArea		= null;
		public Camera		camera			= null;
		public OpenButton	openButton		= null;
		public MailBox		mailBox			= null;

		public JsonPostcard()
		{
			pen = new Pen();
			eraser = new Eraser();
			textArea = new TextArea();
			camera = new Camera();
			openButton = new OpenButton();
			mailBox = new MailBox();
		}

		@Override
		protected void finalize() throws Throwable
		{
			pen = null;
			eraser = null;
			textArea = null;
			camera = null;
			openButton = null;
			mailBox = null;
		}
	}

	public class JsonButton
	{
		public String				mstrTouchDown	= null;
		public String				mstrTouchUp		= null;
		public SparseArray<Event>	listEvent		= null;

		public JsonButton()
		{
			listEvent = new SparseArray<Event>();
		}

		@Override
		protected void finalize() throws Throwable
		{
			listEvent.clear();
			listEvent = null;
			super.finalize();
		}

	}

	public InteractiveObject(Context context)
	{
		super();
		theContext = context;
		metrics = theContext.getResources().getDisplayMetrics();
		oldKeys = new HashMap<String, String>();

		oldKeys.put(JSON_MEDIA_TYPE + "old", "Video_type");
		oldKeys.put(JSON_MEDIA_SRC + "old", "Video_src");
		oldKeys.put(JSON_MEDIA_TYPE, "VideoType");
		oldKeys.put(JSON_MEDIA_SRC, "VideoSrc");
		oldKeys.put(JSON_PLAYER_CONTROLS, "Player_controls");
		oldKeys.put(JSON_ZOOM_LEVEL, "Zoom_Level");
		oldKeys.put(JSON_MAP_TYPE, "Map_Type");
		oldKeys.put(JSON_MAP_TYPE_NAME, "Map_Type_name");
		oldKeys.put(JSON_MARK_AS, "Mark_As");
		oldKeys.put(JSON_SRC_FRONT, "Src_front");
		oldKeys.put(JSON_SRC_BACK, "Src_back");
		oldKeys.put(JSON_GROUP_ID, "group_id");
		oldKeys.put(JSON_TOUCH_DOWN_SRC, "Touch_down_src");
		oldKeys.put(JSON_TOUCH_UP_SRC, "Touch_up_src");
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	public abstract boolean createInteractive(ViewGroup container, String strBookPath, JSONObject jsonAll,
			int nChapter, int nPage) throws JSONException;

	public Context getContext()
	{
		return theContext;
	}

	public boolean parseJsonHeader(JSONObject jsonObject, JsonHeader jsonHeader) throws JSONException
	{
		if (null == jsonObject || null == jsonHeader)
		{
			return false;
		}

		jsonHeader.mstrName = getJsonString(jsonObject, JSON_NAME);
		jsonHeader.mnWidth = getJsonInt(jsonObject, JSON_WIDTH);
		jsonHeader.mnHeight = getJsonInt(jsonObject, JSON_HEIGHT);
		jsonHeader.mnX = getJsonInt(jsonObject, JSON_X);
		jsonHeader.mnY = getJsonInt(jsonObject, JSON_Y);
		jsonHeader.mstrSrc = getJsonString(jsonObject, JSON_SRC);
		jsonHeader.mbIsVisible = getJsonBoolean(jsonObject, JSON_ISVISIBLE);
		jsonHeader.mstrGroupId = getJsonString(jsonObject, JSON_GROUP_ID);

		return true;
	}

	public boolean parseJsonEvent(JSONObject jsonObject, SparseArray<Event> listEvent) throws JSONException
	{
		String strKey = null;
		JSONArray jsonArrayBtnEvent = null;

		strKey = getValidKey(jsonObject, JSON_EVENT);
		if (null != strKey)
		{
			jsonArrayBtnEvent = jsonObject.getJSONArray(strKey);
		}
		else
		{
			return false;
		}

		for (int i = 0; i < jsonArrayBtnEvent.length(); ++i)
		{
			JSONObject jsonBtnEvent = jsonArrayBtnEvent.getJSONObject(i);
			if (null == jsonBtnEvent)
			{
				return false;
			}

			Event event = new Event();

			event.mnType = getJsonInt(jsonBtnEvent, JSON_TYPE);
			event.mstrTypeName = getJsonString(jsonBtnEvent, JSON_TYPE_NAME);
			event.mnEvent = getJsonInt(jsonBtnEvent, JSON_EVENT);
			event.mstrEventName = getJsonString(jsonBtnEvent, JSON_EVENT_NAME);
			event.mnTargetType = getJsonInt(jsonBtnEvent, JSON_TARGET_TYPE);
			event.mstrTargetId = getJsonString(jsonBtnEvent, JSON_TARGET_ID);
			event.mnDisplay = getJsonInt(jsonBtnEvent, JSON_DISPLAY);
			listEvent.put(listEvent.size(), event);
			event = null;
		}

		if (0 >= listEvent.size())
		{
			return false;
		}
		return true;
	}

	public boolean parseJsonGesture(JSONObject jsonObject, JsonGesture jsonGesture) throws JSONException
	{
		if (null == jsonObject || null == jsonGesture)
		{
			return false;
		}
		jsonGesture.mnType = getJsonInt(jsonObject, JSON_TYPE);
		jsonGesture.mstrTypeName = getJsonString(jsonObject, JSON_TYPE_NAME);
		jsonGesture.mnEvent = getJsonInt(jsonObject, JSON_EVENT);
		jsonGesture.mstrEventName = getJsonString(jsonObject, JSON_EVENT_NAME);
		jsonGesture.mnTargetType = getJsonInt(jsonObject, JSON_TARGET_TYPE);
		jsonGesture.mstrTargetId = getJsonString(jsonObject, JSON_TARGET_ID);
		jsonGesture.mnDisplay = getJsonInt(jsonObject, JSON_DISPLAY);
		return true;
	}

	public String checkJsonGesture(JSONObject jsonObject)
	{
		String strKey = getValidKey(jsonObject, JSON_GESTURE);
		return strKey;
	}

	public boolean parseJsonWebPage(JSONObject jsonObject, JsonWebPage jsonWebPage) throws JSONException
	{
		String strKey = null;
		if (null == jsonObject || null == jsonWebPage)
		{
			return false;
		}

		strKey = getValidKey(jsonObject, JSON_OPTIONS);
		if (null != strKey)
		{
			JSONObject joptions = jsonObject.getJSONObject(strKey);
			jsonWebPage.options.mbAutoplay = getJsonBoolean(joptions, JSON_AUTOPLAY);
		}
		return true;
	}

	public boolean parseJsonIframe(JSONObject jsonObject, JsonIframe jsonIframe) throws JSONException
	{
		String strKey = null;
		if (null == jsonObject || null == jsonIframe)
		{
			return false;
		}

		jsonIframe.mbIsOpen = getJsonBoolean(jsonObject, JSON_IS_OPEN);
		jsonIframe.mstrUrl = getJsonString(jsonObject, JSON_URL);
		return true;
	}

	public boolean parseJsonSlideshow(JSONObject jsonObject, JsonSlideshow jsonSlideshow) throws JSONException
	{
		String strKey = null;
		if (null == jsonObject || null == jsonSlideshow)
		{
			return false;
		}
		jsonSlideshow.mstrBackground = getJsonString(jsonObject, JSON_BACKGROUND);

		String strStyle = getJsonString(jsonObject, JSON_STYLE);
		if (null == strStyle)
		{
			jsonSlideshow.mnStyle = getJsonInt(jsonObject, JSON_STYLE);
		}
		else
		{
			jsonSlideshow.mnStyle = Integer.parseInt(strStyle);
		}

		jsonSlideshow.mbFullScreen = getJsonBoolean(jsonObject, JSON_FULLSCREEN);
		jsonSlideshow.mnItemCount = getJsonInt(jsonObject, JSON_ITEM_COUNT);

		strKey = getValidKey(jsonObject, JSON_ITEM);
		JSONArray jsonArrayItem = null;
		if (null != strKey)
		{
			jsonArrayItem = jsonObject.getJSONArray(strKey);
			for (int i = 0; i < jsonArrayItem.length(); ++i)
			{
				JSONObject jsonItem = jsonArrayItem.getJSONObject(i);
				if (null == jsonItem)
				{
					return false;
				}
				int nType = getJsonInt(jsonItem, JSON_TYPE);
				String strTypeName = getJsonString(jsonItem, JSON_TYPE_NAME);
				String strTargetID = getJsonString(jsonItem, JSON_TARGET_ID);
				String strSourceImage = getJsonString(jsonItem, JSON_SOURCE_IMAGE);
				String strTitle = getJsonString(jsonItem, JSON_TITLE);
				String strDescription = getJsonString(jsonItem, JSON_DESCRIPTION);
				jsonSlideshow.listItem.put(jsonSlideshow.listItem.size(), new Item(nType, strTypeName, strTargetID,
						strSourceImage, strTitle, strDescription));
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	public boolean parseJsonScrollable(JSONObject jsonObject, JsonScrollable jsonScrollable) throws JSONException
	{
		String strKey = null;
		if (null == jsonObject || null == jsonScrollable)
		{
			return false;
		}

		strKey = getValidKey(jsonObject, JSON_OFFSET);
		if (null != strKey)
		{
			JSONObject joffset = jsonObject.getJSONObject(strKey);
			jsonScrollable.offSet.mnX = getJsonInt(joffset, JSON_X);
			jsonScrollable.offSet.mnY = getJsonInt(joffset, JSON_Y);
		}

		strKey = getValidKey(jsonObject, JSON_IMGBBOX);
		if (null != strKey)
		{
			JSONObject jImgBBox = jsonObject.getJSONObject(strKey);
			jsonScrollable.imgBBox.mnWidth = getJsonInt(jImgBBox, JSON_WIDTH);
			jsonScrollable.imgBBox.mnHeight = getJsonInt(jImgBBox, JSON_HEIGHT);
		}

		jsonScrollable.mnOverflow = getJsonInt(jsonObject, JSON_OVERFLOW);
		return true;
	}

	public boolean parseJsonVideo(JSONObject jsonObject, JsonVideo jsonVideo) throws JSONException
	{
		String strKey = null;
		if (null == jsonObject || null == jsonVideo)
		{
			return false;
		}

		jsonVideo.mnMediaType = getJsonInt(jsonObject, JSON_MEDIA_TYPE);
		jsonVideo.mstrMediaSrc = getJsonString(jsonObject, JSON_MEDIA_SRC);
		jsonVideo.mstrUrl = getJsonString(jsonObject, JSON_URL);

		strKey = getValidKey(jsonObject, JSON_OPTIONS);
		if (null != strKey)
		{
			JSONObject jOptions = jsonObject.getJSONObject(strKey);
			jsonVideo.options.mnStart = getJsonInt(jOptions, JSON_START);
			jsonVideo.options.mnEnd = getJsonInt(jOptions, JSON_END);
			jsonVideo.options.mbAutoPlay = getJsonBoolean(jOptions, JSON_AUTOPLAY);
			jsonVideo.options.mbLoop = getJsonBoolean(jOptions, JSON_LOOP);
		}

		strKey = getValidKey(jsonObject, JSON_APPEARANCE);
		if (null != strKey)
		{
			JSONObject jAppearance = jsonObject.getJSONObject(strKey);
			jsonVideo.appearance.mbPlayerControls = getJsonBoolean(jAppearance, JSON_PLAYER_CONTROLS);
		}

		return true;
	}

	public boolean parseJsonMap(JSONObject jsonObject, JsonMap jsonMap) throws JSONException
	{
		String strKey = null;
		if (null == jsonObject || null == jsonMap)
		{
			return false;
		}
		jsonMap.mstrAddress = getJsonString(jsonObject, JSON_ADDRESS);
		jsonMap.mstrUrl = getJsonString(jsonObject, JSON_URL);
		jsonMap.mdLongitude = getJsonDouble(jsonObject, JSON_LONGITUDE);
		jsonMap.mdlatitude = getJsonDouble(jsonObject, JSON_LATITUDE);

		strKey = getValidKey(jsonObject, JSON_APPEARANCE);
		if (null != strKey)
		{
			JSONObject jAppearance = jsonObject.getJSONObject(strKey);
			jsonMap.appearance.mnZoomLevel = getJsonInt(jAppearance, JSON_ZOOM_LEVEL);
			jsonMap.appearance.mnMapType = getJsonInt(jAppearance, JSON_MAP_TYPE);
			jsonMap.appearance.mstrMapTypeName = getJsonString(jAppearance, JSON_MAP_TYPE_NAME);
			jsonMap.appearance.mbStreetView = getJsonBoolean(jAppearance, JSON_STREET_VIEW);
			jsonMap.appearance.mstrMarkAs = getJsonString(jAppearance, JSON_MARK_AS);
		}

		return true;
	}

	public boolean parseJsonButton(JSONObject jsonObject, JsonButton jsonButton) throws JSONException
	{
		String strKey = null;
		if (null == jsonObject || null == jsonButton)
		{
			return false;
		}
		jsonButton.mstrTouchDown = getJsonString(jsonObject, JSON_TOUCH_DOWN_SRC);
		jsonButton.mstrTouchUp = getJsonString(jsonObject, JSON_TOUCH_UP_SRC);

		strKey = getValidKey(jsonObject, JSON_EVENT);
		if (null != strKey)
		{
			return parseJsonEvent(jsonObject, jsonButton.listEvent);
		}
		return false;
	}

	public boolean parseJsonPostcard(JSONObject jsonObject, JsonPostcard jsonPostcard) throws JSONException
	{
		String strKey = null;
		if (null == jsonObject || null == jsonPostcard)
		{
			return false;
		}
		jsonPostcard.mstrSrcFront = getJsonString(jsonObject, JSON_SRC_FRONT);
		jsonPostcard.mstrSrcBack = getJsonString(jsonObject, JSON_SRC_BACK);

		strKey = getValidKey(jsonObject, JSON_ERASER);
		if (null != strKey)
		{
			JSONObject jsonEraser = jsonObject.getJSONObject(strKey);
			jsonPostcard.eraser.mnWidth = getJsonInt(jsonEraser, JSON_WIDTH);
			jsonPostcard.eraser.mnHeight = getJsonInt(jsonEraser, JSON_HEIGHT);
			jsonPostcard.eraser.mnX = getJsonInt(jsonEraser, JSON_X);
			jsonPostcard.eraser.mnY = getJsonInt(jsonEraser, JSON_Y);
			jsonPostcard.eraser.mstrSrc = getJsonString(jsonEraser, JSON_SRC);
		}
		else
		{
			jsonPostcard.eraser = null;
		}

		strKey = getValidKey(jsonObject, JSON_PEN);
		if (null != strKey)
		{
			JSONObject jsonPen = jsonObject.getJSONObject(strKey);
			jsonPostcard.pen.mnWidth = getJsonInt(jsonPen, JSON_WIDTH);
			jsonPostcard.pen.mnHeight = getJsonInt(jsonPen, JSON_HEIGHT);
			jsonPostcard.pen.mnX = getJsonInt(jsonPen, JSON_X);
			jsonPostcard.pen.mnY = getJsonInt(jsonPen, JSON_Y);
			jsonPostcard.pen.mstrSrc = getJsonString(jsonPen, JSON_SRC);
		}
		else
		{
			jsonPostcard.pen = null;
		}

		strKey = getValidKey(jsonObject, JSON_TEXT_AREA);
		if (null != strKey)
		{
			JSONObject jsonTextArea = jsonObject.getJSONObject(strKey);
			jsonPostcard.textArea.mnWidth = getJsonInt(jsonTextArea, JSON_WIDTH);
			jsonPostcard.textArea.mnHeight = getJsonInt(jsonTextArea, JSON_HEIGHT);
			jsonPostcard.textArea.mnX = getJsonInt(jsonTextArea, JSON_X);
			jsonPostcard.textArea.mnY = getJsonInt(jsonTextArea, JSON_Y);
			jsonPostcard.textArea.mstrSrc = getJsonString(jsonTextArea, JSON_SRC);
		}
		else
		{
			jsonPostcard.textArea = null;
		}

		strKey = getValidKey(jsonObject, JSON_CAMERA);
		if (null != strKey)
		{
			JSONObject jsonCamera = jsonObject.getJSONObject(strKey);
			jsonPostcard.camera.mnWidth = getJsonInt(jsonCamera, JSON_WIDTH);
			jsonPostcard.camera.mnHeight = getJsonInt(jsonCamera, JSON_HEIGHT);
			jsonPostcard.camera.mnX = getJsonInt(jsonCamera, JSON_X);
			jsonPostcard.camera.mnY = getJsonInt(jsonCamera, JSON_Y);
			jsonPostcard.camera.mstrSrc = getJsonString(jsonCamera, JSON_SRC);
		}
		else
		{
			jsonPostcard.camera = null;
		}

		strKey = getValidKey(jsonObject, JSON_OPEN_BUTTON);
		if (null != strKey)
		{
			JSONObject jsonOpenButton = jsonObject.getJSONObject(strKey);
			jsonPostcard.openButton.mnWidth = getJsonInt(jsonOpenButton, JSON_WIDTH);
			jsonPostcard.openButton.mnHeight = getJsonInt(jsonOpenButton, JSON_HEIGHT);
			jsonPostcard.openButton.mnX = getJsonInt(jsonOpenButton, JSON_X);
			jsonPostcard.openButton.mnY = getJsonInt(jsonOpenButton, JSON_Y);
			jsonPostcard.openButton.mstrSrc = getJsonString(jsonOpenButton, JSON_SRC);
		}
		else
		{
			jsonPostcard.openButton = null;
		}

		strKey = getValidKey(jsonObject, JSON_MAILBOX);
		if (null != strKey)
		{
			JSONObject jsonMailBox = jsonObject.getJSONObject(strKey);
			jsonPostcard.mailBox.mnWidth = getJsonInt(jsonMailBox, JSON_WIDTH);
			jsonPostcard.mailBox.mnHeight = getJsonInt(jsonMailBox, JSON_HEIGHT);
			jsonPostcard.mailBox.mnX = getJsonInt(jsonMailBox, JSON_X);
			jsonPostcard.mailBox.mnY = getJsonInt(jsonMailBox, JSON_Y);
			jsonPostcard.mailBox.mstrSrc = getJsonString(jsonMailBox, JSON_SRC);
		}
		else
		{
			jsonPostcard.mailBox = null;
		}

		return true;
	}

	//	public int getScaleUnit(int original)
	//	{
	//		if (null != metrics && metrics.densityDpi > 160)
	//		{
	//			return (int) (original * metrics.densityDpi / 160);// metrics.density
	//		}
	//		else
	//		{
	//			return original;
	//		}
	//	}

	public int ScaleSize(int nSize)
	{
		return Global.ScaleSize(nSize);
	}

	public boolean isCreateValid(ViewGroup container, String strBookPath, JSONObject jsonAll, String strJsonKey)
	{
		String strKey = null;
		strKey = getValidKey(jsonAll, strJsonKey);
		if (null == getContext() || null == container || null == strBookPath || null == strKey)
		{
			return false;
		}
		return true;
	}

	public boolean isValid(JSONObject jsonObject, String strKey)
	{
		if (null == jsonObject || null == strKey)
		{
			return false;
		}
		if (!jsonObject.isNull(strKey))
		{
			return true;
		}
		return false;
	}

	public String getValidKey(JSONObject jsonObject, String strKey)
	{
		if (null == jsonObject || null == strKey)
		{
			return null;
		}

		if (isValid(jsonObject, strKey))
		{
			return strKey;
		}

		String lowCase = Character.toLowerCase(strKey.charAt(0)) + (strKey.length() > 1 ? strKey.substring(1) : "");

		if (isValid(jsonObject, lowCase))
		{
			return lowCase;
		}

		lowCase = strKey.toLowerCase();
		if (isValid(jsonObject, lowCase))
		{
			return lowCase;
		}

		if (oldKeys.containsKey(strKey))
		{
			String strOld = oldKeys.get(strKey);
			if (isValid(jsonObject, strOld))
			{
				return strOld;
			}

			lowCase = Character.toLowerCase(strOld.charAt(0)) + (strOld.length() > 1 ? strOld.substring(1) : "");
			if (isValid(jsonObject, lowCase))
			{
				return lowCase;
			}

			lowCase = strOld.toLowerCase();
			if (isValid(jsonObject, lowCase))
			{
				return lowCase;
			}
		}

		if (oldKeys.containsKey(strKey + "old"))
		{
			String strOld = oldKeys.get(strKey + "old");
			if (isValid(jsonObject, strOld))
			{
				return strOld;
			}

			lowCase = Character.toLowerCase(strOld.charAt(0)) + (strOld.length() > 1 ? strOld.substring(1) : "");
			if (isValid(jsonObject, lowCase))
			{
				return lowCase;
			}

			lowCase = strOld.toLowerCase();
			if (isValid(jsonObject, lowCase))
			{
				return lowCase;
			}
		}

		return null;
	}

	private String getJsonString(JSONObject jsonObject, String strKey)
	{
		String strValidKey = null;
		String strValue = null;

		strValidKey = getValidKey(jsonObject, strKey);
		if (null != strValidKey)
		{
			try
			{
				strValue = jsonObject.getString(strValidKey);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

		return strValue;
	}

	private int getJsonInt(JSONObject jsonObject, String strKey)
	{
		String strValidKey = null;
		int nValue = Type.INVALID;
		strValidKey = getValidKey(jsonObject, strKey);
		if (null != strValidKey)
		{
			try
			{
				nValue = jsonObject.getInt(strValidKey);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

		return nValue;
	}

	private boolean getJsonBoolean(JSONObject jsonObject, String strKey)
	{
		String strValidKey = null;
		boolean bValue = false;
		strValidKey = getValidKey(jsonObject, strKey);
		if (null != strValidKey)
		{
			try
			{
				bValue = jsonObject.getBoolean(strValidKey);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

		return bValue;
	}

	private double getJsonDouble(JSONObject jsonObject, String strKey)
	{
		String strValidKey = null;
		double dbValue = 0;
		strValidKey = getValidKey(jsonObject, strKey);
		if (null != strValidKey)
		{
			try
			{
				dbValue = jsonObject.getDouble(strValidKey);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

		return dbValue;
	}
}
