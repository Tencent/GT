package com.tencent.wstt.gt.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import com.tencent.wstt.gt.dao.DetailPointData;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 滚动的折线图
 * Created by p_hongjcong on 2017/8/28.
 */

public class ScrollLineChartView extends View{

    //风格设置：
    int backGroundColor = Color.parseColor("#00000000");//背景颜色
    int axisColor = Color.WHITE;//坐标轴颜色
    int dataLineColor =Color.YELLOW;//数据线的颜色

    //基本属性：
    String x_name = "";//X坐标名称
    String y_name = "";//Y坐标名称
    ArrayList<DetailPointData> lineDatas = new ArrayList<>();

    //更多设置：
    Double x_minValue; //X坐标数据最小值
    Double x_maxValue; //X坐标数据最大值
    Double y_minValue; //Y坐标数据最小值
    Double y_maxValue; //Y坐标数据最大值
    int x_capacity = 30; //X轴容量
    double yStep =0.0;//Y轴的刻度间隔，此值是通过计算得出的

    //滑动会改变显示数据的范围
    int nowEndData = -1;//-1则表示最后的那一段数据。

    /**
     * 构造函数：
     * @param context
     */
    public ScrollLineChartView(Context context) {
        super(context);
    }

    /**
     * 绘制函数
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景颜色
        setBackgroundColor(backGroundColor);
        //坐标轴画笔
        Paint axisPaint = new Paint();
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(2);
        axisPaint.setAntiAlias(true); //去锯齿
        axisPaint.setTextSize(20);
        axisPaint.setColor(axisColor);
        //数据画笔
        Paint dataPaint = new Paint();
        dataPaint.setStyle(Paint.Style.STROKE);
        dataPaint.setStrokeWidth(2);
        dataPaint.setAntiAlias(true); //去锯齿
        dataPaint.setColor(dataLineColor);

        //计算宽高、边距、箭头偏移量
        float height = getMeasuredHeight();
        float width = getMeasuredWidth();
        float margin = (float) ((height<width?height:width) * 0.1);
        float arrow_offset = (float) ((height<width?height:width) * 0.02);

        //绘制X轴+箭头+name
        canvas.drawLine(margin, height-margin, width-margin, height-margin, axisPaint);
        canvas.drawLine(width-margin, height-margin, width-margin-arrow_offset, height-margin-arrow_offset, axisPaint);
        canvas.drawLine(width-margin, height-margin, width-margin-arrow_offset, height-margin+arrow_offset ,axisPaint);
        canvas.drawText(x_name==null?"":x_name, width-margin-margin/2, height-margin/2, axisPaint);//文字

        //绘制Y轴和箭头+name
        canvas.drawLine(margin, height-margin, margin, margin, axisPaint);
        canvas.drawLine(margin, margin, margin-arrow_offset, margin+arrow_offset, axisPaint);
        canvas.drawLine(margin, margin, margin+arrow_offset, margin+arrow_offset ,axisPaint);
        canvas.drawText(y_name==null?"":y_name, margin/2, margin/2, axisPaint);//文字

        //绘制数据和刻度：
        if(y_minValue!=null&&y_maxValue!=null&&!y_maxValue.equals(y_minValue) && lineDatas.size()>0){
            //绘制Y轴刻度
            for(double d=y_minValue;d<=y_maxValue;d=d){
                float yLocal = (float) ((height-margin)-(d-y_minValue)*(height-2*margin)/(y_maxValue-y_minValue));//TODO 除0
                //canvas.drawLine(margin, yLocal, margin+20, yLocal, axisPaint);
                canvas.drawLine(margin, yLocal, width-margin, yLocal, axisPaint);
                canvas.drawText(""+d, margin/4, yLocal, axisPaint);//文字


                BigDecimal b1=new BigDecimal(Double.toString(d));
                BigDecimal b2=new BigDecimal(Double.toString(yStep));
                d=b1.add(b2).doubleValue();
            }
            Path path = new Path();
            if(lineDatas!=null&&lineDatas.size()>=2){
                for(int i=0;i<x_capacity;i++){
                    int id = -1;
                    if(nowEndData==-1){
                        id = lineDatas.size()-1-i;
                    }else {
                        id = nowEndData-i;
                    }
                    if (id<0 ||id>lineDatas.size()-1){
                        break;
                    }
                    DetailPointData point = lineDatas.get(id);
                    float xLocal = (width-margin)-(i+1)*(width-2*margin)/x_capacity;//TODO 除0
                    float yLocal = (float) ((height-margin)-(point.y-y_minValue)*(height-2*margin)/(y_maxValue-y_minValue));//TODO 除0
                    if(i==0){
                        path.moveTo(xLocal, yLocal);//最后一个点
                    }else {
                        path.lineTo(xLocal, yLocal);
                    }
                    //绘制X轴刻度：
                    double temp = ((double)id)/(x_capacity/5);
                    if(temp ==(int)temp){
                        canvas.drawLine(xLocal, height-margin,xLocal, height-margin-20, axisPaint);
                        canvas.drawText(""+point.x, xLocal, height-margin/2, axisPaint);//文字
                    }
                }
            }
            //绘制数据线
            canvas.drawPath(path, dataPaint);
        }


    }


    /**
     * 滑动事件处理函数
     * @param event
     * @return
     */
    int lastX = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(nowEndData==-1){
                    nowEndData = lineDatas.size()-1;
                }
                lastX = (int)event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                //计算宽高、边距、箭头偏移量
                float height = getMeasuredHeight();
                float width = getMeasuredWidth();
                float margin = (float) ((height<width?height:width) * 0.1);
                int dataInterval = (int)(width-2*margin)/x_capacity;
                //进行滑动
                int x = (int)event.getRawX();
                int temp = ((x-lastX)/dataInterval);
                nowEndData = nowEndData -temp;
                lastX = lastX + temp * dataInterval;
                if(nowEndData<0){
                    nowEndData=0;
                }else if(nowEndData>lineDatas.size()-1){
                    nowEndData=lineDatas.size()-1;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if(nowEndData>lineDatas.size()-3){
                    nowEndData =-1;
                }
                break;
        }
        return true;
    }


    //风格设置：
    public void setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
    }
    public int getBackGroundColor() {
        return backGroundColor;
    }
    public void setAxisColor(int axisColor){
        this.axisColor = axisColor;
    }
    public int getAxisColor() {
        return axisColor;
    }
    public int getDataLineColor() {
        return dataLineColor;
    }
    public void setDataLineColor(int dataLineColor) {
        this.dataLineColor = dataLineColor;
    }

    //基本属性：
    public String getX_name() {
        return x_name;
    }
    public void setX_name(String x_name) {
        this.x_name = x_name;
    }
    public String getY_name() {
        return y_name;
    }
    public void setY_name(String y_name) {
        this.y_name = y_name;
    }
    public void addData(DetailPointData detailPointData){
        synchronized (lineDatas){
            checkData(detailPointData);//检查数据的值是否超过数据上下限
            lineDatas.add(detailPointData);
            invalidate();//刷新
        }
    }
    public void setDatas(ArrayList<DetailPointData> datas){
        synchronized (lineDatas){
            lineDatas.clear();
            for (int i=0;datas!=null&&i<datas.size();i++){
                checkData(datas.get(i));//检查数据的值是否超过数据上下限
                lineDatas.add(datas.get(i));
            }
            invalidate();//刷新
        }
    }
    void checkData(DetailPointData data){
        if (data==null){
            return;
        }
        if(x_minValue==null||data.x<x_minValue){
            x_minValue = data.x;
        }
        if(x_maxValue==null||data.x>x_maxValue){
            x_maxValue = data.x;
        }
        if(y_minValue==null||data.y<y_minValue){
            y_minValue = data.y;
        }
        if(y_maxValue==null||data.y>y_maxValue){
            y_maxValue = data.y;
        }
        int heightLocal = getHeighterLocalOfDouble(y_maxValue-y_minValue);
        double y_step = Math.pow(10,heightLocal-1);
        int num = (int)((y_maxValue-y_minValue)/y_step);
        yStep = ((int)(num/5))*y_step;
        double temp = y_minValue/y_step;
        if (temp!=(int)temp){
            y_minValue = y_step*((int)(y_minValue/y_step));
        }
        temp = y_maxValue/y_step;
        if (temp!=(int)temp){
            y_maxValue = y_step*((int)(y_maxValue/y_step+1));
        }
    }

    //更多设置：
    public void setX_minValue(Double x_minValue) {
        this.x_minValue = x_minValue;
    }
    public void setX_maxValue(Double x_maxValue) {
        this.x_maxValue = x_maxValue;
    }
    public void setY_minValue(Double y_minValue) {
        this.y_minValue = y_minValue;
    }
    public void setY_maxValue(Double y_maxValue) {
        this.y_maxValue = y_maxValue;
    }
    public Double getX_minValue() {
        return x_minValue;
    }
    public Double getX_maxValue() {
        return x_maxValue;
    }
    public Double getY_minValue() {
        return y_minValue;
    }
    public Double getY_maxValue() {
        return y_maxValue;
    }

    public int getX_capacity() {
        return x_capacity;
    }

    public void setX_capacity(int x_capacity) {
        this.x_capacity = x_capacity;
    }

    int getHeighterLocalOfDouble(double f){
        f = Math.abs(f);
        int local;
        if (f>=1){
            local=0;
            f=f/10;
            while (f>=1){
                local++;
                f=f/10;
            }
        }else {
            local=-1;
            f=f*10;
            while (f<0){
                local--;
                f=f*10;
            }
        }
        return local;
    }

}
