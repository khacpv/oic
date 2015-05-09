package com.oic.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.oic.sdk.R;
import com.oic.sdk.data.ChildData;
import com.oic.sdk.view.util.OicResource;

public class FindBaby extends Activity {
	public static final int RESULT_LOAD_IMAGE = 1;
	
	ChildData child;

	ImageView imvAvatar;
	EditText edtName, edtAge, edtDescription;
	ToggleButton toogleGen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_findbaby);

		imvAvatar = (ImageView) findViewById(R.id.imv_avatar);
		edtName = (EditText) findViewById(R.id.edt_name);
		edtAge = (EditText) findViewById(R.id.edt_age);
		edtDescription = (EditText) findViewById(R.id.edt_description);
		toogleGen = (ToggleButton) findViewById(R.id.toggle_gen);
		
		child = new ChildData();
	}

	public void btnClicked(View v) {
		switch (v.getId()) {
		case R.id.imv_avatar:
			Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, RESULT_LOAD_IMAGE);
			break;
		case R.id.toggle_gen:
			
			break;
		case R.id.btn_send:
			String name = edtName.getText().toString();
			String age = edtAge.getText().toString();
			String des = edtDescription.getText().toString();
			if(validate()){
				child.name = name;
				child.age = age;
				child.description = des;
				send(child);
			}
			
			this.finish();
			break;
		case R.id.btn_cancel:
			this.finish();
			break;
		default:
			break;
		}
	}
	
	public boolean validate(){
		return true;
	}
	
	public void send(ChildData data){
		Toast.makeText(this, "Chúc bạn sớm tìm được người thân!", Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			Bitmap bmp = BitmapFactory.decodeFile(picturePath);
			imvAvatar.setImageBitmap(bmp);
			
			child.bitmapBase64 = OicResource.bmpToStr64(bmp);
		}
	}
}
