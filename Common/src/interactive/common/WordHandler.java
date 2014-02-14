package interactive.common;

import android.content.Context;

public class WordHandler
{

	public WordHandler()
	{
		super();
	}

	public String getString(Context context, String strName)
	{
		int nResId = getResourceId(context, strName, "string");
		String strValue = context.getString(nResId);
		return strValue;
	}

	public int getResourceId(Context context, String name, String defType)
	{
		return context.getResources().getIdentifier(name, defType, context.getPackageName());
	}
}
