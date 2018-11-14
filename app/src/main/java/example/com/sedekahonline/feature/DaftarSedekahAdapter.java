package example.com.sedekahonline.feature;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import example.com.sedekahonline.R;
import example.com.sedekahonline.model.DataSedekah;
import example.com.sedekahonline.remote.SOService;


/**
 * Created by Sander on 6/14/2016.
 */
public class DaftarSedekahAdapter extends RecyclerView.Adapter<DaftarSedekahAdapter.MyViewHolder> {
    private Context context;
    private List<DataSedekah> sedekahList;
    SOService mService;
    ProgressDialog pd;

    public DaftarSedekahAdapter(Context context, List<DataSedekah> sedekahList) {
        this.context = context;
        this.sedekahList = sedekahList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sedekah, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DataSedekah sedekah = sedekahList.get(position);
        holder.tvName.setText(sedekah.getNama_lengkap());
        holder.tvAlamat.setText(sedekah.getAlamat());

        if (sedekah.getGambar() != null && sedekah.getGambar().size()>0) {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(sedekah.getGambar(), 0, sedekah.getGambar().length());
//            if (bitmap == null) {
            Picasso.with(context).load(sedekah.getGambar().get(0).getImage()).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivImage);
//                holder.ivImage.setImageResource(R.drawable.default_profile);
//            } else {
//                holder.ivImage.setImageBitmap(bitmap);
//            }
        } else {
            holder.ivImage.setImageResource(R.drawable.default_profile);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailSedekahActivity.class);
                intent.putExtra("id_sedekah", sedekah.getId());
                intent.putExtra("nama", sedekah.getNama_lengkap());
                intent.putExtra("nohp", sedekah.getNoHp());
                intent.putExtra("alamat", sedekah.getAlamat());
                intent.putExtra("gambar", new Gson().toJson(sedekah.getGambar()));
                intent.putExtra("location", sedekah.getLatitude() + "," + sedekah.getLongitude());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sedekahList.size();
    }

    private Bitmap getBitmapFromResource(String name) {
        int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);
        return bmp;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvAlamat;
        public ImageView ivImage;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvNama);
            tvAlamat = (TextView) view.findViewById(R.id.tvAlamat);
            ivImage = (ImageView) view.findViewById(R.id.ivImage);
        }
    }
}