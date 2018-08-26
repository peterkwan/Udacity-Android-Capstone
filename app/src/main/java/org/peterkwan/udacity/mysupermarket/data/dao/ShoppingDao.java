package org.peterkwan.udacity.mysupermarket.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import org.peterkwan.udacity.mysupermarket.data.entity.Notification;
import org.peterkwan.udacity.mysupermarket.data.entity.ShoppingCartItem;
import org.peterkwan.udacity.mysupermarket.data.entity.WishlistItem;

import java.util.List;

@Dao
public abstract class ShoppingDao {

    @Transaction
    @Query("select * from notification order by notification_date desc")
    public abstract LiveData<List<Notification>> findAllNotifications();

    @Transaction
    @Query("select * from notification where id = :notificationId")
    public abstract LiveData<Notification> findNotificationById(int notificationId);

    @Insert
    public abstract void insertNotification(Notification notification);

    @Transaction
    @Query("select * from wish_list_item")
    public abstract LiveData<List<WishlistItem>> findAllWishlistItems();

    @Transaction
    @Query("select * from wish_list_item where id = :itemId")
    public abstract LiveData<WishlistItem> findWishListItemById(int itemId);

    @Insert
    public abstract void insertWishListItem(WishlistItem item);

    @Delete
    public abstract void deleteWishListItem(WishlistItem item);

    @Insert
    public abstract void insertShoppingCartItem(ShoppingCartItem item);

    @Transaction
    @Query("delete from shopping_cart")
    public abstract void clearShoppingCart();

    @Transaction
    @Query("select * from shopping_cart")
    public abstract LiveData<List<ShoppingCartItem>> findAllShoppingCartItems();

    @Update
    public abstract void updateShoppingCartItem(ShoppingCartItem item);

    @Transaction
    @Query("select * from shopping_cart")
    public abstract List<ShoppingCartItem> findShoppingCartItemList();

    @Delete
    public abstract void deleteShoppingCartItem(ShoppingCartItem item);
}
