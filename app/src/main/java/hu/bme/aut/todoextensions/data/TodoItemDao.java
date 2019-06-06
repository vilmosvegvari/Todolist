package hu.bme.aut.todoextensions.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TodoItemDao {

    @Query("SELECT * FROM todoitem")
    List<TodoItem> getAll();

    @Insert
    long insert(TodoItem shoppingItems);

    @Update
    void update(TodoItem shoppingItem);

    @Delete
    void deleteItem(TodoItem shoppingItem);
}
