package interactive.view.json;

import interactive.common.Device;
import interactive.view.postcard.PostCardUtility;
import interactive.view.webview.InteractiveWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class InteractivePostcard extends InteractiveObject
{

	public InteractivePostcard(Context context)
	{
		super(context);
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	@Override
	public boolean createInteractive(InteractiveWebView webView, String strBookPath, JSONObject jsonAll)
			throws JSONException
	{
		if (!isCreateValid(webView, strBookPath, jsonAll, JSON_POSTCARD))
		{
			return false;
		}

		JSONArray jsonArrayPostcard = jsonAll.getJSONArray(JSON_POSTCARD);
		for (int i = 0; i < jsonArrayPostcard.length(); ++i)
		{
			JSONObject jsonPostcard = jsonArrayPostcard.getJSONObject(i);
			JsonHeader jsonHeader = new JsonHeader();
			JsonPostcard jsonBody = new JsonPostcard();
			if (parseJsonHeader(jsonPostcard, jsonHeader) && parseJsonPostcard(jsonPostcard, jsonBody))
			{
				if (jsonHeader.mbIsVisible)
				{
					PostCardUtility pcu = new PostCardUtility(getContext(), webView);
					pcu.setPosterCard(getScaleUnit(jsonHeader.mnWidth), getScaleUnit(jsonHeader.mnHeight),
							getScaleUnit(jsonHeader.mnX), getScaleUnit(jsonHeader.mnY), strBookPath
									+ jsonBody.mstrSrcFront);
					// 設定繪圖物件
					pcu.setFingerPaintView(getScaleUnit(jsonHeader.mnWidth), getScaleUnit(jsonHeader.mnHeight),
							getScaleUnit(jsonHeader.mnX), getScaleUnit(jsonHeader.mnY), strBookPath
									+ jsonBody.mstrSrcBack);
					// 設定畫筆
					pcu.setDrawingPen(getScaleUnit(jsonBody.pen.mnWidth), getScaleUnit(jsonBody.pen.mnHeight),
							getScaleUnit(jsonBody.pen.mnX), getScaleUnit(jsonBody.pen.mnY), strBookPath
									+ jsonBody.pen.mstrSrc);
					// 設定橡皮擦
					pcu.setDrawingEraser(getScaleUnit(jsonBody.eraser.mnWidth), getScaleUnit(jsonBody.eraser.mnHeight),
							getScaleUnit(jsonBody.eraser.mnX), getScaleUnit(jsonBody.eraser.mnY), strBookPath
									+ jsonBody.eraser.mstrSrc);

					// 設定相機
					pcu.setCamera(getScaleUnit(jsonBody.camera.mnWidth), getScaleUnit(jsonBody.camera.mnHeight),
							getScaleUnit(jsonBody.camera.mnX), getScaleUnit(jsonBody.camera.mnY), strBookPath
									+ jsonBody.camera.mstrSrc);
					// 設定相簿圖片
					Device device = new Device(getContext());
					int listParams[] = { 250, 300, device.getDisplayWidth() - 270, 120 };
					device = null;

					pcu.setAlbum(getScaleUnit(jsonBody.openButton.mnWidth), getScaleUnit(jsonBody.openButton.mnHeight),
							getScaleUnit(jsonBody.openButton.mnX), getScaleUnit(jsonBody.openButton.mnY), strBookPath
									+ jsonBody.openButton.mstrSrc, listParams);
					// 設定繪圖
					pcu.setDrawing(getScaleUnit(jsonBody.camera.mnWidth), getScaleUnit(jsonBody.camera.mnHeight),
							getScaleUnit(jsonHeader.mnX), getScaleUnit(jsonHeader.mnY)
									+ getScaleUnit(jsonHeader.mnHeight), strBookPath + jsonBody.camera.mstrSrc);//pen.getString(JSON_SRC));

					// 設定郵寄
					pcu.setPostBox(getScaleUnit(jsonBody.mailBox.mnWidth), getScaleUnit(jsonBody.mailBox.mnHeight),
							getScaleUnit(jsonBody.mailBox.mnX), getScaleUnit(jsonBody.mailBox.mnY), strBookPath
									+ jsonBody.mailBox.mstrSrc);

					//					if (textArea != null)
					//						pcu.setTextArea(getScaleUnit(textArea.getInt(JSON_WIDTH)),
					//								getScaleUnit(textArea.getInt(JSON_HEIGHT)), getScaleUnit(textArea.getInt(JSON_X)),
					//								getScaleUnit(textArea.getInt(JSON_Y)), strBookPath + textArea.getString(JSON_SRC));
				}
			}
		}
		return false;
	}

}