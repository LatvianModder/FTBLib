package ftb.lib.api;

import java.util.List;

import latmod.lib.FastList;

public class EventPlayerActionButtons extends EventLM
{
	public final int playerID;
	public final boolean self;
	public final boolean addAll;
	public final List<PlayerAction> actions;
	
	public EventPlayerActionButtons(int id, boolean s, boolean a)
	{
		playerID = id;
		self = s;
		addAll = a;
		actions = new FastList<PlayerAction>();
	}
}