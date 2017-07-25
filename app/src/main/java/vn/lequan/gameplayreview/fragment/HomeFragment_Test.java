package vn.lequan.gameplayreview.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.smile.studio.libsmilestudio.recyclerviewer.EndlessRecyclerOnScrollListener;
import com.smile.studio.libsmilestudio.utils.Debug;
import com.smile.studio.network.Face.Face_Youtube;
import com.smile.studio.network.ListVideoChanel.Item;
import com.smile.studio.network.ListVideoChanel.ListVideoChanel;
import com.smile.studio.network.Token;
import com.smile.studio.network.ver2.model.screen_menu.ScreenMenuItem;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.lequan.gameplayreview.R;
import vn.lequan.gameplayreview.adapter.VideoAdapterList;
import vn.lequan.gameplayreview.adapter.VideoAdapterYoutube;
import vn.lequan.gameplayreview.model.GlobalApp;

public class HomeFragment_Test extends Fragment {

    private View view;
    private RecyclerView recyclerViewHome;
    private ProgressBar marker_progress;
    private VideoAdapterYoutube adapter;
    private GridLayoutManager layout;
    private String pageToken = "";
    private int page = 1, pageSize = 10, numberColumns = 3;
    private String nextPageToken = "";
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public static HomeFragment_Test newInstance() {
        Bundle args = new Bundle();
        HomeFragment_Test fragment = new HomeFragment_Test();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            recyclerViewHome = (RecyclerView) view.findViewById(R.id.recyclerViewHome);
            marker_progress = (ProgressBar) view.findViewById(R.id.marker_progress);
            adapter = new VideoAdapterYoutube(getChildFragmentManager(), getActivity(), new ArrayList<Item>(), 1, true);
            layout = new GridLayoutManager(getActivity(), 1);
            recyclerViewHome.setAdapter(adapter);
            recyclerViewHome.setNestedScrollingEnabled(false);
            recyclerViewHome.setHasFixedSize(true);
            recyclerViewHome.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).margin(R.dimen.padding1).color(Color.TRANSPARENT).build());
            recyclerViewHome.setLayoutManager(layout);
            recyclerViewHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) //check for scroll down
                    {
                        visibleItemCount = layout.getChildCount();
                        totalItemCount = layout.getItemCount();
                        pastVisiblesItems = layout.findFirstVisibleItemPosition();

                        if (loading) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                loading = false;
                                initDataToken();
                            }
                        }
                    }
                }
            });
            initData();
        }
        return view;
    }


    private void initData() {
        Call<ListVideoChanel> call = GlobalApp.getInstance().retrofit.create(Face_Youtube.class).getListVideoByChanel(GlobalApp.KEY, GlobalApp.CHANEL_ID, GlobalApp.PART, GlobalApp.ODER, GlobalApp.maxResults);
        call.enqueue(new Callback<ListVideoChanel>() {
            @Override
            public void onResponse(Call<ListVideoChanel> call, Response<ListVideoChanel> response) {
                try {
                    nextPageToken = response.body().getNextPageToken();
                    adapter.addAll(response.body().getItems());
                    marker_progress.setVisibility(View.GONE);
                } catch (Exception e) {

                    Debug.e("Lỗi: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ListVideoChanel> call, Throwable t) {
                com.smile.studio.libsmilestudio.utils.Debug.e("Lỗi: " + t.getMessage());
            }
        });
    }

    private void initDataToken() {
        Debug.e(nextPageToken);
        Call<ListVideoChanel> call = GlobalApp.getInstance().retrofit.create(Face_Youtube.class).getListVideoByChanelToken(nextPageToken, GlobalApp.KEY, GlobalApp.CHANEL_ID, GlobalApp.PART, GlobalApp.ODER, GlobalApp.maxResults);
        call.enqueue(new Callback<ListVideoChanel>() {
            @Override
            public void onResponse(Call<ListVideoChanel> call, Response<ListVideoChanel> response) {
                try {
                    nextPageToken = response.body().getNextPageToken();
                    adapter.addAll(response.body().getItems());
//                    adapter.notifyDataSetChanged();
                    loading = true;
                    marker_progress.setVisibility(View.GONE);

                } catch (Exception e) {

                    Debug.e("Lỗi: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ListVideoChanel> call, Throwable t) {
                com.smile.studio.libsmilestudio.utils.Debug.e("Lỗi: " + t.getMessage());
            }
        });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("");
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}


