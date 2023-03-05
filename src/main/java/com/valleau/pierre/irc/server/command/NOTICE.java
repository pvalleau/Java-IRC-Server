package com.valleau.pierre.irc.server.command;

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
 * Created by Pierre Valleau on 18/02/2023
 NOTICE message

     Command: NOTICE
  Parameters: <target>{,<target>} <text to be sent>

The NOTICE command is used to send notices between users, as well as to send notices to channels. <target> is interpreted the same way as it is for the PRIVMSG command.

The NOTICE message is used similarly to PRIVMSG. The difference between NOTICE and PRIVMSG is that automatic replies must never be sent in response to a NOTICE message. This rule also applies to servers – they must not send any error back to the client on receipt of a NOTICE command. The intention of this is to avoid loops between a client automatically sending something in response to something it received. This is typically used by ‘bots’ (a client with a program, and not a user, controlling their actions) and also for server messages to clients.

One thing for bot authors to note is that the NOTICE message may be interpreted differently by various clients. Some clients highlight or interpret any NOTICE sent to a channel in the same way that a PRIVMSG with their nickname gets interpreted. This means that users may be irritated by the use of NOTICE messages rather than PRIVMSG messages by clients or bots, and they are not commonly used by client bots for this reason.

 */
public class NOTICE implements Executable {



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
           
        }
        else {
            Connection targetConnection = server.findConnectionByNick(target);
            if(targetConnection==null)
            {
            return;
            }
            sendPrivateMessage(connection, targetConnection, server, message);
            
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
        
        
    }


    private void sendPrivateMessage(Connection connection, Connection targetConnection, ServerManager server, String message) {

        if (targetConnection == null) {
           
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
