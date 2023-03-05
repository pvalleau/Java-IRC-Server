package com.valleau.pierre.irc.server.services;

import com.francisbailey.irc.Client;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.RegisteredClient;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

/**
 * @author Pierre Valleau
 *Created  on 05/03/2023
 *
 *https://fr.wikipedia.org/wiki/Services_IRC
  NickServ gère les comptes utilisateurs, leur permettant de s'enregistrer et de s'authentifier.
  
  https://www.astucesinternet.com/modules/news/article.php?storyid=39
  
  * @todo develop it
  * */
public class Nickserv  implements IIrcservice, Executable    {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/** function called during /nick command */
	public static boolean Allow(Connection connection, String nick2, ServerManager server)
	{
		 String nick=connection.getClientInfo().getNick();
		 RegisteredClient cr=server.getRegister().get(nick2);        	 
		 			 
		 if ("ON".equalsIgnoreCase(cr.getProperty("SECURE")))
		 {return false;}
				
		 if ((cr.getProperty("KILL")==null) || "ON".equalsIgnoreCase(cr.getProperty("KILL")))
		 { connection.send(ServerMessageBuilder
                     .from(server.getName())
                     .withReplyCode(ServerMessage.RPL_NICK)
                     .andMessage("Welcome "+connection.getClientInfo().getNick()+", you have 60 secondes to IDENTIFY or change your nick." )
                     .build()
                 );
				return true;
				}
		 if ("OFF".equalsIgnoreCase(cr.getProperty("KILL")))
		 {return true;}
		 if ("QUICK".equalsIgnoreCase(cr.getProperty("KILL")))
		 { connection.send(ServerMessageBuilder
                     .from(server.getName())
                     .withReplyCode(ServerMessage.RPL_NICK)
                     .andMessage("Welcome "+connection.getClientInfo().getNick()+", you have 30 secondes to IDENTIFY or change your nick." )
                     .build()
                 );
				return true;
				}
		 
		 return false;		 
	};
	@Override
	public String help() {
		return "FR:\r\n"
				+ " NickServ\r\n"
				+ "\r\n"
				+ "Le protocole IRC ne vous permet pas d'enregistrer votre surnom. Cela veut dire que tant que vous êtes connecté, votre surnom est utilisé et personne d'autre ne peut l'utiliser. Mais dès que vous vous déconnecterez, d'autres personnes peuvent réutiliser votre surnom, et se faire potentiellement passer pour vous. Pour résoudre ce problème, il existe une solution à base de bot (un programme se connectant à IRC comme vous, mais qui reste toujours connecté) qui vous permet d'enregistrer votre surnom pour que seul vous puissiez l'utiliser. Ce programme s'appelle NickServ.\r\n"
				+ "\r\n"
				+ "Pour commencer, connectez vous sur un serveur IRC et choisissez un surnom libre. Ensuite, il vous suffit d'enregistrer votre surnom en utilisant la commande /msg nickserv REGISTER mot_de_passe email en remplaçant mot_de_passe par un mot de passe de votre choix et email par votre adresse email (selon les serveurs IRC, l'email peut être facultatif).\r\n"
				+ "\r\n"
				+ "Dorénavant, chaque fois que quelqu'un arrivera sur le serveur et voudra utiliser votre surnom (vous y compris), il aura un message de NickServ l'avertissant que ce surnom est réservé et qu'il a 3 minutes pour indiquer le bon mot de passe à NickServ, sous peine de voir son surnom modifié de force.\r\n"
				+ "\r\n"
				+ "Vous devrez alors indiquer à NickServ votre mot de passe à chaque connexion, grâce à la commande /msg nickserv IDENTIFY mot_de_passe où mot_de_passe est le mot de passe que vous aviez choisi."
				+ "";
	}

