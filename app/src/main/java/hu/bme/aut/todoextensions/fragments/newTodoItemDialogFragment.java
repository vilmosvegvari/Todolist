package hu.bme.aut.todoextensions.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import hu.bme.aut.todoextensions.ItemActivity;
import hu.bme.aut.todoextensions.MainActivity;
import hu.bme.aut.todoextensions.R;
import hu.bme.aut.todoextensions.data.TodoItem;

public class newTodoItemDialogFragment extends DialogFragment {

    public static final String TAG = "NewTodoItemDialogFragment";

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText placeEditText;
    private DatePicker deadLineDatePicker;
    private CheckBox alreadyDone;

    public boolean update = false;

    public TodoItem todoItem;

    public interface NewTodoItemDialogListener {
        void onTodoItemCreated(TodoItem newItem);
        void onTodoItemUpdated(TodoItem newItem);
    }

    private View onEdit(){
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_todo_item, null);
        nameEditText = contentView.findViewById(R.id.TodoItemNameEditText);
        descriptionEditText = contentView.findViewById(R.id.TodoItemDescriptionEditText);
        deadLineDatePicker = contentView.findViewById(R.id.TodoItemDatePicker);
        alreadyDone = contentView.findViewById(R.id.TodoItemisDoneCheckBox);
        placeEditText = contentView.findViewById(R.id.TodoItemPlaceEditText);


        nameEditText.setText(todoItem.name);
        descriptionEditText.setText(todoItem.description);
        placeEditText.setText(todoItem.place);
        alreadyDone.setChecked(todoItem.isDone);

        String result[] = todoItem.deadline.split("[.]");

        int y = Integer.parseInt(result[0]);
        int m = Integer.parseInt(result[1]);
        int d = Integer.parseInt(result[2]);

        deadLineDatePicker.updateDate(y,m-1,d);

        return contentView;
    }
    private NewTodoItemDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof NewTodoItemDialogListener) {
            listener = (NewTodoItemDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the NewTodoItemDialogListener interface!");
        }
    }

    private View getContentView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_todo_item, null);
        nameEditText = contentView.findViewById(R.id.TodoItemNameEditText);
        descriptionEditText = contentView.findViewById(R.id.TodoItemDescriptionEditText);
        deadLineDatePicker = contentView.findViewById(R.id.TodoItemDatePicker);
        alreadyDone = contentView.findViewById(R.id.TodoItemisDoneCheckBox);
        placeEditText = contentView.findViewById(R.id.TodoItemPlaceEditText);

        return contentView;
    }

    private boolean isValid() {
        return nameEditText.getText().length() > 0;
    }

    private TodoItem getTodoItem() {
        if (!update) todoItem = new TodoItem();
        todoItem.name = nameEditText.getText().toString();
        todoItem.description = descriptionEditText.getText().toString();
        todoItem.place = placeEditText.getText().toString();
        todoItem.deadline = (deadLineDatePicker.getYear() + "." + ((deadLineDatePicker.getMonth()/10)== 0 ? "0"+(deadLineDatePicker.getMonth()+1) : (deadLineDatePicker.getMonth()+1)) + "." + ((deadLineDatePicker.getDayOfMonth()/10)== 0 ? "0"+deadLineDatePicker.getDayOfMonth() : deadLineDatePicker.getDayOfMonth()) + ".");
        todoItem.isDone = alreadyDone.isChecked();
        todoItem.isRepeatable=false;
        todoItem.path="";
        return todoItem;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(!update) {
            return new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.new_todo_item)
                    .setView(getContentView())
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (isValid() && !update) {
                                listener.onTodoItemCreated(getTodoItem());
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
        }
        else {
            return new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.edit_todo_item)
                    .setView(onEdit())
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (isValid() && !update) {
                                listener.onTodoItemCreated(getTodoItem());
                            }
                            if (isValid() && update) {
                                listener.onTodoItemUpdated(getTodoItem());
                                ((ItemActivity)getActivity()).getItem();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
        }
    }
}
