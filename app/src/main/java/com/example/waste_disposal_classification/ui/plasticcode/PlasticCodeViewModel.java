package com.example.waste_disposal_classification.ui.plasticcode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlasticCodeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PlasticCodeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}