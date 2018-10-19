package yzq.com.myapplication.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

import yzq.com.myapplication.R;

public class MyView extends RelativeLayout {
    private String[] texts = new String[]{"帅","酷","猛","萌","可爱","屌","吊炸天","英俊","潇洒","无敌"};
    private LayoutParams layoutParams;
    /*屏幕宽度*/
    private int screenWidth;
    /*屏幕高度*/
    private int screenHeight;
    public MyView(Context context) {
        super(context);
        init(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
  // 创建图片  显示在底部
    private void init(Context context) {
        layoutParams = new LayoutParams(300,200);
        layoutParams.addRule(CENTER_HORIZONTAL,TRUE);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM,TRUE);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight= wm.getDefaultDisplay().getHeight();

    }
    //随机添加图片
    public void addImageView(){
        Random random =new Random();
        for (int i=0;i<random.nextInt(10);i++){
            TextView  textView = new  TextView(getContext());
            textView.setLayoutParams(layoutParams);
            textView.setText(texts[(int) (Math.random()*texts.length)]);
            String r = Integer.toHexString(random.nextInt(256)).toUpperCase();
            String g = Integer.toHexString(random.nextInt(256)).toUpperCase();
            String b = Integer.toHexString(random.nextInt(256)).toUpperCase();
            r = r.length()==1 ? "0" + r : r ;
            g = g.length()==1 ? "0" + g : g ;
            b = b.length()==1 ? "0" + b : b ;
            textView.setTextColor(Color.parseColor("#" + r + g + b));
            textView.setTextSize(50);
            addView(textView);
            setAnim(textView).start();
            getBezierValueAnimator(textView).start();
        }

    }
    //放大动画
    private AnimatorSet setAnim(View view){
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.2f, 1f);

        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(3000
        );
        enter.setInterpolator(new LinearInterpolator());//线性变化
        enter.playTogether(scaleX,scaleY);
        enter.setTarget(view);
        return enter;
    }
    private ValueAnimator getBezierValueAnimator(View target) {

        //初始化一个贝塞尔计算器- - 传入
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(),getPointF());

        //这里最好画个图 理解一下 传入了起点 和 终点
        PointF randomEndPoint = new PointF((float) (Math.random()*screenWidth), screenHeight/2);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF((float) (Math.random()*screenWidth), screenHeight), randomEndPoint);
        animator.addUpdateListener(new BezierListener(target));
        animator.setTarget(target);
        animator.setDuration(3000);

        return animator;
    }
    private PointF getPointF() {
        PointF pointF = new PointF();
        Random random =new Random();
        pointF.x = random.nextInt((screenWidth - 200));
        //再Y轴上 为了确保第二个控制点 在第一个点之上,我把Y分成了上下两半
        pointF.y = random.nextInt((screenHeight - 200)) / 2;
        return pointF;
    }
    private class BezierListener implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            // 这里顺便做一个alpha动画
            target.setAlpha(1 - animation.getAnimatedFraction());
            if (1- animation.getAnimatedFraction()==0){
               removeView(target);
            }

        }
    }
    /*自定义估值器*/
    public class BezierEvaluator implements TypeEvaluator<PointF> {


        private PointF pointF1;
        private PointF pointF2;
        public BezierEvaluator(PointF pointF1,PointF pointF2){
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }
        @Override
        public PointF evaluate(float time, PointF startValue,
                               PointF endValue) {

            float timeLeft = 1.0f - time;
            PointF point = new PointF();//结果
            //3阶贝塞尔曲线

            point.x = timeLeft * timeLeft * timeLeft * (startValue.x)
                    + 3 * timeLeft * timeLeft * time * (pointF1.x)
                    + 3 * timeLeft * time * time * (pointF2.x)
                    + time * time * time * (endValue.x);

            point.y = timeLeft * timeLeft * timeLeft * (startValue.y)
                    + 3 * timeLeft * timeLeft * time * (pointF1.y)
                    + 3 * timeLeft * time * time * (pointF2.y)
                    + time * time * time * (endValue.y);
            return point;
        }
    }
}
