package com.valleau.pierre.irc.server.command;

import com.francisbailey.irc.Channel;
import com.francisbailey.irc.Connection;
import com.francisbailey.irc.Executable;
import com.francisbailey.irc.ServerManager;
import com.francisbailey.irc.message.ClientMessage;
import com.francisbailey.irc.message.ServerMessage;
import com.francisbailey.irc.message.ServerMessageBuilder;

import java.util.ArrayList;


/**
 * Created by Pierre Valleau on 20/2/2023.
 *CAP message

     Command: CAP
  Parameters: <subcommand> [:<capabilities>]

The CAP command is used for capability negotiation between a server and a client.

The CAP message may be sent from the server to the client.

For the exact semantics of the CAP command and subcommands, please see the Capability Negotiation specification.
 */
public class CAP implements Executable {


    public void execute(Connection connection, ClientMessage clientMessage, ServerManager server) {

        String para1 = clientMessage.getParameter(0);
        String version = null;
        if (clientMessage.getParameterCount()>1)
        version = clientMessage.getParameter(1);
        
        if ("LS".equals(para1)) 
        {
        	/* The CAP LS subcommand

The LS subcommand is used to list the capabilities supported by the server. The client should send an LS subcommand with no other arguments to solicit a list of all capabilities.

If a server receives an LS subcommand while client registration is in progress, it MUST suspend registration until an END subcommand is received from the client.

When sent by the server, the last parameter is a space-separated list of capabilities (possibly with values, depending on the CAP LS Version described below). If no capabilities are available, an empty parameter MUST be sent.

Example:

Client: CAP LS
Server: CAP * LS :multi-prefix sasl

Example with no available capabilities:

Client: CAP LS
Server: CAP * LS :

CAP LS Version

The LS subcommand has an additional argument which is the version number of the latest capability negotiation protocol supported by the client. Newer versions of capability negotiation allow newer features, as described below:

Clients that send 302 as the CAP LS version are presumed to support CAP LS 302 features for the future life of the connection. Clients that do not send any version number with CAP LS are presumed to not support these extra features.

If a client has not indicated support for CAP LS 302 features, the server MUST NOT send these new features to the client.

When CAP version 302 is enabled, the client also implicitly indicates support for the cap-notify capability listed below, and support for the relevant NEW and DEL subcommands.

The CAP version number argument MUST be treated as a number by the server. If the client’s CAP version is not supported by the server, the server MUST enable the features it does support below the given version. For example, if a server supports 302 and 303 features, and a client sends version 304, the server would enable those 302 and 303 features (just as it would do if a client sent the 303 version number). However, if the server also supported a 306 feature, it would NOT enable this feature for the client.

If a client sends a higher CAP version at any time, the server MUST store the higher version. If a client sends a lower CAP version (or omits the version number entirely), servers SHOULD return a CAP LS reply consistent with the request’s version, but keep storing the original (higher) version.

Example:

Client: CAP LS 302
Server: CAP * LS :multi-prefix sasl=PLAIN,EXTERNAL
Client: CAP LS
Server: CAP * LS :multi-prefix sasl
... server should continue considering the client to support CAP LS version 302 ...

Example with a server supporting only 302 versioned features:

Client: CAP LS 307
Server: CAP * LS :multi-prefix sasl=PLAIN,EXTERNAL

CAP LS Version Features

As an overview, these are the new features introduced with each CAP LS version:
CAP 	Name 	Description
302 	Capability values 	Additional data with each capability name when advertised in CAP LS and CAP NEW.
302 	Multiline replies 	CAP LS and CAP LIST can be split across multiple lines, with a minor syntax change that allows clients to wait for the last message and process them together.
302 	cap-notify 	This capability is enabled implicitly with 302, and adds the CAP NEW and CAP DEL messages which let the client know about added and removed capabilities.
Capability Values

If the client supports CAP version 302, the server MAY specify additional data for each capability using the <name>=<value> format in CAP LS and CAP NEW replies, but MUST NOT specify additional data for any other CAP subcommands.

Each capability, if it supports a value, defines what this value means in its specification.

Example:

Client: CAP LS 302
Server: CAP * LS :multi-prefix sasl=PLAIN,EXTERNAL server-time draft/packing=EX1,EX2

Multiline replies to CAP LS and CAP LIST

If the client supports CAP version 302, the server MAY send multiple lines in response to CAP LS and CAP LIST. Clients that support CAP version 302 MUST handle the continuation format described as follows:

If the reply contains multiple lines (due to IRC line length limitations), and the client supports CAP version 302, all but the last reply MUST have a parameter containing only an asterisk (*) preceding the capability list. This lets clients know that more CAP lines are incoming, so that it can delay capability negotiation until it has seen all available server capabilities.

Example of a multiline LS reply to a client that supports CAP version 302:

Client: CAP LS 302
Server: CAP * LS * :multi-prefix extended-join account-notify batch invite-notify tls
Server: CAP * LS * :cap-notify server-time example.org/dummy-cap=dummyvalue example.org/second-dummy-cap
Server: CAP * LS :userhost-in-names sasl=EXTERNAL,DH-AES,DH-BLOWFISH,ECDSA-NIST256P-CHALLENGE,PLAIN

Example of a multiline LIST reply to a client that supports CAP version 302:

Client: CAP LIST
Server: CAP modernclient LIST * :example.org/example-cap example.org/second-example-cap account-notify
Server: CAP modernclient LIST :invite-notify batch example.org/third-example-cap
*/
        	int iversion=0;
        	if (version!=null)
        		iversion=Integer.parseInt(version);
            connection.send(ServerMessageBuilder
                .from("")
                .withReplyCode("")
                .andMessage("CAP * LS :")//CAP * LS :multi-prefix sasl    
                //CAP * LS :multi-prefix sasl=PLAIN,EXTERNAL server-time draft/packing=EX1,EX2
                .build()
                /*
                  	Server: CAP * LS * :multi-prefix extended-join account-notify batch invite-notify tls
					Server: CAP * LS * :cap-notify server-time example.org/dummy-cap=dummyvalue example.org/second-dummy-cap
					Server: CAP * LS :userhost-in-names sasl=EXTERNAL,DH-AES,DH-BLOWFISH,ECDSA-NIST256P-CHALLENGE,PLAIN
                 */
                 
            );
        }
        else  if ("REQ".equals(para1))
        {
        	/* The CAP REQ subcommand

The REQ subcommand is used to request a change in capabilities associated with the active connection. The last parameter is a space-separated list of capabilities. Each capability identifier may be prefixed with a dash (-) to designate that the capability should be disabled.

If a client requests a capability which is already enabled, or tries to disable a capability which is not enabled, the server MUST continue processing the REQ subcommand as though handling this capability was successful.

The capability identifier set must be accepted as a whole, or rejected entirely.

Clients SHOULD ensure that their list of requested capabilities is not too long to be replied to with a single ACK or NAK message. If a REQ’s final parameter gets sufficiently large (approaching the 510 byte limit), clients SHOULD instead send multiple REQ subcommands.

If a server receives a REQ subcommand while client registration is in progress, it MUST suspend registration until an END subcommand is received.

Example adding a capability:

Client: CAP REQ :multi-prefix sasl
Server: CAP * ACK :multi-prefix sasl

Example removing a capability:

Client: CAP REQ :-userhost-in-names
Server: CAP * ACK :-userhost-in-names
*/
        	 connection.send(ServerMessageBuilder
                     .from("")
                     .withReplyCode("")
                     .andMessage("CAP * NAK :"+para1)//CAP * LS :multi-prefix sasl    
                     //CAP * LS :multi-prefix sasl=PLAIN,EXTERNAL server-time draft/packing=EX1,EX2
                     .build());
        	 /* The CAP NAK subcommand

The NAK subcommand designates that the requested capability change was rejected. The server MUST NOT make any change to any capabilities if it replies with a NAK subcommand.

The last parameter is a space-separated list of capabilities.

Example:

Client: CAP REQ :multi-prefix sasl ex3
Server: CAP * NAK :multi-prefix sasl ex3
*/
        	 
        	 /* The CAP ACK subcommand

The ACK subcommand is sent by the server to acknowledge a client-sent REQ, and let the client know that their requested capabilities have been enabled.

The last parameter is a space-separated list of capabilities. Each capability name may be prefixed with a dash (-), indicating that this capability has been disabled as requested.

If an ACK reply originating from the server is spread across multiple lines, a client MUST NOT change capabilities until the last ACK of the set is received. Equally, a server MUST NOT change the capabilities of the client until the last ACK of the set has been sent.
*/
        }
        else  if ("LIST".equals(para1))
        {
        	/* The CAP LIST subcommand

The LIST subcommand is used to list the capabilities enabled on the client’s connection. The client should send a LIST subcommand with no other arguments to solicit a list of enabled capabilities.

When sent by the server, the last parameter is a space-separated list of capabilities. If no capabilities are enabled, an empty parameter must be sent.

Example:

Client: CAP LIST
Server: CAP * LIST :multi-prefix

Example with no enabled capabilities:

Client: CAP LIST
Server: CAP * LIST :
*/
        	 connection.send(ServerMessageBuilder
                     .from("")
                     .withReplyCode("")
                     .andMessage("CAP * LIST :")//CAP * LS :multi-prefix sasl    
                     //CAP * LS :multi-prefix sasl=PLAIN,EXTERNAL server-time draft/packing=EX1,EX2
                     .build());
        }
        else  if ("ACK".equals(para1))
        {
        }
        else  if ("NAK".equals(para1))
        {
        }
        else  if ("NEW".equals(para1))
        {
        	/* The CAP NEW subcommand

The NEW subcommand MUST ONLY be sent to clients that have negotiated CAP version 302 or enabled the cap-notify capability.

The NEW subcommand signals that the server supports one or more new capabilities, and may be sent at any time. Clients that support CAP NEW messages SHOULD respond with a CAP REQ message if they wish to enable one or more of the newly-offered capabilities.

The format of a CAP NEW message is:

CAP <nick> NEW :<extension 1> [<extension 2> ... [<extension n>]]

As with LS, the last parameter is a space-separated list of new capabilities that are now offered by the server. If the client supports CAP version 302, the capabilities SHOULD be listed with values, as in the CAP LS response.

Example:

Server: :irc.example.com CAP modernclient NEW :batch

Example with following REQ:

Server: :irc.example.com CAP tester NEW :away-notify extended-join
Client: CAP REQ :extended-join away-notify
Server: :irc.example.com CAP tester ACK :extended-join away-notify
*/
        }
        else  if ("DEL".equals(para1))
        {
        	/* The CAP DEL subcommand

The DEL subcommand MUST ONLY be sent to clients that have negotiated CAP version 302 or enabled the cap-notify capability.

The DEL subcommand signals that the server no longer supports one or more capabilities that have been advertised. Upon receiving a CAP DEL message, the client MUST treat the listed capabilities as cancelled and no longer available. Clients SHOULD NOT send CAP REQ messages to cancel the capabilities in CAP DEL, as they have already been cancelled by the server.

Servers MUST cancel any capability-specific behavior for a client after sending the CAP DEL message to the client.

Clients MUST gracefully handle situations when the server removes support for any capability.

The format of a CAP DEL message is:

CAP <nick> DEL :<extension 1> [<extension 2> ... [<extension n>]]

The last parameter is a space-separated list of capabilities that are no longer available.

Example:

Server: :irc.example.com CAP modernclient DEL :userhost-in-names multi-prefix away-notify
*/
        }
        else  if ("END ".equals(para1))
        {
        	/* The CAP END subcommand

The END subcommand signals to the server that capability negotiation is complete and requests that the server continue with client registration. If the client is already registered, this command MUST be ignored by the server.*/
        }
        else
        	
        {
                    connection.send(ServerMessageBuilder
                            .from(server.getName())
                            .withReplyCode(ServerMessage.ERR_INVALIDCAPCMD)
                            .andMessage(connection.getClientInfo().getNick() + " " + para1 + " :Invalid CAP command")
                            .build());
                    
                

        }
    }



    public int getMinimumParams() {
        return 1;
    }



    public boolean canExecuteUnregistered() {
        return true;
    }



}
