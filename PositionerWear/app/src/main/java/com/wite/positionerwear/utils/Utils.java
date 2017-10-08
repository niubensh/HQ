package com.wite.positionerwear.utils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wite.positionerwear.R;

/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class Utils {
    public static void setupItem(final View view, final LibraryObject libraryObject, final int position) {

        final ImageView img = (ImageView) view.findViewById(R.id.img_item);

      final TextView textView= (TextView) view.findViewById(R.id.textview);

        img.setImageResource(libraryObject.getRes());
        textView.setText(libraryObject.getTitle());

    }
    public static class LibraryObject {

        private String mTitle;
        private int mRes;

        public LibraryObject(final int res, final String title) {
            mRes = res;
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(final String title) {
            mTitle = title;
        }

        public int getRes() {
            return mRes;
        }

        public void setRes(final int res) {
            mRes = res;
        }
    }
}
