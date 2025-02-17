package com.francisbailey.irc;

/**
 * Created by fbailey on 04/11/16.
 */
public class Client {

    private String nick;
    private String username;
    private String hostName;
    private String realName;


    public Client(String nick, String username, String hostName, String realName) {

        this.nick = nick;
        this.username = username;
        this.hostName = hostName;
        this.realName = realName;
    }

    public String info()
    {
    	return "Nick : "+nick+"\r\n"+
    		   "Username : "+username+"\r\n"+
    		   "HostName : "+hostName+"\r\n"+
    		   "RealName : "+realName+"\r\n";
    }

    public Client() {
        this.nick = "*";
        this.username = "*";
        this.hostName = "*";
        this.realName = "*";
    }
    public Client(Client c) {
        this.nick = c.nick;
        this.username = c.username;
        this.hostName = c.hostName;
        this.realName = c.realName;
    }


    /**
     *  Compile the nick, username and hostname into an IRC hostmask.
     *
     * @return String
     */
    public String getHostmask() {
        return this.nick + "!" + this.username + "@" + this.hostName;
    }


    /**
     * Getters and setters for client properties.
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setHostname(String name) {
        this.hostName = name;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public void setRealname(String name) {
        this.realName = name;
    }

    public String getNick() {
    	if (this.nick==null)
    		return "*";
        return this.nick;
    }

    public String getHostname() {
        return this.hostName;
    }

    public String getUsername() {
        return this.username;
    }

    public String getRealname() {
        return this.realName;
    }
}