	@Override
	public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {
		String cmd = clientMessage.getParameter(1);
        String nick=connection.getClientInfo().getNick();
        
         if("REGISTER".equalsIgnoreCase(cmd))  
         {
        	 String pwd=clientMessage.getParameter(2);        
             String email=null;
             if (clientMessage.getParameterCount()>3)
             email=clientMessage.getParameter(3);
             
             if (pwd.length()<13 || !pwd.matches("%d") || !pwd.matches("%w")|| !pwd.matches("[+-_&é'(-è_%µ§=<>]+"))
				 connection.send(ServerMessageBuilder
	                     .from(server.getName())
	                     .withReplyCode(ServerMessage.ERR_NOTREGISTERED)
	                     .andMessage("pass word too easy please enter a longer(>13) password, with at least 2 uppercases, 2lowercases, 2 special chacracter(+-_&é'(-è_%µ§=<>), 2 numbers" )
	                     .build()
	                 );
			 else
			 {
        	 RegisteredClient c=new RegisteredClient(connection.getClientInfo(),email,pwd);
        	 RegisteredClient cr=server.getRegister().get(c);
        	 if (cr!=null)
        		 connection.send(ServerMessageBuilder
                         .from(server.getName())
                         .withReplyCode(ServerMessage.ERR_ALREADYREGISTERED)
                         .build()
                     );
        	 else   
        	 {
        	 server.getRegister().add(c);
        	 server.registerConnection(connection, connection.getClientInfo());
        	 connection.send(ServerMessageBuilder
                     .from(server.getName())
                     .withReplyCode(ServerMessage.RPL_NICK)
                     .andMessage("Welcome "+connection.getClientInfo().getNick()+" !" )
                     .build()
                 );
        	 }
        	
         }}
         else if("IDENTIFY".equalsIgnoreCase(cmd))  
         {
        	 String pwd=clientMessage.getParameter(2);        
             RegisteredClient cr=server.getRegister().get(connection.getClientInfo());        	 
        	 if (cr.isgoodPassword(pwd))
    		 {
        		 server.registerConnection(connection, connection.getClientInfo());
        		 connection.send(ServerMessageBuilder
                         .from(server.getName())
                         .withReplyCode(ServerMessage.RPL_NICK)
                         .andMessage("Welcome "+connection.getClientInfo().getNick()+" !" )
                         .build()
                     );
    		 } else
    			 connection.send(ServerMessageBuilder
                         .from(server.getName())
                         .withReplyCode(ServerMessage.ERR_PASSWDMISMATCH)
                         .andMessage("Bad passWord "+connection.getClientInfo().getNick()+" !" )
                         .build()
                     );
         }
         else if("info".equalsIgnoreCase(cmd))  
         {
        	 RegisteredClient rc=server.getRegister().get(connection.getClientInfo());
        	 if (rc==null)
        	 {
        		 
        	 }
        	 else
        	 connection.send(ServerMessageBuilder
                     .from(server.getName())
                     .withReplyCode(ServerMessage.RPL_NICK)
                     .andMessage(rc.info() )
                     .build()
                 ); 
         }
         else if("GHOST".equalsIgnoreCase(cmd))  
         {
        	 nick=clientMessage.getParameter(2);        
        	 String pwd=clientMessage.getParameter(3);        
             RegisteredClient cr=server.getRegister().get(nick);        	 
        	 if (cr!=null && cr.isgoodPassword(pwd))
        	 {
        		 server.Kick(nick, connection.getClientInfo().getNick() +" request a GHOST for "+nick);
        	 }
         }
         else if("STATUS".equalsIgnoreCase(cmd))  
         {
        	 String msg="";
        	 RegisteredClient rc=server.getRegister().get(connection.getClientInfo());
        	 boolean bconnected=server.isConnected(nick);
        	  if (rc!=null)
        	  {
        		  if(bconnected)
        			  msg=3+" identified by IDENTIFY";        		  
        	  else
        		  msg=2+" identified by REGISTER";
        	  }
        	  else
        	  {
        		  if(bconnected)
        			  msg=1+" not identified";        		  
        	  else
        		  msg=0+" off line";
        	  }
        		
        	 connection.send(ServerMessageBuilder
                     .from(server.getName())
                     .withReplyCode(ServerMessage.RPL_PRIVMSG)
                     .andMessage(msg )
                     .build()
                 ); 
        	 
         }
         else if("DROP".equalsIgnoreCase(cmd))  
         {
        	 String nick2 = clientMessage.getParameter(2);
        	 if (!connection.isRegistered())
    			 connection.send(ServerMessageBuilder
	                     .from(server.getName())
	                     .withReplyCode(ServerMessage.ERR_NOTREGISTERED)
	                     .andMessage("not registered" )
	                     .build()
	                 );
        	 else 
        		 if (nick2.equals(nick))
        		 {
        			 server.getRegister().remove(nick);
        			 server.closeConnection( connection);//side effect close connection
        		 }
        	 
         }
         else if("RECOVER".equalsIgnoreCase(cmd))  
         {
        	 String nick2 = clientMessage.getParameter(2);
        	 String pwd = clientMessage.getParameter(3);
        	 RegisteredClient rc = server.getRegister().get(nick2);
        	 
        	 if (!rc.isgoodPassword(pwd))
    			 connection.send(ServerMessageBuilder
	                     .from(server.getName())
	                     .withReplyCode(ServerMessage.ERR_PASSWDMISMATCH)
	                     .andMessage(" bad password" )
	                     .build()
	                 );
        	 else         		
        		 {
        		 Connection rober=server.findConnectionByNick(nick2) ;
        		 Connection me=server.findConnectionByNick(nick);
        		 if (me!=null && rober!=null)
        		 {
        		 rober.getClientInfo().setNick(nick2+"-"+(int)(Math.random()*1000000.0));
        		 
        		 rober.send(ServerMessageBuilder
	                     .from(server.getName())
	                     .withReplyCode(ServerMessage.ERR_NICKCOLLISION)
	                     .andMessage(" Bad Nick, change to "+rober.getClientInfo().getNick() )
	                     .build()
	                 );
        		 me.getClientInfo().setNick(nick2);
        		 server.registerConnection(connection, rc);
        		 connection.send(ServerMessageBuilder
	                     .from(server.getName())
	                     .withReplyCode(ServerMessage.RPL_NICK)
	                     .andMessage(" Nick updated" )
	                     .build()
	                 );
        		 }
        		 }
        	 
         }
         else if("SET".equalsIgnoreCase(cmd))  
         {
        	 if (!connection.isRegistered())
    			 connection.send(ServerMessageBuilder
	                     .from(server.getName())
	                     .withReplyCode(ServerMessage.ERR_NOTREGISTERED)
	                     .andMessage("not registered" )
	                     .build()
	                 );
        	 else
        	 {
        	 String key = clientMessage.getParameter(2).toUpperCase();
        	 String value = clientMessage.getParameter(3);
        	 
        	 RegisteredClient rc=server.getRegister().get(connection.getClientInfo());
        	
        	 if (rc!=null)
        	 {
        		 if ("PASSWORD".equalsIgnoreCase(key))
        		 {
        			 if (key.length()<13 || !key.matches("%d") || !key.matches("%w")|| !key.matches("[+-_&é'(-è_%µ§=<>]+"))
        				 connection.send(ServerMessageBuilder
        	                     .from(server.getName())
        	                     .withReplyCode(ServerMessage.ERR_NOTREGISTERED)
        	                     .andMessage("pass word too easy please enter a longer(>13) password, with at least 2 uppercases, 2lowercases, 2 special chacracter(+-_&é'(-è_%µ§=<>), 2 numbers" )
        	                     .build()
        	                 );
        			 else
        				 
        			 rc.setPassword(value);
        			  
        		 }
        		 else
        		 rc.putProperty(key, value);
        		 }
        	 /*
        	  * 

PASSWORD : Change le mot de passe de votre nick
/msg nickserv SET PASSWORD nouveau_mot_de_passe
Remplacez nouveau_mot_de_passe par votre nouveau mot de passe


LANGUAGE : Change la langue dans laquelle les Services vous envoient leurs messages
Pour mettre les services en français : /msg nickserv set language 2


URL : Associe une adresse de site à votre pseudo
/msg nickserv SET URL adresse
remplacez adresse par l'URL de votre site web


EMAIL : Associe un e-mail à votre nick
/msg nickserv SET EMAIL adresse
Remplacez adresse par votre adresse Email


ICQ : Associe un numéro ICQ à votre nick
/msg nickserv SET ICQ numéro
Remplacez numéro par votre numéro ICQ


GREET : Associe un message d'accueil à votre pseudo (ne marche que sur un chan où l'option est activée)
/msg nickserv SET GREET message
Remplacez message par votre message d'accueil.


INFO : Associe un message informatif aux données renvoyées par la commande INFO.
/msg nickserv SET INFO message
Remplacez message par votre message d'information


KILL : Active ou désactive la protection de votre pseudo.
/msg nickserv SET KILL {ON | QUICK | IMMED | OFF}
ON : Un utilisateur qui prendra votre pseudo aura 60 secondes pour en changer.
QUICK : Un utilisateur qui prendra votre pseudo aura 30 secondes pour en changer.
IMMED : Si quelqu'un vous prend votre pseudo, ce dernier sera changé de force par NickServ, et ce, sans attendre.
OFF : désactive la protection de votre pseudo.


SECURE : Active ou désactive la sécurité du pseudo
/msg nickserv SET SECURE {ON | OFF}
ON : active la sécurité, vous devrez obligatoirement entrer votre mot de passe pour être reconnu en tant que propriétaire du pseudo et votre masque doit être dans la liste d'accès.
OFF : désactive la sécurité.


PRIVATE : Empêche votre pseudo d'être affiché par /msg NickServ LIST
/msg nickserv SET PRIVATE {ON | OFF}
ON : Active le mode privé. Votre pseudo n'apparaîtra pas dans les listes générées par la commande LIST de NickServ.


HIDE : Cache certaines informations du pseudo
/msg nickserv SET HIDE option ON : pour activer une option
/msg nickserv SET HIDE option OFF : pour désactiver une option
Remplacez option par :
EMAIL : Masque votre adresse Email
USERMASK : Masque votre user@host vu pour la dernière fois
QUIT : Masque la dernière raison de /quit


HOST : Personnalise votre nom d'hôte personnalisé visible dans le /whois.
/msg nickserv SET HOST {ON | OFF}


MSG : Change le mode de communication des Services
/msg nickserv SET MSG {ON | OFF}
Si l'option est activée, les services communiqueront avec vous par des messages, sinon, ce sera par des notices.
*/
        	 }
         }
         else if("RELEASE".equalsIgnoreCase(cmd))  
         {}
         else if("SENDPASS".equalsIgnoreCase(cmd))  
         {}
         else if("GLIST".equalsIgnoreCase(cmd))  
         {}
         else if("LIST".equalsIgnoreCase(cmd))  
         {
        	 
         }
         else if("LOGOUT".equalsIgnoreCase(cmd))  
         {}
         else if("GROUP".equalsIgnoreCase(cmd))  
         {}
         else if("ACCESS".equalsIgnoreCase(cmd))  
         {}
         else if("DISPLAY".equalsIgnoreCase(cmd))  
         {}
         else if("HELP".equalsIgnoreCase(cmd))  
         {
        	 connection.send(ServerMessageBuilder
                     .from(server.getName())
                     .withReplyCode(ServerMessage.ERR_NOTREGISTERED)
                     .andMessage(help() )
                     .build()
                 );
         }
         
	}

	@Override
	public int getMinimumParams() {		
		return 3;
	}

	@Override
	public boolean canExecuteUnregistered() {
		// TODO Auto-generated method stub
		return false;
	}

}
