package com.valleau.pierre.irc.server.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.exception.InvalidCommandException;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;
import com.francisbailey.irc.mode.Mode;

import java.util.ArrayList;

/**
 * Updated by Pierre Valleau on 5/03/2023
 */
public class MSG implements Executable {


    public Executable build(String rtarget,ClientMessage msg) throws InvalidCommandException {

        Executable exe=null;
        rtarget=rtarget.toUpperCase().charAt(0)+rtarget.toLowerCase().substring(1);
        Class<?> c =null;
        try {
             c = Class.forName("com.valleau.irc.server.services." + rtarget);
            exe = (Executable) c.getConstructor().newInstance();
            return exe;
        }
        catch (Exception e) {
        	
        	
        }       
        return exe;
    }

    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String rtarget = clientMessage.getParameter(0);
        
        Executable exe;
		try {
			exe = build( rtarget, clientMessage);
			 if (exe!=null)
			        exe.execute(connection, clientMessage, server);
		} catch (InvalidCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
       

    }



    public int getMinimumParams() {
        return 3;
    }



    public boolean canExecuteUnregistered() {
        return false;
    }


    private void sendChannelMessage(Connection connection, Channel channel, String target, String message) {

        if (channel.hasUser(connection)) {
            ArrayList<Connection> excluded = new ArrayList<>();
            excluded.add(connection);

            channel.broadcast(ServerMessageBuilder
                .from(connection.getClientInfo().getHostmask())
                .withReplyCode(ServerMessage.RPL_PRIVMSG)
                .andMessage(target + " :" + message)
                .build(),
            excluded);
        }
        else {
            connection.send(ServerMessageBuilder
                .from(connection.getClientInfo().getHostmask())
                .withReplyCode(ServerMessage.ERR_NOTONCHANNEL)
                .andMessage(connection.getClientInfo().getNick() + " :not on channel")
                .build()
            );
        }
    }


    private void sendPrivateMessage(Connection connection, Connection targetConnection, ServerManager server, String message) {

        if (targetConnection == null) {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_NOSUCHNICK)
                .andMessage(connection.getClientInfo().getNick())
                .build()
            );
        } else {
            targetConnection.send(ServerMessageBuilder
                .from(connection.getClientInfo().getHostmask())
                .withReplyCode(ServerMessage.RPL_PRIVMSG)
                .andMessage(targetConnection.getClientInfo().getNick() + " :" + message)
                .build()
            );
        }
    }
}
