package com.kegare.nickname;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Mod(modid = "kegare.nickname")
public class Nickname
{
	private static final Map<String, String> nicknames = Maps.newHashMap();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		try
		{
			Map<String, String> map = new Gson().fromJson(new FileReader(new File("nickname.json")), Map.class);

			nicknames.clear();
			nicknames.putAll(map);
		}
		catch (Exception e) {}
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event)
	{
		String message = event.message;
		EntityPlayerMP player = event.player;

		if (!Strings.isNullOrEmpty(message) && message.startsWith("@nickname"))
		{
			try
			{
				String uuid = player.getUniqueID().toString();
				String username = player.getGameProfile().getName();
				String nickname = message.substring(message.indexOf(' ') + 1);

				if (Strings.isNullOrEmpty(nickname) || nickname.equalsIgnoreCase("@nickname"))
				{
					throw new NullPointerException();
				}
				else
				{
					if ("default".equalsIgnoreCase(nickname) || "username".equalsIgnoreCase(nickname))
					{
						nicknames.put(uuid, username);

						player.addChatMessage(new ChatComponentText("Your nickname has been set to \"" + username + "\"").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true)));
					}
					else
					{
						nicknames.put(uuid, nickname);

						player.addChatMessage(new ChatComponentText("Your nickname has been set to \"" + nickname + "\"").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true)));
					}

					if (!nicknames.isEmpty())
					{
						Gson gson = new GsonBuilder().setPrettyPrinting().create();
						String raw = gson.toJson(nicknames, Map.class);
						FileOutputStream output = new FileOutputStream(new File("nickname.json"));

						output.write(raw.getBytes());
						output.flush();
						output.close();
					}

					player.refreshDisplayName();
				}
			}
			catch (Exception e)
			{
				player.addChatMessage(new ChatComponentText("Usage: @nickname <[Nickname]|default>").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GRAY)));
			}

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerNameFormat(PlayerEvent.NameFormat event)
	{
		if (event.entityPlayer instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
			String uuid = player.getUniqueID().toString();

			if (nicknames.containsKey(uuid))
			{
				event.displayname = nicknames.get(uuid);
			}
		}
	}

	@NetworkCheckHandler
	public boolean netCheckHandler(Map<String, String> mods, Side side)
	{
		return true;
	}
}