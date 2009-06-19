package org.vo.urp.test.mytests;

import java.util.Date;

public class ThreadLocalDemo extends Thread {

  /** A serial number for clients */
  private static int clientNum = 0;

  /** This ThreadLocal holds the Client reference for each Thread */
  private static ThreadLocal myClient;

  public void run() {
    myClient = new ThreadLocal() {
      // The initialValue() method is called magically when you call get().
      protected synchronized Object initialValue() {
        return new Client(clientNum++);
      }
    };
    System.out.println("Thread " + Thread.currentThread().getName() +
      " has client " + myClient.get());
  }

  public static void main(String[] args) {
    Thread t1 = new ThreadLocalDemo();
    Thread t2 = new ThreadLocalDemo();
    Thread t3 = new ThreadLocalDemo();
    t1.start();
    t2.start();
    t3.start();
    
    System.out.println("main:"+myClient);
  }

  /** Simple data class, in real life clients would have more fields! */
  private static class Client {

    private int clNum;

    private Date t;
    Client(int n) {
      clNum = n;
      t = new Date();
    }

    public String toString() {
      return "Client[" +t.getTime()+","+ clNum + "]";
    }
  }
}