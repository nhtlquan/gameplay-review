package vn.lequan.gameplayreview.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.smile.studio.libsmilestudio.facebook.ObjectFacebook;
import com.smile.studio.libsmilestudio.facebook.ShareFacebook;
import com.smile.studio.libsmilestudio.utils.AndroidDeviceInfo;
import com.smile.studio.libsmilestudio.utils.AndroidUtils;
import com.smile.studio.libsmilestudio.utils.Debug;
import com.smile.studio.libsmilestudio.utils.SharedPreferencesUtils;
import com.smile.studio.network.model.API;
import com.smile.studio.network.ver2.face.Face_BookMark;
import com.smile.studio.network.ver2.model.BookMark.BookMark;
import com.smile.studio.network.ver2.model.BookMark.BookMarkRequest;
import com.smile.studio.network.ver2.model.MovieItem.Movie_Key;
import com.smile.studio.network.ver2.model.screen_menu.ScreenMenuItem;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.lequan.gameplayreview.R;
import vn.lequan.gameplayreview.activity.AccountActivity;
import vn.lequan.gameplayreview.adapter.MovieAdapterHome;
import vn.lequan.gameplayreview.model.GlobalApp;
import vn.lequan.gameplayreview.model.TYPEITEM;
import vn.lequan.gameplayreview.view.DialogRating;

/**
 * Created by admin on 22/08/2016.
 */
public class InfoFragmentMovie extends Fragment implements View.OnClickListener {

    private final String TYPE = "type";
    private static final String ItemMovie = "itemmovie";
    private View view;
    private TextView tv_title, tv_description, textView4;
    private boolean xemsau = true, yeuthich = true, share = true;
    private Point point;
    private LinearLayoutManager layout;
    private RecyclerView recyclerView;
    private LinearLayout layout_rating;
    private MaterialRatingBar ratingbar;
    private ImageView thumb, img_xemsau, img_share;
    private MovieAdapterHome adapter;
    private Movie_Key itemMovie;
    private CallbackManager callback;
    boolean isTextViewClicked = false;
    private TextView viewcount;
    private int numberColumns = 3;

    public InfoFragmentMovie() {
    }

