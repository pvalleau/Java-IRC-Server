package com.valleau.pierre.irc.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

class IOThread extends Thread {
	private BufferedReader streamReader;
	    private int t;

	    public IOThread(InputStream is, int t)  {
	        this.t = t;
	        System.out.println("iothread<" + t + ">.init");
	         streamReader = new BufferedReader(new InputStreamReader(is));
	           
	    }
	    public IOThread(BufferedReader br, int t)  {
	        this.t = t;
	        System.out.println("iothread<" + t + ">.init");
	         streamReader = br;
	           
	    }

	    public void run() {
	        try {
	            System.out.println("iothread<" + t + ">.run");
	            String line;

	            while ((line = streamReader.readLine()) != null) {
	                System.out.println("iothread<" + t + "> got line " + line);
	            }
	            System.out.println("iothread " + t + " end run");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}