package vn.lequan.gameplayreview.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.smile.studio.libsmilestudio.utils.AndroidDeviceInfo;
import com.smile.studio.libsmilestudio.utils.Debug;
import com.smile.studio.network.ver2.model.Search.SearchItem;

import java.util.List;

import vn.lequan.gameplayreview.R;
import vn.lequan.gameplayreview.activity.MainActivity;
import vn.lequan.gameplayreview.fragment.ListSeasonOfSeries;
import vn.lequan.gameplayreview.model.GlobalApp;

/**
 * Created by admin on 16/08/2016.
 */
@SuppressLint("ParcelCreator")
public class ListSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Parcelable {

    private Context context;
    private List<SearchItem> lstData;
    private Point point;
    private RequestManager manager;
    private int numberColmns = 0;
    private FragmentManager fragmentManager;
    private boolean isVideo;
    private ProgressDialog pDialog;
    private int size;
    private int lastAnimatedPosition = -1;
    private int lastPosition = -1;
    private boolean screenlist;


    public ListSearchAdapter(FragmentManager fragmentManager, Context context, List<SearchItem> lstData, int numberColmns, boolean isVideo) {
        this.context = context;
        this.lstData = lstData;
        this.numberColmns = numberColmns;
        this.fragmentManager = fragmentManager;
        this.isVideo = isVideo;
        point = AndroidDeviceInfo.getScreenSize(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_video, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        holder.setIsRecyclable(false);
        myViewHolder.init(lstData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public void addAll(List<SearchItem> data) {
        this.lstData.addAll(data);
        size = getItemCount();
        notifyItemRangeInserted(size, getItemCount());
    }

    public void add(SearchItem data) {
        this.lstData.add(data);
        size = getItemCount();
        notifyItemRangeInserted(size, getItemCount());
    }


    public void clean() {
        lstData.clear();
        notifyDataSetChanged();
    }

    public SearchItem getItemAtPosition(int position) {
        return lstData.get(position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }


    private class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_thumb, gradient;
        private TextView title, sub_title;

        public MyViewHolder(View view) {
            super(view);
            img_thumb = (ImageView) view.findViewById(R.id.img_thumb);
            gradient = (ImageView) view.findViewById(R.id.gradient);
            title = (TextView) view.findViewById(R.id.title);
            sub_title = (TextView) view.findViewById(R.id.sub_title);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    title.setSelected(true);
                    return true;
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void init(final SearchItem item, int po) {
            int i = 0;
            i = context.getApplicationContext().getResources().getColor(R.color.background_signon);
            Object localObject2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{i, 0});
            gradient.setBackground((Drawable) localObject2);
//            title.setText(item.getName());
//            sub_title.setText(item.getViewCount().toString() + " view");
            title.setVisibility(View.GONE);
            sub_title.setVisibility(View.GONE);
            Glide.with(context)
                        .load(item.getImage().getThumbnailUrl())
//                    .load(GlobalApp.getInstance().getListImageMovie().get(Utils.random(GlobalApp.getInstance().getListImageMovie().size())))
                    .asBitmap()
                    .error(R.drawable.placeholder_ngang)
                    .placeholder(R.drawable.placeholder_ngang)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)
                    .into(img_thumb);
            Debug.e(item.getType());
            itemView.setLayoutParams(new ViewGroup.LayoutParams(point.x / 3, point.x / 2));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.getType().equals(context.getString(R.string.type_videos))) {
                        MainActivity.openDraggableView();
                        MainActivity.loadvideohomesub(item.getId());
                    } else if (item.getType().equals(context.getString(R.string.type_movies))) {
                        MainActivity.openDraggableView();
                        MainActivity.loadmoviehomesub(item.getId());
                    } else if (item.getType().equals(context.getString(R.string.type_series))) {
//                            GlobalApp.getInstance().fragment = ListSeasonOfSeries.newInstance(item.getId());
                        fragmentManager.beginTransaction().replace(R.id.content_main, ListSeasonOfSeries.newInstance(item.getId())).addToBackStack("back").commit();
                    }
                }
            });
        }

    }
}
