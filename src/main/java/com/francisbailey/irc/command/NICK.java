package com.francisbailey.irc.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

import java.util.ArrayList;


/**
 * Created by fbailey on 16/11/16.
 *
 * @TODO generate new unique id in place of nickname. That way,
 * at connection registration if nick is taken unique id takes its place
 * and then client can automatically set new nick if need be.
 */
public class NICK implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String nick = clientMessage.getParameter(0);
        String oldHostmask = connection.getClientInfo().getHostmask();

        if (!isValidNick(nick)) {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_ERRONEOUSNICKNAME)
                .build()
            );
        }
        else {
            synchronized(this) {
                if (server.findConnectionByNick(nick) != null) {
                    connection.send(ServerMessageBuilder
                            .from(server.getName())
                            .withReplyCode(ServerMessage.ERR_NICKNAMEINUSE)
                            .andMessage(connection.getClientInfo().getNick() + " " + nick + " :Nickname already in use")
                            .build()
                    );

                    return;
                }

                connection.getClientInfo().setNick(nick);
            }

            if (connection.isRegistered()) {
                ArrayList<Channel> channels = server.getChannelManager().getChannelsByUser(connection);
                ArrayList<Connection> exclude = new ArrayList<>();
                exclude.add(connection);

                for (Channel chan: channels) {
                    chan.broadcast(ServerMessageBuilder
                        .from(oldHostmask)
                        .withReplyCode(ServerMessage.RPL_NICK)
                        .andMessage(nick)
                        .build(),
                    exclude);
                }

                connection.send(ServerMessageBuilder
                    .from(oldHostmask)
                    .withReplyCode(ServerMessage.RPL_NICK)
                    .andMessage(nick)
                    .build()
                );
            }
        }
    }



    public int getMinimumParams() {
        return 1;
    }



    public boolean canExecuteUnregistered() {
        return true;
    }


    /**
     * Check if a nickname is valid and return
     * a server reply code if it's not.
     *
     * @param nick
     * @return
     */
    private boolean isValidNick(String nick) {

    	 if (nick.length() > 9) {
             return false;
         }
    	 /*They MUST NOT contain any of the following characters: space (' ', 0x20), comma (',', 0x2C), asterisk ('*', 0x2A), question mark ('?', 0x3F), exclamation mark ('!', 0x21), at sign ('@', 0x40).
    	  * */
    	 if (nick.contains(" ") ) {
             return false;
         }
    	 if (nick.contains(",") ) {
             return false;
         }
    	 if (nick.contains("*") ) {
             return false;
         }
    	 if (nick.contains("?") ) {
             return false;
         }
    	 if (nick.contains("!") ) {
             return false;
         }
    	 if (nick.contains("@") ) {
             return false;
         }
/*They MUST NOT start with any of the following characters: dollar ('$', 0x24), colon (':', 0x3A).*/
    	 
    	 if (nick.startsWith("$") ) {
             return false;
         }

    	 if (nick.startsWith(":") ) {
             return false;
         }
    	 /*They MUST NOT start with a character listed as a channel type prefix*/
    	 if (nick.startsWith("#") ) {
             return false;
         }
    	 if (nick.startsWith("&") ) {
             return false;
         }
    	 /*They SHOULD NOT contain any dot character ('.', 0x2E).*/
    	 if (nick.startsWith(".") ) {
             return false;
         }

    	 return true;
    }

}
