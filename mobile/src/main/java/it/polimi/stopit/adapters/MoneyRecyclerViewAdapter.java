package it.polimi.stopit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_moneygallery, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        MoneyTarget target=mTargets.get(position);

        holder.targetPic.setImageResource(target.getImageResource());
        holder.targetName.setText(target.getName());

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
            mView = view;
            targetPic=(ImageView) view.findViewById(R.id.target_image);
            targetName= (TextView) view.findViewById(R.id.target_name);
        }
    }


}
