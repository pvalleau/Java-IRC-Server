package com.francisbailey.irc;


import com.francisbailey.irc.exception.InvalidCommandException;
import com.francisbailey.irc.message.ClientMessage;

/**
 * Created by fbailey on 16/11/16.
 * updated Pierre valleau 26/02/2023
 */
public class CommandFactory {


    public Executable build(ClientMessage msg) throws InvalidCommandException {

        Executable exe;
        Class<?> c =null;
        try {
             c = Class.forName("com.valleau.irc.command." + msg.getCommand());
            exe = (Executable) c.getConstructor().newInstance();
            return exe;
        }
        catch (Exception e) {
        	
        	 try {
                 c = Class.forName("com.francisbailey.irc.command." + msg.getCommand());
                exe = (Executable) c.getConstructor().newInstance();
            }
            catch (Exception e1) {
                throw new InvalidCommandException(msg.getMessage());
            }

        }

       
        return exe;
    }

}
