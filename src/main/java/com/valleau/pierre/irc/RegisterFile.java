/**
 * 
 */
package com.valleau.pierre.irc;

import java.util.ArrayList;
import java.util.Calendar;

import com.francisbailey.irc.Client;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.RegisteredClient;

/**
 * @author Pierre Valleau
 *
 */
public class RegisterFile {

	private ArrayList<RegisteredClient> registeredClient;
    
	public RegisteredClient get(Client c) {
		RegisteredClient rc=null;
		if (RegisteredClient.class.isInstance(c))
		{
			rc=(RegisteredClient) c;
			int i=registeredClient.indexOf(rc);
			if (i>=0)
			   return registeredClient.get(i);
		}
		
		return get(c.getNick());
		
	}
	public RegisteredClient get(String nick) {
		if (nick==null) return null;
		
		RegisteredClient rc=null;
		for(RegisteredClient c:registeredClient)
		{
			if (nick.equals(c.getNick()))
				return c;
		}
		
		return null;
		
	}

	public void add(RegisteredClient c) {
		if (RegisteredClient.class.isInstance(c))
		  registeredClient.add(c);
		
		
	}
	/** remove a nick*/
	public void remove(String nick) {
		RegisteredClient c = get( nick);
		if (c!=null)
		registeredClient.remove(c);
		
	}

}
