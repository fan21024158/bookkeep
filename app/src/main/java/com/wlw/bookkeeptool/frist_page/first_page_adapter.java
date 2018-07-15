package com.wlw.bookkeeptool.frist_page;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wlw.bookkeeptool.R;
import com.wlw.bookkeeptool.tableBean.everyDeskTable;
import com.wlw.bookkeeptool.tableBean.everyDishTable;

import java.util.ArrayList;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class first_page_adapter extends BaseQuickAdapter<everyDeskTable, BaseViewHolder> {

    Context context;
    ArrayList<everyDeskTable> its;
    private TextView tv0;
    private TextView desk_num;
    private TextView tv1;
    private TextView now_Price;
    private TextView menu_describe;
    private TextView tv2;
    private TextView place_order_time;

    public first_page_adapter(Context context, ArrayList<everyDeskTable> its) {
        super(R.layout.today_order_item, its);
        this.context = context;
        this.its = its;
//                Log.i(TAG, "convert123: " + its.get(0).getAddress().toString());
    }

    @Override
    protected void convert(BaseViewHolder helper, everyDeskTable item) {
        Log.i(TAG, "convert: " + item.toString());;
        helper.setText(R.id.desk_num,item.getDeskNum());
        helper.setText(R.id.now_Price,item.getTotalPrice_desk()+"元");
        String describeStr = "";
        for (everyDishTable e :item.getEveryDeskTableList()){
            describeStr+= e.getFoodname()+"("+e.getFoodCount()+")~";
        }
        helper.setText(R.id.menu_describe,describeStr);
        String time_str =  TimeUtils.date2String(item.getStartBillTime());
        helper.setText(R.id.place_order_time,time_str);



    }


}
