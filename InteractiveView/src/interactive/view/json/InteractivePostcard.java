package interactive.view.json;

import interactive.view.global.Global;
import interactive.view.postcard.Postcard;
import interactive.view.postcard.PostcardMailbox;
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
					int nKey = Global.interactiveHandler.addPostcard(new Postcard(getContext(), webView));

					Global.interactiveHandler.getPostcard(nKey).initPostcardFrame(jsonHeader.mstrName,
							ScaleSize(jsonHeader.mnX), ScaleSize(jsonHeader.mnY), ScaleSize(jsonHeader.mnWidth),
							ScaleSize(jsonHeader.mnHeight), strBookPath + jsonBody.mstrSrcFront,
							strBookPath + jsonBody.mstrSrcBack);

					if (null != jsonBody.pen)
					{
						Global.interactiveHandler.getPostcard(nKey).initPen(ScaleSize(jsonBody.pen.mnWidth),
								ScaleSize(jsonBody.pen.mnHeight), ScaleSize(jsonBody.pen.mnX),
								ScaleSize(jsonBody.pen.mnY), strBookPath + jsonBody.pen.mstrSrc);
					}

					if (null != jsonBody.eraser)
					{
						Global.interactiveHandler.getPostcard(nKey).initEraser(ScaleSize(jsonBody.eraser.mnWidth),
								ScaleSize(jsonBody.eraser.mnHeight), ScaleSize(jsonBody.eraser.mnX),
								ScaleSize(jsonBody.eraser.mnY), strBookPath + jsonBody.eraser.mstrSrc);
					}

					if (null != jsonBody.camera)
					{
						Global.interactiveHandler.getPostcard(nKey).initCamera(ScaleSize(jsonBody.camera.mnWidth),
								ScaleSize(jsonBody.camera.mnHeight), ScaleSize(jsonBody.camera.mnX),
								ScaleSize(jsonBody.camera.mnY), strBookPath + jsonBody.camera.mstrSrc);
					}

					if (null != jsonBody.openButton)
					{
						Global.interactiveHandler.getPostcard(nKey).initOpenButton(
								ScaleSize(jsonBody.openButton.mnWidth), ScaleSize(jsonBody.openButton.mnHeight),
								ScaleSize(jsonBody.openButton.mnX), ScaleSize(jsonBody.openButton.mnY),
								strBookPath + jsonBody.openButton.mstrSrc);
					}

					if (null != jsonBody.mailBox)
					{
						PostcardMailbox mailBox = new PostcardMailbox(getContext());
						mailBox.setDisplay(ScaleSize(jsonBody.mailBox.mnX), ScaleSize(jsonBody.mailBox.mnY),
								ScaleSize(jsonBody.mailBox.mnWidth), ScaleSize(jsonBody.mailBox.mnHeight));
						mailBox.setImage(strBookPath + jsonBody.mailBox.mstrSrc);
						webView.addView(mailBox);
					}

					if (null != jsonBody.textArea)
					{
						Global.interactiveHandler.getPostcard(nKey).initTextArea(ScaleSize(jsonBody.textArea.mnWidth),
								ScaleSize(jsonBody.textArea.mnHeight), ScaleSize(jsonBody.textArea.mnX),
								ScaleSize(jsonBody.textArea.mnY), strBookPath + jsonBody.textArea.mstrSrc);
					}

				}
			}
		}
		return false;
	}
}
