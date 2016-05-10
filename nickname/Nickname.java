package nickname;

import java.util.Map;

import com.google.common.base.Strings;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "nickname")
public class Nickname
{
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		NicknameManager.loadNicknames();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandNickname());
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event)
	{
		String message = event.getMessage();
		EntityPlayerMP player = event.getPlayer();

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
						NicknameManager.setNickname(uuid, username);

						TextComponentString text = new TextComponentString("Your nickname has been set to \"" + username + "\"");
						text.getChatStyle().setColor(TextFormatting.GRAY).setItalic(Boolean.valueOf(true));

						player.addChatMessage(text);
					}
					else
					{
						NicknameManager.setNickname(uuid, nickname);

						TextComponentString text = new TextComponentString("Your nickname has been set to \"" + nickname + "\"");
						text.getChatStyle().setColor(TextFormatting.GRAY).setItalic(Boolean.valueOf(true));

						player.addChatMessage(text);
					}

					NicknameManager.saveNicknames();

					player.refreshDisplayName();
				}
			}
			catch (Exception e)
			{
				TextComponentString text = new TextComponentString("Usage: @nickname <[Nickname]|default>");
				text.getChatStyle().setColor(TextFormatting.DARK_GRAY);

				player.addChatMessage(text);
			}

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerNameFormat(PlayerEvent.NameFormat event)
	{
		if (event.getEntityPlayer() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
			String uuid = player.getUniqueID().toString();

			if (NicknameManager.hasNickname(uuid))
			{
				event.setDisplayname(NicknameManager.getNickname(uuid));
			}
		}
	}

	@NetworkCheckHandler
	public boolean netCheckHandler(Map<String, String> mods, Side side)
	{
		return true;
	}
}