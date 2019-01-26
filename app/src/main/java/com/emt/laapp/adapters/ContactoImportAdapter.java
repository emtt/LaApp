package com.emt.laapp.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emt.laapp.R;
import com.emt.laapp.databinding.ItemImportContactoBinding;

import java.util.List;

import laapp.emt.com.core.model.Contacto;


public class ContactoImportAdapter extends RecyclerView.Adapter<ContactoImportAdapter.ItemHolder> {
    private List<Contacto> mContacto;
    ContactoItemListener mContactoItemListener;

    public ContactoImportAdapter(List<Contacto> mContacto, ContactoItemListener contactoItemListener) {
        this.mContacto = mContacto;
        this.mContactoItemListener = contactoItemListener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemImportContactoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_import_contacto, parent, false);

        return new ContactoImportAdapter.ItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        ItemImportContactoBinding binding = holder.mBinding;
        binding.setContacto(mContacto.get(position));

        holder.mBinding.contactCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", mContacto.get(position).getNombre());
                mContactoItemListener.Onclick(mContacto.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacto.size();
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {
        ItemImportContactoBinding mBinding;

        public ItemHolder(ItemImportContactoBinding binding) {
            super(binding.contactCard);
            this.mBinding = binding;
        }
    }
}