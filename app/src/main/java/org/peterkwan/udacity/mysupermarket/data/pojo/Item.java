package org.peterkwan.udacity.mysupermarket.data.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Item implements Parcelable {

    private int itemId;
    private String name;
    private String nameEng;
    private String nameTradChi;
    private String nameSimpChi;
    private Double unitPrice;
    private String imageFilename;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(itemId);
        parcel.writeString(name);
        parcel.writeString(nameEng);
        parcel.writeString(nameTradChi);
        parcel.writeString(nameSimpChi);
        parcel.writeDouble(unitPrice);
        parcel.writeString(imageFilename);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item[] newArray(int i) {
            return new Item[i];
        }

        @Override
        public Item createFromParcel(Parcel parcel) {
            return new Item(parcel);
        }
    };

    private Item(Parcel parcel) {
        itemId = parcel.readInt();
        name = parcel.readString();
        nameEng = parcel.readString();
        nameTradChi = parcel.readString();
        nameSimpChi = parcel.readString();
        unitPrice = parcel.readDouble();
        imageFilename = parcel.readString();
    }
}
