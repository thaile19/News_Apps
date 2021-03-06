package thaile.com.aiw_client_finalproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thaile.com.aiw_client_finalproject.Adapter.AdapterHome;

/**
 * Created by Thai Le on 11/22/2016.
 */

public class FragmentHome extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Context context;
    private LinearLayout layout_first_news;
    private ProgressBar progressBar;
    private ListView listView;
    private int increaseData = 0;
    private boolean isLoading = false;
    private List<NewsObj> listNews;
    private ImageView img;
    private TextView txtv_title;
    private TextView txtv_shortintro;
    private TextView txtv_date;
    private TextView txtv_updated;
    private View view;
    private AdapterHome adapter;
    private NewsObj firstObj;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        initData(AppHelper.BASE_URL+increaseData);
        return view;
    }

    private void initData(String myUrl) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, myUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String str = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                            updateDisplay(str);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("MEO","add params");
                Map<String, String> params  = new HashMap<>();
                params.put("num", String.valueOf(increaseData));
                return  params;
            }
        };

        //add request to queue
        queue.add(stringRequest);

    }

    private void updateDisplay(String str) {
        if (isLoading){
            List<NewsObj> listObj = AppHelper.parseToList(str);
            if (listObj.size() == 0){
                Log.e("HOME", "hết");
            } else {
                adapter.addListItemToAdapter(listObj);
                isLoading = false;
            }
        } else {
            listNews =  AppHelper.parseToList(str);
            firstObj = listNews.get(0);
            txtv_title.setText(firstObj.getTitleNews());
            txtv_shortintro.setText(firstObj.getShortIntro());
            txtv_date.setText(firstObj.getDateNews());
            txtv_updated.setText("Tin mới cập nhật");
            txtv_updated.setBackgroundColor(Color.parseColor("#E0E0E0"));
            Picasso.with(context).load(firstObj.getImgNews()).into(img);

            listNews.remove(0);
            adapter = new AdapterHome(context, listNews);
            listView.setAdapter(adapter);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }


    private void initView() {
        listNews = new ArrayList<>();
        txtv_title = (TextView) view.findViewById(R.id.txtv_lastest_title);
        txtv_shortintro = (TextView) view.findViewById(R.id.txtv_lastest_shortintro);
        img = (ImageView) view.findViewById(R.id.imageView);
        txtv_date = (TextView) view.findViewById(R.id.txtv_date);
        txtv_updated = (TextView) view.findViewById(R.id.txtv_updated);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        layout_first_news = (LinearLayout) view.findViewById(R.id.layout_first_news);
        layout_first_news.setOnClickListener(this);
        listView = (ListView) view.findViewById(R.id.mylistview);
        listView.setOnItemClickListener(this);
        listView.setNestedScrollingEnabled(true);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (listView.getAdapter() == null)
                    return ;

                if (listView.getAdapter().getCount() == 0)
                    return ;

                int l = visibleItemCount + firstVisibleItem;
                if (l >= totalItemCount && !isLoading) {
                    isLoading = true;
                    increaseData = increaseData + 8;
                    initData(AppHelper.BASE_URL+increaseData);
                    Log.e("HEP", AppHelper.BASE_URL+increaseData);
                    Log.e("HOME=====>", listNews.size()+"\n"+"--"+increaseData);
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewsObj obj = listNews.get(position);
        AppHelper.sendData(getActivity(), obj);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_first_news:
                AppHelper.sendData(getActivity(), firstObj);
                break;
            default:
                break;
        }
    }

}
