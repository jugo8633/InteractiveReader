/**
 * @author jugo
 * @descript define event & message
 */

package interactive.interactiveview.message;

import frame.common.Type;

public class EventMessage
{
	/**
	 * 定義程式執行的模式
	 */
	public static final int	RUN_MODE						= EventMessage.RUN_READER;

	/**
	 * @author jugo
	 * @descript define view run
	 */
	public static final int	WND_MSG							= 2000;
	public static final int	WND_SHOW						= WND_MSG + 1;
	public static final int	WND_STOP						= WND_MSG + 2;
	public static final int	WND_EXCP						= WND_MSG + 3;
	public static final int	WND_ITEM_CLICK					= WND_MSG + 4;
	public static final int	WND_SHOW_USER_WND				= WND_MSG + 5;
	public static final int	WND_FINISH						= WND_MSG + 6;
	public static final int	WND_BTN_OK						= WND_MSG + 7;
	public static final int	WND_BTN_CANCEL					= WND_MSG + 8;
	public static final int	WND_SHOW_BOOK_STORE				= WND_MSG + 9;
	public static final int	WND_SHOW_BOOKSHELF				= WND_MSG + 10;
	public static final int	WND_SHOW_PREVIEW				= WND_MSG + 11;
	public static final int	WND_SHOW_READER					= WND_MSG + 12;
	public static final int	WND_SHOW_BOOK_LIST				= WND_MSG + 13;
	public static final int	WND_UPDATE_BOOK_LIST			= WND_MSG + 14;

	public static final int	EVENT_HANDLE					= 3000;
	public static final int	EVENT_HANDLE_CREATED			= EVENT_HANDLE + 1;
	public static final int	EVENT_HANDLE_ON_CLICK			= EVENT_HANDLE + 2;
	public static final int	EVENT_HANDLE_ON_PRESS			= EVENT_HANDLE + 3;
	public static final int	EVENT_HANDLE_ON_TOUCH			= EVENT_HANDLE + 4;
	public static final int	EVENT_HANDLE_ON_TOUCH_DOWN		= EVENT_HANDLE + 5;
	public static final int	EVENT_HANDLE_ON_TOUCH_UP		= EVENT_HANDLE + 6;
	public static final int	EVENT_HANDLE_ON_TOUCH_MOVE		= EVENT_HANDLE + 7;
	public static final int	EVENT_HANDLE_ON_TOUCH_CANCEL	= EVENT_HANDLE + 8;
	public static final int	EVENT_HANDLE_ON_TOUCH_OUTSIDE	= EVENT_HANDLE + 9;
	public static final int	EVENT_HANDLE_ON_DOUBLE_CLICK	= EVENT_HANDLE + 10;

	public static final int	HANDLER_MSG						= 4000;

	public static final int	DATA_TYPE						= 5000;
	public static final int	DATA_APP						= DATA_TYPE + 1;
	public static final int	DATA_BOOK_STORE					= DATA_TYPE + 2;
	public static final int	DATA_BOOKSHELF					= DATA_TYPE + 3;
	public static final int	DATA_PREVIEW					= DATA_TYPE + 4;
	public static final int	DATA_OPEN						= DATA_TYPE + 5;

	/**
	 * @author jugo
	 * @descript define view run
	 */
	// windows
	public static final int	RUN_WND							= 6000;
	public static final int	RUN_READER						= RUN_WND + 1;
	public static final int	RUN_BOOK_LIST					= RUN_WND + 2;
	public static final int	RUN_MAX							= RUN_WND + 3;

	// book list mode
	public static final int	RUN_BOOK_STORE					= RUN_WND + 4;
	public static final int	RUN_BOOKSHELF					= RUN_WND + 5;
	public static final int	RUN_PREVIEW						= RUN_WND + 6;

	public static final int	MSG_CUSTOM						= 7000;
	public static final int	MSG_CHAPTER						= MSG_CUSTOM + 1;
	public static final int	MSG_PAGE						= MSG_CUSTOM + 2;
	public static final int	MSG_VIEW_INIT					= MSG_CUSTOM + 3;
	public static final int	MSG_WEB							= MSG_CUSTOM + 4;
	public static final int	MSG_JUMP						= MSG_CUSTOM + 5;
	public static final int	MSG_VIEW_CHANGE					= MSG_CUSTOM + 6;
	public static final int	MSG_VIDEO_PLAY					= MSG_CUSTOM + 7;
	public static final int	MSG_VIDEO_PAUSE					= MSG_CUSTOM + 8;
	public static final int	MSG_VIDEO_STOP					= MSG_CUSTOM + 9;
	public static final int	MSG_SHOW_PROGRESS				= MSG_CUSTOM + 10;
	public static final int	MSG_FLIPPER_CLOSE				= MSG_CUSTOM + 11;
	public static final int	MSG_NOTIFY_ROTATE				= MSG_CUSTOM + 12;
	public static final int	MSG_IMAGE_CLICK					= MSG_CUSTOM + 13;
	public static final int	MSG_SHOW_ITEM					= MSG_CUSTOM + 14;

	/**
	 * @author jugo
	 * @descript define key code
	 */
	public static final int	KEY_BACK						= 4;

	public static class ObjectMessage
	{
		public int	nDataType	= Type.INVALID;
		public int	nDataKey	= Type.INVALID;

		public ObjectMessage()
		{

		}
	}

	public EventMessage()
	{

	}
}
