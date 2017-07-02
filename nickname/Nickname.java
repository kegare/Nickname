package nickname;

import java.util.Map;

import com.google.common.base.Strings;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "nickname", acceptedMinecraftVersions = "[1.12,)")
public class Nickname
{
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		NicknameManager.loadNicknames();

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
				String uuid = player.getCachedUniqueIdString();
				String username = player.getGameProfile().getName();
				String nickname = message.substring(message.indexOf(' ') + 1);

				if (Strings.isNullOrEmpty(nickname) || nickname.equalsIgnoreCase("@nickname"))
				{
					throw new NullPointerException();
				}
				else
				{
					if ("default".equalsIgnoreCase(nickname) || "username".equalsIgnoreCase(nickname) || "clear".equalsIgnoreCase(nickname))
					{
						NicknameManager.setNickname(uuid, username);

						player.sendStatusMessage(new TextComponentTranslation("message.nickname.set", username), true);
					}
					else
					{
						NicknameManager.setNickname(uuid, nickname);

						player.sendStatusMessage(new TextComponentTranslation("message.nickname.set", nickname), true);
					}

					NicknameManager.saveNicknames();

					player.refreshDisplayName();
				}
			}
			catch (Exception e)
			{
				ITextComponent msg = new TextComponentTranslation("text.nickname.usage");

				msg.appendText(": @nickname <[").appendSibling(new TextComponentTranslation("text.nickname.nickname")).appendText("]|clear>");
				msg.getStyle().setColor(TextFormatting.GRAY);

				player.sendMessage(msg);
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
			String uuid = player.getCachedUniqueIdString();

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