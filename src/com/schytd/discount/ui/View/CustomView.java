package com.schytd.discount.ui.View;

import com.schytd.discount_android.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CustomView extends LinearLayout{
	
	
	final static String MATERIALDESIGNXML = "http://schemas.android.com/apk/res-auto";
	final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";
	
	final int disabledBackgroundColor = Color.parseColor("#E2E2E2");
	int beforeBackground;
	public Context context;
	// Indicate if user touched this view the last time
	public boolean isLastTouch = false;

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(enabled)
			setBackgroundColor(beforeBackground);
		else
			setBackgroundColor(disabledBackgroundColor);
		invalidate();
	}
	
	boolean animation = false;
	
	@Override
	protected void onAnimationStart() {
		super.onAnimationStart();
		animation = true;
	}
	
	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();
		animation = false;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(animation)
			invalidate();
	}
}
