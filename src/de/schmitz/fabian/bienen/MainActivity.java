package de.schmitz.fabian.bienen;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int SKALIERTE_GROESSE = 320;
	private static final String URI_SCHLUESSEL = "dieUri";
	private Button zaehlButton;
	private Button kameraButton,btnScale,btnConfirm;
	private TextView bienenzahl;
	private ImageView imageView,ivP1 ,ivP2 ,ivP3 ,ivP4;
	private float diffX,diffY;
	private boolean rechneVerschiebung;
	private int grenzwert = 115;
	private int bienenProProzentFlaeche = 10;

	private static final int IMAGE_CAPTURE = 1000;

	private static final String TAG = "BienenZaehlApp";

	private OnClickListener zaehlClickListener = new OnClickListener()
	{

		public void onClick(View v) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof BitmapDrawable) {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				BienenBitmap bienenBitmap = erzeugeBienenBitmap(bitmap);

				imageView.setImageBitmap(bienenBitmap.getBlackAndWhite());

				bienenzahl.setText("Anzahl Bienen: "
						+ bienenBitmap.getBienenAnzahl());

			}
		}

		public BienenBitmap erzeugeBienenBitmap(Bitmap original) {
			int width, height;
			height = original.getHeight();
			width = original.getWidth();
			Bitmap bwbitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			int helligkeitRot;
			int helligkeitGruen;
			int helligkeitBlau;
			int helligkeitTransparenz = 0xFF000000;
			int helligkeitGesamt;
			int countBlackPixel = 0;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int colour = original.getPixel(x, y);
					helligkeitRot = colour & 0x00ff0000;
					helligkeitBlau = helligkeitRot >> 16;

					if (helligkeitBlau < grenzwert) {
						helligkeitBlau = 0;
						countBlackPixel++;

					} else {
						helligkeitBlau = 255;
					}

					helligkeitRot = helligkeitBlau << 16;
					helligkeitGruen = helligkeitBlau << 8;
					helligkeitGesamt = helligkeitRot | helligkeitGruen
							| helligkeitBlau | helligkeitTransparenz;
					bwbitmap.setPixel(x, y, helligkeitGesamt);
				}
			}

			return new BienenBitmap(bwbitmap, berechneBienenZahl(
					countBlackPixel, width * height));
		}
	};
	//Confirm after Scale
	private OnClickListener btnConfirmClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			btnScale.setVisibility(View.VISIBLE);
			btnConfirm.setVisibility(View.INVISIBLE);
			ivP1.setVisibility(View.INVISIBLE);
			ivP2.setVisibility(View.INVISIBLE);
			ivP3.setVisibility(View.INVISIBLE);
			ivP4.setVisibility(View.INVISIBLE);

		}
	};
	//Scale Picture
	private OnClickListener btnScaleClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{

				btnScale.setVisibility(View.INVISIBLE);
				btnConfirm.setVisibility(View.VISIBLE);
				ivP1.setVisibility(View.VISIBLE);
				ivP2.setVisibility(View.VISIBLE);
				ivP3.setVisibility(View.VISIBLE);
				ivP4.setVisibility(View.VISIBLE);
			if(rechneVerschiebung)
			{
				diffX = ivP1.getWidth()/2;
				diffY = (ivP4.getY()-ivP1.getY());
				rechneVerschiebung = false;
			}
		}
	};





	private OnClickListener kameraClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			startCamera();
		}
	};

	//Punkt1 wird verschoben
	private OnTouchListener ivP1TouchListener = new OnTouchListener()
	{
		public boolean onTouch(View v , MotionEvent ev)
		{

			if (ev.getAction()==MotionEvent.ACTION_MOVE)
			{
					ivP1.setX(ev.getRawX()-diffX);
					ivP1.setY(ev.getRawY()-diffY);
			}
			return true;
		}
	};
	//Punkt2 wird verschoben
	private OnTouchListener ivP2TouchListener = new OnTouchListener()
	{
		public boolean onTouch(View v , MotionEvent ev)
		{

			if (ev.getAction()==MotionEvent.ACTION_MOVE)
			{
				ivP2.setX(ev.getRawX()-diffX);
				ivP2.setY(ev.getRawY()-diffY);
			}
			return true;
		}
	};
	//Punkt3 wird verschoben
	private OnTouchListener ivP3TouchListener = new OnTouchListener()
	{
		public boolean onTouch(View v , MotionEvent ev)
		{

			if (ev.getAction()==MotionEvent.ACTION_MOVE)
			{
				ivP3.setX(ev.getRawX()-diffX);
				ivP3.setY(ev.getRawY()-diffY);
			}
			return true;
		}
	};
	//Punkt4 wird verschoben
	private OnTouchListener ivP4TouchListener = new OnTouchListener()
	{
		public boolean onTouch(View v , MotionEvent ev)
		{

			if (ev.getAction()==MotionEvent.ACTION_MOVE)
			{
				ivP4.setX(ev.getRawX()-diffX);
				ivP4.setY(ev.getRawY()-diffY);
			}
			return true;
		}
	};

	private Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.imageView);
		ivP1 = (ImageView) findViewById(R.id.ivP1);
		ivP2 = (ImageView) findViewById(R.id.ivP2);
		ivP3 = (ImageView) findViewById(R.id.ivP3);
		ivP4 = (ImageView) findViewById(R.id.ivP4);
		zaehlButton = (Button) findViewById(R.id.button1);
		zaehlButton.setOnClickListener(zaehlClickListener);
		btnScale = (Button) findViewById(R.id.btnScale);
		btnScale.setOnClickListener(btnScaleClickListener);
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(btnConfirmClickListener);
		bienenzahl = (TextView) findViewById(R.id.Bienenanzahl);
		kameraButton = (Button) findViewById(R.id.kameraButton);
		kameraButton.setOnClickListener(kameraClickListener);
		ivP1.setOnTouchListener(ivP1TouchListener);
		ivP2.setOnTouchListener(ivP2TouchListener);
		ivP3.setOnTouchListener(ivP3TouchListener);
		ivP4.setOnTouchListener(ivP4TouchListener);
		rechneVerschiebung = true;

	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "Sichere URI Wert: " + imageUri);
		outState.putParcelable(URI_SCHLUESSEL, imageUri);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		imageUri = (Uri) savedInstanceState.get(URI_SCHLUESSEL);
		Log.d(TAG, "Restaurierter URI Wert: " + imageUri);
	}

	public int berechneBienenZahl(int schwarz, int gesamtPixel) {
		float bruchteilSchwarz = (float) schwarz / (float) gesamtPixel;
		float prozentSchwarz = 100 * bruchteilSchwarz;
		return (int) prozentSchwarz * bienenProProzentFlaeche;
	}

	private void startCamera() {
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "Bienenknipser");
		values.put(MediaStore.Images.Media.DESCRIPTION, "Descriptionxxxxxx");
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "Keine SD Karte gesteckt!", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		imageUri = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, IMAGE_CAPTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IMAGE_CAPTURE) {
			if (resultCode == RESULT_OK) {
				try {
					Bitmap bitmapVonKameraBild = MediaStore.Images.Media
							.getBitmap(getContentResolver(), imageUri);

					// Gr��e des aufgenommenen Bildes
					float w1 = bitmapVonKameraBild.getWidth();
					float h1 = bitmapVonKameraBild.getHeight();
					// auf eine H�he von 300 Pixel skalieren
					//int h2 = SKALIERTE_GROESSE;
					//int w2 = (int) (w1 / h1 * (float) h2);
					int h2 = 200;
					int w2 = 320;
					Bitmap skaliert = Bitmap.createScaledBitmap(
							bitmapVonKameraBild, w2, h2, false);

					imageView.setImageBitmap(skaliert);
				} catch (Exception e) {
					Log.e(TAG, "setBitmap()", e);
				}
			} else {
				int rowsDeleted = getContentResolver().delete(imageUri, null,
						null);
				Log.d(TAG, rowsDeleted + " rows deleted");
			}
		}
	}

}
