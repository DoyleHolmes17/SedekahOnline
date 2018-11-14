package example.com.sedekahonline.feature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import example.com.sedekahonline.R;
import example.com.sedekahonline.model.Ulasan;


/**
 * Created by Sander on 6/14/2016.
 */
public class UlasanAdapter extends RecyclerView.Adapter<UlasanAdapter.MyViewHolder> {
    private Context context;
    private List<Ulasan> ulasansList;

    public UlasanAdapter(Context context, List<Ulasan> ulasansList) {
        this.context = context;
        this.ulasansList = ulasansList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ulasan, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Ulasan ulasan = ulasansList.get(position);
        holder.tvName.setText(ulasan.getNama_user());
        holder.tvKomentar.setText(ulasan.getUlasan());
        holder.tvTanggal.setText(ulasan.getTanggalUlasan());
//        Bitmap bmp = getBitmapFromResource(ulasan.getGambar());
//        holder.ivImage.setImageBitmap(bmp);


    }

    @Override
    public int getItemCount() {
        return ulasansList.size();
    }

    private Bitmap getBitmapFromResource(String name) {
        int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);
        return bmp;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvKomentar, tvTanggal;
        public ImageView ivImage;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvNama);
            tvKomentar = (TextView) view.findViewById(R.id.tvKomentar);
            tvTanggal = (TextView) view.findViewById(R.id.tvTanggal);
            ivImage = (ImageView) view.findViewById(R.id.ivImage);
        }
    }
}