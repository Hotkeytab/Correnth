package com.dna.plank.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dna.plank.Modeles.Historique;
import com.dna.plank.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder>{
    private List<Historique> mhistoricList;
    private Context mContext;



    private RecyclerView mRecyclerV;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Les Item's
        public TextView Nomex;
        public TextView dureeEx;
        public TextView TemEc;
        public TextView ratio;

        public ImageView exImageImgV;


        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            Nomex = (TextView) v.findViewById(R.id.name);
            dureeEx = (TextView) v.findViewById(R.id.age);
            TemEc = (TextView) v.findViewById(R.id.occupation);
            ratio = (TextView) v.findViewById(R.id.ratio);

        exImageImgV = (ImageView) v.findViewById(R.id.image);




        }
    }


    public ScoreAdapter(List<Historique> mhistoricList, Context mContext, RecyclerView mRecyclerV) {
        this.mhistoricList = mhistoricList;
        this.mContext = mContext;
        this.mRecyclerV = mRecyclerV;
    }

    @NonNull
    @Override
    public ScoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.single_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull ScoreAdapter.ViewHolder holder, int position) {

        final Historique person = mhistoricList.get(position);
        holder.Nomex.setText("Excercice: " + person.getNomEx());
        holder.dureeEx.setText("Durée totale: " + person.getDureeEx());
        holder.TemEc.setText("Durée de pose: " + person.getDureepose());
        holder.ratio.setText(person.getRatio()+" %");
        Picasso.with(mContext).load(person.getImage()).placeholder(R.drawable.plank).into(holder.exImageImgV);



    }

    @Override
    public int getItemCount() {
        return mhistoricList.size();}
}
