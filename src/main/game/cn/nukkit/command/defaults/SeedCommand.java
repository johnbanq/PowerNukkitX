package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class SeedCommand extends VanillaCommand {

    public SeedCommand(String name) {
        super(name, "Show the level's seed");//no translation in client
        this.setPermission("nukkit.command.seed");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.enableParamTree();
    }

    @Since("1.19.60-r1")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        long seed;
        if (sender instanceof Player) {
            seed = ((Player) sender).getLevel().getSeed();
        } else {
            seed = sender.getServer().getDefaultLevel().getSeed();
        }
        log.addSuccess("commands.seed.success", String.valueOf(seed)).output();
        return 1;
    }
}
