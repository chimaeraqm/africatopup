package com.crazydwarf.africatopup.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crazydwarf.africatopup.R;
import com.crazydwarf.africatopup.activity.HistoryActivity;
import com.crazydwarf.africatopup.view.CountryItemAdapter;
import com.crazydwarf.africatopup.view.HistoryItemAdapter;

import java.util.Locale;

public class CountrySelectDialog extends Dialog
{
    public CountrySelectDialog(@NonNull final Context context)
    {
        super(context, R.style.CurrentDialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_countries,null);
        setContentView(view);
        RecyclerView mRecyclerview = findViewById(R.id.rv_countries);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));

        TypedArray countryArray = context.getResources().obtainTypedArray(R.array.select_countries);
        TypedArray codeArray = context.getResources().obtainTypedArray(R.array.select_codes);
        TypedArray flagsArray = context.getResources().obtainTypedArray(R.array.select_flags);
        String[] countries = new String[countryArray.length()];
        Integer[] codes = new Integer[codeArray.length()];
        Integer[] flags = new Integer[flagsArray.length()];
        for(int i=0;i<countryArray.length();i++)
        {
            countries[i] = countryArray.getString(i);
            codes[i] = codeArray.getInteger(i,0);
            flags[i] = flagsArray.getResourceId(i,0);
        }
        CountryItemAdapter countryItemAdapter = new CountryItemAdapter(countries,codes,flags);
        mRecyclerview.setAdapter(countryItemAdapter);

        //TODO: 显示RecycleView点击事件
        countryItemAdapter.setOnCountryItemRVClickListener(new CountryItemAdapter.onCountryItemRVClickListener() {
            @Override
            public void onItemClick(View view) {
                dismiss();
            }
        });
    }
}