/**
 * 
 */
package com.valleau.pierre.irc.client.test;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.valleau.pierre.irc.client.ClientCmdine;

import junit.framework.TestCase;

/**
 * @author Pierre Valleau
 *
 */
public class ChannelTest  {

	static ClientCmdine irc;
	static ClientCmdine irc2;
	static String server   = "irc.swiftirc.net";//"localhost";//
	static int port        = 6667;

	 @BeforeClass
	    public static void beforeAllTestMethods() {
	        System.out.println("Invoked once before all test methods");
	   
		
	//	com.francisbailey.irc.Main(new String[0]);
		
		 irc=new ClientCmdine(server,port);
		 //  irc.connect(server,port);
		irc.login("MyNickName","chatterBot");
				
		
		 irc2=new ClientCmdine(server,port);
		irc2.login("MyNickName2","chatterBot2");
		waitms(200);
		 while ( irc.isAnswer()) {
             System.out.println("IRC1:"+irc.getAnswer());
         }
		 while ( irc2.isAnswer()) {
             System.out.println("IRC2:"+irc2.getAnswer());
         }
	}
	 
	 
	 private static void waitms(int i) {
		 try {
				TimeUnit.MILLISECONDS.sleep(i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}


	@Before
	    public void beforeEachTestMethod() {
	        System.out.println("Invoked before each test method");
	    }
	 
	    @After
	    public void afterEachTestMethod() {
	        System.out.println("Invoked after each test method");
	    }
	    
	    
	@Test
	  public void testnull() {
		 System.out.println("Invoked for testnull");
		    assertNotNull("L'instance n'est pas créée", irc);
		  }

	/**
	 * @throws java.lang.Exception
	 */
	 @AfterClass
    public static void afterAllTestMethods() {
		 System.out.println("Invoked once after all test methods");
		 irc.exit();
		irc2.exit();
	 }

	@Test
	public void test() {
		 System.out.println("Invoked on test");
	//	 irc.flushAnswer();
		// irc2.flushAnswer();
		 System.out.println(">");
		 waitms(1000);
		 while ( irc.isAnswer()) {
             System.out.println("IRC1:"+irc.getAnswer());
         }
		 while ( irc2.isAnswer()) {
             System.out.println("IRC2:"+irc2.getAnswer());
         }
		 System.out.println(":");
		 irc.open("#test-irc-channel");
/*		 assertEquals(":MyNickName!chatterBot@127.0.0.1 JOIN #test-irc-channel ", irc.getAnswer()); 
		 assertEquals(":irc.javaircserver.com 366 MyNickName #test-irc-channel :End of NAMES list ", irc.getAnswer()); 
		 assertEquals(":MyNickName2!chatterBot2@127.0.0.1 JOIN #test-irc-channel ", irc.getAnswer()); 
		 assertEquals(":irc.javaircserver.com 332 MyNickName #test-irc-channel :Channel created by MyNickName ", irc.getAnswer()); 
	*/	 irc.message("hello world");
	//	 assertEquals(":irc.javaircserver.com 353 MyNickName = #test-irc-channel :MyNickName ", irc.getAnswer()); 
		 irc2.open("#test-irc-channel");
		 irc2.message("hello world");
		 waitms(1000);
		 System.out.println(">");
		 while ( irc.isAnswer()) {
             System.out.println("IRC1:"+irc.getAnswer());
         }
		 while ( irc2.isAnswer()) {
             System.out.println("IRC2:"+irc2.getAnswer());
         }
		 System.out.println(":");
		 assertEquals(":irc.javaircserver.com 353 * = #test-irc-channel :* ", irc.getAnswer()); 
			 
	}

}
