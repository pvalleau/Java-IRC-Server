package com.francisbailey.irc.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.ChannelManager;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;
import com.francisbailey.irc.mode.Mode;

import java.util.ArrayList;

/**
 * Created by pierre Valleau  on 2023/02/18
 *
 NAMES message

     Command: NAMES
  Parameters: <channel>{,<channel>}

The NAMES command is used to view the nicknames joined to a channel and their channel membership prefixes. The param of this command is a list of channel names, delimited by a comma (",", 0x2C) character.

The channel names are evaluated one-by-one. For each channel that exists and they are able to see the users in, the server returns one of more RPL_NAMREPLY numerics containing the users joined to the channel and a single RPL_ENDOFNAMES numeric. If the channel name is invalid or the channel does not exist, one RPL_ENDOFNAMES numeric containing the given channel name should be returned. If the given channel has the secret channel mode set and the user is not joined to that channel, one RPL_ENDOFNAMES numeric is returned. Users with the invisible user mode set are not shown in channel responses unless the requesting client is also joined to that channel.

Servers MAY allow more than one target channel. They can advertise the maximum the number of target users per NAMES command via the TARGMAX RPL_ISUPPORT parameter.

Numeric Replies:

    RPL_NAMREPLY (353)
    RPL_ENDOFNAMES (366)

Command Examples:

  NAMES #twilight_zone,#42        ; List all visible users on
                                  "#twilight_zone" and "#42".

  NAMES                           ; Attempt to list all visible users on
                                  the network, which SHOULD be responded to
                                  as specified above.


 */
public class NAMES   implements Executable {

	public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

		String channel = null;
		if (clientMessage.getParameters().size()>0)
			channel=clientMessage.getParameter(0);
		String channellist[]  = new String[1];
		channellist[0]=channel;
		ChannelManager channelManager = server.getChannelManager();
	
		if (channel == null || channel.isBlank())
			{
			channel="";
			for(Channel c:channelManager.getChannels())
			channel+=c.getName()+",";
			channel=channel.substring(0,channel.length()-1);
			}
		if (channel.contains(","))
			channellist	=channel.split(",");
		
		for(String achannel:channellist)
		{
		Channel chan=channelManager.getChannel(achannel);
		String symbol="=";
		if(chan.hasMode(Mode.SECRET))
			symbol="@";
		if(chan.hasMode(Mode.PRIVATE))
			symbol="*";
		String prefix=chan.getPrefix(connection);
		
		for(Connection user :chan.getUsers())		
		connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.RPL_NAMREPLY )
				.andMessage(connection.getClientInfo().getNick() +" "+ symbol+" "+ achannel+" :"+prefix+user.getClientInfo().getNick()).build());
	
		connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.RPL_ENDOFNAMES  )
				.andMessage(connection.getClientInfo().getNick() +" "+  achannel+" :End of /NAMES list").build());
		}
	
		
	
	}

	@Override
	public int getMinimumParams() {
		return 0;
	}

	@Override
	public boolean canExecuteUnregistered() {
		// TODO Auto-generated method stub
		return true;
	}

}
