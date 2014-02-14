package interactive.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionHandler
{

	public VersionHandler()
	{
		super();
	}

	public int getVersionCode(Context context)
	{
		PackageInfo pinfo;
		try
		{
			pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pinfo.versionCode;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}

		return 0;
	}

	public String getVersionName(Context context)
	{
		PackageInfo pinfo;
		try
		{
			pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pinfo.versionName;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
