package com.emt.laapp.viewmodel.injection;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.ContentResolver;

import com.emt.laapp.viewmodel.ImportActivityViewModel;

public class ImportActivityFactory extends ViewModelProvider.NewInstanceFactory {
    private ContentResolver mContentResolver;

    public ImportActivityFactory(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ImportActivityViewModel(mContentResolver);
    }
}