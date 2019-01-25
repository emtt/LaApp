package com.emt.laapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ContentResolver;
import android.databinding.ObservableField;
import android.os.AsyncTask;

import com.emt.laapp.App;
import com.emt.laapp.utils.ContactUtils;

import java.util.List;

import laapp.emt.com.core.database.AppDatabase;
import laapp.emt.com.core.database.ContactoDao;
import laapp.emt.com.core.model.Contacto;

public class ImportActivityViewModel extends ViewModel {
    private ContactoDao contactoDao;
    final private MutableLiveData<List<Contacto>> mData = new MutableLiveData<>();
    private ContentResolver mContentResolver;
    public final ObservableField<Integer> busy = new ObservableField<>();


    public ImportActivityViewModel(ContentResolver contentResolver) {
        contactoDao = AppDatabase.getAppDatabase(App.getAppContext()).contactoDao();
        this.mContentResolver = contentResolver;

        //Obtain list of contacts from device
        this.mData.setValue(getmData());
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

    public void agregarContacto(Contacto contacto) {
        new insertAsyncTask().execute(contacto);
    }

    private static class insertAsyncTask extends AsyncTask<Contacto, Void, Void> {
        ContactoDao contactoDao;

        @Override
        protected Void doInBackground(final Contacto... params) {
            contactoDao = AppDatabase.getAppDatabase(App.getAppContext()).contactoDao();
            contactoDao.insert(params[0]);
            return null;
        }

    }
}
