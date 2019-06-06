package hu.bme.aut.todoextensions.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "todoitem")
public class TodoItem {

    @Ignore
    public TodoItem(String name, String description, String place, String deadline, boolean isDone) {
        this.name = name;
        this.description = description;
        this.place = place;
        this.deadline = deadline;
        this.isDone = isDone;
    }

    public TodoItem() {
    }

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "place")
    public String place;

    @ColumnInfo(name = "deadline")
    public String deadline;

    @ColumnInfo(name = "is_done")
    public boolean isDone;

    @ColumnInfo(name = "is_rep")
    public boolean isRepeatable;

    @ColumnInfo(name = "pic_path")
    public String path;

}
