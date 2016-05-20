package com.schytd.discount.ui.switchbtn.rebound;

public abstract class SpringLooper {

  protected BaseSpringSystem mSpringSystem;

  public void setSpringSystem(BaseSpringSystem springSystem) {
    mSpringSystem = springSystem;
  }

  public abstract void start();

  /**
   * The looper will no longer run the {@link Runnable}.
   */
  public abstract void stop();
}