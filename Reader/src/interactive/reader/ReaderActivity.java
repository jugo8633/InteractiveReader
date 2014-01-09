package interactive.reader;

import interactive.common.Logs;
import android.app.Activity;
import android.os.Bundle;

public class ReaderActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		int nResId = getResourceId("reader", "layout");

		this.setContentView(nResId);

		Logs.showTrace("Reader Activity Create");
	}

	public int getResourceId(String name, String defType)
	{
		return getResources().getIdentifier(name, defType, getPackageName());
	}
}
