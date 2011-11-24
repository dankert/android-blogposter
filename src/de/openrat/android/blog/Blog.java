package de.openrat.android.blog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Blog extends Activity
{
	private static final int TAKE_PHOTO_CODE = 1;
	private String imageFilename;
	private ImageView image;
	private EditText title;
	private EditText text;
	private Button button;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		title = (EditText) findViewById(R.id.title);
		text = (EditText) findViewById(R.id.text);
		image = (ImageView) findViewById(R.id.image);
		image.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				takePhoto();
			}
		});
		button = (Button) findViewById(R.id.save);
		button.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(Blog.this);

				if (prefs.getBoolean("use_upload", false))
				{
					final Intent uploadIntent = new Intent(Blog.this,
							UploadIntentService.class);

					uploadIntent.putExtra(UploadIntentService.EXTRA_TITLE,
							title.getText());
					uploadIntent.putExtra(UploadIntentService.EXTRA_TEXT, text
							.getText());
					uploadIntent.putExtra(UploadIntentService.EXTRA_IMAGE,
							imageFilename);
					startService(uploadIntent);

				}
				if (prefs.getBoolean("use_email", false))
				{
					final Intent mailIntent = new Intent(Blog.this,
							MailIntentService.class);
					mailIntent.putExtra(UploadIntentService.EXTRA_TITLE, title
							.getText());
					mailIntent.putExtra(UploadIntentService.EXTRA_TEXT, text
							.getText());
					mailIntent.putExtra(UploadIntentService.EXTRA_IMAGE,
							imageFilename);

					startService(mailIntent);
				}

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater mi = new MenuInflater(getApplication());
		mi.inflate(R.menu.main, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_preferences:
				startActivity(new Intent(this, PreferencesActivity.class));
				return true;
			case R.id.menu_take_photo:
				takePhoto();
				return true;
		}
		return false;
	}

	/*
	 * 
	 * Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
	 * public void onPictureTaken(byte[] imageData, Camera c) {
	 * 
	 * if (imageData != null) {
	 * 
	 * Intent mIntent = new Intent();
	 * 
	 * FileUtilities.StoreByteImage(mContext, imageData, 50, "ImageName");
	 * mCamera.startPreview();
	 * 
	 * setResult(FOTO_MODE,mIntent); finish();
	 * 
	 * 
	 * } } };
	 */

	private void takePhoto()
	{
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
				.fromFile(getTempFile(this)));
		startActivityForResult(intent, TAKE_PHOTO_CODE);
	}

	private File getTempFile(Context context)
	{
		// it will return /sdcard/image.tmp
		final File path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path.exists())
		{
			path.mkdir();
		}
		return new File(path, "image.tmp");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
				case TAKE_PHOTO_CODE:
					final File file = getTempFile(this);
					this.imageFilename = file.getAbsoluteFile().getName();
					try
					{
						Bitmap captureBmp = android.provider.MediaStore.Images.Media
								.getBitmap(getContentResolver(), Uri
										.fromFile(file));

						image.setImageBitmap(captureBmp);
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					break;
			}
		}
	}

}