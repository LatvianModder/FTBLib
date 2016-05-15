package ftb.lib.mod.net;

import ftb.lib.FTBLib;
import ftb.lib.ReloadType;
import ftb.lib.api.ForgeWorldMP;
import ftb.lib.api.ForgeWorldSP;
import ftb.lib.api.GameModes;
import ftb.lib.api.events.ReloadEvent;
import ftb.lib.api.events.SyncEvent;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.lib.api.net.MessageToClient;
import ftb.lib.api.notification.ClientNotifications;
import ftb.lib.api.notification.Notification;
import ftb.lib.mod.FTBLibLang;
import ftb.lib.mod.FTBLibMod;
import ftb.lib.mod.client.FTBLibModClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageReload extends MessageToClient<MessageReload>
{
	public int typeID;
	public boolean login;
	public String modeID;
	public NBTTagCompound tag;
	
	public MessageReload() { }
	
	public MessageReload(ReloadType t, EntityPlayerMP ep, boolean l)
	{
		typeID = t.ordinal();
		login = l;
		modeID = ForgeWorldMP.inst.getMode().getID();
		tag = SyncEvent.generateData(ep, login);
	}
	
	@Override
	public LMNetworkWrapper getWrapper()
	{ return FTBLibNetHandler.NET; }
	
	@Override
	public void fromBytes(ByteBuf io)
	{
		typeID = io.readUnsignedByte();
		login = io.readBoolean();
		modeID = readString(io);
		tag = readTag(io);
	}
	
	@Override
	public void toBytes(ByteBuf io)
	{
		io.writeByte(typeID);
		io.writeBoolean(login);
		writeString(io, modeID);
		writeTag(io, tag);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage(MessageReload m, Minecraft mc)
	{
		long ms = System.currentTimeMillis();
		
		ReloadType type = ReloadType.values()[m.typeID];
		
		boolean first = ForgeWorldSP.inst == null;
		if(first) { ForgeWorldSP.inst = new ForgeWorldSP(mc.getSession().getProfile()); }
		
		ForgeWorldSP.inst.setModeRaw(m.modeID);
		SyncEvent.readData(m.tag, m.login);
		
		//TODO: new EventFTBWorldClient(ForgeWorldSP.inst).post();
		
		if(type.reload(Side.CLIENT))
		{
			reloadClient(ms, type, m.login);
		}
		else if(type == ReloadType.SERVER_ONLY_NOTIFY_CLIENT)
		{
			Notification n = new Notification("reload_client_config", FTBLibLang.reload_client_config.textComponent(), 7000);
			n.title.getStyle().setColor(TextFormatting.WHITE);
			n.desc = new TextComponentString('/' + FTBLibModClient.reload_client_cmd.getAsString());
			n.setColor(0xFF333333);
			ClientNotifications.add(n);
		}
	}
	
	public static void reloadClient(long ms, ReloadType type, boolean login)
	{
		if(ms == 0L) { ms = System.currentTimeMillis(); }
		GameModes.reload();
		EntityPlayer ep = FTBLibMod.proxy.getClientPlayer();
		ReloadEvent event = new ReloadEvent(ForgeWorldSP.inst, ep, type, login);
		if(FTBLib.ftbu != null) { FTBLib.ftbu.onReloaded(event); }
		MinecraftForge.EVENT_BUS.post(event);
		
		if(!login)
		{
			FTBLibLang.reload_client.printChat(ep, (System.currentTimeMillis() - ms) + "ms");
		}
		
		FTBLibMod.logger.info("Current Mode: " + ForgeWorldSP.inst.getMode());
	}
}