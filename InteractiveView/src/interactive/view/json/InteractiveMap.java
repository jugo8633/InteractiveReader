package interactive.view.json;

import interactive.view.global.Global;
import interactive.view.handler.InteractiveEvent;
import interactive.view.map.GoogleMapView;
import interactive.view.webview.InteractiveWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;

import android.content.Context;

public class InteractiveMap extends InteractiveObject
{
	public InteractiveMap(Context context)
	{
		super(context);
	}

	@Override
	public boolean createInteractive(final InteractiveWebView webView, String strBookPath, JSONObject jsonAll)
			throws JSONException
	{
		String strKey = null;
		if (!isCreateValid(webView, strBookPath, jsonAll, JSON_MAP))
		{
			return false;
		}

		strKey = getValidKey(jsonAll, JSON_MAP);
		if (null == strKey)
		{
			return false;
		}
		JSONArray jsonArrayMap = jsonAll.getJSONArray(strKey);
		for (int i = 0; i < jsonArrayMap.length(); ++i)
		{
			final JSONObject jsonMap = jsonArrayMap.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonMap jsonBody = new JsonMap();
			if (parseJsonHeader(jsonMap, jsonHeader) && parseJsonMap(jsonMap, jsonBody))
			{
				int nType = GoogleMap.MAP_TYPE_NORMAL;
				switch (jsonBody.appearance.mnMapType)
				{
				case InteractiveEvent.MAP_TYPE_NORMAL:
					nType = GoogleMap.MAP_TYPE_NORMAL;
					break;
				case InteractiveEvent.MAP_TYPE_SATELLITE:
					nType = GoogleMap.MAP_TYPE_SATELLITE;
					break;
				case InteractiveEvent.MAP_TYPE_MIX:
					nType = GoogleMap.MAP_TYPE_HYBRID;
					break;
				}

				if (!jsonHeader.mbIsVisible)
				{
					// 等待被呼叫 顯示方式為 activity
					Global.interactiveHandler.addGoogleMap(jsonHeader.mstrName, nType, jsonBody.mdLongitude,
							jsonBody.mdlatitude, jsonBody.appearance.mnZoomLevel, jsonBody.appearance.mstrMarkAs,
							ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY), ScaleSize(jsonHeader.mnWidth),
							ScaleSize(jsonHeader.mnHeight), webView.getBackgroundImage());
				}
				else
				{
					// 顯示方式為 parent view 的 child view
					GoogleMapView googleMapView = new GoogleMapView(getContext());
					googleMapView.setDisplay(ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
							ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight));

					googleMapView.init(jsonHeader.mstrName, Global.theActivity, nType, jsonBody.mdLongitude,
							jsonBody.mdlatitude, jsonBody.appearance.mnZoomLevel, jsonBody.appearance.mstrMarkAs);

					webView.addView(googleMapView);
				}
			}
			jsonHeader = null;
			jsonBody = null;
		}
		return false;
	}

}
