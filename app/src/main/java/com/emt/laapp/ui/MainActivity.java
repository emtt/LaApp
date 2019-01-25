package com.emt.laapp.ui;

import android.Manifest;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.emt.laapp.R;
import com.emt.laapp.adapters.ContactoAdapter;
import com.emt.laapp.adapters.ContactoItemListener;
import com.emt.laapp.databinding.ActivityMainBinding;
import com.emt.laapp.viewmodel.MainActivityViewModel;
import com.emt.laapp.viewmodel.injection.MainActivityFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import laapp.emt.com.core.model.Contacto;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LifecycleOwner, ContactoItemListener {

    private LifecycleRegistry mLifecycleRegistry;
    private MainActivityViewModel mViewModel;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private ActivityMainBinding mBinding;
    private int MyVersion = Build.VERSION.SDK_INT;
    private static final int CONTACTS_PERMISSIONS = 101;
    private ContactoAdapter adapter;
    private ArrayList<Contacto> listContactos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);

        String TAG = MainActivity.class.getSimpleName();
        Timber.tag(TAG);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        MainActivityFactory factory = new MainActivityFactory(mDisposable);
        mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);
        mBinding.setViewModel(mViewModel);


        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                initControls();
            }
        } else {
            initControls();
        }


    }

    private void initControls() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvContactos.setLayoutManager(layoutManager);

        adapter = new ContactoAdapter(listContactos, this);
        mBinding.rvContactos.setAdapter(adapter);
        mBinding.btnAdd.setOnClickListener(btnAddListener);

        setVMVisible(true);

        initData();
    }

    private void initData() {

        mViewModel.getContactos().observe(this, contactListObserver);

    }

    View.OnClickListener btnAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Timber.d("btnAddclicked");
        }
    };

    Observer<List<Contacto>> contactListObserver = new Observer<List<Contacto>>() {

        @Override
        public void onChanged(@Nullable List<Contacto> contactos) {
            Timber.d("OnChanged ContactList from DB");
            if (contactos != null) {
                setVMVisible(false);
                Timber.d("Lista de Contactos from DB " + contactos.toString() + "Size " + contactos.size());
                listContactos.clear();
                listContactos.addAll(contactos);

            } else {
                Timber.d("NO REGRESÓ LISTA DE CONTACTOS");
                setVMVisible(false);

            }
            adapter.notifyDataSetChanged();
        }
    };

    /*
    private void test() {

        setVMVisible(true);

        searchNumber();
    }
    */


    private void searchNumber() {

        mViewModel.searchContacto("5585981240").observe(this, contactoObserver);


    }

    Observer<Contacto> contactoObserver = new Observer<Contacto>() {

        @Override
        public void onChanged(@Nullable Contacto contacto) {

            if (contacto != null) {
                Timber.d("Contacto onChanged Contacto " + contacto.getTelefono());
                /*productos.addAll(responseProducto.getProductos());
                adapter.notifyDataSetChanged();
                */
                setVMVisible(false);
            } else {
                Timber.d("Contacto Es Nullo");
                setVMVisible(false);
            }
        }

    };

    private void setVMVisible(boolean mVisible) {
        mBinding.pgBar.setVisibility(mVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }


    public void onStart() {
        super.onStart();
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
        mDisposable.clear();
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CONTACTS_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initControls();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.error_permissions), Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void Onclick(Contacto contacto) {

    }
}
