package com.francisbailey.irc;

import com.francisbailey.irc.exception.ChannelKeyIsSetException;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.mode.Mode;
import com.francisbailey.irc.mode.ModeSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;


/**
 * @TODO MaxUserLimit dynamically set from config
 * Created by fbailey on 01/12/16.
 * updated by Pierre Valleau on 2023/2/18.
 * Channels

A channel is a named group of one or more clients. All clients in the channel will receive all messages addressed to that channel. The channel is created implicitly when the first client joins it, and the channel ceases to exist when the last client leaves it. While the channel exists, any client can reference the channel using the name of the channel. Networks that support the concept of ‘channel ownership’ may persist specific channels in some way while no clients are connected to them.

Channel names are strings (beginning with specified prefix characters). Apart from the requirement of the first character being a valid channel type prefix character; the only restriction on a channel name is that it may not contain any spaces (' ', 0x20), a control G / BELL ('^G', 0x07), or a comma (',', 0x2C) (which is used as a list item separator by the protocol).

There are several types of channels used in the IRC protocol. The first standard type of channel is a regular channel, which is known to all servers that are connected to the network. The prefix character for this type of channel is ('#', 0x23). The second type are server-specific or local channels, where the clients connected can only see and talk to other clients on the same server. The prefix character for this type of channel is ('&', 0x26). Other types of channels are described in the Channel Types section.

Along with various channel types, there are also channel modes that can alter the characteristics and behaviour of individual channels. See the Channel Modes section for more information on these.

To create a new channel or become part of an existing channel, a user is required to join the channel using the JOIN command. If the channel doesn’t exist prior to joining, the channel is created and the creating user becomes a channel operator. If the channel already exists, whether or not the client successfully joins that channel depends on the modes currently set on the channel. For example, if the channel is set to invite-only mode (+i), the client only joins the channel if they have been invited by another user or they have been exempted from requiring an invite by the channel operators.

Channels also contain a topic. The topic is a line shown to all users when they join the channel, and all users in the channel are notified when the topic of a channel is changed. Channel topics commonly state channel rules, links, quotes from channel members, a general description of the channel, or whatever the channel operators want to share with the clients in their channel.

A user may be joined to several channels at once, but a limit may be imposed by the server as to how many channels a client can be in at one time. This limit is specified by the CHANLIMIT RPL_ISUPPORT parameter. See the Feature Advertisement section for more details on RPL_ISUPPORT.

If the IRC network becomes disjoint because of a split between servers, the channel on either side is composed of only those clients which are connected to servers on the respective sides of the split, possibly ceasing to exist on one side. When the split is healed, the connecting servers ensure the network state is consistent between them.

 */
public class Channel {

    private String topic;
    private String topicAuthor;
    private String name;

    private int userLimit;
    private String key;

    private HashMap<Mode,ArrayList<Pattern>> masks;
    private ArrayList<Connection> users;
    
    private ModeSet modes;
    /*Channel Operators

Channel operators (or “chanops”) on a given channel are considered to ‘run’ or ‘own’ that channel. In recognition of this status, channel operators are endowed with certain powers which let them moderate and keep control of their channel.

Most IRC operators do not concern themselves with ‘channel politics’. In addition, a large number of networks leave the management of specific channels up to chanops where possible, and try not to interfere themselves. However, this is a matter of network policy, and it’s best to consult the Message of the Day when looking at channel management.

IRC servers may also define other levels of channel moderation. These can include ‘halfop’ (half operator), ‘protected’ (protected user/operator), ‘founder’ (channel founder), and any other positions the server wishes to define. These moderation levels have varying privileges and can execute, and not execute, various channel management commands based on what the server defines.

The commands which may only be used by channel moderators include:

    KICK: Eject a client from the channel
    MODE: Change the channel’s modes
    INVITE: Invite a client to an invite-only channel (mode +i)
    TOPIC: Change the channel topic in a mode +t channel

Channel moderators are identified by the channel member prefix ('@' for standard channel operators, '%' for halfops) next to their nickname whenever it is associated with a channel (e.g. replies to the NAMES, WHO, and WHOIS commands).

Specific prefixes and moderation levels are covered in the Channel Membership Prefixes section.
*/
    private HashMap<Connection, ModeSet> channelUserModes;
    
    static public boolean isValidName(String channel)
    {
    	if(channel.contains(" "))
    		return false;
    	
    	if(channel.contains("\0x07"))//^G BELL
    		return false;
    	if(channel.contains(","))
    		return false;
    	
    	
    	
    	if(channel.startsWith("#"))
    		return true;
    	if(channel.startsWith("&"))
    		return true;
    	
    	
    	return false;
    }

