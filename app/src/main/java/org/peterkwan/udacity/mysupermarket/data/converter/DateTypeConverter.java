package org.peterkwan.udacity.mysupermarket.data.converter;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;

import java.util.Date;

public class DateTypeConverter {

    @TypeConverter
    public static Date fromTimestamp(@NonNull Long value) {
        return new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(@NonNull Date date) {
        return date.getTime();
    }
}
