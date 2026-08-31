package com.quickkart.app.utils;

import android.content.Context;

import com.quickkart.app.R;

/**
 * Category and product images are stored in the DB as string keys
 * (e.g. "product_1", "cat_mobiles") so that admin-added items can also
 * reference a fixed pool of placeholder art. This resolves those keys to
 * actual drawable resource ids.
 */
public class ImageMapper {

    public static int resolve(Context context, String key) {
        if (key == null || key.isEmpty()) {
            return R.drawable.ic_image_placeholder;
        }
        int resId = context.getResources().getIdentifier(key, "drawable", context.getPackageName());
        return resId != 0 ? resId : R.drawable.ic_image_placeholder;
    }
}
