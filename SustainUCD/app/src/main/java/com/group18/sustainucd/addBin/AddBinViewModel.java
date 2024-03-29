package com.group18.sustainucd.addBin;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * View model used by the AddBinFragment. After any kind of configuration changes this class
 * will help to don't lose data and to survive to this kind of under control events
 */
public class AddBinViewModel extends ViewModel {

    private MutableLiveData<Bitmap> imageBitmap;
    private MutableLiveData<Drawable> paperDrawable;
    private MutableLiveData<Drawable> batteryDrawable;
    private MutableLiveData<Drawable> foodDrawable;
    private MutableLiveData<Drawable> glassDrawable;
    private MutableLiveData<Drawable> plasticDrawable;
    private MutableLiveData<Drawable> electronicDrawable;

    public AddBinViewModel() {
        imageBitmap = new MutableLiveData<>();
        paperDrawable = new MutableLiveData<>();
        batteryDrawable = new MutableLiveData<>();
        foodDrawable = new MutableLiveData<>();
        glassDrawable = new MutableLiveData<>();
        plasticDrawable = new MutableLiveData<>();
        electronicDrawable = new MutableLiveData<>();
    }

    public LiveData<Bitmap> getImageBitmap() { return imageBitmap; }

    public MutableLiveData<Drawable> getPaperDrawable() {
        return paperDrawable;
    }

    public MutableLiveData<Drawable> getBatteryDrawable() {
        return batteryDrawable;
    }

    public MutableLiveData<Drawable> getFoodDrawable() {
        return foodDrawable;
    }

    public MutableLiveData<Drawable> getGlassDrawable() {
        return glassDrawable;
    }

    public MutableLiveData<Drawable> getPlasticDrawable() {
        return plasticDrawable;
    }

    public MutableLiveData<Drawable> getElectronicDrawable() {
        return electronicDrawable;
    }
}