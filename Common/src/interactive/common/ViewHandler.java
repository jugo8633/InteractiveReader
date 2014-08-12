package interactive.common;

import android.content.Context;

public abstract class ViewHandler
{
	public static int getResourceId(Context context, String name, String defType)
	{
		return context.getResources().getIdentifier(name, defType, context.getPackageName());
	}
}
