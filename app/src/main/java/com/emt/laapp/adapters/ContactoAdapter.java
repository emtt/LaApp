package com.emt.laapp.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emt.laapp.R;
import com.emt.laapp.databinding.ItemContactoBinding;

import java.util.List;

import laapp.emt.com.core.model.Contacto;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ItemHolder> {
    private List<Contacto> mContacto;
    ContactoItemListener mContactoItemListener;

    public ContactoAdapter(List<Contacto> mContacto, ContactoItemListener contactoItemListener) {
        this.mContacto = mContacto;
        this.mContactoItemListener = contactoItemListener;
    }


    @Override
    public ContactoAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemContactoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_contacto, parent, false);

        return new ItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(ContactoAdapter.ItemHolder holder, final int position) {
        ItemContactoBinding binding = holder.mBinding;
        binding.setContacto(mContacto.get(position));

        holder.mBinding.contactCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactoItemListener.Onclick(mContacto.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacto.size();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        ItemContactoBinding mBinding;

        public ItemHolder(ItemContactoBinding binding) {
            super(binding.contactCard);
            this.mBinding = binding;
        }
    }
}
