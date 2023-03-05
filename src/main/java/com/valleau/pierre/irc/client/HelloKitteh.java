package com.valleau.pierre.irc.client;

import org.kitteh.irc.client.library.Client.Builder.Server;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.Client.Builder;


public class HelloKitteh {
    public static void main(String[] args) {
    	Builder b = Client.builder().nick("KittehBot");
    	Builder.Server n = b.server();
        Client client = (Server)( Client.builder().nick("KittehBot"))
        		.host("127.0.0.1").then().buildAndConnect();

        client.addChannel("#kitteh.org");
        client.sendMessage("#kitteh.org", "Hello World!");
    }
}