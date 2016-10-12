package com.vietdms.mobile.dmslauncher.CustomClass;

import android.view.ViewGroup;

import com.vietdms.mobile.dmslauncher.Fragment.RightFragment;


/**
 * Created by Cuongph on 27/05/2016.
 */
public class LayoutManagerObject {
    public RightFragment.Layouts LName;
    public ViewGroup LayoutView;
    public LayoutManagerObject(RightFragment.Layouts name, ViewGroup layout)
    {
        this.LName = name;
        this.LayoutView = layout;
    }
}
