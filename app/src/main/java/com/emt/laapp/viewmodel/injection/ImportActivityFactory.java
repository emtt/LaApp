package com.emt.laapp.viewmodel.injection;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.ContentResolver;

import com.emt.laapp.viewmodel.ImportActivityViewModel;

import io.reactivex.disposables.CompositeDisposable;

public class ImportActivityFactory extends ViewModelProvider.NewInstanceFactory {
    private ContentResolver mContentResolver;
    private CompositeDisposable mDisposable;

    public ImportActivityFactory(ContentResolver contentResolver, CompositeDisposable disposable) {
        this.mContentResolver = contentResolver;
        this.mDisposable = disposable;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ImportActivityViewModel(mContentResolver, mDisposable);
    }
}