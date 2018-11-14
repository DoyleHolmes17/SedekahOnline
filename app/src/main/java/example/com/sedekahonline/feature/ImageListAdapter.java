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

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import example.com.sedekahonline.R;
import example.com.sedekahonline.model.DataSedekah;
import example.com.sedekahonline.model.ImageData;
import example.com.sedekahonline.remote.SOService;


/**
 * Created by Sander on 6/14/2016.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.MyViewHolder> {
    private Context context;
    private List<ImageData> imageDataList;

    public ImageListAdapter(Context context, List<ImageData> imageDataList) {
        this.context = context;
        this.imageDataList = imageDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ImageData imageData = imageDataList.get(position);

        if (imageData.getImage() != null) {
            Picasso.with(context).load(imageData.getImage()).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.ivImage);

        } else {
            holder.ivImage.setImageResource(R.drawable.default_profile);
        }
    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivImage;

        public MyViewHolder(View view) {
            super(view);
            ivImage = (ImageView) view.findViewById(R.id.ivImage);
        }
    }
}