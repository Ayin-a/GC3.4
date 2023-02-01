package emu.grasscutter.command.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.server.packet.send.PacketWindSeedClientNotify;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static emu.grasscutter.config.Configuration.WINDY;

@Command(label = "windy", usage = "windy", permission = "server.spawn")
public final class WindyCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (targetPlayer == null) {
            CommandHandler.sendMessage(null, "Run this command in-game.");
            return;
        }

        String name = null;

        try {
            name = args.get(0);
        } catch (Exception ignored) {}

        PacketWindSeedClientNotify packet = new PacketWindSeedClientNotify(name);

        targetPlayer.sendPacket(packet);

        Path path = Paths.get(WINDY("scripts/" + name + ".lua.windy"));

        if (Files.exists(path))
            CommandHandler.sendMessage(targetPlayer, "Executed windy");
        else
            CommandHandler.sendMessage(targetPlayer, "Windy not found");

    }

}
