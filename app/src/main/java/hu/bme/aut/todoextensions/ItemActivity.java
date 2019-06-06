package hu.bme.aut.todoextensions;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


import hu.bme.aut.todoextensions.data.TodoItem;
import hu.bme.aut.todoextensions.fragments.newTodoItemDialogFragment;

public class ItemActivity extends AppCompatActivity implements newTodoItemDialogFragment.NewTodoItemDialogListener {

    TextView DescView;
    TextView NameView;
    TextView DlineView;
    TextView PlaceView;

    ToggleButton tb;
    ToggleButton repeatbutton;
    Button image;

    ImageView imgPicker;
    MenuItem edit;

    String PicturePath="";
    TodoItem todoItem;

    private void loadImageFromStorage(String path) {
        try {
            File f=new File(path, todoItem.id+".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imgPicker.setImageBitmap(b);
            imgPicker.setVisibility(View.VISIBLE);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }


    private void saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File mypath=new File(directory,todoItem.id+ ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);

            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PicturePath = directory.getAbsolutePath();
        todoItem.path=PicturePath;

        int pos = MainActivity.adapter.getPositionByID(todoItem);
        TodoItem item = MainActivity.adapter.items.get(pos);
        item.path = PicturePath;
        MainActivity.adapter.onItemChange(pos,item);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent intent = getIntent();

        String name = intent.getStringExtra("Name");
        String description = intent.getStringExtra("Description");
        String deadline = intent.getStringExtra("DeadLine");
        String place = intent.getStringExtra("Place");
        boolean isDone = intent.getBooleanExtra("Done", false);
        long id = intent.getLongExtra("ID", 0);
        boolean isRepeatable = intent.getBooleanExtra("Repeat",false);
        PicturePath = intent.getStringExtra("Path");

        todoItem = new TodoItem(name, description, place, deadline, isDone);
        todoItem.id = id;
        todoItem.isRepeatable=isRepeatable;

        DescView = findViewById(R.id.DescView);
        NameView = findViewById(R.id.NameView);
        DlineView = findViewById(R.id.DlineView);
        PlaceView = findViewById(R.id.PlaceView);
        tb = findViewById(R.id.ToggleButtonDone);
        repeatbutton = findViewById(R.id.RepeatButton);
        imgPicker = findViewById(R.id.imgPicker);
        image = findViewById(R.id.Image);

        if (!PicturePath.equals("")){
            image.setText(R.string.delete_image);
            loadImageFromStorage(PicturePath);
        }

        getItem();
        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = MainActivity.adapter.getPositionByID(todoItem);
                MainActivity.adapter.onItemDone(pos);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PicturePath.equals("")) {
                    image.setText(R.string.delete_image);
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

                }
                else
                {
                    image.setText(R.string.add_image);
                    PicturePath="";

                    imgPicker.setVisibility(View.INVISIBLE);

                    todoItem.path=PicturePath;
                    onTodoItemUpdated(todoItem);
                }
            }
        });



        repeatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = MainActivity.adapter.getPositionByID(todoItem);
                TodoItem item = MainActivity.adapter.items.get(pos);
                if (item.isRepeatable) item.isRepeatable=false;
                else item.isRepeatable=true;
                MainActivity.adapter.onItemChange(pos,item);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data!= null) {

            final Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveToInternalStorage(bitmap);

            loadImageFromStorage(PicturePath);

        }
    }

    public void getItem() {
        DescView.setText(todoItem.description);
        NameView.setText(todoItem.name);
        DlineView.setText(todoItem.deadline);
        PlaceView.setText(todoItem.place);

        if (todoItem.isDone) tb.setChecked(true);
        else tb.setChecked(false);

        if (todoItem.isRepeatable) repeatbutton.setChecked(true);
        else repeatbutton.setChecked(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        edit = menu.findItem(R.id.action_edit);
        menu.findItem(R.id.action_dragging).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_alarm).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                i.putExtra(AlarmClock.EXTRA_MESSAGE, todoItem.name);
                i.putExtra(AlarmClock.EXTRA_HOUR, 9);
                i.putExtra(AlarmClock.EXTRA_MINUTES, 30);
                startActivity(i);
                return true;
            }
        });

        edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                newTodoItemDialogFragment df = new newTodoItemDialogFragment();
                df.update = true;
                df.todoItem = todoItem;
                df.show(getSupportFragmentManager(), newTodoItemDialogFragment.TAG);
                return true;
            }
        });

        menu.findItem(R.id.action_drag).setVisible(false);

        menu.findItem(R.id.action_calendar).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                requestNeededPermission();

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_CALENDAR)
                        == PackageManager.PERMISSION_GRANTED) {
                    String result[] = todoItem.deadline.split("[.]");

                    int y = Integer.parseInt(result[0]);
                    int m = Integer.parseInt(result[1]);
                    int d = Integer.parseInt(result[2]);

                    Calendar time = Calendar.getInstance();
                    time.set(y, m - 1, d, 0, 0, 0);


                    Log.d("asd", "Time" + new Date(y, m, d).getTime());
                    ContentResolver cr = getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Events.ALL_DAY, true);
                    values.put(CalendarContract.Events.DTSTART, time.getTimeInMillis());
                    values.put(CalendarContract.Events.DTEND, time.getTimeInMillis() + 60000);
                    values.put(CalendarContract.Events.TITLE, todoItem.name);
                    values.put(CalendarContract.Events.EVENT_LOCATION, todoItem.place);
                    values.put(CalendarContract.Events.DESCRIPTION, todoItem.description);
                    values.put(CalendarContract.Events.CALENDAR_ID, 1);
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
                    if (todoItem.isRepeatable)
                        values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;COUNT=4");

                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);


                    Toast.makeText(getApplicationContext(), "Exported to calendar", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        return true;
    }

    public void requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    101);
        } else {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_edit) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTodoItemCreated(TodoItem newItem) {

    }

    @Override
    public void onTodoItemUpdated(TodoItem newItem) {
        int pos = MainActivity.adapter.getPositionByID(newItem);
        MainActivity.adapter.items.set(pos,newItem);
        MainActivity.adapter.onItemChange(pos,newItem);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
