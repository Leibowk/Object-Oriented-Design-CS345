package main;

import graphical.WindowController;

public class Deadwood {

  static WindowController application;

  public static void main(String[] args) {
    application = new WindowController();
    application.init(args);
  }
}
