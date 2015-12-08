package com.horical.appnote.TouchPaint;

import com.horical.appnote.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class TouchPaintActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener,
        ColorPicker.OnColorChangedListener {
    private DrawingView drawingView;
    private LinearLayout areaColorView;
    private View viewChangeColor;

    private LinearLayout drawerTool;

    private ArrayList<ImageButton> listImageButton;
    private ArrayList<Button> listButton;

    private AlertDialog.Builder alertBuilderChangeColor;
    private AlertDialog alertChangeColor;

    private ColorPicker colorPicker;

    private Handler timer;

    private Uri imageFromCamera;

    private static final int SELECT_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;

    public interface DrawerListener {
        void onDrawComplete(String imagePath);
    }

    private static DrawerListener Listener;

    public static void setListener(DrawerListener listener) {
        Listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchpaint);

        listImageButton = new ArrayList<ImageButton>();
        listButton = new ArrayList<Button>();

        drawingView = (DrawingView) findViewById(R.id.DrawArea);

        listImageButton.add((ImageButton) findViewById(R.id.buttonLoadImageSDCard));
        listImageButton.add((ImageButton) findViewById(R.id.buttonSaveImage));
        listImageButton.add((ImageButton) findViewById(R.id.buttonErase));
        listImageButton.add((ImageButton) findViewById(R.id.buttonCamera));
        listImageButton.add((ImageButton) findViewById(R.id.buttonPaintPalette));
        listImageButton.add((ImageButton) findViewById(R.id.buttonDelete));
        listImageButton.add((ImageButton) findViewById(R.id.buttonRedo));
        listImageButton.add((ImageButton) findViewById(R.id.buttonUndo));
        listImageButton.add((ImageButton) findViewById(R.id.buttonOpenDrawerTool));
        listImageButton.add((ImageButton) findViewById(R.id.buttonCloseDrawerTool));

        ((SeekBar) findViewById(R.id.seekbarChangeSize)).setOnSeekBarChangeListener(this);

        areaColorView = (LinearLayout) findViewById(R.id.ColorShow);

        timer = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.getData().getBoolean("OutOfTime")){
                    for (ImageButton imageButton: listImageButton){
                        imageButton.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        };

        drawerTool = (LinearLayout) findViewById(R.id.DrawerTool);

        viewChangeColor = LayoutInflater.from(this).inflate(R.layout.color_picker_layout, null);

        listButton.add((Button) viewChangeColor.findViewById(R.id.buttonColorBlack));
        listButton.add((Button) viewChangeColor.findViewById(R.id.buttonColorRed));
        listButton.add((Button) viewChangeColor.findViewById(R.id.buttonColorGreen));
        listButton.add((Button) viewChangeColor.findViewById(R.id.buttonColorBlue));
        listButton.add((Button) viewChangeColor.findViewById(R.id.buttonColorYellow));
        listButton.add((Button) viewChangeColor.findViewById(R.id.buttonColorGray));
        listButton.add((Button) viewChangeColor.findViewById(R.id.buttonColorPink));
        listButton.add((Button) viewChangeColor.findViewById(R.id.buttonColorLime));
        listButton.add((Button) viewChangeColor.findViewById(R.id.buttonColorBlueSky));
        listButton.add((Button) viewChangeColor.findViewById(R.id.buttonColorGold));

        if (colorPicker == null) {
            colorPicker = new ColorPicker(TouchPaintActivity.this, this, 0xffffff);
        }

        ((LinearLayout) viewChangeColor.findViewById(R.id.colorPickerLayout)).addView(colorPicker.getColorPickerView());

        alertBuilderChangeColor = new AlertDialog.Builder(this);
        alertBuilderChangeColor.setView(viewChangeColor);
        alertBuilderChangeColor.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertChangeColor = alertBuilderChangeColor.create();

        areaColorView.setBackgroundColor(drawingView.getCurrentPaintColor());

        for(ImageButton imageButton: listImageButton){
            imageButton.setOnClickListener(this);
        }

        for(Button button: listButton){
            button.setOnClickListener(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case SELECT_IMAGE:
                    if(requestCode == SELECT_IMAGE){
                        Uri imageUri = data.getData();
                        String imagePath = getPath(imageUri);
                        imagePath.getBytes();
                        drawingView.setBitmap(BitmapFactory.decodeFile(imagePath));
                    }
                    break;
                case TAKE_PHOTO:
                    getContentResolver().notifyChange(imageFromCamera, null);
                    ContentResolver contentResolver = getContentResolver();
                    Bitmap photoFromCamera;
                    try {
                        photoFromCamera = Bitmap.createBitmap(MediaStore.Images.Media.getBitmap(contentResolver, imageFromCamera));
                        drawingView.setBitmap(photoFromCamera);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void time(){
        Thread threadTimer = new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1500);
                Message handleMessage = timer.obtainMessage();
                handleMessage.getData().putBoolean("OutOfTime", true);
                timer.sendMessage(handleMessage);
            }
        });
        threadTimer.start();
    }

    private void intentTakePhoto(){
        Intent intentTakePhoto = new Intent("android.media.action.IMAGE_CAPTURE");
        File intentPhoto = new File(Environment.getExternalStorageDirectory(), "Photo.jpg");
        intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(intentPhoto));
        imageFromCamera = Uri.fromFile(intentPhoto);
        startActivityForResult(intentTakePhoto, TAKE_PHOTO);
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        drawingView.ActiveEraser(false);
        for (ImageButton imageButton: listImageButton){
            imageButton.setBackgroundColor(Color.TRANSPARENT);
        }
        switch(seekBar.getId()){
            case R.id.seekbarChangeSize:
                drawingView.setCurrentWidth(progress*1.f);
                break;
        }

        areaColorView.setBackgroundColor(drawingView.getCurrentPaintColor());
        ((LinearLayout) viewChangeColor.findViewById(R.id.ColorShow)).setBackgroundColor(drawingView.getCurrentPaintColor());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.seekbarChangeSize){
            this.showPaintSize();
        }
    }

    private void showColorCode(){
        Toast.makeText(getApplicationContext(), "Current paint's color: "
                +drawingView.getCurrentPaintColorUtil().toString(), Toast.LENGTH_SHORT).show();
    }
    private void showPaintSize(){
        Toast.makeText(getApplicationContext(), "Current paint's size: "+drawingView.getCurrentWidth(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        for (ImageButton imageButton: listImageButton){
            if (imageButton.getId() != R.id.buttonOpenDrawerTool) {
                imageButton.setBackgroundColor((imageButton.getId()==v.getId())?Color.GRAY:Color.TRANSPARENT);
            }
        }
        switch (v.getId()){
            case R.id.buttonOpenDrawerTool:
                drawerOpen();
                break;
            case R.id.buttonCloseDrawerTool:
                drawerClose();
                break;
            case R.id.buttonLoadImageSDCard:
                Intent intentLoadImageSDCard = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentLoadImageSDCard.setType("image/*");
                startActivityForResult(Intent.createChooser(intentLoadImageSDCard, "Select image"), SELECT_IMAGE);
                break;
            case R.id.buttonCamera:
                intentTakePhoto();
                break;
            case R.id.buttonSaveImage:
                View promptView = LayoutInflater.from(this).inflate(R.layout.prompt_dialog, null);

                final AlertDialog.Builder alertBuilderSaveImage = new AlertDialog.Builder(this);
                alertBuilderSaveImage.setView(promptView);

                final EditText dialogEdittext = (EditText) promptView.findViewById(R.id.DialogEdittext);

                alertBuilderSaveImage.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishDrawer(dialogEdittext.getText().toString());
                    }
                });
                alertBuilderSaveImage.create().show();
                break;
            case R.id.buttonErase:
                drawingView.ActiveEraser(true);
                break;
            case R.id.buttonPaintPalette:
                alertChangeColor.show();
                break;
            case R.id.buttonDelete:
                drawingView.EmptyCanvas();
                break;
            case R.id.buttonUndo:
                drawingView.Undo();
                break;
            case R.id.buttonRedo:
                drawingView.Redo();
                break;

            case R.id.buttonColorBlack:
            case R.id.buttonColorRed:
            case R.id.buttonColorGreen:
            case R.id.buttonColorBlue:
            case R.id.buttonColorYellow:
            case R.id.buttonColorGray:
            case R.id.buttonColorPink:
            case R.id.buttonColorLime:
            case R.id.buttonColorBlueSky:
            case R.id.buttonColorGold:
                drawingView.changeColor(((Button) viewChangeColor.findViewById(v.getId())).getText().toString());
                areaColorView.setBackgroundColor(drawingView.getCurrentPaintColor());
                ((LinearLayout) viewChangeColor.findViewById(R.id.ColorShow)).setBackgroundColor(drawingView.getCurrentPaintColor());

                this.showColorCode();
                drawingView.ActiveEraser(false);
                break;

        }

        time();
    }

    private void finishDrawer(String data) {
        String path = drawingView.saveImageIntoSDCard(TouchPaintActivity.this, data);
        Listener.onDrawComplete(path);
        finish();
    }

    public void drawerOpen(){
        drawerTool.setVisibility(View.VISIBLE);
        ((ImageButton) findViewById(R.id.buttonOpenDrawerTool)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.layoutCloseDrawerTool)).setVisibility(View.VISIBLE);
    }

    public void drawerClose(){
        drawerTool.setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.buttonOpenDrawerTool)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.layoutCloseDrawerTool)).setVisibility(View.GONE);
    }

    @Override
    public void colorChanged(int color) {
        drawingView.changeColor(color);
        areaColorView.setBackgroundColor(drawingView.getCurrentPaintColor());
        ((LinearLayout) viewChangeColor.findViewById(R.id.ColorShow)).setBackgroundColor(drawingView.getCurrentPaintColor());

        this.showColorCode();
        drawingView.ActiveEraser(false);
    }
}
