package com.gwp.util;

/**
 * Title:        Mutable Integer Class
 * Description:
 * Copyright:    Copyright (c) 2004
 * Company:      Wavelink Corporation
 * @author Marcel von Gunten
 * @version 1.0
 */

public class IntValue {

  public int n;

  public IntValue(int x) { 
    n = x;
    }

  public String toString() { 
    return Integer.toString(n);
  }

  public int getValue() {
    return n;
  }

  public int add(int delta) {
    return (n += delta);
  }

  public int set(int newValue) {
    return (n = newValue);
  }



}

