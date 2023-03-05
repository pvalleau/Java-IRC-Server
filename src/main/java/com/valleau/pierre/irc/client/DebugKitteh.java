package com.valleau.pierre.irc.client;

import java.text.SimpleDateFormat;

import org.kitteh.irc.client.library.Client;

public class DebugKitteh {
    public static void main(String[] args) {
        Client.Builder builder = Client.builder();

        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        builder.listeners().input(line -> System.out.println(sdf.format(new Date()) + ' ' + "[I] " + line));
        builder.listeners().output(line -> System.out.println(sdf.format(new Date()) + ' ' + "[O] " + line));
        builder.listeners().exception(Throwable::printStackTrace);
        // and then build() or buildAndConnect()
    }
}