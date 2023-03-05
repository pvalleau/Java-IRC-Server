/**
 * 
 */
package com.valleau.pierre.irc.client;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * @author Pierre Valleau
 * a very basic client to do debug in command line
 */
public class ClientCmdine {
	

	/**
	 * 
	 */
	public ClientCmdine() {
		// TODO Auto-generated constructor stub
	}
	public ClientCmdine(String server,int port) {
		this();
		 connect(server,port);
			
	}

	public ClientCmdine(String server,int port,String user,String nickname,String channel) {
		 this(server,port);
		 login(nickname,user);
		 open(channel);	
	}
  
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		 try {

			 ClientCmdine irc=new ClientCmdine();
			 irc.CreateChannelCountryLanguage();
		      String server   = "localhost";//"irc.europnet.org";//
		      int port        = 6667;
		   // creating an object of Random class   
		      Random random = new Random();
		      int id=random.nextInt(10000);
		      String nickname = "bot-r"+id;
		      String channel  = "#bot-test-ch";
		      String message  = "hi, all";
 
		      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		      irc.connect(server,port);
				  
		      irc.login(nickname,"chatterBot");
				 irc.open(channel);
				 irc.message(message);
				 
		        IOThread it1 = new IOThread(irc.getInReader(),id);
			      it1.start();
			
			
// Create the console object
		         
		        String line="";
		        

	               
		        while (!(line=in.readLine()).equals("exit"))
		        {
		        	irc.message(line);	
		        	
		        }
		        
		      /*
		      // サーバーからの応答確認
		      InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
		      BufferedReader breader = new BufferedReader(inputStreamReader);
		      String line = null;
		      int tries = 1;
		      while ((line = breader.readLine()) != null) {
		        System.out.println(">>> "+line);
		        int firstSpace = line.indexOf(" ");
		        int secondSpace = line.indexOf(" ", firstSpace + 1);
		        if (secondSpace >= 0) {
		          String code = line.substring(firstSpace+1, secondSpace);
		          if (code.equals("004")) {
		            break;
		          }
		        }
		      }
		    */
		        irc.exit();
		     
		    }catch (Exception e) {
		      e.printStackTrace();
		    }
		  
	}
	private BufferedReader  getInReader() {

		return in;
	}

	public void exit() {
		 try {
			bwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void message(String message) {

	      sendString(bwriter,"PRIVMSG "+channel+" :"+message);
	}

	public void open(String channel) {

		 this.channel=channel;
	      sendString(bwriter,"JOIN "+channel);
		
	}

	public void login(String nickname,String user) {
		sendString(bwriter,"NICK "+nickname);
	    sendString(bwriter,"USER "+user+"  8 * :"+user+" Java IRC Bot");
	      
	}
	Socket socket=null;
	BufferedWriter bwriter=null;
	String channel="";
	BufferedReader in=null;
	public void connect(String server, int port) {

	       try {
			socket = new Socket(server,port);
	
	      System.out.println("*** Connected to server.");
	      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
	      System.out.println("*** Opened OutputStreamWriter.");
	      bwriter = new BufferedWriter(outputStreamWriter);
	      System.out.println("*** Opened BufferedWriter.");
	      
	      InputStream pi1 = (socket.getInputStream());
	       in = new BufferedReader(new InputStreamReader(pi1));
		     
	     
	        
	   	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean isAnswer()
	{
		try {
			return in.ready();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public String getAnswer()
	{
		try {
			return in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	public void flushAnswer()
	{
		
		try {

			while(in.ready())
				in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}

	static void sendString(BufferedWriter bw, String str) {
	    try {
	      bw.write(str + "\r\n");
	      bw.flush();
	    }
	    catch (Exception e) {
	      System.out.println("Exception: "+e);
	    }
	  }
	public void CreateChannelCountryLanguage()
	{
	
	Locale[] locales = Locale.getAvailableLocales();

	for (Locale obj : locales) 
		if(obj.getCountry()!=null && !obj.getCountry().isBlank())
			if(obj.getLanguage()!=null && !obj.getLanguage().isBlank())
				{
		String chan="#"+obj.getCountry()+"-"+obj.getLanguage()+"-"+obj.getDisplayCountry(obj).replaceAll(" ", "_")+"-"+obj.getDisplayLanguage(obj);
		//NICK.
		send("bot-en-US");
		
		//JOIN.
		send(chan);
		//TOPIC.
		send(chan,"en-US : Welcome to the channel of people living in "+obj.getDisplayCountry(Locale.ENGLISH)+" and speaking "+obj.getDisplayLanguage(Locale.ENGLISH)+"\r\n"+">"+obj.getDisplayCountry(obj)+" : "+obj.getDisplayLanguage(obj));
		
	}
	}
	private void send(String string) {
	System.out.println(string);		
	}
	private void send(String chan,String string) {
		System.out.println(chan+":"+string);		
		}
}