    public Channel(String name, String topic) {

        this.name = name;
        this.topic = topic;
        this.users = new ArrayList<>();
        this.modes = new ModeSet();
        this.channelUserModes = new HashMap<>();
        this.topicAuthor = "Server";
        this.masks = new HashMap<>(); 
        this.userLimit=-1;
        
    }

    /**
     * Add a mode to a channel if it doesn't already have the given mode
     * @param mode
     */
    public synchronized boolean addMode(Mode mode) {
        return this.modes.addMode(mode);
    }

    /**
     *
     * @return
     */
    public synchronized String getModes() {
        return this.modes.getModes();
    }

    /**
     * Remove a mode if the channel has one
     *
     * @param mode
     * @return
     */
    public synchronized boolean removeMode(Mode mode) {
        return this.modes.removeMode(mode);
    }


    /**
     * Clear all modes
     */
    public synchronized void clearModes() {
        this.modes.clearModes();
    }


    /**
     * Verify that the channel has a given mode
     * @param mode
     * @return
     */
    public synchronized boolean hasMode(Mode mode) {
        return this.modes.hasMode(mode);
    }


    /**
     * Add a user to the channel
     * @param connection
     */
    public synchronized void addUser(Connection connection) {
        if (!this.users.contains(connection)) {
            this.users.add(connection);
        }
        if (this.users.size()==1)
        {
    	addModeForUser(connection, Mode.MODERATED);
        addModeForUser(connection, Mode.Founder );
        }
    
    }


    /**
     * Remove a user from the channel
     * @param connection
     */
    public synchronized void removeUser(Connection connection) {
        if (this.users.contains(connection)) {
            this.channelUserModes.remove(connection);
            this.users.remove(connection);
        }
    }


    /**
     * Add a mode for a given user
     * @param connection
     * @param mode
     */
    public synchronized void addModeForUser(Connection connection, Mode mode) {
        if (this.hasUser(connection)) {
            ModeSet ms;

            if (this.channelUserModes.containsKey(connection)) {
                ms = this.channelUserModes.get(connection);
            } else {
                ms = new ModeSet();
            }

            ms.addMode(mode);
            this.channelUserModes.put(connection, ms);
        }
    }


    /**
     * Remove a mode for a given channel user
     * @param connection
     * @param mode
     */
    public synchronized void removeModeForUser(Connection connection, Mode mode) {
        if (this.channelUserModes.containsKey(connection)) {
            ModeSet ms = this.channelUserModes.get(connection);
            ms.removeMode(mode);
            this.channelUserModes.put(connection, ms);
        }
    }


    /**
     * Check if a channel user has a given mode
     * @param connection
     * @param mode
     * @return
     */
    public synchronized boolean hasModeForUser(Connection connection, Mode mode) {
        if (this.channelUserModes.containsKey(connection)) {
            ModeSet ms = this.channelUserModes.get(connection);

            return ms.hasMode(mode);
        }

        return false;
    }


    /**
     * Get all channel modes for a user
     * @param connection
     * @return
     */
    public synchronized ModeSet getModesForUser(Connection connection) {
        return this.channelUserModes.get(connection);
    }


    /**
     * Get all user nicks names currently in the channel
     * @return
     */
    public synchronized ArrayList<String> getNicks() {
        ArrayList<String> nicks = new ArrayList<>();

        for (Connection con: this.users) {
            nicks.add(con.getClientInfo().getNick());
        }

        return nicks;
    }


    /**
     * Get all users connected to channel
     * @return
     */
    public synchronized ArrayList<Connection> getUsers() {

        return (ArrayList<Connection>)this.users.clone();
    }


    /**
     * Check for the existence of a user in a channel
     * @param connection
     * @return
     */
    public synchronized boolean hasUser(Connection connection) {
        return this.users.contains(connection);
    }


    /**
     *
     * @param nick
     * @return
     */
    public synchronized Connection findConnectionByNick(String nick) {
        for (Connection user: this.users) {
            if (user.getClientInfo().getNick().equals(nick)) {
                return user;
            }
        }

        return null;
    }


