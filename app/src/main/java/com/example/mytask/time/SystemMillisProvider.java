package com.example.mytask.time;

public class SystemMillisProvider implements MillisProvider {

  @Override
  public long getMillis() {
    return System.currentTimeMillis();
  }
}
