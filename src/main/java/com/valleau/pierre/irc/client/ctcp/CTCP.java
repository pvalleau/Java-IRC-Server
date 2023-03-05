/**
 * 
 */
package com.valleau.pierre.irc.client.ctcp;

import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;

/**
 * @author Pierre Valleau
 *
 */
public class CTCP   implements Executable, Message {

	public void send(String target,String command,String target,List<String> arguments)
	{
		
	}
	public void sendLine(Connection connection, ServerManager server,ClientMessage clientMessage) {
		connection.send(clientMessage);
	}
	@Override
	public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMinimumParams() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public boolean canExecuteUnregistered() {
		// TODO Auto-generated method stub
		return false;
	}

}
