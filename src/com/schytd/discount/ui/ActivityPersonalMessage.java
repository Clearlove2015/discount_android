package com.schytd.discount.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.schytd.discount.bussiness.UserBussiness;
import com.schytd.discount.bussiness.impl.UserBussinessImpl;
import com.schytd.discount.enties.User;
import com.schytd.discount.tools.ImageUtils;
import com.schytd.discount.tools.ImageUtils.OnLoadImageListener;
import com.schytd.discount.ui.View.RoundImageView;
import com.schytd.discount.ui.View.SelectPicPopupWindow;
import com.schytd.discount_android.R;

public class ActivityPersonalMessage extends Activity implements
		OnClickListener {
	public static int RESULT_MAIN_ACTIVITY = 112;
	public static int REQUEST_UPDATE_NICK = 121;
	public static int RESULT_TO_MAIN_NAME = 112;
	public static int REQUEST_GENDER = 122;
	public static int RESULT_PERSON_CENTER = 134;
	private TextView mTextViewSex;
	private TextView mTextViewPhoneNum;
	private TextView mTextViewNick;
	// 调用业务层
	private RoundImageView mImageView_img;
	// 业务层
	private UserBussiness mUserBussiness;
	private Bitmap bitmap;
	private User user;
	private int flag = 0;// 性别

	private void init() {
		mUserBussiness = new UserBussinessImpl(this);
		mTextViewSex = (TextView) findViewById(R.id.personal_msg_sex);
		mTextViewPhoneNum = (TextView) findViewById(R.id.personal_msg_phone_number);
		mTextViewNick = (TextView) findViewById(R.id.personal_msg_nickname);
		mImageView_img = (RoundImageView) this.findViewById(R.id.img);
		// 获取用户的信息
		user = (User) getIntent().getSerializableExtra("user");
		if (user != null) {
			mTextViewNick.setText(user.getName());
			mTextViewSex.setText(user.getGender());
			mTextViewPhoneNum.setText(user.getNum());
			// 设置头像
			ImageUtils imageUtils = new ImageUtils(this, user.getUserIcon());
			imageUtils.onLoadImage(new OnLoadImageListener() {
				@Override
				public void OnLoadImage(Bitmap bitmap, String bitmapPath) {
					if (bitmap != null) {
						mImageView_img.setImageBitmap(bitmap);
					}
				}
			});
			gender = user.getGender();
			nickName = user.getName();
		}
	}

	private String gender;
	private String nickName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_message);
		init();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_UPDATE_NICK) {
			if (resultCode == ActivityUpdataNickName.RESULTCODE) {
				String nickName = data.getStringExtra("NickName");
				mTextViewNick.setText(nickName);
			}
		}
		if (requestCode == REQUEST_GENDER) {
			if (resultCode == ActivityGender.RESULT_GENDER) {
				gender = data.getStringExtra("up_gender");
				mTextViewSex.setText(gender);
				// 传递性别
				if (gender.equals("男")) {
					flag = 0;
				} else {
					flag = 1;
				}
			}
		}
		if (requestCode == 2) {
			if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(ActivityPersonalMessage.this, "你没有选择任何图片",
						Toast.LENGTH_SHORT).show();
				return;
			}
			int previewWidth = mImageView_img.getWidth();
			int previewHeight = mImageView_img.getHeight();

			BitmapFactory.Options options = new BitmapFactory.Options();
			// 现在我想知道图片的真实宽高值，但是不用将整副图片读取到内存中就可以知道这两个值
			// 节省内存
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(this.filePath, options);
			int pictureWidth = options.outWidth;
			int pictureHeight = options.outHeight;

			int scaleFactor = Math.min(pictureWidth / previewWidth,
					pictureHeight / previewHeight);

			options.inJustDecodeBounds = false;
			options.inSampleSize = scaleFactor;
			bitmap = BitmapFactory.decodeFile(this.filePath, options);
			// 判断图片的大小
			int size = bitmap.getByteCount() / 1024;
			// 头像的格式
			String mImageFormat = filePath.substring(filePath.indexOf(".") + 1,
					filePath.length());
			if (size > 3000) {
				menuWindow.dismiss();
				Toast.makeText(ActivityPersonalMessage.this, "图片过大",
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				// 上传头像
				new ImageTask().execute(mImageFormat, filePath);
			}
			mImageView_img.setImageBitmap(bitmap);
			menuWindow.dismiss();
		}
		if (requestCode == 1) {
			if (data == null) {
				Toast.makeText(ActivityPersonalMessage.this, "你没有选择任何图片",
						Toast.LENGTH_SHORT).show();
			} else {
				Uri uri = data.getData();
				if (uri == null) {
					Toast.makeText(ActivityPersonalMessage.this, "你没有选择任何图片",
							Toast.LENGTH_SHORT).show();

				} else {
					String path = null;
					if (Integer.valueOf(android.os.Build.VERSION.SDK) >= Build.VERSION_CODES.KITKAT) {
						// android 4.4 获取相机中的图片
						path = getPath(getApplicationContext(), uri);

					} else {
						String[] pojo = { MediaStore.Images.Media.DATA };
						Cursor c = getContentResolver().query(uri, pojo, null,
								null, null);
						if (c != null) {
							int columnIndex = c.getColumnIndexOrThrow(pojo[0]);
							c.moveToFirst();
							path = c.getString(columnIndex);
							c.close();
						}
					}
					if (path == null) {
						Toast.makeText(ActivityPersonalMessage.this,
								"你未获得图片的真实路径", Toast.LENGTH_SHORT).show();
						return;
					}

					if (path.endsWith("png") || path.endsWith("jpg")
							|| path.endsWith("jpeg")) {

						int previewWidth = mImageView_img.getWidth();
						int previewHeight = mImageView_img.getHeight();

						BitmapFactory.Options options = new BitmapFactory.Options();
						// 现在我想知道图片的真实宽高值，但是不用将整副图片读取到内存中就可以知道这两个值
						// 节省内存
						options.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(path, options);
						int pictureWidth = options.outWidth;
						int pictureHeight = options.outHeight;

						int scaleFactor = Math.min(pictureWidth / previewWidth,
								pictureHeight / previewHeight);

						options.inJustDecodeBounds = false;
						options.inSampleSize = scaleFactor;
						bitmap = BitmapFactory.decodeFile(path, options);
						// 判断图片的大小
						int size = bitmap.getByteCount() / 1024;
						// 头像的格式
						String mImageFormat = path.substring(
								path.indexOf(".") + 1, path.length());
						if (size > 3000) {
							menuWindow.dismiss();
							Toast.makeText(ActivityPersonalMessage.this,
									"图片过大", Toast.LENGTH_SHORT).show();
							return;
						} else {
							// 上传头像
							new ImageTask().execute(mImageFormat, path);
						}
						if (bitmap != null) {
							mImageView_img.setImageBitmap(bitmap);
						}
						menuWindow.dismiss();
					} else {
						Toast.makeText(ActivityPersonalMessage.this,
								"你未选择图片文件", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

	// 上传头像api
	private class ImageTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				Toast.makeText(ActivityPersonalMessage.this, "上传成功！",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ActivityPersonalMessage.this, "上传失败！",
						Toast.LENGTH_SHORT).show();

			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			List<String> result = null;
			try {
				if (bitmap != null) {
					result = mUserBussiness.upLoadUserImage(params[0],
							params[1]);
					if (result.get(0).equals("0")) {
						return true;
					}
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	// 重写返回键事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.putExtra("gender", flag);
			setResult(RESULT_PERSON_CENTER, intent);
			finish();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_back:
			Intent data = new Intent();
			data.putExtra("name", mTextViewNick.getText().toString());
			setResult(RESULT_TO_MAIN_NAME, data);
			Intent intent = new Intent();
			intent.putExtra("gender", flag);
			setResult(RESULT_PERSON_CENTER, intent);
			finish();

			break;
		case R.id.personal_msg_btn1:
			// 修改昵称
			Intent i = new Intent(ActivityPersonalMessage.this,
					ActivityUpdataNickName.class);
			i.putExtra("oldName", mTextViewNick.getText().toString());
			startActivityForResult(i, REQUEST_UPDATE_NICK);
			break;
		case R.id.personal_msg_btn2:
			// 二维码名片
			try{
				Intent i_code = new Intent(ActivityPersonalMessage.this,
						ActivityMyQrCode.class);
				i_code.putExtra("path", user.getUserIcon());
				i_code.putExtra("name", user.getName());
				startActivity(i_code);
			}catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.personal_msg_btn3:
			// 修改性别
			Intent gender_intent = new Intent(ActivityPersonalMessage.this,
					ActivityGender.class);
			gender_intent.putExtra("gender", gender);
			gender_intent.putExtra("nickname", nickName);
			startActivityForResult(gender_intent, REQUEST_GENDER);
			break;
		case R.id.personal_msg_btn4:
			break;
		case R.id.personal_msg_btn5:
			// 修改密码
			startActivity(new Intent(ActivityPersonalMessage.this,
					ActivityUpdatePassword.class));
			break;
		case R.id.user_img:
			// 更换头像
			showImgChoiceWindow();
			break;
		}

	}

	private String filePath;
	// 自定义的弹出框类
	SelectPicPopupWindow menuWindow;
	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			// 照相
			case R.id.take_photo:
				// 如果你的程序要操作（增，删，改）SK卡目录，需要权限
				File dir = new File(Environment.getExternalStorageDirectory()
						+ "/schytd/camera");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// 文件夹路径
				String dirPath = dir.getAbsolutePath();
				// 文件路径
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss",
						Locale.CHINA);
				// 构建一个随机时间的文件名
				// /mnt/sdcard/rico/camera/test_20150414_114730.jpg
				filePath = dirPath + "/schytd_" + sdf.format(new Date())
						+ ".jpg";
				Uri outputFileUri = Uri.fromFile(new File(filePath));
				Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent2.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				startActivityForResult(intent2, 2);
				break;
			// 得到图片
			case R.id.get_image:
				if (Integer.valueOf(android.os.Build.VERSION.SDK) >= Build.VERSION_CODES.KITKAT) {
					startPickPhotoActivity();
				} else {
					Intent intent = new Intent();
					intent.setType("images/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("image/*");
					intent.putExtra("return-data", true);
					startActivityForResult(intent, 1);
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = MediaColumns.DATA;
		final String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPath(final Context context, final Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				final String selection = MediaColumns._ID + "=?";
				final String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	/**
	 * Access the gallery to pick up an image.
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void startPickPhotoActivity() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("image/*"); // Or 'image/ jpeg '
		startActivityForResult(intent, 1);
	}

	private void showImgChoiceWindow() {
		// 实例化SelectPicPopupWindow
		menuWindow = new SelectPicPopupWindow(ActivityPersonalMessage.this,
				itemsOnClick);
		// 显示窗口
		menuWindow.showAtLocation(
				ActivityPersonalMessage.this.findViewById(R.id.main),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}
}
