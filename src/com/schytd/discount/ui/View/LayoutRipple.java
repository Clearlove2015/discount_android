package com.schytd.discount.ui.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.schytd.discount.enties.ConstantData;
import com.schytd.discount_android.R;

public class LayoutRipple extends CustomView {

	int background;
	float rippleSpeed = getResources().getDimension(R.dimen.x11);
	int rippleSize = 4;

	OnClickListener onClickListener;
	int backgroundColor = Color.parseColor("#000000");

	Integer rippleColor;
	Float xRippleOrigin;
	Float yRippleOrigin;
	int dpi = ConstantData.Dpi;

	// 根据不同屏幕设置大小不同的图片
	public float setSpeed(int densityDpi) {
		// 分辨率在480*800以下
		if (densityDpi < 240) {
			return 30f;
		}
		// 分辨率在480*800~720*1280区间
		else if (densityDpi >= 240 && densityDpi <= 320) {
			return 35f;
		}
		// 分辨率在720*1280以上
		else if (densityDpi > 320) {
			return 45f;
		} else {
			return 50f;
		}
	}
	public LayoutRipple(Context context, AttributeSet attrs) {
		super(context, attrs);
		setSpeed(dpi);
		setAttributes(context, attrs);
	}

	// Set atributtes of XML to View
	protected void setAttributes(Context context, AttributeSet attrs) {
		final TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.ripple);
		backgroundColor = typedArray.getColor(R.styleable.ripple_before_color,
				getResources().getColor(R.color.Indigo_nav_color));
		// Set background Color
		// Color by resource
		int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML,
				"background", -1);
		if (bacgroundColor != -1) {
			setBackgroundColor(getResources().getColor(bacgroundColor));
		} else {
			// Color by hexadecimal
			background = attrs.getAttributeIntValue(ANDROIDXML, "background",
					-1);
			if (background != -1)
				setBackgroundColor(background);
			else
				setBackgroundColor(this.backgroundColor);
		}
		// Set Ripple Color
		// Color by resource
		int rippleColor = attrs.getAttributeResourceValue(MATERIALDESIGNXML,
				"rippleColor", -1);
		if (rippleColor != -1) {
			setRippleColor(getResources().getColor(rippleColor));
		} else {
			// Color by hexadecimal
			int background = attrs.getAttributeIntValue(MATERIALDESIGNXML,
					"rippleColor", -1);
			if (background != -1)
				setRippleColor(background);
			else
				setRippleColor(makePressColor());
		}
		rippleSpeed = attrs.getAttributeFloatValue(MATERIALDESIGNXML,
				"rippleSpeed", setSpeed(dpi));
	}
	// Set color of background
	public void setBackgroundColor(int color) {
		this.backgroundColor = color;
		if (isEnabled())
			beforeBackground = backgroundColor;
		super.setBackgroundColor(color);
	}

	public void setRippleSpeed(int rippleSpeed) {
		this.rippleSpeed = rippleSpeed;
	}

	// ### RIPPLE EFFECT ###

	float x = -1, y = -1;
	float radius = -1;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		invalidate();
		if (isEnabled()) {
			isLastTouch = true;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				radius = getHeight() / rippleSize;
				x = event.getX();
				y = event.getY();
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				radius = getHeight() / rippleSize;
				x = event.getX();
				y = event.getY();
				if (!((event.getX() <= getWidth() && event.getX() >= 0) && (event
						.getY() <= getHeight() && event.getY() >= 0))) {
					isLastTouch = false;
					x = -1;
					y = -1;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if ((event.getX() <= getWidth() && event.getX() >= 0)
						&& (event.getY() <= getHeight() && event.getY() >= 0)) {
					radius++;
				} else {
					isLastTouch = false;
					x = -1;
					y = -1;
				}
			}
			if (event.getAction() == MotionEvent.ACTION_CANCEL) {
				isLastTouch = false;
				x = -1;
				y = -1;
			}
		}
		return true;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		if (!gainFocus) {
			x = -1;
			y = -1;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// super.onInterceptTouchEvent(ev);
		return true;
	}

	public Bitmap makeCircle() {
		Bitmap output = Bitmap.createBitmap(getWidth(), getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawARGB(0, 0, 0, 0);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		if (rippleColor == null)
			rippleColor = makePressColor();
		paint.setColor(rippleColor);
		x = (xRippleOrigin == null) ? x : xRippleOrigin;
		y = (yRippleOrigin == null) ? y : yRippleOrigin;
		canvas.drawCircle(x, y, radius, paint);
		if (radius > getHeight() / rippleSize)
			radius += rippleSpeed;
		if (radius >= getWidth()) {
			x = -1;
			y = -1;
			radius = getHeight() / rippleSize;
			if (onClickListener != null)
				onClickListener.onClick(this);
		}
		return output;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (x != -1) {
			Rect src = new Rect(0, 0, getWidth(), getHeight());
			Rect dst = new Rect(0, 0, getWidth(), getHeight());
			canvas.drawBitmap(makeCircle(), src, dst, null);
			invalidate();
		}
	}

	/**
	 * Make a dark color to ripple effect
	 * 
	 * @return
	 */
	protected int makePressColor() {
		int r = (this.backgroundColor >> 16) & 0xFF;
		int g = (this.backgroundColor >> 8) & 0xFF;
		int b = (this.backgroundColor >> 0) & 0xFF;
		r = (r - 30 < 0) ? 0 : r - 30;
		g = (g - 30 < 0) ? 0 : g - 30;
		b = (b - 30 < 0) ? 0 : b - 30;
		return Color.rgb(r, g, b);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		onClickListener = l;
	}

	public void setRippleColor(int rippleColor) {
		this.rippleColor = rippleColor;
	}

	public void setxRippleOrigin(Float xRippleOrigin) {
		this.xRippleOrigin = xRippleOrigin;
	}

	public void setyRippleOrigin(Float yRippleOrigin) {
		this.yRippleOrigin = yRippleOrigin;
	}

}
