package com.valleau.pierre.irc.client.msg;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.command.PART;
import com.francisbailey.irc.command.PRIVMSG;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;
import com.valleau.pierre.irc.server.command.NOTICE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pierre Valleau on 20/02/2023.
 * https://modern.ircdocs.horse/ctcp.html
 * https://fr.wikipedia.org/wiki/Client-To-Client_Protocol
 */
public class CTCP  implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {
	}
	static public void send(Connection connection, ServerManager server,String message) {

        ArrayList<Channel> channels = server.getChannelManager().getChannelsByUser(connection);
        ArrayList<Connection> exclude = new ArrayList<>();
        exclude.add(connection);

        if (message==null || message.isblanc()) 
		message = "Client disconnected";

        if (clientMessage.getParameterCount() > 0) {
            message = clientMessage.getParameter(0);
        }

        for (Channel chan: channels) {
            PART command = new PART();
            command.partFromChannel(chan, connection, message);
        }

        connection.send(ServerMessageBuilder
            .from(connection.getClientInfo().getHostmask())
            .withReplyCode(ServerMessage.ERROR )
            .andMessage(":" + message)
            .build()
        );

        server.closeConnection(connection);
    }


    public int getMinimumParams() {
        return 0;
    }


    public boolean canExecuteUnregistered() {
        return true;
    }
    public void send_Query(Connection connection, Connection targetConnection, ServerManager server, String command, String param) {
    	List<String> params = new ArrayList<String>();
    	params.add(param);
    	send_Query( connection,  targetConnection,  server,  command,  params) ;
    			
    }
    	 public void send_Query(Connection connection, Connection targetConnection, ServerManager server, String command, List<String> params) {
    				 
    	char delimiter='\1';
    	String body = delimiter+command;
    	for(String param:params)
    		body+=" "+param;
    	body+=delimiter;
    	PRIVMSG.send( connection,  targetConnection,  server, body);
    	 }
    public void send_Reply(Connection connection, Connection targetConnection, ServerManager server, String command, List<String> params) {
		 
    	char delimiter='\1';
    	String body = delimiter+command;
    	for(String param:params)
    		body+=" "+param;
    	body+=delimiter;
    	NOTICE.send( connection,  targetConnection,  server, body);
    	 }
    public void send_ExtendedFormatting(Connection connection, Connection targetConnection,ServerManager server,String msg,List<String> params)
    {
    	send_Query(connection,targetConnection,server,msg,params);
    }
    public String send_ExtendedQuery(Connection connection, Connection targetConnection,ServerManager server,String msg,List<String> params)
    {
    	send_Query(connection,targetConnection,server,msg,params);
    	String rep=recieveReply(connection,targetConnection,server);
    	return rep;
    }
    public String send_MetadataQuery(Connection connection, Connection targetConnection,ServerManager server,String msg,List<String> params)
    {
    	send_Query(connection,targetConnection,server,msg,params);
    	String rep=recieveReply(connection,targetConnection,server);
    	return rep;
    }
    
    public void sendACTION(Connection connection, Connection targetConnection,ServerManager server,String msg)
    {
    	
    //	sendQuery(connection,targetConnection,server,"PING",info);
    	//String rep=recieveReply(connection,targetConnection,server);
    	List<String> params = new ArrayList<String>();
    	params.add(msg);
    	send_ExtendedFormatting(connection,targetConnection,server,"ACTION",params);
    }
    public String sendCLIENTINFO(Connection connection, Connection targetConnection,ServerManager server)
    {
    	
    //	sendQuery(connection,targetConnection,server,"PING",info);
    	//String rep=recieveReply(connection,targetConnection,server);
    	List<String> params = new ArrayList<String>();
    	String rep=send_MetadataQuery(connection,targetConnection,server,"CLIENTINFO",params);
    	return rep;
    }
    /*
     * DCC

  Type:    Extended Query
  Params:  DCC <type> <argument> <address> <port>

DCC (Direct Client-to-Client) is used to setup and control connections that go directly between clients, bypassing the IRC server. This is typically used for features that require a large amount of traffic between clients or simply wish to bypass the server itself such as file transfer, direct chat, and voice messages.

Properly implementing the various DCC types requires a document all of its own, and are not described here.

DCC is widely implemented. Clients MAY implement this CTCP message.
*/
    public String sendDCC(Connection connection, Connection targetConnection,ServerManager server, String type, String argument, String address, String port)
    {
    	
    //	sendQuery(connection,targetConnection,server,"PING",info);
    	//String rep=recieveReply(connection,targetConnection,server);
    	List<String> params = new ArrayList<String>();
    	params.add(type);
    	params.add(argument);
    	params.add(address);
    	params.add(port);
    	String rep=send_ExtendedQuery(connection,targetConnection,server,"DCC",params);
    	return rep;
    }
    
    /*
     * FINGER

  Type:   Metadata Query
  Reply:  FINGER <info>

This metadata query returns miscellaneous info about the user, typically the same information that’s held in their realname field.

However, some implementations return the client name and version instead.

FINGER is widely implemented, but largely obsolete. Clients MAY implement this CTCP message.

Example:

  Query:     FINGER
  Response:  FINGER WeeChat 1.5

*/
    public String sendFINGER(Connection connection, Connection targetConnection,ServerManager server,String info)
    {
    	
    //	sendQuery(connection,targetConnection,server,"PING",info);
    	//String rep=recieveReply(connection,targetConnection,server);
    	List<String> params = new ArrayList<String>();
    	params.add(info);
    	String rep=send_MetadataQuery(connection,targetConnection,server,"FINGER",params);
    	return rep;
    }
    /* PING

  Type:    Extended Query
  Params:  PING <info>

This extended query is used to confirm reachability with other clients and to check latency. When receiving a CTCP PING, the reply must contain exactly the same parameters as the original query.

PING is universally implemented. Clients MUST implement this CTCP message.

Example:

  Query:     PING 1473523721 662865
  Response:  PING 1473523721 662865
  
  Query:     PING foo bar baz
  Response:  PING foo bar baz

     * */
    public String sendPING(Connection connection, Connection targetConnection,ServerManager server,String info)
    {
    	
    //	sendQuery(connection,targetConnection,server,"PING",info);
    	//String rep=recieveReply(connection,targetConnection,server);
    	List<String> params = new ArrayList<String>();
    	params.add(info);
    	String rep=send_ExtendedQuery(connection,targetConnection,server,"PING",params);
    	return rep;
    }

    
    /*SOURCE

  Type:   Metadata Query
  Reply:  SOURCE <info>

This metadata query is used to return the location of the source code for the client.

SOURCE is rarely implemented. Clients MAY implement this CTCP message.

Example:

  Query:     SOURCE
  Response:  SOURCE https://weechat.org/download
*/
    public String sendSOURCE(Connection connection, Connection targetConnection,ServerManager server,String info)
    {
    	
    //	sendQuery(connection,targetConnection,server,"PING",info);
    	//String rep=recieveReply(connection,targetConnection,server);
    	List<String> params = new ArrayList<String>();
    	params.add(info);
    	String rep=send_MetadataQuery(connection,targetConnection,server,"SOURCE",params);
    	return rep;
    }
    
    /*TIME

  Type:    Extended Query
  Params:  TIME <timestring>

This extended query is used to return the client’s local time in an unspecified human-readable format. We recommend ISO 8601 format, but raw ctime() output appears to be the most common in practice.

New implementations SHOULD default to UTC time for privacy reasons.

TIME is almost universally implemented. Clients SHOULD implement this CTCP message.

Example:

  Query:     TIME
  Response:  TIME 2016-09-26T00:45:36Z
*/
    public String sendTIME(Connection connection, Connection targetConnection,ServerManager server)
    {
    	
    //	sendQuery(connection,targetConnection,server,"PING",info);
    	//String rep=recieveReply(connection,targetConnection,server);
    	List<String> params = new ArrayList<String>();
    	String rep=send_ExtendedQuery(connection,targetConnection,server,"TIME",params);
    	return rep;
    }
    /*VERSION

  Type:   Metadata Query
  Reply:  VERSION <verstring>

This metadata query is used to return the name and version of the client software in use. There is no specified format for the version string.

VERSION is universally implemented. Clients MUST implement this CTCP message.

Example:

  Query:     VERSION
  Response:  VERSION WeeChat 1.5-rc2 (git: v1.5-rc2-1-gc1441b1) (Apr 25 2016)
*/
    public String sendVERSION(Connection connection, Connection targetConnection,ServerManager server)
    {
    	
    //	sendQuery(connection,targetConnection,server,"PING",info);
    	//String rep=recieveReply(connection,targetConnection,server);
    	List<String> params = new ArrayList<String>();
    	//params.add(info);
    	String rep=send_MetadataQuery(connection,targetConnection,server,"VERSION",params);
    	return rep;
    }
    
    /*USERINFO

  Type:   Metadata Query
  Reply:  USERINFO <info>

This metadata query returns miscellaneous info about the user, typically the same information that’s held in their realname field.

However, some implementations return <nickname> (<realname>) instead.

USERINFO is widely implemented, but largely obsolete. Clients MAY implement this CTCP message.

Example:

  Query:     USERINFO
  Response:  USERINFO fred (Fred Foobar)
*/
    public String sendUSERINFO(Connection connection, Connection targetConnection,ServerManager server)
    {
    	
    //	sendQuery(connection,targetConnection,server,"PING",info);
    	//String rep=recieveReply(connection,targetConnection,server);
    	List<String> params = new ArrayList<String>();
    	//params.add(info);
    	String rep=send_MetadataQuery(connection,targetConnection,server,"USERINFO",params);
    	return rep;
    }
    
}
