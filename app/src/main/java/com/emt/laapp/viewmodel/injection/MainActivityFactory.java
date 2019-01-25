package com.emt.laapp.viewmodel.injection;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import com.emt.laapp.viewmodel.MainActivityViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivityFactory extends ViewModelProvider.NewInstanceFactory {
    CompositeDisposable mDisposable;

    public MainActivityFactory(CompositeDisposable disposable) {
        this.mDisposable = disposable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MainActivityViewModel(mDisposable);
    }
}
