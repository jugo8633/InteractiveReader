package interactive.facebook;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import interactive.common.Logs;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;

public class Facebook
{

	private Activity				theActivity		= null;
	private Session.StatusCallback	statusCallback	= new SessionStatusCallback();

	public Facebook(Activity activity)
	{
		super();
		theActivity = activity;
	}

	public void init()
	{
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null)
		{
			session = new Session(theActivity);
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED))
			{
				session.openForRead(new Session.OpenRequest(theActivity).setCallback(statusCallback));
			}
		}

		try
		{
			PackageInfo info = theActivity.getPackageManager().getPackageInfo(theActivity.getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures)
			{
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
				Logs.showTrace("MY KEY HASH: " + sign);
			}
		}
		catch (NameNotFoundException e)
		{
		}
		catch (NoSuchAlgorithmException e)
		{
		}

	}

	public void stop()
	{
		Session.getActiveSession().removeCallback(statusCallback);
		Logs.showTrace("Facebook remove callback ##############");
	}

	public void login()
	{
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed())
		{
			session.openForRead(new Session.OpenRequest(theActivity).setCallback(statusCallback));
			Logs.showTrace("Facebook Session open #################");
		}
		else
		{
			Session.openActiveSession(theActivity, true, statusCallback);
		}
	}

	public void ActivityResult(int requestCode, int resultCode, Intent data)
	{
		Session.getActiveSession().onActivityResult(theActivity, requestCode, resultCode, data);
		Logs.showTrace("Facebook run facebook activity result");
	}

	private class SessionStatusCallback implements Session.StatusCallback
	{
		@Override
		public void call(Session session, SessionState state, Exception exception)
		{
			Logs.showTrace("Facebook status: " + state.toString() + " #######################");

			if (session.isOpened())
			{
				Request.newMeRequest(session, new Request.GraphUserCallback()
				{
					@Override
					public void onCompleted(GraphUser user, Response response)
					{
						Logs.showTrace("Facebook User: " + user.getName() + " logined");
					}
				});
			}
		}
	}

}
