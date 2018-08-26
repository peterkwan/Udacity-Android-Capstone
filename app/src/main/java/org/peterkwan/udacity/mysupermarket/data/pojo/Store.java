package org.peterkwan.udacity.mysupermarket.data.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Store implements Parcelable {

    private int storeId;
    private String storeName;
    private String storeNameEng;
    private String storeAddress;
    private String openingHours;
    private double latitude;
    private double longitude;

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(storeId);
        parcel.writeString(storeName);
        parcel.writeString(storeNameEng);
        parcel.writeString(storeAddress);
        parcel.writeString(openingHours);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Parcelable.Creator<Store> CREATOR = new Parcelable.Creator<Store>() {

        @Override
        public Store[] newArray(int i) {
            return new Store[i];
        }

        @Override
        public Store createFromParcel(Parcel parcel) {
            return new Store(parcel);
        }
    };

    private Store(Parcel parcel) {
        storeId = parcel.readInt();
        storeName = parcel.readString();
        storeNameEng = parcel.readString();
        storeAddress = parcel.readString();
        openingHours = parcel.readString();
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
    }
}
