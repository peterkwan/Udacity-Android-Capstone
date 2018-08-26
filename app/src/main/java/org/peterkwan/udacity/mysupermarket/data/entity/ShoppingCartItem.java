package org.peterkwan.udacity.mysupermarket.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "shopping_cart")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShoppingCartItem implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    private int id;

    @NonNull
    @SerializedName("item_name_en")
    @ColumnInfo(name = "item_name_en")
    @Expose
    private String itemNameEng = "";

    @NonNull
    @SerializedName("item_name_zh")
    @ColumnInfo(name = "item_name_zh")
    @Expose
    private String itemNameTradChi = "";

    @NonNull
    @SerializedName("item_name_cn")
    @ColumnInfo(name = "item_name_cn")
    @Expose
    private String itemNameSimpChi = "";

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("unit_price")
    @ColumnInfo(name = "unit_price")
    @Expose
    private double unitPrice;

    @NonNull
    @SerializedName("image_path")
    @ColumnInfo(name = "image_path")
    @Expose
    private String imagePath = "";

    @SerializedName("status")
    @Expose
    private int status;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(itemNameEng);
        parcel.writeString(itemNameTradChi);
        parcel.writeString(itemNameSimpChi);
        parcel.writeInt(quantity);
        parcel.writeDouble(unitPrice);
        parcel.writeString(imagePath);
        parcel.writeInt(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Parcelable.Creator<ShoppingCartItem> CREATOR = new Parcelable.Creator<ShoppingCartItem>() {

        @Override
        public ShoppingCartItem[] newArray(int i) {
            return new ShoppingCartItem[i];
        }

        @Override
        public ShoppingCartItem createFromParcel(Parcel parcel) {
            return new ShoppingCartItem(parcel);
        }
    };

    private ShoppingCartItem(Parcel parcel) {
        id = parcel.readInt();
        itemNameEng = parcel.readString();
        itemNameTradChi = parcel.readString();
        itemNameSimpChi = parcel.readString();
        quantity = parcel.readInt();
        unitPrice = parcel.readDouble();
        imagePath = parcel.readString();
        status = parcel.readInt();
    }
}
