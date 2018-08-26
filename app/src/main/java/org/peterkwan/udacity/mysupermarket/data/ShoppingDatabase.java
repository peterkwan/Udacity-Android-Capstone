package org.peterkwan.udacity.mysupermarket.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import org.peterkwan.udacity.mysupermarket.data.converter.DateTypeConverter;
import org.peterkwan.udacity.mysupermarket.data.dao.ShoppingDao;
import org.peterkwan.udacity.mysupermarket.data.entity.Notification;
import org.peterkwan.udacity.mysupermarket.data.entity.ShoppingCartItem;
import org.peterkwan.udacity.mysupermarket.data.entity.WishlistItem;

@Database(entities = {Notification.class, WishlistItem.class, ShoppingCartItem.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class ShoppingDatabase extends RoomDatabase {

    private static final String LOG_TAG = ShoppingDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "my_supermarket";

    private static ShoppingDatabase database;

    public static ShoppingDatabase getInstance(final Context context) {
        if (database == null) {
            synchronized (ShoppingDatabase.class) {
                if (database == null) {
                    Log.d(LOG_TAG, "Creating new database instance");
                    database = Room.databaseBuilder(context.getApplicationContext(), ShoppingDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }

        Log.d(LOG_TAG, "Getting the database instance");

        return database;
    }

    public abstract ShoppingDao shoppingDao();
}
