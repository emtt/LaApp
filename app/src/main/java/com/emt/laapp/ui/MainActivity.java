package com.emt.laapp.ui;

import android.annotation.TargetApi;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.emt.laapp.R;
import com.emt.laapp.adapters.ContactoAdapter;
import com.emt.laapp.adapters.ContactoItemListener;
import com.emt.laapp.databinding.ActivityMainBinding;
import com.emt.laapp.viewmodel.MainActivityViewModel;
import com.emt.laapp.viewmodel.injection.MainActivityFactory;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.disposables.CompositeDisposable;
import laapp.emt.com.core.model.Contacto;
import timber.log.Timber;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity implements LifecycleOwner, ContactoItemListener {

    private LifecycleRegistry mLifecycleRegistry;
    private MainActivityViewModel mViewModel;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private ActivityMainBinding mBinding;
    private int MyVersion = Build.VERSION.SDK_INT;

    private ContactoAdapter adapter;
    private ArrayList<Contacto> listContactos = new ArrayList<>();
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;

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

        permissions.add(READ_CONTACTS);
        permissions.add(READ_PHONE_STATE);
        permissions.add(SEND_SMS);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
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

        mBinding.txtSearchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initData();
    }

    private void initData() {

        mViewModel.getContactos().observe(this, contactListObserver);
        registerNumber();
    }

    View.OnClickListener btnAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, ImportActivity.class);
            MainActivity.this.startActivity(i);
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



    public String createTransactionID() throws Exception {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    private void registerNumber() {
        try {
            if (!Prefs.contains("UUID")) {
                Prefs.putString("UUID", createTransactionID());
            }

            searchUser(Prefs.getString("UUID", ""));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void searchUser(String userid) {

        mViewModel.searchContacto(userid).observe(this, contactoObserver);


    }

    private void registerUser() {

        mViewModel.registerUser(Prefs.getString("UUID", "")).observe(this, registroObserver);

    }


    Observer<Contacto> contactoObserver = new Observer<Contacto>() {

        @Override
        public void onChanged(@Nullable Contacto contacto) {
            if (contacto != null) {
                Timber.d("Registrado");
                Timber.d("Contacto UUID: " + contacto.getTelefono());
                setVMVisible(false);
            } else {
                Timber.d("No existe el registro. Procede a registrar");
                registerUser();
                setVMVisible(false);
            }
        }

    };

    Observer<Contacto> registroObserver = new Observer<Contacto>() {

        @Override
        public void onChanged(@Nullable Contacto contacto) {
            if (contacto != null) {
                setVMVisible(false);
            } else {
                Timber.d("Contacto Es Nulo");
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

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel(getResources().getString(R.string.permissionsMsg),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                } else {
                    initControls();
                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void Onclick(Contacto contacto) {

    }
}
