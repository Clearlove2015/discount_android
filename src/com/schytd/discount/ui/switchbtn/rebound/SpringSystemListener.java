
package com.schytd.discount.ui.switchbtn.rebound;

public interface SpringSystemListener {

  void onBeforeIntegrate(BaseSpringSystem springSystem);

  /**
   * Runs after each pass through the physics integration loop providing an opportunity to do any
   * setup or alterations to the Physics state after integrating.
   * @param springSystem the BaseSpringSystem listened to
   */
  void onAfterIntegrate(BaseSpringSystem springSystem);
}

