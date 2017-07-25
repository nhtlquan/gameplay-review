package vn.lequan.gameplayreview.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
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
import com.smile.studio.libsmilestudio.utils.Utils;
import com.smile.studio.network.ver2.model.screen_menu.ScreenMenuItem;

import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import vn.lequan.gameplayreview.R;
import vn.lequan.gameplayreview.fragment.ListSeasonOfSeries;
import vn.lequan.gameplayreview.model.GlobalApp;

/**
 * Created by admin on 16/08/2016.
 */
public class SeriesAdapterList extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Parcelable {

    private Context context;
    private List<ScreenMenuItem> lstData;
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


    public SeriesAdapterList(FragmentManager fragmentManager, Context context, List<ScreenMenuItem> lstData, int numberColmns, boolean isVideo) {
        this.context = context;
        this.lstData = lstData;
        this.numberColmns = numberColmns;
        this.fragmentManager = fragmentManager;
        this.isVideo = isVideo;
        point = AndroidDeviceInfo.getScreenSize(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_video_fake, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(point.x , point.x * 9 / 32 + Utils.convertDpToPixel(context, 10)));
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
//        holder.setIsRecyclable(false);
        myViewHolder.init(lstData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public void addAll(List<ScreenMenuItem> data) {
        this.lstData.addAll(data);
        size = getItemCount();
        notifyItemRangeInserted(size, getItemCount());
    }

    public void add(ScreenMenuItem data) {
        this.lstData.add(data);
        size = getItemCount();
        notifyItemRangeInserted(size, getItemCount());
    }


    public void clean() {
        lstData.clear();
        notifyDataSetChanged();
    }

    public ScreenMenuItem getItemAtPosition(int position) {
        return lstData.get(position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(point, flags);
        dest.writeInt(numberColmns);
        dest.writeByte((byte) (isVideo ? 1 : 0));
        dest.writeInt(size);
        dest.writeInt(lastAnimatedPosition);
        dest.writeInt(lastPosition);
        dest.writeByte((byte) (screenlist ? 1 : 0));
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_thumb, gradient;
        private TextView title, sub_title;
        private MaterialRatingBar ratingbar;

        public MyViewHolder(View view) {
            super(view);
            img_thumb = (ImageView) view.findViewById(R.id.img_thumb);
//            gradient = (ImageView) view.findViewById(R.id.gradient);
            title = (TextView) view.findViewById(R.id.title);
            ratingbar = (MaterialRatingBar) view.findViewById(R.id.ratingbar);
            sub_title = (TextView) view.findViewById(R.id.sub_title);
        }

        public void init(final ScreenMenuItem item, int po) {
//            thumb.setImageResource(R.drawable.account_bg);
            ratingbar.setRating(item.getRating());
            title.setText(item.getName());
            sub_title.setText(item.getViewCount().toString() + " view");

            Glide.with(context)
                    .load(item.getImage().getThumbnailUrl())
//                    .load(GlobalApp.getInstance().getListImageMovie().get(Utils.random(GlobalApp.getInstance().getListImageMovie().size())))
                    .asBitmap()
                    .error(R.drawable.placeholder_ngang)
                    .placeholder(R.drawable.placeholder_ngang)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img_thumb);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewCompat.animate(view)
                            .setDuration(200)
                            .scaleX(0.9f)
                            .scaleY(0.9f)
                            .setInterpolator(new CycleInterpolator())
                            .setListener(new ViewPropertyAnimatorListener() {
                                @Override
                                public void onAnimationStart(final View view) {

                                }

                                @Override
                                public void onAnimationEnd(final View view) {
                                    GlobalApp.getInstance().fragment = ListSeasonOfSeries.newInstance(item.getId());
                                    fragmentManager.beginTransaction()
                                            .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                                            .replace(R.id.content_main, GlobalApp.getInstance().fragment)
                                            .addToBackStack("back").commit();
                                }

                                @Override
                                public void onAnimationCancel(final View view) {

                                }
                            })
                            .withLayer()
                            .start();
                }
            });
        }

        public class CycleInterpolator implements android.view.animation.Interpolator {

            private final float mCycles = 0.5f;

            @Override
            public float getInterpolation(final float input) {
                return (float) Math.sin(2.0f * mCycles * Math.PI * input);
            }
        }
    }
}
