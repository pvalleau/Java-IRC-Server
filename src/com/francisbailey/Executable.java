package com.francisbailey;

/**
 * Created by fbailey on 04/11/16.
 */
public interface Executable {

    public void execute(Connection c, ClientMessage cm, ServerManager instance);

}
