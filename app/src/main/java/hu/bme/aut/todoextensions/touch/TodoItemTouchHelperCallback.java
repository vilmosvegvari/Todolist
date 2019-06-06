package hu.bme.aut.todoextensions.touch;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;


public class TodoItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private TouchHelperNotifier touchHelperNotifier;

    public TodoItemTouchHelperCallback(TouchHelperNotifier touchHelperNotifier) {
        this.touchHelperNotifier = touchHelperNotifier;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        touchHelperNotifier.onItemMoved(
                viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.END )
            touchHelperNotifier.onItemDismissed(viewHolder.getAdapterPosition());
        else if (direction == ItemTouchHelper.START)
            touchHelperNotifier.onItemDone(viewHolder.getAdapterPosition());
        else Log.d("kaki", "Nem jók a Swipe irányok");
    }
}
