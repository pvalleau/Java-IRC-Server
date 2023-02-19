package com.francisbailey.irc.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;
import com.francisbailey.irc.mode.Mode;

import java.util.ArrayList;

/**
 * Created by fbailey on 01/12/16.
 * Updated by Pierre Valleau on 18/02/2023
 */
public class PRIVMSG implements Executable {



    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String rtarget = clientMessage.getParameter(0);
        String message = clientMessage.getParameter(1);
        String targets[]=new String[1];
        targets[0]=rtarget;
        if (rtarget.contains(","))
        	targets=rtarget.split(",");
        for(String target:targets)
        {
        // target is a channel, check that the channel exists
        if (server.getChannelManager().isChannel(target)) {
        	
            Channel chan = server.getChannelManager().getChannel(target);
            if (!chan.hasModeForUser(connection, Mode.BAN_MASK)
            		|| chan.hasModeForUser(connection, Mode.BAN_MASK_EXCEPTION))
            sendChannelMessage(connection, chan, target, message);
        }
        else if (server.getChannelManager().isChannelType(target)){
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_NOSUCHCHANNEL)
                .andMessage(connection.getClientInfo().getNick())
                .build()
            );
        }
        else {
            Connection targetConnection = server.findConnectionByNick(target);
            if(targetConnection==null)
            { connection.send(ServerMessageBuilder
                        .from(server.getName())
                        .withReplyCode(ServerMessage.ERR_NOSUCHNICK )
                        .andMessage(connection.getClientInfo().getNick()+" "+
                        		target+" :No such nick/channel")
                        .build()
                    );
            return;
            }
            sendPrivateMessage(connection, targetConnection, server, message);
            if(targetConnection.getModes().hasMode(Mode.AWAY))
            	 connection.send(ServerMessageBuilder
                         .from(server.getName())
                         .withReplyCode(ServerMessage.RPL_AWAY)
                         .andMessage(connection.getClientInfo().getNick()+" "+
                        		 targetConnection.getClientInfo().getNick()+" :")
                         .build()
                     );
        }
        }

    }



    public int getMinimumParams() {
        return 2;
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
