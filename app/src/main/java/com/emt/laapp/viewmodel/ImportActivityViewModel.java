package com.emt.laapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ContentResolver;
import android.databinding.ObservableField;
import android.os.AsyncTask;

import com.emt.laapp.App;
import com.emt.laapp.ui.MainActivity;
import com.emt.laapp.utils.ContactUtils;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import laapp.emt.com.core.database.AppDatabase;
import laapp.emt.com.core.database.ContactoDao;
import laapp.emt.com.core.model.Contacto;
import laapp.emt.com.core.rest.ProjectRepository;
import timber.log.Timber;

public class ImportActivityViewModel extends ViewModel {
    private CompositeDisposable mDisposable;
    private ContactoDao contactoDao;
    final private MutableLiveData<List<Contacto>> mData = new MutableLiveData<>();
    private ContentResolver mContentResolver;
    public final ObservableField<Integer> busy = new ObservableField<>();
    private Contacto mContacto;


    public ImportActivityViewModel(ContentResolver contentResolver, CompositeDisposable disposable) {
        this.mDisposable = disposable;
        contactoDao = AppDatabase.getAppDatabase(App.getAppContext()).contactoDao();
        this.mContentResolver = contentResolver;
        //Obtain list of contacts from device
        this.mData.setValue(getmData());

        String TAG = ImportActivityViewModel.class.getSimpleName();
        Timber.tag(TAG);
    }

    /**
     * @return LiveData List of contacts to view
     */
    public MutableLiveData<List<Contacto>> getContactos() {
        return mData;
    }


    /**
     * @return a list of contacts from contentProvider that access Contacts API
     */
    private List<Contacto> getmData() {
        return ContactUtils.readContacts(mContentResolver);
    }


    /**
     * Search for an agregado of the user in back end
     *
     * @param contacto
     * @return And RX Observable<Contacto></Contacto>
     */
    public MutableLiveData<Contacto> searchAgregado(final Contacto contacto) {
        final MutableLiveData<Contacto> responseContacto = new MutableLiveData<>();
        mContacto = contacto;
        mDisposable.add(
                ProjectRepository.getInstance(App.getAppContext())
                        .searchAgregado(Prefs.getString("UUID", ""), mContacto.getTelefono())
                        .subscribeWith(new DisposableObserver<Contacto>() {

                            @Override
                            public void onNext(Contacto contacto) {
                                Timber.d("Error " + contacto.getError() + " mensaje:" + contacto.getMessage());
                                if (contacto != null && !contacto.getError()) {
                                    responseContacto.postValue(contacto);
                                } else {
                                    registerAgregado(Prefs.getString("UUID", ""), mContacto);
                                    responseContacto.postValue(null);

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

    public MutableLiveData<Contacto> registerAgregado(String userid, Contacto contacto) {
        final MutableLiveData<Contacto> responseContacto = new MutableLiveData<>();
        mDisposable.add(
                ProjectRepository.getInstance(App.getAppContext())
                        .registerAgregado(userid, contacto.getTelefono())
                        .subscribeWith(new DisposableObserver<Contacto>() {

                            @Override
                            public void onNext(Contacto contacto) {
                                if (contacto != null && !contacto.getError()) {
                                    Timber.d("Contacto agregado en backedn: " + contacto.getTelefono());
                                    agregarContacto(mContacto);
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
                                Timber.d("REGISTER AGREGADO COMPLETE");
                            }

                        }));

        return responseContacto;
    }

    /**
     * Save new contacto in backend and local database
     *
     * @param contacto
     */
    public void agregarContacto(Contacto contacto) {

        new insertAsyncTask().execute(contacto);
    }

    private static class insertAsyncTask extends AsyncTask<Contacto, Void, Void> {
        ContactoDao contactoDao;

        @Override
        protected Void doInBackground(final Contacto... params) {
            contactoDao = AppDatabase.getAppDatabase(App.getAppContext()).contactoDao();
            contactoDao.insert(params[0]);
            Timber.d("Contacto agregado en DB: " + params[0].getTelefono());
            return null;
        }

    }
}
