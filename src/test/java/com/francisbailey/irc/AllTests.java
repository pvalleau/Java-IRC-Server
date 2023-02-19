package com.francisbailey.irc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.francisbailey.irc.command.*;
import com.francisbailey.irc.mode.strategy.*;

@RunWith(Suite.class)
@SuiteClasses({ ChannelTest.class, ConfigTest.class,
	CHANMODETest.class ,
	//CommandTest.class ,
	JOINTest.class ,TOPICTest.class ,OPERTest.class ,USERMODETest.class ,WHOTest.class ,
	ChannelModeArgStrategyTest.class ,
	ChannelUserModeStrategyTest.class ,
	//ModeStrategyTest.class ,
	StandardChannelModeStrategyTest.class ,
	StandardUserModeStrategyTest.class ,
	
})
public class AllTests {

}
