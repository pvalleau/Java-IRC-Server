package com.valleau.pierre.irc.server.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

import java.util.ArrayList;

/**
 * Created by Pierre Valleau on 20/02/2023.
 * 
 * ERROR message

    Command: ERROR
 Parameters: <reason>

This message is sent from a server to a client to report a fatal error, before terminating the client’s connection.

This MUST only be used to report fatal errors. Regular errors should use the appropriate numerics or the IRCv3 standard replies framework.

Numeric Replies:

    None

Command Example:

  ERROR :Connection timeout        ; Server closing a client connection because it
                                   is unresponsive.

 */
public class ERROR implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {
	}
    
    public synchronized void  send(Connection connection, ServerManager server,String message) {

        ArrayList<Channel> channels = server.getChannelManager().getChannelsByUser(connection);
        ArrayList<Connection> exclude = new ArrayList<>();
        exclude.add(connection);

        if (message==null || message.isBlank()) 
		message = "Client disconnected";

     

        for (Channel chan: channels) {
            PART command = new PART();
            command.partFromChannel(chan, connection, message);
        }

        connection.send(ServerMessageBuilder
            .from(connection.getClientInfo().getHostmask())
            .withReplyCode("" )
            .andMessage("ERROR:" + message)
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
}
