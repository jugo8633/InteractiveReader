package interactive.view.handler;

import interactive.common.Type;

public class InteractiveEventData
{
	public int		mnType			= Type.INVALID;
	public String	mstrTypeName	= null;
	public int		mnEvent			= Type.INVALID;
	public String	mstrEventName	= null;
	public int		mnTargetType	= Type.INVALID;
	public String	mstrTargetID	= null;
	public int		mnDisplay		= Type.INVALID;

	public InteractiveEventData(int nType, String strTypeName, int nEvent, String strEventName, int nTargetType,
			String strTargetID, int nDisplay)
	{
		mnType = nType;
		mstrTypeName = strTypeName;
		mnEvent = nEvent;
		mstrEventName = strEventName;
		mnTargetType = nTargetType;
		mstrTargetID = strTargetID;
		mnDisplay = nDisplay;
	}
}
