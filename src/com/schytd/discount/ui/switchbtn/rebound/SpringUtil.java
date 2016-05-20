
package com.schytd.discount.ui.switchbtn.rebound;

public class SpringUtil {

  public static double mapValueFromRangeToRange(
      double value,
      double fromLow,
      double fromHigh,
      double toLow,
      double toHigh) {
    double fromRangeSize = fromHigh - fromLow;
    double toRangeSize = toHigh - toLow;
    double valueScale = (value - fromLow) / fromRangeSize;
    return toLow + (valueScale * toRangeSize);
  }

  /**
   * Clamp a value to be within the provided range.
   * @param value the value to clamp
   * @param low the low end of the range
   * @param high the high end of the range
   * @return the clamped value
   */
  public static double clamp(double value, double low, double high) {
    return Math.min(Math.max(value, low), high);
  }
  
  public static int clamp(int value, int low, int high) {
	    return Math.min(Math.max(value, low), high);
	  }
  
  
}

