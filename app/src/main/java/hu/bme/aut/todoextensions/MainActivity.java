package hu.bme.aut.todoextensions;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.util.List;

import hu.bme.aut.todoextensions.adapter.TodoAdapter;
import hu.bme.aut.todoextensions.data.TodoItem;
import hu.bme.aut.todoextensions.data.TodoListDatabase;
import hu.bme.aut.todoextensions.fragments.newTodoItemDialogFragment;
import hu.bme.aut.todoextensions.touch.TodoItemTouchHelperCallback;

public class MainActivity extends AppCompatActivity implements TodoAdapter.TodoItemClickListener, newTodoItemDialogFragment.NewTodoItemDialogListener {

    public RecyclerView recyclerView;
    public static TodoAdapter adapter;

    public static ItemTouchHelper touchHelper;
    public static TodoListDatabase database;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new newTodoItemDialogFragment().show(getSupportFragmentManager(), newTodoItemDialogFragment.TAG);
            }
        });

        database = Room.databaseBuilder(
                getApplicationContext(),
                TodoListDatabase.class,
                "todo-list"
        ).build();

        initRecyclerView();

        ItemTouchHelper.Callback callback =
                new TodoItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.MainRecyclerView);
        adapter = new TodoAdapter(this);
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<TodoItem>>() {

            @Override
            protected List<TodoItem> doInBackground(Void... voids) {
                return database.todoItemDao().getAll();
            }

            @Override
            protected void onPostExecute(List<TodoItem> todoItems) {
                adapter.update(todoItems);
            }
        }.execute();
    }

    @Override
    public void onItemChanged(final TodoItem item) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                database.todoItemDao().update(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                Log.d("MainActivity", "TodoItem update was successful");
            }
        }.execute();
    }

    public void onItemClicked(final TodoItem item){

        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("Name",item.name);
        intent.putExtra("Description",item.description);
        intent.putExtra("DeadLine",item.deadline);
        intent.putExtra("Done",item.isDone);
        intent.putExtra( "Place", item.place);
        intent.putExtra("ID", item.id);
        intent.putExtra("Repeat", item.isRepeatable);
        intent.putExtra("Path", item.path);
        startActivity(intent);
    }

    @Override
    public void onTodoItemCreated(final TodoItem newItem) {
        new AsyncTask<Void, Void, TodoItem>() {

            @Override
            protected TodoItem doInBackground(Void... voids) {
                newItem.id = database.todoItemDao().insert(newItem);
                return newItem;
            }

            @Override
            protected void onPostExecute(TodoItem todoItem) {
                adapter.addItem(todoItem);
            }
        }.execute();
    }

    @Override
    public void onTodoItemUpdated(TodoItem newItem) {

    }

    public void onItemRemoved(final TodoItem item) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                database.todoItemDao().deleteItem(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                adapter.deleteItem(item);
            }
        }.execute();
    }

    @Override
    public int getRecyclerPosition(View v) {
        return recyclerView.getChildAdapterPosition(v);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.action_calendar).setVisible(false);
        menu.findItem(R.id.action_dragging).setVisible(false);
        menu.findItem(R.id.action_alarm).setVisible(false);
        menu.findItem(R.id.action_dragging).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (TodoAdapter.drag) {
                    TodoAdapter.drag = false;
                    menu.findItem(R.id.action_dragging).setVisible(false);
                }
                else {
                    TodoAdapter.drag=true;;
                    menu.findItem(R.id.action_dragging).setVisible(true);
                }
                return true;
            }
        });
        menu.findItem(R.id.action_drag).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (TodoAdapter.drag) {
                    TodoAdapter.drag = false;
                    menu.findItem(R.id.action_dragging).setVisible(false);
                }
                else {
                    TodoAdapter.drag=true;;
                    menu.findItem(R.id.action_dragging).setVisible(true);
                }
                return true;
            }
        });
        menu.findItem(R.id.action_delete).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog deletedialog = DeleteAllDialog();
                deletedialog.show();
                return true;
            }
        });

        return true;
    }

    public AlertDialog DeleteAllDialog(){
        AlertDialog deletedialog =new AlertDialog.Builder(this)
                .setMessage("Are you sure?")
                .setPositiveButton("Delete all", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.deleteAll();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", null)
                .create();
        return deletedialog;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
