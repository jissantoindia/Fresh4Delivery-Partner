package com.seclob.fbpartner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class EnqAdapterSearch extends RecyclerView.Adapter<EnqAdapterSearch.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<EnqModelSearch> mEnqModelSearchs = new ArrayList<>(1000);
    EnqModelSearch EnqModelSearch;
    String vidID,lId;
    //private List<DataHolder> displayedList;
    SharedPreferences sharedPreferences;
    String Pincod="666666",USid="0",lName="Name",key,text;

    public EnqAdapterSearch(Context ctx){
        this.ctx = ctx;
        sharedPreferences = ctx.getSharedPreferences("SECGRO",MODE_PRIVATE);
        lId=sharedPreferences.getString("lId","");

    }

    public void renewItems(List<EnqModelSearch> list) {
        this.mEnqModelSearchs = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_list, parent,false);
        return new MyViewHolder(inflate);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        EnqModelSearch = mEnqModelSearchs.get(position);
        holder.Oid.setText("Order ID:"+EnqModelSearch.getOid());
        holder.Osts.setText(EnqModelSearch.getOsts());
        holder.Oname.setText(EnqModelSearch.getOname());
        holder.Omobile.setText(EnqModelSearch.getOmobile());
        holder.Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(v.getContext(), mEnqModelSearchs.get(position).getCapSer(), Toast.LENGTH_SHORT).show();

//                Log.e("Key",key);
                Intent intents = new Intent(v.getContext(), OrderActivity.class);
                intents.putExtra("oid",mEnqModelSearchs.get(position).getOid());
                intents.putExtra("lid",mEnqModelSearchs.get(position).getOadsid());
                v.getContext().startActivity(intents);
            }

        });
    }

    @Override
    public int getItemCount() {
        return mEnqModelSearchs.size();
    }

    public void updateList(List<EnqModelSearch> list){
        mEnqModelSearchs = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Oid,Osts,Oname,Omobile;
        LinearLayout Layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            Oid = (TextView) itemView.findViewById(R.id.oid);
            Osts = (TextView) itemView.findViewById(R.id.osts);
            Oname = (TextView) itemView.findViewById(R.id.oname);
            Omobile = (TextView) itemView.findViewById(R.id.omobile);
            Layout = itemView.findViewById(R.id.scard);
        }

    }


}