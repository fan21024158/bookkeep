package com.wlw.bookkeeptool.tableBean;

import litepal.annotation.Column;
import litepal.crud.LitePalSupport;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 每一桌菜的记录（每一桌结账就会记录）
 */
public class everyDeskTable  extends LitePalSupport {
   @Column(unique = true)
   private int id;
   String username;
   String deskNum;   //顾客所在的桌子号
   String totalPrice_desk; // 这一桌的总价
   Date startBillTime; //下单时间
   Date endBillTime;  //每桌结账时间
   private List<everyDishTable> everyDeskTableList = new ArrayList<everyDishTable>(); //everyDishTable 表 与 everyDeskTable 表  是 多对一的关系

   public String getUsername() {
      return username == null ? "" : username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getDeskNum() {
      return deskNum == null ? "" : deskNum;
   }

   public void setDeskNum(String deskNum) {
      this.deskNum = deskNum;
   }

   public String getTotalPrice_desk() {
      return totalPrice_desk == null ? "" : totalPrice_desk;
   }

   public void setTotalPrice_desk(String totalPrice_desk) {
      this.totalPrice_desk = totalPrice_desk;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public Date getStartBillTime() {
      return startBillTime;
   }

   public void setStartBillTime(Date startBillTime) {
      this.startBillTime = startBillTime;
   }

   public Date getEndBillTime() {
      return endBillTime;
   }

   public void setEndBillTime(Date endBillTime) {
      this.endBillTime = endBillTime;
   }

   public List<everyDishTable> getEveryDeskTableList() {
      if (everyDeskTableList == null) {
         return new ArrayList<>();
      }
      return everyDeskTableList;
   }

   public void setEveryDeskTableList(List<everyDishTable> everyDeskTableList){
      this.everyDeskTableList = everyDeskTableList;
   }
}