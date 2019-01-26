package com.emt.laapp.utils;

import android.databinding.BindingAdapter;
import android.widget.TextView;

import laapp.emt.com.core.model.Contacto;

public class Bindings {

    @BindingAdapter({"capital_letter"})
    public static void setCapital(TextView textView, Contacto contacto) {
        String string = String.valueOf((contacto.getNombre()).charAt(0)).toUpperCase();
        textView.setText(string);
    }
}
