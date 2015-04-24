package com.cmpe295.grabz.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmpe295.grabz.Dto.ItemDto;
import com.cmpe295.grabz.Dto.StoreItemDto;
import com.cmpe295.grabz.R;
import com.cmpe295.grabz.fragment.AisleItemsFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


public class ItemDetailActivity extends Activity {
    public static final String LOG_PREFIX = "ItemDetailActivity";
    public static final String title = "Item Details";
    private TextView itemNameView;
    private TextView itemDescriptionView;
    private ImageView imageView;
    private TextView categoryView;
    private TextView sizeView;
    private TextView colorView;
    private TextView priceView;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String itemId = bundle.getString(AisleItemsFragment.ITEM_ID);
        String source = bundle.getString(AisleItemsFragment.SOURCE);
        if (source.equals("aisleItem")) {
            String tagId = bundle.getString(AisleItemsFragment.TAG_ID);
            String url = getString(R.string.awsLink) + "/tags/" + tagId + "/items/" + itemId;
            (new AsyncItemDetailsLoader()).execute(url);
        }
        else if (source.equals("basketItem")){
            String url = getString(R.string.awsLink) + "/items/" + itemId;
            (new AsyncBasketItemDetailsLoader()).execute(url);
        }
        itemNameView = (TextView)findViewById(R.id.itemNameDetail);
        imageView = (ImageView)findViewById(R.id.imageDetail);
        itemDescriptionView = (TextView)findViewById(R.id.itemDescriptionDetail);
        priceView = (TextView)findViewById(R.id.priceDetail);
        categoryView = (TextView)findViewById(R.id.category);
        colorView = (TextView)findViewById(R.id.color);
        sizeView = (TextView)findViewById(R.id.size);

        setTitle(title);
        ((TextView)findViewById(R.id.priceHeader)).setText("Price:");
        ((TextView)findViewById(R.id.categoryHeader)).setText("Category:");
        ((TextView)findViewById(R.id.colorHeader)).setText("Color:");
        ((TextView)findViewById(R.id.sizeHeader)).setText("Size:");

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);
        return true;
    }

    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
        }

/*        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private class AsyncItemDetailsLoader extends
            AsyncTask<String, Void, StoreItemDto> {
        private final ProgressDialog dialog = new ProgressDialog(ItemDetailActivity.this);
        HttpStatus responseCode;

        @Override
        protected void onPostExecute(StoreItemDto storeItem) {
            if (responseCode == HttpStatus.OK && storeItem != null) {
                dialog.dismiss();
                itemNameView.setText(storeItem.getStoreItem().getItem().getName());
                itemDescriptionView.setText(storeItem.getStoreItem().getItem().getDescription());
                categoryView.setText(storeItem.getStoreItem().getItem().getCategory());
                colorView.setText(storeItem.getStoreItem().getItem().getColor());
                sizeView.setText(storeItem.getStoreItem().getItem().getSize());
                priceView.setText(String.valueOf(storeItem.getStoreItem().getPrice()));

                imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(storeItem.getStoreItem().getItem().getImageUrl(),imageView);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching item details..");
            dialog.show();
        }

        @Override
        protected StoreItemDto doInBackground(String... params) {

            try {
                String url = params[0];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                ResponseEntity<StoreItemDto> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, StoreItemDto.class);
                StoreItemDto storeItem = responseEntity.getBody();
                responseCode = responseEntity.getStatusCode();


                return storeItem;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

    }
    private class AsyncBasketItemDetailsLoader extends
            AsyncTask<String, Void, ItemDto> {
        private final ProgressDialog dialog = new ProgressDialog(ItemDetailActivity.this);
        HttpStatus responseCode;

        @Override
        protected void onPostExecute(ItemDto item) {
            if (responseCode == HttpStatus.OK && item != null) {
                dialog.dismiss();
                itemNameView.setText(item.getItem().getName());
                itemDescriptionView.setText(item.getItem().getDescription());
                categoryView.setText(item.getItem().getCategory());
                colorView.setText(item.getItem().getColor());
                sizeView.setText(item.getItem().getSize());
                priceView.setText("Varies for different outlets");

                imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(item.getItem().getImageUrl(),imageView);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching item details..");
            dialog.show();
        }

        @Override
        protected ItemDto doInBackground(String... params) {

            try {
                String url = params[0];
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                restTemplate.getMessageConverters().add(mapper);
                ResponseEntity<ItemDto> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ItemDto.class);
                ItemDto storeItem = responseEntity.getBody();
                responseCode = responseEntity.getStatusCode();


                return storeItem;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

    }
}