    /**
     * Pattern is a final class, so it's equals() method cannot
     * be overridden. As a result we can not use the default
     * list.contains or list.remove on a Pattern object.
     * @param mode
     * @param searchMask
     * @return
     */
    public boolean hasMask(Mode mode, String searchMask) {

        ArrayList<Pattern> masks = this.masks.get(mode);

        if (mode == null) {
            return false;
        }

        for (Pattern mask: masks) {
            if (mask.pattern().equals(searchMask)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the channel topic
     * @return
     */
    public String getTopic() {
        return this.topic;
    }


    public String getTopicAuthor() {
        return this.topicAuthor;
    }


    /**
     * Set the channel topic
     * @param topic
     * @return
     */
    public void setTopic(String topic, String topicAuthor) {
        this.topic = topic;
        this.topicAuthor = topicAuthor;
    }


    /**
     * Get the channel name
     * @return
     */
    public String getName() {
        return this.name;
    }


    /**
     * Send a message to all users on the channel
     * @param serverMessage
     */
    public synchronized void broadcast(ServerMessage serverMessage) {
        this.broadcast(serverMessage, null);
    }


    /**
     * Broadcast message to all users on channel except
     * those in the exclusion list.
     * @param serverMessage
     * @param exclude
     */
    public synchronized void broadcast(ServerMessage serverMessage, ArrayList<Connection> exclude) {
        for (Connection c: this.users) {
            if (exclude == null || !exclude.contains(c)) {
                c.send(serverMessage);
            }
        }
    }


    /**
     * Add a mask for a given mode
     * @param mode
     * @param mask
     */
    public synchronized void addMask(Mode mode, String mask) {

        ArrayList<Pattern> masks = this.masks.get(mode);
        Pattern maskPattern = Pattern.compile(mask);

        if (masks == null) {
            masks = new ArrayList<>();
            masks.add(maskPattern);
        } else if (!masks.contains(maskPattern)) {
            masks.add(maskPattern);
        }

        this.masks.put(mode, masks);
    }


    /**
     * Get all masks for a given mode
     * @param mode
     * @return
     */
    public synchronized ListIterator<Pattern> getMask(Mode mode) {
        ArrayList<Pattern> masks = this.masks.get(mode);

        if (masks != null && masks.size() > 0) {
            return masks.listIterator();
        }

        return null;
    }


    /**
     * Remove a mask if one exists for the given mode
     * @param mode
     * @param searchMask
     */
    public synchronized void removeMask(Mode mode, String searchMask) {
        ArrayList<Pattern> masks = this.masks.get(mode);

        if (mode == null) {
            return;
        }

        for (int i = 0; i < masks.size(); i++) {
            Pattern mask = masks.get(i);
            if (mask.pattern().equals(searchMask)) {
                masks.remove(i);
                return;
            }
        }
    }


    /**
     * Clear all masks for a given mode
     * @param mode
     */
    public synchronized void clearMasks(Mode mode) {
        ArrayList<Pattern> masks = this.masks.get(mode);

        if (masks != null) {
            masks.clear();
        }
    }


    public synchronized void setKey(String key) throws ChannelKeyIsSetException {

        if (this.key != null) {
            throw new ChannelKeyIsSetException();
        }

        this.key = key;
    }


    public synchronized String getKey() {
        return this.key;
    }


    public synchronized void clearKey() {
        this.key = null;
    }


    public synchronized void setUserLimit(int userLimit)
        throws IllegalArgumentException {

        if (userLimit < 1) {
            throw new IllegalArgumentException("User limit must be greater than 1");
        }

        this.userLimit = userLimit;
    }

    public synchronized int getUserLimit() {
        return this.userLimit;
    }

    /** confirn if the user can connect */
	public boolean isAllowedKey(Connection user, String key) {
		// TODO Auto-generated method stub
		return true;
		
		
	}

	public Connection getUsers(String nickname) {
		if ((nickname==null) || nickname.isBlank())
			return null;
		for(Connection user:getUsers())
			if (nickname.equals(user.getClientInfo().getNick()))
					return user;
		return null;
	}

	public String getPrefix(Connection connection) {
		String p="";
		if (hasModeForUser(connection, Mode.Founder ))
			p+="~";
		if (hasModeForUser(connection, Mode.Protected ))
			p+="&";
		if (hasModeForUser(connection, Mode.CHAN_OPERATOR )		
		   || hasModeForUser(connection, Mode.CHAN_OPERATOR )			
		   || hasModeForUser(connection, Mode.CHAN_OPERATOR )
		   )
			p+="@";
		if (hasModeForUser(connection, Mode.Halfop  ))
			p+="%";
		if (hasModeForUser(connection, Mode.Halfop  ))
			p+="%";
		if (hasModeForUser(connection, Mode.VOICE  ))
			p+="+";
		
		
		return p;
	}
}
