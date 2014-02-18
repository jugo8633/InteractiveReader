package interactive.view.handler;

import android.util.SparseArray;

public class InteractiveButtonData
{
	public String								mstrTag			= null;
	public String								mstrGroupId		= null;
	public SparseArray<InteractiveImageData>	listImageData	= null;
	public SparseArray<InteractiveEventData>	listEventData	= null;

	public InteractiveButtonData()
	{
		listImageData = new SparseArray<InteractiveImageData>();
	}
}
