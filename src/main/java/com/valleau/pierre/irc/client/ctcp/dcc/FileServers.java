/**
 * 
 */
package com.valleau.pierre.irc.client.ctcp.dcc;

/**
 * @author Pierre Valleau
 *
 *https://en.wikipedia.org/wiki/Direct_Client-to-Client
 *File servers (FSERVs)

A DCC fserve, or file server, lets a user browse, read and download files located on a DCC server.

Typically, this is implemented with a DCC CHAT session (which presents the user with a command prompt) or special CTCP commands to request a file. The files are sent over DCC SEND or DCC XMIT. There are many implementations of DCC file servers, among them is the FSERV command in the popular mIRC client. 
 
https://www.mirc.com/help/html/index.html?file_server.html

File Server

 

The mIRC fileserver allows other users to access files on your hard disk and is therefore dangerous since if used improperly it will allow them to access private/confidential information.

 

The /fserve command

A fileserver is initiated by using the /fserve command which initiates a DCC Chat to the specified user.

 

/fserve <nickname> <maxgets> <homedir> [textfile]

 

nickname is the user's nickname.

maxgets is the maximum number of simultaneous dcc gets that a user can have during a fileserver session.

homedir is the home directory that contains the files and directories that you want to allow the user to access.

textfile is a text file that contains a welcome message that is displayed to the user when they first connect.

 

/fserve goat 5 server welcome.txt

 

The above command will initiate a filserver session to user goat, with a maximum of five simultaneous dcc gets, the homedir as server, and will send goat the welcome message in the welcome.txt file.

 

In each directory, you can place a dirinfo.srv file which describes that directory. Each time the user performs a CD to change into a directory, mIRC will look for this file and if it finds it, the text in it will be sent to the user.

 

Fileserver commands

The commands available to a user connected to your fileserver are:

 

cd <directory> - change to the specified directory.

 

dir [-b|k] [-#] [/w] - lists the name and size of each file in the current directory. The /w switch forces a wide listing. The [-b|k] selects bytes or k's. The [-#] specifies the number of files on each line in a horizontal listing.

 

ls [-b|k] [-#] - lists the name of each file in the current directory using a wide listing.

 

get <filename> - asks the fileserver to DCC Send the specified file.

 

read [-numlines] <filename.txt> - reads the specified text file. The user will be sent a default of 20 lines and then prompted whether to continue listing. The -numlines option changes the default number of lines to a value between 5 and 50.

 

help - lists the available commands.

 

exit or bye - terminates the connection.

 

Note:

1. If a directory has a large number of files try to split them up into subdirectories, this will improve performance.

2. If a user is idle for too long the fileserver will automatically close the connection. You can set the idle time out in the DCC Options dialog.

3. A user is limited to opening a single fileserver session at any one time. If mIRC initiates a fileserver session to a user and that user does not respond then the fileserver session will have to time-out and close before that user can ask for another session.
 */
public class FileServers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
