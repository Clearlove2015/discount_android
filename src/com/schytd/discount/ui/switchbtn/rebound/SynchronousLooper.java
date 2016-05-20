
package com.schytd.discount.ui.switchbtn.rebound;

public class SynchronousLooper extends SpringLooper {

  public static double SIXTY_FPS = 16.6667;
  private double mTimeStep;
  private boolean mRunning;

  public SynchronousLooper() {
    mTimeStep = SIXTY_FPS;
  }

  public double getTimeStep() {
    return mTimeStep;
  }

  public void setTimeStep(double timeStep) {
    mTimeStep = timeStep;
  }

  @Override
  public void start() {
    mRunning = true;
    while (!mSpringSystem.getIsIdle()) {
      if (mRunning == false) {
        break;
      }
      mSpringSystem.loop(mTimeStep);
    }
  }

  @Override
  public void stop() {
    mRunning = false;
  }
}

