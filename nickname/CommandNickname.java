package nickname;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandNickname extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "nickname";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/nickname <Player> <[Nickname]|default>";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 2)
		{
			throw new WrongUsageException(getCommandUsage(sender));
		}
		else
		{
			EntityPlayerMP player = getPlayer(server, sender, args[0]);
			String uuid = player.getUniqueID().toString();
			String username = player.getGameProfile().getName();
			String nickname = args[1];

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

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getAllUsernames()) : Collections.<String>emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}
}