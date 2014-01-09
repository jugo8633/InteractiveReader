package interactive.common;

import android.os.Handler;
import android.os.Message;

public class EventHandler extends EventMessage
{
	private Handler	m_handler	= null;

	public EventHandler(Handler handler)
	{
		m_handler = handler;
	}

	public void sendMsg(int what, int arg1, int arg2, Object obj)
	{
		if (m_handler == null)
		{
			Logs.showTrace("invalid Handler");
			return;
		}
		Message msg = new Message();
		msg.what = what;
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		msg.obj = obj;
		m_handler.sendMessage(msg);
		msg = null;
	}

	synchronized public static void notify(Handler handler, int nWhat, int nEvent, int nPosition, Object object)
	{
		if (null != handler)
		{
			Message msg = new Message();
			msg.what = nWhat;
			msg.arg1 = nEvent;
			msg.arg2 = nPosition;
			msg.obj = object;
			handler.sendMessage(msg);
		}
	}
}
