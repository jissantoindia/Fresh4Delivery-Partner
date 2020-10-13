package com.seclob.fbpartner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class EnqAdapterSearch1 extends RecyclerView.Adapter<EnqAdapterSearch1.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<EnqModelSearch1> mEnqModelSearch1s1 = new ArrayList<>(1000);
    EnqModelSearch1 EnqModelSearch1;
    String vidID,lId;
    //private List<DataHolder> displayedList;
    SharedPreferences sharedPreferences;

    public EnqAdapterSearch1(Context ctx){
        this.ctx = ctx;
        sharedPreferences = ctx.getSharedPreferences("SECGRO",MODE_PRIVATE);
        lId=sharedPreferences.getString("lId","");

    }

    public void renewItems(List<EnqModelSearch1> list) {
        this.mEnqModelSearch1s1 = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.foodlist, parent,false);
        return new MyViewHolder(inflate);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        EnqModelSearch1 = mEnqModelSearch1s1.get(position);
        holder.Oid.setText(EnqModelSearch1.getOid());
        holder.Osts.setText(EnqModelSearch1.getOsts());
        holder.Oname.setText(EnqModelSearch1.getOname());
    }

    @Override
    public int getItemCount() {
        return mEnqModelSearch1s1.size();
    }

    public void updateList(List<EnqModelSearch1> list){
        mEnqModelSearch1s1 = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Oid,Osts,Oname;

        public MyViewHolder(View itemView) {
            super(itemView);

            Oid = (TextView) itemView.findViewById(R.id.foodprice);
            Osts = (TextView) itemView.findViewById(R.id.foodstatus);
            Oname = (TextView) itemView.findViewById(R.id.fooname);
        }

    }


}