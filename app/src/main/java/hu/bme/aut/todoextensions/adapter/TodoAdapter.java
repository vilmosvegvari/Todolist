package hu.bme.aut.todoextensions.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hu.bme.aut.todoextensions.MainActivity;
import hu.bme.aut.todoextensions.R;
import hu.bme.aut.todoextensions.data.TodoItem;
import hu.bme.aut.todoextensions.touch.TouchHelperNotifier;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> implements TouchHelperNotifier{

    public final List<TodoItem> items;

    private TodoItemClickListener listener;

    private View iv;
    public static boolean drag = false;

    public TodoAdapter(TodoItemClickListener listener) {

        this.listener = listener;
        items = new ArrayList<>();
    }

    public int getPositionByID(TodoItem item) {
        for (TodoItem i: items){
            if (i.id == item.id) return items.indexOf(i);
        }
        return -1;
    }


    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_todo_list, parent, false);
        iv = itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!drag){
                    int pos = listener.getRecyclerPosition(v);
                    listener.onItemClicked(items.get(pos));
                }
            }
        });
        return new TodoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TodoViewHolder holder, int position) {
        final TodoItem item = items.get(position);
        holder.nameTextView.setText(item.name);
        holder.deadlineTextView.setText(item.deadline);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemDismissed(int position) {
        TodoItem item = items.get(position);
        listener.onItemRemoved(item);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemDone(int position) {
        TodoItem item = items.get(position);
        if (!item.isDone) item.isDone = true;
        else item.isDone = false;
        listener.onItemChanged(item);
        notifyItemChanged(position);
    }

    public void onItemChange(int position,TodoItem todoItem) {
        items.set(position,todoItem);
        listener.onItemChanged(items.get(position));
        notifyItemChanged(position);
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        if(drag) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(items, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(items, i, i - 1);
                }
            }
            TodoItem oldItem = items.get(toPosition);
            TodoItem newItem = items.get(fromPosition);

            Long temp = oldItem.id;
            oldItem.id = newItem.id;
            newItem.id = temp;

            listener.onItemChanged(oldItem);
            listener.onItemChanged(newItem);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    public interface TodoItemClickListener{
        void onItemChanged(TodoItem item);
        void onItemRemoved(TodoItem item);
        void onItemClicked(TodoItem item);
        int getRecyclerPosition(View v);
    }

    public void addItem(TodoItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void deleteItem(TodoItem item) {
        int index = items.indexOf(item);
        items.remove(item);
        notifyItemRemoved(index);
    }

    public void deleteAll(){
        for (int i = items.size()-1 ; 0 <= i ; i--){
            listener.onItemRemoved(items.get(i));
            notifyItemRemoved(i);
        }
    }

    public void update(List<TodoItem> shoppingItems) {
        items.clear();
        items.addAll(shoppingItems);
        notifyDataSetChanged();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView deadlineTextView;

        TodoItem item;

        TodoViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.TodoItemNameTextView);
            deadlineTextView= itemView.findViewById(R.id.TodoItemDeadlineTextView);
        }
    }
}
