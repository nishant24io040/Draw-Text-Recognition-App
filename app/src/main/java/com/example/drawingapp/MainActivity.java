package com.example.drawingapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


public class MainActivity extends AppCompatActivity {
    AlertDialog.Builder alertDialog;
    FloatingActionButton undo,colorpicker,clear,redo;
    Button button;
    Mycanvas stoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        stoke = findViewById(R.id.drawableView);
        button = findViewById(R.id.button);
        undo = findViewById(R.id.undo);
        colorpicker = findViewById(R.id.colorpicker);
        clear = findViewById(R.id.clear);
        redo = findViewById(R.id.redo);
        alertDialog = new AlertDialog.Builder(MainActivity.this);


       colorpicker.setOnClickListener(view -> ColorPickerDialogBuilder
               .with(MainActivity.this)
               .setTitle("Choose color")
               .initialColor(getResources().getColor(R.color.white))
               .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
               .density(30)
               .showColorPreview(true)
               .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                   stoke.setColor(selectedColor);
               })
               .setNegativeButton("cancel", (dialog, which) -> { })
               .build()
               .show());
        undo.setOnClickListener(view ->{
            stoke.undo();
        } );
        clear.setOnClickListener(view ->{
            stoke.clearAll();
        });
        redo.setOnClickListener(view -> stoke.redo());

        button.setOnClickListener(view -> {
            View view1 = getLayoutInflater().inflate(R.layout.dialog,null);
            final EditText copableText = view1.findViewById(R.id.editText);
            TextView btnCancel = view1.findViewById(R.id.close);
            TextView btnCopy =view1.findViewById(R.id.copy);
            alertDialog.setView(view1);

            Bitmap bitmap = stoke.NeedBitmap();
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            if(!textRecognizer.isOperational()){
                Toast.makeText(this, "Could not get text", Toast.LENGTH_SHORT).show();
            }
            else {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                StringBuilder sb = new StringBuilder();
                for (int i=0;i<items.size();i++){
                    TextBlock myitem = items.valueAt(i);
                    sb.append(myitem.getValue());
                }
                copableText.setText(sb.toString());
            }

            final AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(true);
            btnCancel.setOnClickListener(view2 -> {
                alert.dismiss();
            });
            btnCopy.setOnClickListener(view3 -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("dataTobeCopy", copableText.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
            });
            alert.show();
        });





    }
    
    }


