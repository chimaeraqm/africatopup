package com.crazydwarf.africatopup.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.crazydwarf.africatopup.R;
import com.crazydwarf.africatopup.Utilities.UserUtil;
import com.crazydwarf.africatopup.activity.BundleActivity;
import com.crazydwarf.africatopup.activity.TxtDisplayActivity;
import com.crazydwarf.africatopup.dialog.CountrySelectDialog;
import com.crazydwarf.africatopup.view.CommonAdapter;

public class QueryFragment extends Fragment
{
    private ImageView imFlag;
    private RecyclerView mRecyclerview;
    private int selePosition = 10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_query,container,false);
        mRecyclerview = view.findViewById(R.id.list_carriers);
        imFlag = view.findViewById(R.id.im_flag);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        //根据国家的选择刷新运营商列表
        imFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountrySelectDialog countrySelectDialog = new CountrySelectDialog(getActivity(), new CountrySelectDialog.dialogItemSelectionListener() {
                    @Override
                    public void onClick(View view, int position) {
                        selePosition = position;
                        mRecyclerview.invalidate();
                    }
                });
                countrySelectDialog.show();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        TypedArray operatorSeqArray = getActivity().getResources().obtainTypedArray(R.array.operator_seq);
        int selres = operatorSeqArray.getResourceId(selePosition,R.array.operator_egypt_20);
        TypedArray operatorArray = getActivity().getResources().obtainTypedArray(selres);

        String[] countriesSeq = new String[operatorArray.length()];
        Integer[] ids = new Integer[operatorArray.length()];
        for(int i=0;i<operatorArray.length();i++)
        {
            countriesSeq[i] = operatorArray.getString(i);
            ids[i] = R.drawable.ic_keyboard_arrow_left_black_32dp;
        }
        CommonAdapter commonAdapter = new CommonAdapter(countriesSeq,ids);
        mRecyclerview.setAdapter(commonAdapter);

        //TODO:添加Recycleview点击事件
        commonAdapter.setOnCommonRVItemClickListener(new CommonAdapter.OnCommonRVItemClickListener() {
            @Override
            public void onItemClick(View view) {
                Toast.makeText(getActivity(), "显示运营商资费清单", Toast.LENGTH_SHORT).show();
                String txt = UserUtil.readFromRaw(getActivity(),R.raw.ribbontmp);
                Intent intent = new Intent(getActivity(), TxtDisplayActivity.class);
                intent.putExtra("OPERATOR_INFO",txt);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view) {

            }
        });

    }
}
