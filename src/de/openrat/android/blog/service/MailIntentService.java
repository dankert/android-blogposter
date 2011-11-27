/**
 * 
 */
package de.openrat.android.blog.service;

import java.io.File;
import java.io.IOException;

import de.openrat.android.blog.Blog;
import de.openrat.android.blog.FileUtils;
import de.openrat.android.blog.HTTPRequest;
import de.openrat.android.blog.R;
import de.openrat.android.blog.R.string;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author dankert
 * 
 */
public class MailIntentService extends IntentService
{

	private static final int NOTIFICATION_UPLOAD = 1;

	public MailIntentService()
	{
		super("UploadIntentService");
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		final String filePath = intent.getStringExtra(UploadIntentService.EXTRA_IMAGE);

		final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		final Intent notificationIntent = new Intent(this, Blog.class);
		final PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		final File file = new File(filePath);
		final String tickerText = getResources()
				.getString(R.string.upload_long);
		final Notification notification = new Notification(
				android.R.drawable.ic_menu_rotate, tickerText, System
						.currentTimeMillis());
		notification.setLatestEventInfo(getApplicationContext(), getResources()
				.getString(R.string.upload), file.getName(), contentIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_NO_CLEAR;
		nm.notify(NOTIFICATION_UPLOAD, notification);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		try
		{
			HTTPRequest request = new HTTPRequest();
			request.setFile(prefs.getString("param_image", "image"), FileUtils
					.getBytesFromFile(file), "image.png", "image/png", "binary");
			// Alles OK.
			final String msgText = getResources().getString(R.string.upload_ok);
			notification.tickerText = getResources().getString(
					R.string.upload_ok_long);
			notification.setLatestEventInfo(getApplicationContext(), msgText,
					file.getName(), contentIntent);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			nm.notify(NOTIFICATION_UPLOAD, notification);
			Log.d(this.getClass().getName(), msgText);
		}
		catch (IOException e)
		{
			// Fehler ist aufgetreten.
			final String msgText = getResources().getString(
					R.string.upload_fail);
			notification.tickerText = getResources().getString(
					R.string.upload_fail_long);
			notification.setLatestEventInfo(getApplicationContext(), msgText, e
					.getMessage()
					+ ": " + file.getName(), contentIntent);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			nm.notify(NOTIFICATION_UPLOAD, notification);

			Log.e(this.getClass().getName(), msgText, e);
		}
		finally
		{
		}
	}
}
