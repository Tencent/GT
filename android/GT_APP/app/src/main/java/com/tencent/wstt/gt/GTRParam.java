package com.tencent.wstt.gt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by p_hongjcong on 2017/7/11.
 */

public class GTRParam implements Parcelable{

    public GTRParam(){
    }
    protected GTRParam(Parcel in) {
    }
    public static final Creator<GTRParam> CREATOR = new Creator<GTRParam>() {
        @Override
        public GTRParam createFromParcel(Parcel in) {
            return new GTRParam(in);
        }

        @Override
        public GTRParam[] newArray(int size) {
            return new GTRParam[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }




}
