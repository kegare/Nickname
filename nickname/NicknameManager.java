package nickname;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraftforge.fml.common.FMLLog;

public class NicknameManager
{
	public static final Map<String, String> NICKNAME_MAP = Maps.newHashMap();

	public static void loadNicknames()
	{
		try
		{
			File file = new File("nickname.json");

			if (file.exists())
			{
				@SuppressWarnings("unchecked")
				Map<String, String> map = new Gson().fromJson(new FileReader(file), Map.class);

				NICKNAME_MAP.clear();
				NICKNAME_MAP.putAll(map);
			}
		}
		catch (Exception e)
		{
			FMLLog.log(Level.ERROR, e, "An error occurred while trying to load nicknames");
		}
	}

	public static void saveNicknames()
	{
		try
		{
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String data = gson.toJson(NICKNAME_MAP, Map.class);

			try (FileOutputStream output = new FileOutputStream(new File("nickname.json")))
			{
				output.write(data.getBytes());
				output.flush();
			}
		}
		catch (Exception e)
		{
			FMLLog.log(Level.ERROR, e, "An error occurred while trying to save nicknames");
		}
	}

	public static boolean hasNickname(String uuid)
	{
		return NICKNAME_MAP.containsKey(uuid);
	}

	public static String getNickname(String uuid)
	{
		return NICKNAME_MAP.get(uuid);
	}

	public static void setNickname(String uuid, String nickname)
	{
		NICKNAME_MAP.put(uuid, nickname);
	}
}