package com.emt.laapp.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.emt.laapp.R;
import com.emt.laapp.databinding.ItemContactoBinding;

import java.util.ArrayList;
import java.util.List;

import laapp.emt.com.core.model.Contacto;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ItemHolder> implements Filterable {
    private List<Contacto> mContacto;
    private List<Contacto> mContactoFiltered;
    private ContactoItemListener mContactoItemListener;

    public ContactoAdapter(List<Contacto> mContacto, ContactoItemListener contactoItemListener) {
        this.mContacto = mContacto;
        this.mContactoFiltered = mContacto;
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
        binding.setContacto(mContactoFiltered.get(position));

        holder.mBinding.contactCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactoItemListener.Onclick(mContacto.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContactoFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mContactoFiltered = mContacto;
                } else {
                    List<Contacto> filteredList = new ArrayList<>();
                    for (Contacto row : mContacto) {
                        if (row.getNombre().toLowerCase().contains(charString.toLowerCase()) || row.getTelefono().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    mContactoFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mContactoFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mContactoFiltered = (ArrayList<Contacto>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        ItemContactoBinding mBinding;

        public ItemHolder(ItemContactoBinding binding) {
            super(binding.contactCard);
            this.mBinding = binding;
        }
    }
}
