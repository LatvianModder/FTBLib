package com.feed_the_beast.ftblib.cmd;

import com.feed_the_beast.ftblib.cmd.team.CmdTeam;
import com.feed_the_beast.ftblib.events.RegisterFTBCommandsEvent;
import com.feed_the_beast.ftblib.lib.cmd.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;

/**
 * @author LatvianModder
 */
public class CmdFTB extends CmdTreeBase
{
	public CmdFTB(boolean dedi)
	{
		super("ftb");
		addSubcommand(new CmdReload());
		addSubcommand(new CmdMySettings());
		addSubcommand(new CmdTeam());
		addSubcommand(new CmdNotify());

		if (CommonUtils.DEV_ENV)
		{
			addSubcommand(new CmdAddFakePlayer()); //FIXME: Implement server created teams and players
		}

		new RegisterFTBCommandsEvent(this, dedi).post();
	}
}