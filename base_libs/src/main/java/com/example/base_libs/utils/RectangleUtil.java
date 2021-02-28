package com.example.base_libs.utils;

import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.example.base_libs.R;


public class RectangleUtil {
    /***
     * 根据自定义属性设置背景
     * @param attrs
     * @param view
     */
    public static void setBackgroundByAttributeSet(AttributeSet attrs, View view) {
        TypedArray ta = view.getContext().obtainStyledAttributes(attrs, R.styleable.RectangleLayout);
        //填充色
        int solidColor = ta.getColor(R.styleable.RectangleLayout_solidColor, 0);
        //填充色 渐变开始颜色
        int solidStartColor = ta.getColor(R.styleable.RectangleLayout_solidStartColor, 0);
        //填充色 渐变结束颜色
        int solidEndColor = ta.getColor(R.styleable.RectangleLayout_solidEndColor, 0);
        //填充色 渐变方向
        int solidOrientation = ta.getInt(R.styleable.RectangleLayout_solidOrientation, 0);
        //边框色
        int strokeColor = ta.getColor(R.styleable.RectangleLayout_strokeColor, 0);
        //边框宽度
        float strokeWidth = ta.getDimension(R.styleable.RectangleLayout_strokeWidth, 0f);
        //圆角，优先级最高
        float radius = ta.getDimension(R.styleable.RectangleLayout_radius, 0f);
        //圆角-左上
        float topLeftRadius = ta.getDimension(R.styleable.RectangleLayout_topLeftRadius, 0f);
        //圆角-右上
        float topRightRadius = ta.getDimension(R.styleable.RectangleLayout_topRightRadius, 0f);
        //圆角-左下
        float bottomLeftRadius = ta.getDimension(R.styleable.RectangleLayout_bottomLeftRadius, 0f);
        //圆角-右下
        float bottomRightRadius = ta.getDimension(R.styleable.RectangleLayout_bottomRightRadius, 0f);

        if (radius != 0f) {
            topLeftRadius = radius;
            topRightRadius = radius;
            bottomLeftRadius = radius;
            bottomRightRadius = radius;
        }
        //处理渐变
        GradientDrawable.Orientation orientation = null;
        if (solidColor == 0 && solidStartColor != 0 && solidEndColor != 0) {
            /***
             * <enum name="TOP_BOTTOM" value="1" />
             * <enum name="TR_BL" value="2" />
             * <enum name="RIGHT_LEFT" value="3" />
             * <enum name="BR_TL" value="4" />
             * <enum name="BOTTOM_TOP" value="5" />
             * <enum name="BL_TR" value="6" />
             * <enum name="LEFT_RIGHT" value="7" />
             * <enum name="TL_BR" value="8" />
             */
            switch (solidOrientation) {
                case 1:
                    orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                    break;
                case 2:
                    orientation = GradientDrawable.Orientation.TR_BL;
                    break;
                case 3:
                    orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                    break;
                case 4:
                    orientation = GradientDrawable.Orientation.BR_TL;
                    break;
                case 5:
                    orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                    break;
                case 6:
                    orientation = GradientDrawable.Orientation.BL_TR;
                    break;
                case 7:
                    orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                    break;
                case 8:
                    orientation = GradientDrawable.Orientation.TL_BR;
                    break;
                default:
                    orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                    break;
            }
        } else {
            solidStartColor = solidColor;
            solidEndColor = solidColor;
        }
        view.setBackground(getDrawable(GradientDrawable.RECTANGLE, new int[]{solidStartColor, solidEndColor}, orientation, strokeColor, (int) strokeWidth, topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius));
    }

    /***
     * 获取Drawable
     * @param gradientType 形状
     * @param solidColor 填充色
     * @param orientation 渐变方向参照GradientDrawable.Orientation
     * @param strokeColor 边框的颜色
     * @param strokeWidth 边框宽度
     * @param topLeftRadius 左上，右上，右下，左下
     */
    public static GradientDrawable getDrawable(int gradientType, int[] solidColor, GradientDrawable.Orientation orientation, int strokeColor, int strokeWidth, float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
        GradientDrawable drawable = new GradientDrawable();
        //设置形状
        drawable.setGradientType(gradientType);
        //设置填充色
        drawable.setColors(solidColor);
        //渐变方向
        if (orientation != null) {
            drawable.setOrientation(orientation);
        }
        //设置边框
        if (strokeWidth != 0 && strokeColor != 0) {
            drawable.setStroke(strokeWidth, strokeColor);
        }
        //设置圆角
        if (topLeftRadius != 0f || topRightRadius != 0f || bottomRightRadius != 0f || bottomLeftRadius != 0f) {
            drawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
        }
        return drawable;
    }

    public static GradientDrawable solid(int color, int radius) {
        int[] colors = new int[]{color, color};
        return RectangleUtil.getDrawable(
                GradientDrawable.RECTANGLE,
                colors,
                GradientDrawable.Orientation.LEFT_RIGHT,
                0,
                0,
                radius, radius, radius, radius
        );
    }
}
