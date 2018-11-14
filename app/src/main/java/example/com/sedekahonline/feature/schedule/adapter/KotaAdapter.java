package example.com.sedekahonline.feature.schedule.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.List;

import example.com.sedekahonline.R;
import example.com.sedekahonline.feature.schedule.activity.ScheduleActivity;
import example.com.sedekahonline.model.Location;


/**
 * Created by Sander on 6/14/2016.
 */
public class KotaAdapter extends RecyclerView.Adapter<KotaAdapter.MyViewHolder> {

    String idk,kota;
    private Context context;
    private List<Location> kotaList;

    public KotaAdapter(Context context, List<Location> kotaList) {
        this.context = context;
        this.kotaList = kotaList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kota, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        holder.tvNamaKota.setText(kotaList.get(position).getNama_kota());

        holder.tvNamaKota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ScheduleActivity.class);
                intent.putExtra("selectedCity", kotaList.get(position).getId());
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return kotaList.size();
    }

    private Bitmap getBitmapFromResource(String name) {
        int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);
        return bmp;
    }

    public void clear() {
        kotaList.clear();
        notifyItemRangeRemoved(0, getItemCount());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNamaKota;

        public MyViewHolder(View view) {
            super(view);
            tvNamaKota = (TextView) view.findViewById(R.id.tvNamaKota);
        }
    }
}