package kegare.nickname;

import com.google.common.base.Strings;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

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
	protected static final Properties nicknameTable = new Properties();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		try
		{
			File file = new File("nickname.xml");

			if (file.exists())
			{
				nicknameTable.clear();
				nicknameTable.loadFromXML(new FileInputStream(file));
			}
		}
		catch (Exception ignored) {}
	}

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

		if (!Strings.isNullOrEmpty(message) && message.startsWith("@nickname"))
		{
			try
			{
				String username = player.username;
				String nickname = message.substring(message.indexOf(' ') + 1);

				if (Strings.isNullOrEmpty(nickname) || nickname.equalsIgnoreCase("@nickname"))
				{
					throw new NullPointerException();
				}
				else
				{
					if ("default".equalsIgnoreCase(nickname) || "username".equalsIgnoreCase(nickname))
					{
						nicknameTable.setProperty(username, username);

						player.sendChatToPlayer(ChatMessageComponent.createFromText("Your nickname has been set to \"" + username + "\"").setColor(EnumChatFormatting.GRAY).setItalic(true));
					}
					else
					{
						nicknameTable.setProperty(username, nickname);

						player.sendChatToPlayer(ChatMessageComponent.createFromText("Your nickname has been set to \"" + nickname + "\"").setColor(EnumChatFormatting.GRAY).setItalic(true));
					}

					if (!nicknameTable.isEmpty())
					{
						nicknameTable.storeToXML(new FileOutputStream(new File("nickname.xml")), null);
					}

					player.refreshDisplayName();
				}
			}
			catch (Exception e)
			{
				player.sendChatToPlayer(ChatMessageComponent.createFromText("Usage: @nickname <[Nickname]|default>").setColor(EnumChatFormatting.DARK_GRAY));
			}

			event.setCanceled(true);
		}
	}

	@ForgeSubscribe
	public void onPlayerNameFormat(PlayerEvent.NameFormat event)
	{
		if (event.entityPlayer instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
			String username = player.username;

			if (nicknameTable.containsKey(username))
			{
				event.displayname = nicknameTable.getProperty(username, username);
			}
		}
	}
}