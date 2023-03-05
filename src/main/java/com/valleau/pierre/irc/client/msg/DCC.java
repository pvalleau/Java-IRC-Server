/**
 * 
 */
package com.valleau.pierre.irc.client.msg;

/**
 * @author Pierre Valleau
 *
 *https://modern.ircdocs.horse/dcc.html
 */
public class DCC {
	CTCP dcc;
	/**
	 * https://fr.wikipedia.org/wiki/Direct_client-to-client
	 */
	public DCC() {
		dcc=new CTCP();
		
	}
	String sendQuery(String type, String argument, String  host, String  port)
	{
		
		return dcc.sendDCC(null, null, null, type, argument, host, port);
		
	}

}
