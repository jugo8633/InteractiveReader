/**
 * @author jugo
 * @description ScrollHandler主要控制scroll物件於scroll時 如果是over scroll則換頁
 */

package interactive.view.scroll;

import android.view.MotionEvent;
import android.view.View;
import interactive.common.EventHandler;
import interactive.common.EventMessage;
import interactive.common.Type;
import interactive.view.global.Global;

public class ScrollHandler
{

	public static final int	HORIZON			= 0;
	public static final int	VERTICAL		= 1;

	private boolean			mbOverScrolled	= false;
	private int				mnDirect		= Type.INVALID;
	private float			mfX				= Type.INVALID;
	private float			mfY				= Type.INVALID;
	private int				mnChapter		= Type.INVALID;
	private int				mnPage			= Type.INVALID;

	public ScrollHandler(int nDirect)
	{
		super();
		mnDirect = nDirect;
	}

	public void setPosition(int nChapter, int nPage)
	{
		mnChapter = nChapter;
		mnPage = nPage;
	}

	public void setOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
	{
		switch (mnDirect)
		{
		case HORIZON:
			mbOverScrolled = clampedX;
			break;
		case VERTICAL:
			mbOverScrolled = clampedY;
			break;
		}
	}

	private boolean horizonTouchEvent(View view, MotionEvent event)
	{
		boolean bResult = false;
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if (!mbOverScrolled)
			{
				mfX = Type.INVALID;
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_HORIZON, 0, 0, null);
			}
			else
			{
				mfX = event.getRawX();
			}
			mfY = event.getRawY();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (Type.INVALID != mfX && mbOverScrolled)
			{
				float nX = event.getRawX();
				int nMove = (int) Math.abs(mfX - nX);
				if (10 <= nMove)
				{
					if (mfX < nX)
					{
						EventHandler.notify(Global.handlerActivity, EventMessage.MSG_JUMP, mnChapter - 1, Type.INVALID,
								null);
					}
					else
					{
						EventHandler.notify(Global.handlerActivity, EventMessage.MSG_JUMP, mnChapter + 1, Type.INVALID,
								null);
					}
					bResult = true;
					mbOverScrolled = false;
				}
			}
			EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_HORIZON, 0, 0, null);
			if (Type.INVALID != mfY)
			{
				float nY = event.getRawY();
				int nMove = (int) Math.abs(mfY - nY);
				if (200 <= nMove)
				{
					if (mfY < nY)
					{
						EventHandler.notify(Global.handlerActivity, EventMessage.MSG_JUMP, Type.INVALID, mnPage - 1,
								null);
					}
					else
					{
						EventHandler.notify(Global.handlerActivity, EventMessage.MSG_JUMP, Type.INVALID, mnPage + 1,
								null);
					}
					bResult = true;
					mbOverScrolled = false;
				}
			}
			mfY = Type.INVALID;
			mfX = Type.INVALID;
			break;
		}

		return bResult;
	}

	private boolean verticalTouchEvent(View view, MotionEvent event)
	{
		boolean bResult = false;
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if (!mbOverScrolled)
			{
				mfY = Type.INVALID;
				EventHandler.notify(Global.handlerActivity, EventMessage.MSG_LOCK_VERTICAL, 0, 0, null);
			}
			else
			{
				mfY = event.getRawY();
			}
			mfX = event.getRawX();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (Type.INVALID != mfY && mbOverScrolled)
			{
				float nY = event.getRawY();
				int nMove = (int) Math.abs(mfY - nY);
				if (10 <= nMove)
				{
					if (mfY < nY)
					{
						EventHandler.notify(Global.handlerActivity, EventMessage.MSG_JUMP, Type.INVALID, mnPage - 1,
								null);
					}
					else
					{
						EventHandler.notify(Global.handlerActivity, EventMessage.MSG_JUMP, Type.INVALID, mnPage + 1,
								null);
					}
					bResult = true;
					mbOverScrolled = false;
				}
			}
			EventHandler.notify(Global.handlerActivity, EventMessage.MSG_UNLOCK_VERTICAL, 0, 0, null);
			if (Type.INVALID != mfX)
			{
				float nX = event.getRawX();
				int nMove = (int) Math.abs(mfX - nX);
				if (200 < nMove)
				{
					if (mfX < nX)
					{
						EventHandler.notify(Global.handlerActivity, EventMessage.MSG_JUMP, mnChapter - 1, Type.INVALID,
								null);
					}
					else
					{
						EventHandler.notify(Global.handlerActivity, EventMessage.MSG_JUMP, mnChapter + 1, Type.INVALID,
								null);
					}
					bResult = true;
					mbOverScrolled = false;
				}
			}
			mfY = Type.INVALID;
			mfX = Type.INVALID;
			break;
		}

		return bResult;
	}

	public boolean setTouchEvent(View view, MotionEvent event)
	{
		switch (mnDirect)
		{
		case HORIZON:
			return horizonTouchEvent(view, event);
		case VERTICAL:
			return verticalTouchEvent(view, event);
		}
		return false;
	}
}
