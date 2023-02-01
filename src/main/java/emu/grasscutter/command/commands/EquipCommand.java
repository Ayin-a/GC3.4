package emu.grasscutter.command.commands;

import emu.grasscutter.command.Command;
import emu.grasscutter.command.CommandHandler;
import emu.grasscutter.data.GameData;
import emu.grasscutter.data.excels.MonsterData;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.entity.*;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.proto.SceneWeaponInfoOuterClass;
import emu.grasscutter.server.packet.send.PacketAvatarEquipChangeNotify;
import emu.grasscutter.utils.Position;
import emu.grasscutter.game.world.Scene;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(label = "equip", usage = "equip <weaponId>", permission = "server.equip")
public final class EquipCommand implements CommandHandler {

    @Override
    public void execute(Player sender, Player targetPlayer, List<String> args) {
        if (targetPlayer == null) {
            CommandHandler.sendMessage(null, "Run this command in-game.");
            return;
        }

        int id = 0; // This is just to shut up the linter, it's not a real default

        try {
            id = Integer.parseInt(args.get(0));
        } catch (NumberFormatException ignored) {
            CommandHandler.sendMessage(targetPlayer, "Invalid weapon ID");
            return;
        }

        MonsterData monsterData = GameData.getMonsterDataMap().get(21010201);
        monsterData.setWeaponId(id);
        Scene scene = targetPlayer.getScene();

        // Radius
        double maxRadius = Math.sqrt(1 * 0.2 / Math.PI);
        Position pos = GetRandomPositionInCircle(targetPlayer.getPosition(), maxRadius).addY(-10000);

        // Generate entity
        EntityMonster entity = new EntityMonster(scene, monsterData, pos, 200);
        scene.addEntity(entity);

        // Current avatar entity
        EntityAvatar entityAvatar = targetPlayer.getTeamManager().getCurrentAvatarEntity();

        // Current avatar and weapon
        Avatar currentAvatar = entityAvatar.getAvatar();
        GameItem currentWeapon = currentAvatar.getWeapon();

        // Sleep
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Generate and send packet
        int weaponEntityId = entity.getWeaponEntityId();
        SceneWeaponInfoOuterClass.SceneWeaponInfo sceneWeaponInfo = currentWeapon.createSceneWeaponInfo(weaponEntityId);
        PacketAvatarEquipChangeNotify packet = new PacketAvatarEquipChangeNotify(currentAvatar, currentWeapon,
            sceneWeaponInfo);
        targetPlayer.sendPacket(packet);
        CommandHandler.sendMessage(targetPlayer, "Equipped " + id);
    }

    private Position GetRandomPositionInCircle(Position origin, double radius) {
        Position target = origin.clone();
        origin.setY(-10000);
        double angle = Math.random() * 360;
        double r = Math.sqrt(Math.random() * radius * radius);
        target.addX((float) (r * Math.cos(angle))).addZ((float) (r * Math.sin(angle)));
        return target;
    }
}
