
package com.schytd.discount.ui.switchbtn.rebound;

public interface SpringListener {

  void onSpringUpdate(Spring spring);

  void onSpringAtRest(Spring spring);

  void onSpringActivate(Spring spring);

  /**
   * called whenever the spring notifies of displacement state changes
   * @param spring the spring whose end state has changed
   */
  void onSpringEndStateChange(Spring spring);
}

