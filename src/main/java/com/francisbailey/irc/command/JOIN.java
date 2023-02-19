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
 * Created by fbailey on 01/12/16.
 */
public class JOIN implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

    	String repchannel = clientMessage.getParameter(0);
    	String repkey = null;
    	ChannelManager channelManager = server.getChannelManager();
        
    	if (repchannel.equals("0"))
    	{
    		/**
    		 * Note that this command also accepts the special argument of ("0", 0x30) instead of any of the usual parameters, which requests that the sending client leave all channels they are currently connected to. The server will process this command as though the client had sent a PART command for each channel they are a member of.
    		 * */
    		for(Channel achan:channelManager.getChannels())
    		if(	achan.hasUser(connection))
    		PART.partFromChannel(achan, connection, "");
    		return;
    	}
    	if(clientMessage.getParameterCount()>1)
    		repkey=clientMessage.getParameter(1);
        String nick = connection.getClientInfo().getNick();
        
        
        String channels[]=new String[1];
        channels[0]=repchannel;
        if (repchannel.contains(","))
        	channels=repchannel.split(",");
        
        String keys[]=new String[1];
        keys[0]=repkey;
        if (repkey!=null && repkey.contains(","))
        	keys=repkey.split(",");
        /**
         * This message may be sent from a server to a client to notify the client that someone has joined a channel. In this case, the message <source> will be the client who is joining, and <channel> will be the channel which that client has joined. Servers SHOULD NOT send multiple channels in this message to clients, and SHOULD distribute these multiple-channel JOIN messages as a series of messages with a single channel name on each.
         * */
        for(int i=0;i<channels.length;i++ )
        {
        	String channel=channels[i];
        	String key=null;
        	if(i<keys.length)
        	key=keys[i];
        	
        if (!channelManager.hasChannel(channel)) {
        	
        	
        	/*
            connection.send(ServerMessageBuilder
                .from(server.getName())
                .withReplyCode(ServerMessage.ERR_NOSUCHCHANNEL)
                .andMessage(nick + " " + channel + " :No such channel")
                .build()
            );*/
        	if (Channel.isValidName(channel))
        	channelManager.addChannel(channel, "Channel created by "+nick);
        }
        //else 
        
        {
        	
        	/** @todo implements Invite-Only && Invite-Exception Channel Mode
        	 * Invite-Only Channel Mode

This mode is standard, and the mode letter used for it is "+i".

This channel mode controls whether new users need to be invited to the channel before being able to join.

If this mode is set on a channel, a user must have received an INVITE for this channel before being allowed to join it. If they have not received an invite, they will receive an ERR_INVITEONLYCHAN (473) reply and the command will fail.
Invite-Exception Channel Mode

This mode is used in almost all IRC software today. The standard mode letter used for it is "+I", but it SHOULD be defined in the INVEX RPL_ISUPPORT parameter on connection.

This channel mode controls a list of channel masks that are exempt from the invite-only channel mode. If this mode has values, each of these values should be a client mask.

If this mode is set on a channel, and a client sends a JOIN request for that channel, their nickmask is compared with each ‘exempted’ client mask. If their nickmask matches any one of the masks set by this mode, and the channel is in invite-only mode, they do not need to require an INVITE in order to join the channel.
*/
        	
        	
        	/** @todo implement restrict the number of channels
        	 * Servers MAY restrict the number of channels a client may be joined to at one time. This limit SHOULD be defined in the CHANLIMIT RPL_ISUPPORT parameter. If the client cannot join this channel because they would be over their limit, they will receive an ERR_TOOMANYCHANNELS (405) reply and the command will fail.


        	 * */
            Channel chan = channelManager.getChannel(channel);
            ArrayList<Connection> channelUsers = chan.getUsers();

          
         
            if ( (chan.getUserLimit()>0) 
            		&& (chan.getUserLimit()<=chan.getUsers().size())
            	)
            {
        		connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.ERR_CHANNELISFULL)
        				.andMessage(connection.getClientInfo().getNick() + " "+channel+" :Cannot join channel (+l)")
        						.build());


            }
            else if (!chan.isAllowedKey(connection,key))
                {
            		connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.ERR_INVALIDKEY)
            				.andMessage(connection.getClientInfo().getNick() + " "+channel+" :Key is not well-formed")
            						.build());
	

                }
            else
            	/**Ban Channel Mode

            	This mode is standard, and the mode letter used for it is "+b".

            	This channel mode controls a list of client masks that are ‘banned’ from joining or speaking in the channel. If this mode has values, each of these values should be a client mask.

            	If this mode is set on a channel, and a client sends a JOIN request for this channel, their nickmask (the combination of nick!user@host) is compared with each banned client mask set with this mode. If they match one of these banned masks, they will receive an ERR_BANNEDFROMCHAN (474) reply and the JOIN command will fail. See the ban exception mode for more details.
            	*/
                if (chan.hasModeForUser(connection, Mode.BAN_MASK)
                		/** Exception Channel Mode

This mode is used in almost all IRC software today. The standard mode letter used for it is "+e", but it SHOULD be defined in the EXCEPTS RPL_ISUPPORT parameter on connection.

This channel mode controls a list of client masks that are exempt from the ‘ban’ channel mode. If this mode has values, each of these values should be a client mask.

If this mode is set on a channel, and a client sends a JOIN request for this channel, their nickmask is compared with each ‘exempted’ client mask. If their nickmask matches any one of the masks set by this mode, and their nickmask also matches any one of the masks set by the ban channel mode, they will not be blocked from joining due to the ban mode.*/
                		
                		&& !chan.hasModeForUser(connection, Mode.BAN_MASK_EXCEPTION))
                	
                {
                	
                	/** ERR_BANNEDFROMCHAN (474)

  "<client> <channel> :Cannot join channel (+b)"

Returned to indicate that a JOIN command failed because the client has been banned from the channel and has not had a ban exception set for them. The text used in the last param of this message may vary.*/
                	
                	
            		connection.send(ServerMessageBuilder.from(server.getName()).withReplyCode(ServerMessage.ERR_BANNEDFROMCHAN)
            				.andMessage(connection.getClientInfo().getNick() + " "+channel+" :Cannot join channel (+b)")
            						.build());
		
                }
                else
                if (channelUsers.indexOf(connection) <= 0
            		) {
            	/// If a client’s JOIN command to the server is successful, the server MUST send, in this order:
                chan.addUser(connection);

                String hostmask = connection.getClientInfo().getHostmask();
                /// A JOIN message with the client as the message <source> and the channel they have joined as the first parameter of the message.
                chan.broadcast(ServerMessageBuilder
                    .from(hostmask)
                    .withReplyCode(ServerMessage.RPL_JOIN)
                    .andMessage(chan.getName())
                    .build()
                );
            }

            this.sendChannelUsers(connection, chan, clientMessage.getCommandOrigin());
            //The channel’s topic (with RPL_TOPIC (332) and optionally RPL_TOPICWHOTIME (333)), and no message if the channel does not have a topic.
            TOPIC topicCommand = new TOPIC();

            /*A list of users currently joined to the channel (with one or more RPL_NAMREPLY (353) numerics followed by a single RPL_ENDOFNAMES (366) numeric). These RPL_NAMREPLY messages sent by the server MUST include the requesting client that has just joined the channel.
             * 
             */
            topicCommand.sendTopic(server.getName(), connection, chan);
        }
        }
    }


    public int getMinimumParams() {
        return 1;
    }


    public boolean canExecuteUnregistered() {
        return false;
    }


    /**
     * After a user successfully joins a channel they must be sent a list of
     * all users currently in the channel
     *
     * @param connection - The client that joined the channel
     * @param channel - The channel the client joined
     * @param origin - The origin of the message
     */
    private void sendChannelUsers(Connection connection, Channel channel, String origin) {

        ArrayList<String> nicks = channel.getNicks();
        String chanName = channel.getName();
        String nick = connection.getClientInfo().getNick();

        for (String chanNick: nicks) {
            connection.send(ServerMessageBuilder.from(origin)
                .withReplyCode(ServerMessage.RPL_NAMREPLY)
                .andMessage(nick + " = " + chanName + " :" + chanNick)
                .build()
            );
        }

        connection.send(ServerMessageBuilder.from(origin)
            .withReplyCode(ServerMessage.RPL_ENDOFNAMES)
            .andMessage(nick + " " + chanName + " :End of NAMES list")
            .build()
        );
    }
}
