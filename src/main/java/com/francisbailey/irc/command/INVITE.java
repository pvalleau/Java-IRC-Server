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
 INVITE message

     Command: INVITE
  Parameters: <nickname> <channel>

The INVITE command is used to invite a user to a channel. The parameter <nickname> is the nickname of the person to be invited to the target channel <channel>.

The target channel SHOULD exist (at least one user is on it). Otherwise, the server SHOULD reject the command with the ERR_NOSUCHCHANNEL numeric.

Only members of the channel are allowed to invite other users. Otherwise, the server MUST reject the command with the ERR_NOTONCHANNEL numeric.

Servers MAY reject the command with the ERR_CHANOPRIVSNEEDED numeric. In particular, they SHOULD reject it when the channel has invite-only mode set, and the user is not a channel operator.

If the user is already on the target channel, the server MUST reject the command with the ERR_USERONCHANNEL numeric.

When the invite is successful, the server MUST send a RPL_INVITING numeric to the command issuer, and an INVITE message, with the issuer as <source>, to the target user. Other channel members SHOULD NOT be notified.

Numeric Replies:

    RPL_INVITING (341)
    ERR_NEEDMOREPARAMS (461)
    ERR_NOSUCHCHANNEL (403)
    ERR_NOTONCHANNEL (442)
    ERR_CHANOPRIVSNEEDED (482)
    ERR_USERONCHANNEL (443)

Command Examples:

  INVITE Wiz #foo_bar    ; Invite Wiz to #foo_bar

Message Examples:

  :dan-!d@localhost INVITE Wiz #test    ; dan- has invited Wiz
                                        to the channel #test

 */
public class INVITE  implements Executable {

	public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

		String nickname = null;
		if (clientMessage.getParameters().size()>0)
			nickname=clientMessage.getParameter(0);
		String channel  = null;
		if (clientMessage.getParameters().size()>1)
			channel= clientMessage.getParameter(1);
		ChannelManager channelManager = server.getChannelManager();
		
		Channel chan=channelManager.getChannel(channel);
		if (chan==null)
		{
			connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.ERR_NOSUCHCHANNEL)
					.andMessage(connection.getClientInfo().getNick() +" "+ channel+" :No such channel").build());
			return;
		}
		if(!chan.hasUser(connection))
		{
			connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.ERR_NOTONCHANNEL )
				.andMessage(connection.getClientInfo().getNick() +" "+ channel+" :You're not on that channel").build());
		return;		
		}
		
		if(chan.hasMode(Mode.INVITE)
				&& !chan.hasModeForUser(connection, Mode.OPERATOR))
		{
			connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.ERR_CHANOPRIVSNEEDED  )
				.andMessage(connection.getClientInfo().getNick() +" "+ channel+" :You're not channel operator").build());
		return;		
		}
		if(chan.getUsers(nickname)!=null)
		{
			connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.ERR_USERONCHANNEL  )
				.andMessage(connection.getClientInfo().getNick() +" "+ channel+" :is already on channel").build());
		return;		
		}
		
		
		connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.RPL_INVITING )
				.andMessage(connection.getClientInfo().getNick()+" " + nickname+" "+ channel).build());

		
		connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.RPL_INVITING)
				.andMessage(connection.getClientInfo().getHostmask() + " INVITE  "+connection.getClientInfo().getNick()+" has invited "+nickname+"\r\n"
						+ "                                        to the channel "+channel).build());

	
	}

	@Override
	public int getMinimumParams() {
		return 2;
		/** @todo implement Invite list
		 * 
		 * Invite list

Servers MAY allow the INVITE with no parameter, and reply with a list of channels the sender is invited to as RPL_INVITELIST (336) numerics, ending with a RPL_ENDOFINVITELIST (337) numeric.
*/
	}

	@Override
	public boolean canExecuteUnregistered() {
		// TODO Auto-generated method stub
		return true;
	}

}