    public static InfoFragmentMovie newInstance(Movie_Key itemMovie) {
        Bundle args = new Bundle();
        args.putParcelable(ItemMovie, itemMovie);
        InfoFragmentMovie fragment = new InfoFragmentMovie();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_info, container, false);
            point = AndroidDeviceInfo.getScreenSize(getActivity());
            callback = CallbackManager.Factory.create();
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            viewcount = (TextView) view.findViewById(R.id.viewcount);
            ratingbar = (MaterialRatingBar) view.findViewById(R.id.ratingbar);
            img_xemsau = (ImageView) view.findViewById(R.id.img_xemsau);
            layout_rating = (LinearLayout) view.findViewById(R.id.layout_rating);
            img_share = (ImageView) view.findViewById(R.id.img_share);
            tv_description = (TextView) view.findViewById(R.id.tv_description);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.addItemDecoration(new VerticalDividerItemDecoration.Builder(getContext()).color(Color.TRANSPARENT).sizeResId(R.dimen.padding1).build());
            adapter = new MovieAdapterHome(getChildFragmentManager(), getActivity(), new ArrayList<ScreenMenuItem>(), numberColumns, false);
            recyclerView.setAdapter(setAdapterAnimation(adapter));
            recyclerView.setHasFixedSize(true);
            itemMovie = getArguments().getParcelable(ItemMovie);
            onEvent(itemMovie);
            img_xemsau.setOnClickListener(this);
            img_share.setOnClickListener(this);
            layout_rating.setOnClickListener(this);
            if (itemMovie.getData().getMovie().getBookmark() != null) {
                if (itemMovie.getData().getMovie().getBookmark() == 1) {
                    img_xemsau.setImageResource(R.drawable.ic_xemsau_click);
                } else {
                    img_xemsau.setImageResource(R.drawable.ic_xemsau);
                }
            }
        }
        return view;
    }

    private AlphaInAnimationAdapter setAdapterAnimation(RecyclerView.Adapter adapter) {
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setFirstOnly(true);
        alphaAdapter.setDuration(1000);
        alphaAdapter.setInterpolator(new OvershootInterpolator(.5f));
        return alphaAdapter;
    }

    @Override
    public void onClick(View v) {
        ViewCompat.animate(v)
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
                        switch (view.getId()) {
                            case R.id.img_xemsau:
                                if (SharedPreferencesUtils.getBoolean(getActivity(), API.checkLogin)) {
                                    if (itemMovie.getData().getMovie().getBookmark() == 1) {
                                        BookMarkRequest bookMarkRequest = new BookMarkRequest(0);
                                        Call<BookMark> call = GlobalApp.getInstance().retrofit_ver2.create(Face_BookMark.class).creatBookmark(TYPEITEM.MOVIE.getValue(), itemMovie.getData().getMovie().getId(), bookMarkRequest, GlobalApp.getInstance().acessToken);
                                        call.enqueue(new Callback<BookMark>() {
                                            @Override
                                            public void onResponse(Call<BookMark> call, Response<BookMark> response) {
                                                try {
                                                    BookMark value = response.body();
                                                    Debug.e(value.getMessage());
                                                    img_xemsau.setImageResource(R.drawable.ic_xemsau);
                                                    itemMovie.getData().getMovie().setBookmark(0);
                                                    Toast.makeText(getContext(), "Đã xóa khỏi danh sách yêu thích!", Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
                                                    Debug.e("Lỗi: " + e.getMessage());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<BookMark> call, Throwable t) {
                                                Debug.e("Lỗi: " + t.getMessage());
                                            }
                                        });
                                    } else {
                                        BookMarkRequest bookMarkRequest = new BookMarkRequest(1);
                                        Call<BookMark> call = GlobalApp.getInstance().retrofit_ver2.create(Face_BookMark.class).creatBookmark(TYPEITEM.MOVIE.getValue(), itemMovie.getData().getMovie().getId(), bookMarkRequest, GlobalApp.getInstance().acessToken);
                                        call.enqueue(new Callback<BookMark>() {
                                            @Override
                                            public void onResponse(Call<BookMark> call, Response<BookMark> response) {
                                                try {
                                                    BookMark value = response.body();
                                                    Debug.e(value.getMessage());
                                                    img_xemsau.setImageResource(R.drawable.ic_xemsau_click);
                                                    itemMovie.getData().getMovie().setBookmark(1);
                                                    Toast.makeText(getContext(), "Thêm vào yêu thích thành công!", Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
                                                    Debug.e("Lỗi: " + e.getMessage());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<BookMark> call, Throwable t) {
                                                Debug.e("Lỗi: " + t.getMessage());
                                            }
                                        });
                                    }
                                } else {
                                    Intent intent = new Intent(getActivity(), AccountActivity.class);
                                    getActivity().startActivity(intent);
                                    return;
                                }
                                break;
                            case R.id.img_share:
                                if (share) {
                                    share = false;
//                    img_share.setImageResource(R.drawable.ic_chiase_click);
                                    onActionShareFacebook(itemMovie.getData().getMovie().getName(), itemMovie.getData().getMovie().getDescription(), AndroidUtils.gotoWebStore("vn.lequan.gameplayreview"), itemMovie.getData().getMovie().getImage().getThumbnailUrl());
                                } else {
                                    share = true;
                                    img_share.setImageResource(R.drawable.ic_chiase);
                                }
                                break;
                            case R.id.layout_rating:
                                Debug.e("Quan");
                                DialogRating dialogSetting = new DialogRating(TYPEITEM.MOVIE.getValue(), itemMovie.getData().getMovie().getId());
                                if (!dialogSetting.isHidden()) {
                                    Debug.e("isHidden");
                                    dialogSetting.show(getFragmentManager(), DialogRating.class.getSimpleName());
                                }
                                break;
                        }
                    }

                    @Override
                    public void onAnimationCancel(final View view) {

                    }
                })
                .withLayer()
                .start();
    }

    private class CycleInterpolator implements android.view.animation.Interpolator {

        private final float mCycles = 0.5f;

        @Override
        public float getInterpolation(final float input) {
            return (float) Math.sin(2.0f * mCycles * Math.PI * input);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Movie_Key itemMovie) {
        try {
            ratingbar.setRating(itemMovie.getData().getMovie().getRating());
            tv_title.setText(itemMovie.getData().getMovie().getName());
            tv_description.setText(Html.fromHtml(itemMovie.getData().getMovie().getDescription()));
            viewcount.setText(itemMovie.getData().getMovie().getViewCount() + " view");
            for (ScreenMenuItem screenMenuItem : itemMovie.getData().getRelatedMovies()) {
                screenMenuItem.setType("movies");
            }
            adapter.addAll(itemMovie.getData().getRelatedMovies());
        } catch (Exception e) {
            Debug.e("Lỗi: " + e.getMessage());
        }
    }

    public void onActionShareFacebook(String title, String description, String url, String image) {
        ShareFacebook performShare = new ShareFacebook(getActivity(), callback);
        performShare.onActionShareFacebook(new ObjectFacebook(title, description, url, image), new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Debug.showAlert(getActivity(), "Đã chia sẻ trên facebook");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Debug.showAlert(getActivity(), error.getMessage());
            }
        });
    }

}
