package it.polimi.stopit.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.polimi.stopit.OnPassingData;
import it.polimi.stopit.R;
import it.polimi.stopit.model.MoneyTarget;

/**
 * Created by alessiorossotti on 17/12/15.
 */
public class MoneyRecyclerViewAdapter extends RecyclerView.Adapter<MoneyRecyclerViewAdapter.ViewHolder> {

    private final List<MoneyTarget> mTargets;
    private final OnPassingData myListener;
    private int selectedPos=1000;

    public MoneyRecyclerViewAdapter(List<MoneyTarget> items, OnPassingData myListener) {
        mTargets = items;
        this.myListener = myListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_moneygallery, parent, false);


        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        MoneyTarget target=mTargets.get(position);

        holder.targetPic.setImageResource(target.getImageResource());
        holder.targetPic.setTag(target.getImageResource());
        holder.targetName.setText(target.getName());
        holder.setIsRecyclable(false);
        holder.mView.setSelected(selectedPos == position);

        if(holder.mView.isSelected()){

            holder.mView.findViewById(R.id.target_image).setBackgroundColor(Color.parseColor("#CCCCCC"));

        }else{

            holder.mView.findViewById(R.id.target_image).setBackgroundColor(Color.TRANSPARENT);
            holder.mView.clearAnimation();
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyItemChanged(selectedPos);

                if (v.isSelected()) {

                    v.setSelected(false);
                    System.out.println("ALREADY SELECTED: selected Pos: " + selectedPos);
                    selectedPos = 1000;
                    holder.mView.findViewById(R.id.target_image).setBackgroundColor(Color.TRANSPARENT);
                    holder.mView.clearAnimation();

                } else {

                    System.out.println("SELECTED: selected Pos: " + selectedPos);

                    selectedPos = position;
                    notifyItemChanged(selectedPos);
                    holder.mView.findViewById(R.id.target_image).setBackgroundColor(Color.parseColor("#CCCCCC"));
                    Animation animationPop = AnimationUtils.loadAnimation(v.getContext(), R.anim.popup);
                    v.findViewById(R.id.target_image).setAnimation(animationPop);

                    ImageView img = (ImageView) v.findViewById(R.id.target_image);
                    TextView name = (TextView) v.findViewById(R.id.target_name);
                    myListener.callBack(name.getText().toString(), Integer.parseInt(img.getTag().toString()));
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mTargets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final ImageView targetPic;
        public final TextView targetName;
        public final int pos;

        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            mView = view;
            targetPic=(ImageView) view.findViewById(R.id.target_image);
            targetName= (TextView) view.findViewById(R.id.target_name);
            pos=getLayoutPosition();
        }
    }


}
