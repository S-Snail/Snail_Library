package com.example.base_libs.widgets;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.base_libs.utils.RectangleUtil;

/***
 * @author : Liumengran
 * @desc : 自定义矩形layout，代替下面样式
 *
 * <shape xmlns:android="http://schemas.android.com/apk/res/android"
 *        android:shape="rectangle"
 *        android:useLevel="true">
 *
 *     <stroke
 *           android:width="2dp"
 *           android:color="#000000" />
 *
 *     <solid android:color="#999999"/>
 *
 *     <corners android:radius="20dp"/>
 *
 * </shape>
 *
 * <com.yitlib.common.widgets.RectangleTextView
 *     app:solidColor="#E78548"
 *     app:strokeColor="#E78548"
 *     app:strokeWidth="0.5dp"
 *     app:radius="4dp"
 *     app:topLeftRadius="4dp"
 *     app:topRightRadius="4dp"
 *     app:bottomLeftRadius="4dp"
 *     app:bottomRightRadius="4dp"/>
 */
public class RectangleTextView extends AppCompatTextView {


    public RectangleTextView(Context context) {
        super(context);
        init(null);
    }

    public RectangleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RectangleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        RectangleUtil.setBackgroundByAttributeSet(attrs, this);
    }

    public void changeStyle(int solidColor, int strokeColor, int strokeWidth, float radius) {
        changeStyle(new int[]{solidColor, solidColor}, null, strokeColor, strokeWidth, radius, radius, radius, radius);
    }

    public void changeStyle(int solidColor, int strokeColor, int strokeWidth, float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
        changeStyle(new int[]{solidColor, solidColor}, null, strokeColor, strokeWidth, topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius);
    }

    /***
     * 代码更换样式
     * @param solidColors 填充色:开始颜色，中间夜色，结束颜色
     * @param orientation 渐变方向
     * @param strokeColor 边框的颜色
     * @param strokeWidth 边框宽度
     * @param topLeftRadius 左上，右上，右下，左下 单位:px
     */
    public void changeStyle(int[] solidColors, GradientDrawable.Orientation orientation, int strokeColor, int strokeWidth, float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
        setBackground(RectangleUtil.getDrawable(GradientDrawable.RECTANGLE, solidColors, orientation, strokeColor, strokeWidth, topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius));
    }
}
