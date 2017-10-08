package com.wite.positionerwear.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2017/8/26.
 */

public class testview extends RecyclerView {
    public testview(Context context) {
        super(context);
    }
  LayoutManager manger=new LayoutManager() {
      @Override
      public LayoutParams generateDefaultLayoutParams() {
          return null;
      }


  };




}
