package interactive.view.json;

import interactive.view.global.Global;
import interactive.view.handler.InteractiveDefine;
import interactive.view.handler.InteractiveGoogleMapData;
import interactive.view.map.GoogleMapView;
import interactive.view.webview.InteractiveWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;

import android.content.Context;
import android.util.SparseArray;

public class InteractiveMap extends InteractiveObject
{
	private String	mstrBackground	= null;

	public InteractiveMap(Context context)
	{
		super(context);
	}

	public void setBackground(String strBackground)
	{
		mstrBackground = strBackground;
	}

	@Override
	public boolean createInteractive(final InteractiveWebView webView, String strBookPath, JSONObject jsonAll,
			int nChapter, int nPage) throws JSONException
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
				if (jsonHeader.mbIsVisible)
				{
					int nType = getGoogleMapType(jsonBody.appearance.mnMapType);
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

	private int getGoogleMapType(int nType)
	{
		int nGoogleMapType = GoogleMap.MAP_TYPE_NORMAL;
		switch (nType)
		{
		case InteractiveDefine.MAP_TYPE_NORMAL:
			nGoogleMapType = GoogleMap.MAP_TYPE_NORMAL;
			break;
		case InteractiveDefine.MAP_TYPE_SATELLITE:
			nGoogleMapType = GoogleMap.MAP_TYPE_SATELLITE;
			break;
		case InteractiveDefine.MAP_TYPE_MIX:
			nGoogleMapType = GoogleMap.MAP_TYPE_HYBRID;
			break;
		}
		return nGoogleMapType;
	}

	public boolean getInteractiveGoogleMap(JSONObject jsonAll, SparseArray<InteractiveGoogleMapData> listGoogleMapData,
			int nChapter, int nPage) throws JSONException
	{
		if (null == jsonAll || null == listGoogleMapData)
		{
			return false;
		}

		String strKey = getValidKey(jsonAll, JSON_MAP);
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
				int nType = getGoogleMapType(jsonBody.appearance.mnMapType);
				listGoogleMapData.put(listGoogleMapData.size(), new InteractiveGoogleMapData(jsonHeader.mstrName,
						nType, jsonBody.mdLongitude, jsonBody.mdlatitude, jsonBody.appearance.mnZoomLevel,
						jsonBody.appearance.mstrMarkAs, ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY),
						ScaleSize(jsonHeader.mnWidth), ScaleSize(jsonHeader.mnHeight), mstrBackground,
						jsonHeader.mbIsVisible));
			}
			jsonHeader = null;
			jsonBody = null;
		}
		return true;
	}
}
