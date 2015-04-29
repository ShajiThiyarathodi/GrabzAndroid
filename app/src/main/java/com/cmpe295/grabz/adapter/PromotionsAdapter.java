package com.cmpe295.grabz.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmpe295.grabz.Dto.AisleItemDto;
import com.cmpe295.grabz.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by shajithiyarathodi on 4/22/15.
 */
public class PromotionsAdapter extends ArrayAdapter<AisleItemDto> {

    private List<AisleItemDto> itemList;
    private Context context;
    ImageLoader imageLoader;

    public List<AisleItemDto> getItemList() {
        return itemList;
    }
    public void setItemList(List<AisleItemDto> itemList) {
        this.itemList = itemList;

    }

    public PromotionsAdapter(Context ctx,
                             List<AisleItemDto> itemList) {
        super(ctx, R.layout.promotions_row_layout, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }
    public View getView(int position, View convertedView, ViewGroup parent) {
        View v = convertedView;
        ItemHolder holder = new ItemHolder();

        if (convertedView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.promotions_row_layout, parent, false);


            TextView itemNameListV = (TextView) v.findViewById(R.id.promoItemName);
            TextView priceListV = (TextView) v
                    .findViewById(R.id.originalPrice);
            priceListV.setPaintFlags(priceListV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            TextView promoPriceListV = (TextView) v
                    .findViewById(R.id.promoPrice);
            ImageView imageListV = (ImageView) v
                    .findViewById(R.id.promoThumbImage);

            holder.itemNameView = itemNameListV;
            holder.priceView = priceListV;
            holder.imageView = imageListV;
            holder.promoPriceView = promoPriceListV;

            v.setTag(holder);
        } else
            holder = (ItemHolder) v.getTag();

        AisleItemDto aisleItem = itemList.get(position);
        holder.itemNameView.setText(aisleItem.getAisleItem().getName());
        String price = '$'+ String.valueOf(aisleItem.getAisleItem().getPrice());
        holder.priceView.setText(price);
        String newPrice = '$'+ String.valueOf(aisleItem.getAisleItem().getPromotionalPrice());
        holder.promoPriceView.setText(newPrice);
        String imageUrl = aisleItem.getAisleItem().getImageUrl();
        imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(imageUrl, holder.imageView);

        return v;
    }

    private static class ItemHolder {
        public TextView itemNameView;
        public TextView priceView;
        public TextView promoPriceView;
        public ImageView imageView;
    }


}

