package com.wlw.bookkeeptool.CustomerMenu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.jia.libui.MyDialog.MyDialog;
import com.jia.libui.Navigation.impl.ChatNavigation;
import com.jia.libui.sildeItemLayout.SlidingContentView;
import com.jia.libui.sildeItemLayout.SlidingItemLayout;
import com.jia.libutils.RxAndroidUtils.RxjavaUtil;
import com.jia.libutils.RxAndroidUtils.UITask;
import com.jia.libutils.WindowUtils;
import com.wlw.bookkeeptool.Kitchen.KitchenActivity;
import com.wlw.bookkeeptool.Order.AddRemark_PopupWindow;
import com.wlw.bookkeeptool.R;
import com.wlw.bookkeeptool.tableBean.EveryDeskTable;
import com.wlw.bookkeeptool.tableBean.EveryDishTable;
import com.wlw.bookkeeptool.tableBean.menuBean;
import com.wlw.bookkeeptool.utils.AnimatorUtils;
import com.wlw.bookkeeptool.utils.mWindowUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import litepal.LitePal;
import litepal.tablemanager.Connector;

import static com.wlw.bookkeeptool.MyApplication.UserName;

/**
 * Created by wlw on 2018/7/12.
 */

public class CustomerMenu_infoActivity extends Activity {
    Context context;
    private SelectMenuShow_Rv_Adapter selectMenuShow_rv_adapter;  //选择菜单
    private CustomerMenu_Super_Rv_Adapter customerMenu_super_rv_adapter; //填充菜单
    private DrawerLayout mDrawerLayout;
    private TextView mDeskNum;
    private TextView mDownMenuTime;
    private TextView mNowPrice;
    private RecyclerView mSuperRv;
    private TextView mTv1;
    private LinearLayout mImgAdd;
    private LinearLayout mImgCheckOut;
    private TextView mTv2;
    private RelativeLayout mSlideMenu;
    private ListView mImgList;
    private TextView mTypeShowId;
    private RecyclerView mShowMenuRv;
    private LinearLayout parentLayout;
    private EveryDeskTable everyDKTbean;//从上个界面 传过来的（桌的）实体
    private AddRemark_PopupWindow addRemark_popupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.addmenu_check_activity);
        SQLiteDatabase db = Connector.getDatabase();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
        initView();
        initdata();
        initevent();
        initNavigation();
    }

    private void initdata() {
        getData_for_imglist();
        getData_for_showMenuRv();
        getData_for_superMenuRv();
    }
    //    给SuperMenuRv取数据
    private void getData_for_superMenuRv() {
        Intent intent = getIntent();
        int DeskID = intent.getIntExtra("DeskID", 0);
        everyDKTbean = LitePal.find(EveryDeskTable.class, DeskID,true);
        mNowPrice.setText(everyDKTbean.getTotalPrice_desk() + "元");
        String strDate = TimeUtils.date2String(everyDKTbean.getStartBillTime());
        mDownMenuTime.setText(strDate);
        mDeskNum.setText(everyDKTbean.getDeskNum() + "");
        customerMenu_super_rv_adapter = new CustomerMenu_Super_Rv_Adapter(everyDKTbean.getEveryDishTableList());
        mSuperRv.setAdapter(customerMenu_super_rv_adapter);
    }

    //    给showMenuR取数据
    private void getData_for_showMenuRv() {
        //这里分两种策略 1 一次性全查  2用哪类查哪类
        //目前预估数量不大 使用 1
        ArrayList<menuBean> allmenuBean = (ArrayList<menuBean>) LitePal.findAll(menuBean.class);
        selectMenuShow_rv_adapter = new SelectMenuShow_Rv_Adapter(context, allmenuBean);
        mShowMenuRv.setAdapter(selectMenuShow_rv_adapter);
    }
    //    给imglist取数据
    private void getData_for_imglist() {
        String[] arrTitle = {"热", "凉", "主", "饮", "其他"};
        ImgListAdapter imgListAdapter = new ImgListAdapter(context, arrTitle);
        mImgList.setAdapter(imgListAdapter);
        imgListAdapter.notifyDataSetChanged();
    }
    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDeskNum = (TextView) findViewById(R.id.desk_num);
        mDownMenuTime = (TextView) findViewById(R.id.down_menu_time);
        mNowPrice = (TextView) findViewById(R.id.now_price);
        mSuperRv = (RecyclerView) findViewById(R.id.super_rv);
        mTv1 = (TextView) findViewById(R.id.tv1);
        mImgAdd = (LinearLayout) findViewById(R.id.img_add);
        mImgCheckOut = (LinearLayout) findViewById(R.id.img_check_out);
        mTv2 = (TextView) findViewById(R.id.tv2);
        mSlideMenu = (RelativeLayout) findViewById(R.id.slide_menu);
        mImgList = (ListView) findViewById(R.id.img_list);
        mTypeShowId = (TextView) findViewById(R.id.type_show);
        parentLayout = findViewById(R.id.parentLayout);
        mShowMenuRv =  findViewById(R.id.show_menu_rv);
        mShowMenuRv.setLayoutManager(new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false));
        mSuperRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    private void initevent() {
        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorUtils.scaleAnimation(v);
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        mImgCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorUtils.scaleAnimation(v);
                the_order_checkout();
            }
        });
        mImgList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final List<menuBean> menuBean_type_list = LitePal.where("foodtype == ?", position + "").find(menuBean.class);
                //在这里更新右面的ShowMenuRv布局
                RxjavaUtil.doInUIThread(new UITask<String>() {
                    @Override
                    public void doInUIThread() {
                        selectMenuShow_rv_adapter.replaceData(menuBean_type_list);
                    }
                });
            }
        });
        selectMenuShow_rv_adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                menuBean superMenu = (menuBean) adapter.getData().get(position);
                final EveryDishTable everyDishTable = new EveryDishTable(UserName, superMenu.getFoodname(),"0",1, superMenu.getPrice(), superMenu.getPrice(), new Date(), 2, UUID.randomUUID().toString(),"");
                RxjavaUtil.doInUIThread(new UITask<String>() {
                    @Override
                    public void doInUIThread() {
                        customerMenu_super_rv_adapter.addData(everyDishTable);
                    }
                });
            }
        });
        customerMenu_super_rv_adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                final TextView remark_tv = (TextView) viewGroup.getChildAt(6);
                final EveryDishTable everyDishTable = (EveryDishTable) adapter.getData().get(position);
                switch (view.getId()) {
                    case R.id.minus:
                        RxjavaUtil.doInUIThread(new UITask<String>() {
                            @Override
                            public void doInUIThread() {
                                int foodCount = everyDishTable.getFoodCount();
                                if (foodCount > 1) {
                                    everyDishTable.setFoodCount(foodCount -= 1);
                                } else {
                                    Toast.makeText(context, "已经是最小值", Toast.LENGTH_SHORT).show();
                                }
//                                BigDecimal b1 = new BigDecimal(Float.toString(everyDishTable.getTotalPrice_dish()));
//                                BigDecimal v = b1.multiply(BigDecimal.valueOf(foodCount));
//                                String s = v.toString();
                                everyDishTable.setTotalPrice_dish(foodCount * everyDishTable.getUnitPrice_dish());
                                customerMenu_super_rv_adapter.notifyItemChanged(position);
                            }
                        });
                        break;
                    case R.id.plus:
                        RxjavaUtil.doInUIThread(new UITask<String>() {
                            @Override
                            public void doInUIThread() {
                                int foodCount = everyDishTable.getFoodCount();
                                everyDishTable.setFoodCount(foodCount += 5);
                                float v1 = foodCount * everyDishTable.getTotalPrice_dish();
                                everyDishTable.setTotalPrice_dish(foodCount * everyDishTable.getUnitPrice_dish());
                                customerMenu_super_rv_adapter.notifyItemChanged(position);
                                Toast.makeText(context, "加", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case R.id.delete_view:
                        RxjavaUtil.doInUIThread(new UITask<String>() {
                            @Override
                            public void doInUIThread(){
                                showDig();
                                Toast.makeText(context, "删",Toast.LENGTH_SHORT).show();
                            }
                            private void showDig() {
                                MyDialog.Builde builde = new MyDialog.Builde(context);
                                builde.setMessage("您确定清除【" + everyDishTable.getFoodname() + "】？");
                                builde.setTitle("提示");
                                builde.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builde.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                     if (everyDishTable.getItemType()==2){
                                         customerMenu_super_rv_adapter.remove(position);
                                         customerMenu_super_rv_adapter.notifyItemChanged(position);
                                     }else{
                                        SQLiteDatabase database = LitePal.getDatabase();
                                        database.beginTransaction();
                                        try{
                                            EveryDishTable everyDT = customerMenu_super_rv_adapter.getData().get(position);
                                            if (everyDT.isSaved()){
                                                everyDT.delete();
                                            }
                                            float s = everyDKTbean.getTotalPrice_desk() - everyDT.getTotalPrice_dish();
                                            everyDKTbean.setTotalPrice_desk(s);
                                            everyDKTbean.save();
                                            customerMenu_super_rv_adapter.remove(position);
                                            customerMenu_super_rv_adapter.notifyItemChanged(position);
                                            mNowPrice.setText(s+"");
//                                            int a = 1/0;
                                            database.setTransactionSuccessful();
                                        }catch (Exception e){
                                            Log.i("FANJAVA", "异常了");
                                        }finally {
                                            database.endTransaction();
                                        }
                                     }
                                    }
                                });
                                builde.create().show();
                            }
                        });
                        break;
                    case R.id.add_menu:
                        RxjavaUtil.doInUIThread(new UITask<String>() {
                            @Override
                            public void doInUIThread() {
                                SQLiteDatabase database = LitePal.getDatabase();
                                database.beginTransaction(); //开始
                                try{
                                    everyDishTable.setItemType(1);
                                    everyDishTable.save();//单条菜保存;
                                    customerMenu_super_rv_adapter.remove(position);
                                    float s = everyDishTable.getTotalPrice_dish() + everyDKTbean.getTotalPrice_desk();
                                    everyDKTbean.getEveryDishTableList().add(everyDishTable);
                                    everyDKTbean.setTotalPrice_desk(s);
                                    everyDKTbean.save();
                                    customerMenu_super_rv_adapter.notifyItemChanged(position);
                                    mNowPrice.setText(s + "元");
                                    Toast.makeText(context, "加", Toast.LENGTH_SHORT).show();
                                    database.setTransactionSuccessful(); //中间不出错就成功
                                }catch (Exception e){
                                    Log.i("FANJAVA", "异常了");
                                }finally {
                                    database.endTransaction(); // 事物结束
                                }
                            }
                        });
                        break;
                    case R.id.slidingContentView:
                        SlidingItemLayout parent = (SlidingItemLayout) view.getParent();
                        SlidingContentView slidingContentView = (SlidingContentView)view;
                        SlidingItemLayout.SlidingStatus currentStaus = parent.getCurrentStaus();
                        if (currentStaus== SlidingItemLayout.SlidingStatus.Close){
                            parent.open();
                        }
                        Toast.makeText(context, view.getId()+"|||||"+view.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.up_food:
                        String isfinish = everyDishTable.getIsfinish();
                        if (isfinish.equals("0")){
                            everyDishTable.setIsfinish("1");
                            everyDishTable.save();//单条菜保存;
                            customerMenu_super_rv_adapter.notifyItemChanged(position);
                        }else if (isfinish.equals("1")){
                            everyDishTable.setIsfinish("0");
                            everyDishTable.save();//单条菜保存;
                            customerMenu_super_rv_adapter.notifyItemChanged(position);
                        }
                        break;
                    case R.id.remark_bt:
                        Toast.makeText(context, "加备注", Toast.LENGTH_SHORT).show();
                        showpopu(remark_tv,everyDishTable);
                        break;
                }
            }
        });
    }

    //初始化Toolbar
    public void initNavigation() {
        ChatNavigation.Builder homeBuilder = new ChatNavigation.Builder(this, parentLayout);
        homeBuilder.setTitleRes("顾客餐单详情");
        homeBuilder.setLeftImageLeftRes(R.mipmap.ic_callback1).setLeftImageLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        homeBuilder.setRightImageLeftRes(R.drawable.menu_share_select).setRightImageLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //纸飞机
              String  staffID = SPUtils.getInstance().getString("staffID", "0");
               Intent i =new Intent(context,KitchenActivity.class);
                Gson gson = new Gson();
                String menuData = gson.toJson(everyDKTbean);
                i.putExtra("staffID",staffID);
               i.putExtra("menuData",menuData);
               startActivity(i);
                Toast.makeText(context, "去复制去分享", Toast.LENGTH_SHORT).show();
            }
        });
        homeBuilder.setRightImageRightRes(R.drawable.menu_clear_select).setRightImageRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_the_order();
            }
        });

        homeBuilder.builder().build(); //builder是组装  build是创建
        AdaptationStatusbar();
    }

    //删除这张餐单
    private void delete_the_order() {
        Toast.makeText(context, "删除此餐单", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_remind2).setTitle("重要提示").setMessage("您是否要删除此菜单全部数据")
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (everyDKTbean.isSaved()){
                    everyDKTbean.delete();
                  }
                dialog.dismiss();
                finish();
            }
        }).show();
    }
    //此餐单结账
    private void the_order_checkout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_check_1).setTitle("结账").setMessage("您是现在要结账吗？")
                .setNegativeButton("等等",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("嗯，现在",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                everyDKTbean.setEndBillTime(new Date());
                everyDKTbean.setIsCheckout("1");
                if (everyDKTbean.save()){
                    Toast.makeText(context, "结账成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
//                    Date endBillTime;  //每桌结账时间
//                    @Column(defaultValue = "0")//指定字段默认值 0 代表没打样 ，1表示打过样的记录
//                    private String isCheckout; //买单结账
//                    @Column(defaultValue = "0")//指定字段默认值 0 代表没打样 ，1表示打过样的记录
//                    private String isEndwork; //打烊了么
                dialog.dismiss();
            }
        }).show();
    }

    //计算状态栏
    private void AdaptationStatusbar() {
        int statusheight = WindowUtils.getStatusHeight(this);
        LinearLayout layout = (LinearLayout) parentLayout.getChildAt(0);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
        layoutParams.height += statusheight;
        layout.setLayoutParams(layoutParams);
        layout.setPadding(0, layout.getPaddingTop() + statusheight, 0, 0);
    }
    //打开PopuWindow 修改 列表
    private void showpopu(TextView textView, EveryDishTable everyDishTable){
        addRemark_popupWindow = new AddRemark_PopupWindow(context,textView,everyDishTable);
        addRemark_popupWindow.setAnimationStyle(R.style.mine_popupwindow_anim);//设置出现的动画
        addRemark_popupWindow.showAsDropDown(mImgCheckOut,0,0);//设置显示位置
        mWindowUtil.lightoff(this);
        addRemark_popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mWindowUtil.lightOn((Activity) context);
            }
        });
    }
}
