package org.peterkwan.udacity.mysupermarket.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "wish_list_item")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WishlistItem {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    private int id;

    @NonNull
    @SerializedName("name")
    @Expose
    private String name = "";
}
