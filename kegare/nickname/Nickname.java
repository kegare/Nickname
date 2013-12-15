package kegare.nickname;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.google.common.base.Strings;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod
(
	modid = "kegare.nickname"
)
@NetworkMod
(
	clientSideRequired = false,
	serverSideRequired = false
)
public class Nickname
{
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ForgeSubscribe
	public void onServerChat(ServerChatEvent event)
	{
		String message = event.message;
		EntityPlayerMP player = event.player;

		if (!Strings.isNullOrEmpty(message) && message.startsWith("@"))
		{
			player.getEntityData().setString("Nickname", message.substring(1));
			player.refreshDisplayName();
		}
	}

	@ForgeSubscribe
	public void onPlayerNameFormat(PlayerEvent.NameFormat event)
	{
		if (event.entityPlayer instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
			NBTTagCompound nbt = player.getEntityData();

			if (nbt.hasKey("Nickname"))
			{
				event.displayname = nbt.getString("Nickname");
			}
		}
	}
}