/**
 * @author jugo
 * @descript define event & message
 */

package interactive.common;

public class EventMessage
{
	public static final int	MSG_CUSTOM					= 1024;
	public static final int	MSG_CHAPTER					= MSG_CUSTOM + 1;
	public static final int	MSG_PAGE					= MSG_CUSTOM + 2;
	public static final int	MSG_VIEW_INIT				= MSG_CUSTOM + 3;
	public static final int	MSG_WEB						= MSG_CUSTOM + 4;
	public static final int	MSG_JUMP					= MSG_CUSTOM + 5;
	public static final int	MSG_VIEW_CHANGE				= MSG_CUSTOM + 6;
	public static final int	MSG_VIDEO_PLAY				= MSG_CUSTOM + 7;
	public static final int	MSG_VIDEO_PAUSE				= MSG_CUSTOM + 8;
	public static final int	MSG_VIDEO_STOP				= MSG_CUSTOM + 9;
	public static final int	MSG_SHOW_PROGRESS			= MSG_CUSTOM + 10;
	public static final int	MSG_FLIPPER_CLOSE			= MSG_CUSTOM + 11;
	public static final int	MSG_NOTIFY_ROTATE			= MSG_CUSTOM + 12;
	public static final int	MSG_IMAGE_CLICK				= MSG_CUSTOM + 13;
	public static final int	MSG_SHOW_ITEM				= MSG_CUSTOM + 14;
	public static final int	MSG_DOUBLE_CLICK			= MSG_CUSTOM + 15;
	public static final int	MSG_START_UNEXPRESS			= MSG_CUSTOM + 16;
	public static final int	MSG_CHECKED_BOOK			= MSG_CUSTOM + 17;
	public static final int	MSG_GO_FORWARD				= MSG_CUSTOM + 18;
	public static final int	MSG_OPTION_ITEM_SELECTED	= MSG_CUSTOM + 19;
	public static final int	MSG_LOCK_PAGE				= MSG_CUSTOM + 20;

	/**
	 * @author jugo
	 * @descript define key code
	 */
	public static final int	KEY_BACK					= 4;
}
