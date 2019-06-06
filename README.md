# Todolist
Created an app for a friendly 24 hours challenge. The main task was to finish homework in a certain time.
My goal was to create an usable Todo Application which can do the following:
* Create TODO-s.
* Modify already existing TODO-s.
* Swipe gestures (left to make it done, right to delete it)
* Detailed Activity for each TODO
* Check TODO to make it done
* Fragment for creating new TODO, and modifying existing
* Persistent Database for todo-s

What is implemented:
* Create TODO-s.
* Modify already existing TODO-s.
* Swipe gestures (left to make it done, right to delete it)
* Detailed Activity for each TODO
* Check TODO to make it done
* Fragment for creating new TODO, and modifying existing
* Persistent Database for todo-s
* Own drawn icon.
* TODO can be dragged to swap
* Delete all button
* Add an image to TODO from Gallery
* Calendar import
* Repeat option which creates more Events to Calendar
* Alarm option which creates an alarm clock to next day with the TODO-s name. (For Important TODO-s)

# Functions

The activity you get, when you first start the app: 

![Home](https://i.imgur.com/gyHAqYP.jpg)

You can add new Items with the button in the bottom-right corner. Here you can create a TODO. The only necessary thing is the name if its missing you can not create a TODO. The other ones can be missing.

![](https://i.imgur.com/UeQyoxZ.jpg)
![](https://i.imgur.com/j5Cj3RW.jpg)

After you created your TODO there is a RecyclerView to check all your tasks.

![](https://i.imgur.com/Oexz8L6.jpg)

If you want to see a detailed view of your TODO you can just tap it. After tapping you can see all the basic informations for the TODO you created. In this Activity you can add an image, to store with your TODO. You have the option to make your TODO done by tapping the not done button. Same for Repeatness.
In the top right Corner you can see an Alarm Clock, Calendar and Pen button. These are the alarm, calendar import, modify options.

![](https://i.imgur.com/2aFJRTw.jpg)
![](https://i.imgur.com/zxg9Enf.jpg)

If you click on the modify button the same interface(Fragment) comes up, like when you created the TODO.

![](https://i.imgur.com/4IHUYBg.jpg)

You can create as many TODO-s as you want.

![](https://i.imgur.com/9klFMz7.jpg)

In the top-right corner of the screen there is an option for dragging and delete all.

![](https://i.imgur.com/o3Q3D9E.jpg)

Dragging means you can rearrange your items with dragging to a certain place. If you are in dragging mode, there is a drag button which appears in the top right. If you are done with dragging you can tap it to disable this mode.

![](https://i.imgur.com/h5IAmsf.jpg)

Swipe options for checking the task done and deleting are working. This functions works with the dragging mode disabled too.

![](https://i.imgur.com/Ep3k742.jpg)
![](https://i.imgur.com/etBJ2lf.jpg)

If you choose the delete all option there is dialog which asks if you are sure. This one is a security net for not deleting all your tasks by accident.

![](https://i.imgur.com/SRArwz3.jpg)

# Known issues

There is a tons of issues in the code. It clearly needs a refactor. Considering it was 24 hours to implement this much functionality I am satisfied with the result.

TODO:
* The image should be copied to internal storage, not saving the path for the extarnal storage.
* Code is hard to read.
* There are some icons which has the bad dpi.
* The UI is simple but ugly.
* There are a lot of functions that can be implemented.
* There are no tests currently. 
* Deadline saved in string format to database without a converter. (Using toString())

# References
Used ROOM for persistance.
Imgur is great site to upload pictures.
It was a friend asked challenge but there are many speedcoding challenges on the internet.
