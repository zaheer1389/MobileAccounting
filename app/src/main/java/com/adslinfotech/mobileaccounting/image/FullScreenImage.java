package com.adslinfotech.mobileaccounting.image;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;
import com.itextpdf.text.pdf.BaseField;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FullScreenImage extends SimpleAccountingActivity {
  private static final int DRAG = 1;
  private static final int NONE = 0;
  private static final int ZOOM = 2;
  private Bitmap mBitmapImage = null;
  private File mFile;
  private ImageView mImage;
  private Matrix matrix = new Matrix();
  private PointF midPoint = new PointF();
  private int mode = 0;
  private float oldDist = BaseField.BORDER_WIDTH_THIN;
  private Matrix savedMatrix = new Matrix();
  private PointF startPoint = new PointF();

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.full_screen_image);
    this.mImage = (ImageView) findViewById(R.id.imageViewfull);
    setImage(getIntent().getByteArrayExtra("image"));
    this.mImage.setOnTouchListener(new OnTouchListener() {
      @SuppressLint({"ClickableViewAccessibility"})
      public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        System.out.println("matrix=" + FullScreenImage.this.savedMatrix.toString());
        switch (event.getAction() & 255) {
          case 0:
            FullScreenImage.this.savedMatrix.set(FullScreenImage.this.matrix);
            FullScreenImage.this.startPoint.set(event.getX(), event.getY());
            FullScreenImage.this.mode = 1;
            break;
          case 1:
          case 6:
            FullScreenImage.this.mode = 0;
            break;
          case 2:
            if (FullScreenImage.this.mode != 1) {
              if (FullScreenImage.this.mode == 2) {
                float newDist = spacing(event);
                if (newDist > 10.0f) {
                  FullScreenImage.this.matrix.set(FullScreenImage.this.savedMatrix);
                  float scale = newDist / FullScreenImage.this.oldDist;
                  FullScreenImage.this.matrix.postScale(scale, scale, FullScreenImage.this.midPoint.x, FullScreenImage.this.midPoint.y);
                  break;
                }
              }
            }
            FullScreenImage.this.matrix.set(FullScreenImage.this.savedMatrix);
            FullScreenImage.this.matrix.postTranslate(event.getX() - FullScreenImage.this.startPoint.x, event.getY() - FullScreenImage.this.startPoint.y);
            break;
          case 5:
            FullScreenImage.this.oldDist = spacing(event);
            if (FullScreenImage.this.oldDist > 10.0f) {
              FullScreenImage.this.savedMatrix.set(FullScreenImage.this.matrix);
              midPoint(FullScreenImage.this.midPoint, event);
              FullScreenImage.this.mode = 2;
              break;
            }
            break;
        }
        view.setImageMatrix(FullScreenImage.this.matrix);
        return true;
      }

      private float spacing(MotionEvent event) {
        float y = event.getY(0) - event.getY(1);
        return event.getX(0) - event.getX(1);
      }

      private void midPoint(PointF point, MotionEvent event) {
        point.set((event.getX(0) + event.getX(1)) / BaseField.BORDER_WIDTH_MEDIUM, (event.getY(0) + event.getY(1)) / BaseField.BORDER_WIDTH_MEDIUM);
      }
    });
  }

  private void setImage(byte[] byteImage) {
    if (byteImage == null) {
      this.mImage.setImageResource(R.drawable.profile_icon);
    } else if (byteImage != null) {
      try {
        if (byteImage.length != 0) {
          this.mBitmapImage = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);
          this.mImage.setImageBitmap(this.mBitmapImage);
        }
      } catch (Exception e) {
      }
    }
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_full_image, menu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_share:
        share();
        return false;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void share() {
    if (this.mBitmapImage != null) {
      Intent i = new Intent("android.intent.action.SEND");
      i.setType("image/*");
      i.putExtra("android.intent.extra.STREAM", getLocalBitmapUri(this.mBitmapImage));
      startActivityForResult(Intent.createChooser(i, "Share Image"), 1);
      return;
    }
    Toast.makeText(this, "Not a valid account image.", Toast.LENGTH_SHORT).show();
  }

  public Uri getLocalBitmapUri(Bitmap bmp) {
    Uri bmpUri = null;
    try {
      this.mFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
      FileOutputStream out = new FileOutputStream(this.mFile);
      bmp.compress(CompressFormat.PNG, 90, out);
      out.close();
      bmpUri = Uri.fromFile(this.mFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bmpUri;
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1) {
      try {
        this.mFile.delete();
      } catch (Exception e) {
      }
    }
  }
}
