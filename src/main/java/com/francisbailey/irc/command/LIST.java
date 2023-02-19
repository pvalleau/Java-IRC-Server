package com.francisbailey.irc.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.ChannelManager;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

import java.util.ArrayList;

/**
 * Created by pierre Valleau  on 2023/02/18.
 *
  */
public class LIST implements Executable {

	public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

		String ListChannel = null;
		if (clientMessage.getParameters().size()>0)
			ListChannel=clientMessage.getParameter(0);
		String elistcond  = null;
		if (clientMessage.getParameters().size()>1)
			elistcond= clientMessage.getParameter(1);
		
		String oldHostmask = connection.getClientInfo().getHostmask();
		ChannelManager channelManager = server.getChannelManager();
		connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.RPL_LISTSTART)
				.andMessage(connection.getClientInfo().getNick() + " Channel :Users  Name").build());

		for(Channel c:channelManager.getChannels())
		connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.RPL_LIST)
				.andMessage(connection.getClientInfo().getNick() + " "+c.getName()+" " +c.getUsers().size()+" :"+c.getTopic())
				.build());

		connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.RPL_LISTEND)
				.andMessage(connection.getClientInfo().getNick() + " :End of /LIST").build());

	}

	@Override
	public int getMinimumParams() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canExecuteUnregistered() {
		// TODO Auto-generated method stub
		return true;
	}

}
