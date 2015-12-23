package it.polimi.stopit.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import it.polimi.stopit.R;
import it.polimi.stopit.model.MoneyTarget;

/**
 * Created by alessiorossotti on 20/12/15.
 */
public class MoneyTargetsAdapter extends RecyclerView.Adapter<MoneyTargetsAdapter.ViewHolder>{

    private final List<MoneyTarget> mTargets;
    private Context context;

    public MoneyTargetsAdapter(List<MoneyTarget> items, Context context) {

        mTargets = items;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_moneytarget, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        MoneyTarget target=mTargets.get(position);

        holder.targetImg.setImageResource(target.getImageResource());
        holder.targetName.setText(target.getName());
        holder.targetDuration.setText("" + (target.getDuration()));
        holder.progressBar.setMax((int) target.getMoneyAmount());

        if(target.getMoneySaved()==target.getMoneyAmount()){

            holder.progressBar.setProgress((int) target.getMoneySaved());
            holder.targetPrice.setText("Completed");
            holder.progressBar.invalidateDrawable(ContextCompat.getDrawable(context,R.drawable.progress_bar));
            holder.progressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_completed));

        }else {

            holder.progressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar));
            holder.progressBar.setProgress((int) target.getMoneySaved());
            holder.targetPrice.setText((target.getMoneySaved()/100)+"."+(target.getMoneySaved()%100)+" / " + target.getMoneyAmount()/100 + " €");
        }
    }

    @Override
    public int getItemCount() {
        return mTargets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final ImageView targetImg;
        public final TextView targetName;
        public final TextView targetPrice;
        public final TextView targetDuration;
        public final ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            targetImg=(ImageView) view.findViewById(R.id.target_image);
            targetName= (TextView) view.findViewById(R.id.target_name);
            targetPrice= (TextView) view.findViewById(R.id.target_price);
            targetDuration= (TextView) view.findViewById(R.id.target_duration);
            progressBar = (ProgressBar) view.findViewById(R.id.target_progress);
        }
    }


}
