package com.example.project.keywordnews;

import android.graphics.drawable.Drawable;

/**
 * Created by SCIT on 2018-03-04.
 */

public class ListViewItem {

    private String imgStr;
    private String titleStr;
    private String descStr;


    public String getImgStr() {return imgStr;}
    public String getTitleStr() {
        return this.titleStr ;
    }
    public String getDescStr() {
        return this.descStr ;
    }
    public void setImgStr(String imgStr) {this.imgStr = imgStr;}
    public void setTitleStr(String title) {
        titleStr = title ;
    }
    public void setDescStr(String desc) {
        descStr = desc ;
    }

    public ListViewItem() {}

    @Override
    public String toString() {
        return "ListViewItem{" +
                "imgStr='" + imgStr + '\'' +
                ", titleStr='" + titleStr + '\'' +
                ", descStr='" + descStr + '\'' +
                '}';
    }

    //출처: http://recipes4dev.tistory.com/43 [개발자를 위한 레시피]
}
