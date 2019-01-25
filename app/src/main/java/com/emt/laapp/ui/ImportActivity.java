package com.emt.laapp.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.emt.laapp.R;
import com.emt.laapp.adapters.ContactoImportAdapter;
import com.emt.laapp.adapters.ContactoItemListener;
import com.emt.laapp.databinding.ActivityImportBinding;
import com.emt.laapp.viewmodel.ImportActivityViewModel;
import com.emt.laapp.viewmodel.injection.ImportActivityFactory;

import java.util.List;

import laapp.emt.com.core.model.Contacto;

public class ImportActivity extends AppCompatActivity implements LifecycleOwner, ContactoItemListener {
    private ImportActivityViewModel mViewModel;
    private LifecycleRegistry mLifecycleRegistry;
    private ActivityImportBinding mBinding;
    private Contacto mContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);

        initControls();
    }

    private void initControls() {

        mBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_import);

        ImportActivityFactory factory = new ImportActivityFactory(this.getContentResolver());
        mViewModel = ViewModelProviders.of(this, factory).get(ImportActivityViewModel.class);
        mBinding.setViewModel(mViewModel);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvContactosAgenda.setLayoutManager(layoutManager);
        mViewModel.busy.set(View.GONE);

        initData();
    }

    private void initData() {
        mViewModel.getContactos().observe(this, new Observer<List<Contacto>>() {
            @Override
            public void onChanged(@Nullable List<Contacto> contactos) {
                /*for (Contacto contacto : contactos) {
                    Log.i("Import", " FROM AGENDA: Nombre:" + contacto.getNombre()
                            + " Celular:  " + contacto.getTelefono());
                }*/
                mBinding.rvContactosAgenda.setAdapter(new ContactoImportAdapter(contactos,
                        ImportActivity.this));
                mViewModel.busy.set(View.GONE);

            }
        });

    }

    @Override
    public void Onclick(Contacto contacto) {
        mContacto = contacto;
        new AlertDialog.Builder(ImportActivity.this)
                .setTitle(getResources().getString(R.string.addContactTitle))
                .setMessage(getResources().getString(R.string.addContactQuestion, contacto.getNombre()))
                .setPositiveButton(getResources().getString(R.string.ok), okDialogListener)
                .setNegativeButton(getResources().getString(R.string.cancel), cancelDialogListener)
                .create()
                .show();
    }

    DialogInterface.OnClickListener okDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            saveContacto(mContacto);
        }
    };

    DialogInterface.OnClickListener cancelDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //ImportActivity.this.finish();
        }
    };

    private void saveContacto(Contacto contacto) {

        if (contacto != null) mViewModel.agregarContacto(contacto);

    }

    public void onStart() {
        super.onStart();
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }


}

