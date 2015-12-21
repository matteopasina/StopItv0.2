package it.polimi.stopit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.fragments.MoneyGalleryFragment.OnListFragmentInteractionListener;
import it.polimi.stopit.model.MoneyTarget;

/**
 * Created by alessiorossotti on 17/12/15.
 */
public class MoneyRecyclerViewAdapter extends RecyclerView.Adapter<MoneyRecyclerViewAdapter.ViewHolder> {

    private final List<MoneyTarget> mTargets;
    private final OnListFragmentInteractionListener mListener;

    public MoneyRecyclerViewAdapter(List<MoneyTarget> items, OnListFragmentInteractionListener listener) {
        mTargets = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_moneygallery, parent, false);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.isSelected()) {
                    v.setSelected(false);
                    v.clearAnimation();

                } else {

                    /*
                    for(int i=0;i<parent.getChildCount();i++){

                        RecyclerView viewI=(RecyclerView) parent.getChildAt(i).findViewById(R.id.list);
                        viewI.findViewById(R.id.target_image).clearAnimation();
                        viewI.destroyDrawingCache();
                        viewI.setSelected(false);
                        viewI.setBackgroundColor(Color.TRANSPARENT);
                    }*/
                    v.setSelected(true);
                    Animation animationPop = AnimationUtils.loadAnimation(v.getContext(), R.anim.popup);
                    v.findViewById(R.id.target_image).setAnimation(animationPop);
                }
            }
        });
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        MoneyTarget target=mTargets.get(position);

        holder.targetPic.setImageResource(target.getImageResource());
        holder.targetName.setText(target.getName());

        // mark  the view as selected:
        holder.mView.setSelected(mTargets.contains(position));

    }

    @Override
    public int getItemCount() {
        return mTargets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final ImageView targetPic;
        public final TextView targetName;

        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            mView = view;
            targetPic=(ImageView) view.findViewById(R.id.target_image);
            targetName= (TextView) view.findViewById(R.id.target_name);
        }
    }


}
