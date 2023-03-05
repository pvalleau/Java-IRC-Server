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
 * Created by Pierre Valleau on 20/2/2023.
 *
 PASS message

     Command: PASS
  Parameters: <password>

The PASS command is used to set a ‘connection password’. If set, the password must be set before any attempt to register the connection is made. This requires that clients send a PASS command before sending the NICK / USER combination.

The password supplied must match the one defined in the server configuration. It is possible to send multiple PASS commands before registering but only the last one sent is used for verification and it may not be changed once the client has been registered.

If the password supplied does not match the password expected by the server, then the server SHOULD send ERR_PASSWDMISMATCH (464) and MAY then close the connection with ERROR. Servers MUST send at least one of these two messages.

Servers may also consider requiring SASL authentication upon connection as an alternative to this, when more information or an alternate form of identity verification is desired.

Numeric replies:

    ERR_NEEDMOREPARAMS (461)
    ERR_ALREADYREGISTERED (462)
    ERR_PASSWDMISMATCH (464)

Command Example:

  PASS secretpasswordhere

 */
public class PASS implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String pwd = clientMessage.getParameter(0);
     
        if (!isValidPWD(pwd)) {
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_PASSWDMISMATCH)
                .build());
                connection.terminate();
            
        
        }
    }



    public int getMinimumParams() {
        return 1;
    }



    public boolean canExecuteUnregistered() {
        return true;
    }


    /**
     * Check if a password is valid and return
     * a server reply code if it's not.
     *
     * @param pwd
     * @return
     */
    private boolean isValidPWD(String pwd) {

    	 if ("password123".equals(pwd))
    		 return true;
    	 return false;
    }

}
