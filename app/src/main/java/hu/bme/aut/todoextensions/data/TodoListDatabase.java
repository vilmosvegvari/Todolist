package hu.bme.aut.todoextensions.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(
        entities = {TodoItem.class},
        version = 1
)

public abstract class TodoListDatabase extends RoomDatabase{
    public abstract TodoItemDao todoItemDao();
}
