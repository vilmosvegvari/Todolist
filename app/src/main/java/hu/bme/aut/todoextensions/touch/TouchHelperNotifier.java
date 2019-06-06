package hu.bme.aut.todoextensions.touch;

public interface TouchHelperNotifier {

    public void onItemDismissed(int position);

    public void onItemDone(int position);

    public void onItemMoved(int fromPosition, int toPosition);
}
