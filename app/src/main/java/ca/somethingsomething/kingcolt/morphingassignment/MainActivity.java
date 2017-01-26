package ca.somethingsomething.kingcolt.morphingassignment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity
{
    private final static int PHOTO_CHOICE1 = 1;
    private final static int PHOTO_CHOICE2 = 2;

    private MorphImage mrphImg1, mrphImg2;
    private int startPointX, startPointY;
    private boolean drawEdit; //draw mode if true; edit mode if false

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mrphImg1 = new MorphImage((ImageView)findViewById(R.id.imgBtn1), 1);
        mrphImg2 = new MorphImage((ImageView)findViewById(R.id.imgBtn2), 2);
        drawEdit = true;

        mrphImg1.getView().setOnTouchListener(new Button.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent mEvent)
            {
                touchedImage(mEvent, mrphImg1, mrphImg2);
                return true;
            }
        });

        mrphImg2.getView().setOnTouchListener(new Button.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent mEvent)
            {
                touchedImage(mEvent, mrphImg2, mrphImg1);
                return true;
            }
        });
    }

    private void touchedImage(MotionEvent mEvent, MorphImage active, MorphImage other)
    {
        if(active.getScaled() != null && other.getScaled() != null)
        {
            if(drawEdit)
            {
                switch (mEvent.getAction()) //Drawing Lines
                {
                    case MotionEvent.ACTION_DOWN:
                        startPointX = (int) mEvent.getX();
                        startPointY = (int) mEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        active.drawLine(startPointX, startPointY,
                                (int) mEvent.getX(), (int) mEvent.getY());
                        other.drawLine(startPointX, startPointY,
                                (int) mEvent.getX(), (int) mEvent.getY());
                        break;
                }
            }
            else
            {
                switch (mEvent.getAction()) //Editing Lines
                {
                    case MotionEvent.ACTION_DOWN:
                        int index = active.selectLine((int)mEvent.getX(), (int)mEvent.getY());
                        other.selectLine(index, active.whichPoint());
                        break;
                    case MotionEvent.ACTION_UP:
                        if(active.getSelectedLine() != null)
                            active.moveLine((int)mEvent.getX(), (int)mEvent.getY());
                        break;
                }
            }
        }
        else
        {
            switch (mEvent.getAction())
            {
                case MotionEvent.ACTION_UP:
                    openGallery(active.getId());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.topmenu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.morph:
                unavailableAlert();
                return true;
            case R.id.rotate1:
                mrphImg1.rotateImg();
                return true;
            case R.id.rotate2:
                mrphImg2.rotateImg();
                return true;
            case R.id.draworedit:
                if(drawEdit) {
                    item.setTitle("Edit Line Mode");
                    drawEdit = false;
                } else {
                    item.setTitle("Draw Line Mode");
                    drawEdit = true;
                }
                return true;
            case R.id.remove:
                mrphImg1.deleteImage();
                mrphImg2.deleteImage();
                return true;
            case R.id.settings:
                unavailableAlert();
                return true;
            case R.id.help:
                unavailableAlert();
                return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Uri pickedImage;
        InputStream inputStream;
        Bitmap bitmap;
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK || data == null)
            return;
        try
        {
            pickedImage = data.getData();
            inputStream = getContentResolver().openInputStream(pickedImage);
            bitmap = BitmapFactory.decodeStream(inputStream);

            switch(requestCode)
            {
                case PHOTO_CHOICE1:
                    mrphImg1.chooseImg(bitmap);
                    break;
                case PHOTO_CHOICE2:
                    mrphImg2.chooseImg(bitmap);
                    break;
            }
        }
        catch (Exception e)
        {
            System.err.println("The image was not found.");
        }
    }

    private void openGallery(int whichView)
    {
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, whichView);
    }

    private void unavailableAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sorry this feature has not yet been implemented.")
                .setTitle("Unavailable Feature");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}