package vn.lequan.gameplayreview.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.smile.studio.libsmilestudio.utils.SharedPreferencesUtils;
import com.smile.studio.libsmilestudio.utils.Utils;
import com.smile.studio.network.model.API;
import com.smile.studio.network.ver2.model.screen_menu.ScreenMenuItem;

import java.util.List;

import vn.lequan.gameplayreview.R;
import vn.lequan.gameplayreview.activity.AccountActivity;
import vn.lequan.gameplayreview.activity.MainActivity;
import vn.lequan.gameplayreview.fragment.ListSeasonOfSeries;
import vn.lequan.gameplayreview.model.GlobalApp;

/**
 * Created by admin on 16/08/2016.
 */
@SuppressLint("ParcelCreator")
public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Parcelable {

    private Context context;
    private List<ScreenMenuItem> lstData;
    private Point point;
    private int numberColmns = 0;
    private FragmentManager fragmentManager;
    private int size;
    private boolean screenlist;


    public VideoAdapter(FragmentManager fragmentManager, Context context, List<ScreenMenuItem> lstData, int numberColmns, boolean screenlist) {
        this.context = context;
        this.lstData = lstData;
        this.numberColmns = numberColmns;
        this.fragmentManager = fragmentManager;
        this.screenlist = screenlist;
        point = AndroidDeviceInfo.getScreenSize(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_video, parent, false);
        if (screenlist) {
            view.setLayoutParams(new ViewGroup.LayoutParams(point.x, point.x * 9 / 16));
        } else {
            view.setLayoutParams(new ViewGroup.LayoutParams(point.x / 2 + Utils.convertDpToPixel(context, 20), point.x / 2 * 9 / 16 + Utils.convertDpToPixel(context, 20)));
        }
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
        public void init(final ScreenMenuItem item, int po) {
            int i = 0;
            i = context.getApplicationContext().getResources().getColor(R.color.background_signon);
            Object localObject2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{i, 0});
            gradient.setBackground((Drawable) localObject2);
            title.setText(item.getName());
            sub_title.setText(item.getViewCount().toString() + " view");
            Glide.with(context)
                    .load(item.getImage().getThumbnailUrl())
//                    .load(GlobalApp.getInstance().getListVideo().get(Utils.random(GlobalApp.getInstance().getListVideo().size())))
                    .asBitmap()
                    .error(R.drawable.placeholder_ngang)
                    .placeholder(R.drawable.placeholder_ngang)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)
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
                                    if (item.getType().equals(context.getString(R.string.type_series))) {
//                                        GlobalApp.getInstance().fragment = ListSeasonOfSeries.newInstance(item.getId());
                                        fragmentManager.beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout).replace(R.id.content_main, ListSeasonOfSeries.newInstance(item.getId())).addToBackStack("back").commit();
                                    } else {
                                        if (item.getType().equals(context.getString(R.string.type_videos))) {
                                            MainActivity.loadvideohomesub(item.getId());
                                        } else if (item.getType().equals(context.getString(R.string.type_movies))) {
                                            MainActivity.loadmoviehomesub(item.getId());
                                        } else if (item.getType().equals(context.getString(R.string.type_live_channels))) {
                                            MainActivity.loadchanelhomesub(item.getId());
                                        }
                                    }
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
