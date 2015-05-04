package com.cmpe295.grabz.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.cmpe295.grabz.Dto.AisleItemDto;
import com.cmpe295.grabz.Dto.LinkDto;
import com.cmpe295.grabz.R;
import com.cmpe295.grabz.activity.ItemDetailActivity;
import com.cmpe295.grabz.adapter.PromotionsAdapter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class PromotionsFragment extends Fragment {
    public static final String PROMOTIONSTITLE = "Deals of the day!";
    public static final String LOG_PREFIX = "PromotionsFragment";
    public static final String CLASS_NAME = PromotionsFragment.class.getSimpleName();

    private List<AisleItemDto> aisleItemList = new ArrayList<AisleItemDto>();
    private ArrayAdapter promotionsAdapter;
    private GridView gridView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PromotionsFragment.
     */
    public static PromotionsFragment newInstance() {
        PromotionsFragment fragment = new PromotionsFragment();

        return fragment;
    }

    public PromotionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_promotions, container,
                false);

        getActivity().setTitle(PROMOTIONSTITLE);

        Context parentCtx = getActivity().getApplicationContext();

        //Aisle item list view, empty list now
        gridView = (GridView) rootView.findViewById(R.id.PromotionsGridView);

        promotionsAdapter = new PromotionsAdapter(parentCtx, aisleItemList);

        gridView.setAdapter(promotionsAdapter);
        final String tagId = getActivity().getSharedPreferences(AisleItemsFragment.GRABZPREFERENCES, Context.MODE_PRIVATE).getString(AisleItemsFragment.TAG_ID,"");
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view,
                                    int position, long id) {
                String itemHref = null;
                LinkDto link = null;
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        ItemDetailActivity.class);
                Iterator<LinkDto> iterator =  ((AisleItemDto)gridView.getItemAtPosition(position)).getLinks().iterator();
                while(iterator.hasNext()){
                    link = iterator.next();
                    if(link.getRel().equals("view-item")){
                        itemHref = link.getHref();
                        break;
                    }
                }
                intent.putExtra(AisleItemsFragment.ITEM_HREF, itemHref);
                intent.putExtra(AisleItemsFragment.SOURCE, "promotions");
                startActivity(intent);
            }
        });


        String url = getString(R.string.awsLink)+"/tags/"+tagId+"/promotions/items/";

        (new AsyncPromoListLoader())
                .execute(url);

        // Inflate the layout for this fragment
        return rootView;
    }

    private class AsyncPromoListLoader extends
            AsyncTask<String, Void, AisleItemDto[]> {
        private final ProgressDialog dialog = new ProgressDialog(
                getActivity());
        HttpStatus responseCode;

        @Override
        protected void onPostExecute(AisleItemDto[] result) {
            if (responseCode == HttpStatus.OK && result != null) {
                Log.d(LOG_PREFIX, "In Post Execute" + String.valueOf(result.length));
                dialog.dismiss();
                promotionsAdapter.addAll(result);
                promotionsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading deals for you! ..");
            dialog.show();
        }

        @Override
        protected AisleItemDto[] doInBackground(String... params) {

            try {
                String url = params[0];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                ResponseEntity<AisleItemDto[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, AisleItemDto[].class);
                AisleItemDto[] items = responseEntity.getBody();
                responseCode = responseEntity.getStatusCode();
                Log.d(LOG_PREFIX, String.valueOf(items.length));

                return items;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

    }

}
