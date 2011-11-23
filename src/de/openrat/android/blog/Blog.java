package de.openrat.android.blog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Blog extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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

	private static final int TAKE_PHOTO_CODE = 1;

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
					try
					{
						Bitmap captureBmp = android.provider.MediaStore.Images.Media
								.getBitmap(getContentResolver(), Uri
										.fromFile(file));
						// do whatever you want with the bitmap (Resize, Rename,
						// Add To Gallery, etc)
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