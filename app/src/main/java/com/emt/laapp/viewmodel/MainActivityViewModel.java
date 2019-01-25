package com.emt.laapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.emt.laapp.App;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import laapp.emt.com.core.database.AppDatabase;
import laapp.emt.com.core.database.ContactoDao;
import laapp.emt.com.core.model.Contacto;
import laapp.emt.com.core.rest.ProjectRepository;
import timber.log.Timber;

public class MainActivityViewModel extends ViewModel {
    private CompositeDisposable mDisposable;
    private ContactoDao contactoDao;
    private LiveData<List<Contacto>> mData;

    public MainActivityViewModel(CompositeDisposable disposable) {
        this.mDisposable = disposable;
        contactoDao = AppDatabase.getAppDatabase(App.getAppContext()).contactoDao();
        mData = contactoDao.getAll();
    }


    /**
     * Search in repository for the number of the device, if it's registered then
     * response an Contacto object trough Livedata for the View.
     *
     * @param telefono
     * @return A Mutable LiveData of the Contacto object.
     */
    public MutableLiveData<Contacto> searchContacto(String telefono) {
        final MutableLiveData<Contacto> responseContacto = new MutableLiveData<>();
        mDisposable.add(
                ProjectRepository.getInstance(App.getAppContext())
                        .searchContacto(telefono)
                        .subscribeWith(new DisposableObserver<Contacto>() {

                            @Override
                            public void onNext(Contacto contacto) {
                                if (contacto != null && !contacto.getError()) {
                                    responseContacto.postValue(contacto);
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e("Error %s", e.getMessage());
                                responseContacto.postValue(null);
                            }

                            @Override
                            public void onComplete() {
                                Timber.d("REQUEST NUMBER COMPLETE");
                            }

                        }));

        return responseContacto;
    }

    /**
     * @return LiveData list of contacts in database
     */
    public LiveData<List<Contacto>> getContactos() {
        return mData;
    }

}
