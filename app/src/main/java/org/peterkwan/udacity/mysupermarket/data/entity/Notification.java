package org.peterkwan.udacity.mysupermarket.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Notification {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    private int id;

    @NonNull
    @SerializedName("title")
    @Expose
    private String title = "";

    @NonNull
    @SerializedName("message")
    @Expose
    private String message = "";

    @NonNull
    @SerializedName("notification_date")
    @ColumnInfo(name = "notification_date")
    @Expose
    private Date notificationDate = new Date();
}
