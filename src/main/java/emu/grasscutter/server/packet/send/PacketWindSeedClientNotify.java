package emu.grasscutter.server.packet.send;

import com.google.protobuf.ByteString;
import emu.grasscutter.game.avatar.Avatar;
import emu.grasscutter.game.inventory.EquipType;
import emu.grasscutter.game.inventory.GameItem;
import emu.grasscutter.net.packet.BasePacket;
import emu.grasscutter.net.packet.PacketOpcodes;
import emu.grasscutter.net.proto.WindSeedClientNotifyOuterClass;
import emu.grasscutter.net.proto.WindSeedClientNotifyOuterClass.WindSeedClientNotify;
import emu.grasscutter.net.proto.SceneWeaponInfoOuterClass;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;

import static emu.grasscutter.config.Configuration.WINDY;

public class PacketWindSeedClientNotify extends BasePacket {

    public PacketWindSeedClientNotify() {
        super(PacketOpcodes.WindSeedClientNotify);

        String bruh = "G0x1YVMBGZMNChoKBAQICHhWAAAAAAAAAAAAAAAod0ABCkBtYWluLmx1YQAAAAAAAAAAAAEEFAAAACQAQABWQAAALEAAASQAQABWgAAALEAAASQAQABWwAAALEAAASQAQQApQEEAKYBBACnAQQBWAAIALIAAAV1AQgDWgAIAbICAAV8Aw4UZAIAADQAAAAQGcHJpbnQEDGhlbGxvIHdvcmxkBA90aGlzIGlzIGEgdGVzdAQUdGhhbmtzIGZvciB0aGUgaGVscAQDQ1MEDFVuaXR5RW5naW5lBAtHYW1lT2JqZWN0BAVGaW5kBCkvQmV0YVdhdGVybWFya0NhbnZhcyhDbG9uZSkvUGFuZWwvVHh0VUlEBA1HZXRDb21wb25lbnQEBVRleHQEBXRleHQECkBraW5wcmVncwEAAAABAAAAAAAUAAAAAQAAAAEAAAABAAAAAgAAAAIAAAACAAAAAwAAAAMAAAADAAAABQAAAAUAAAAFAAAABQAAAAUAAAAFAAAABgAAAAYAAAAGAAAABgAAAAYAAAABAAAABHVpZA8AAAAUAAAAAQAAAAVfRU5W";

        byte[] decodedString = Base64.getDecoder().decode(new String(bruh).getBytes(StandardCharsets.UTF_8));

        ByteString bs = ByteString.copyFrom(decodedString);

        System.out.println(bs);

        WindSeedClientNotify.AreaNotify areaNotify = WindSeedClientNotify.AreaNotify.newBuilder().setAreaId(101701).setAreaCode(bs).setAreaType(1).build();

        WindSeedClientNotify.Builder proto = WindSeedClientNotify.newBuilder()
            .setAreaNotify(areaNotify);

        this.setData(proto);
    }

    public PacketWindSeedClientNotify(String filename) {
        super(PacketOpcodes.WindSeedClientNotify);


        ByteString bs = null;

        try {

            // Windy auto compile
            try {
                Path path = Paths.get(WINDY("scripts/" + filename + ".lua"));
                File script = new File(String.valueOf(path));
                Path exec = Paths.get(WINDY("luac.py"));
                File builder = new File(String.valueOf(exec));
                String command = "python " + builder.getAbsolutePath() + " " + script.getAbsolutePath();
                Runtime.getRuntime().exec(command);
                System.out.println(command);
            } catch (Exception ignored) {}

            Path path = Paths.get(WINDY("scripts/" + filename + ".lua.windy"));

            String b64 = "";
            if (Files.exists(path))
                b64 = Files.readString(path);
            byte[] decodedString = Base64.getDecoder().decode(new String(b64).getBytes(StandardCharsets.UTF_8));
            bs = ByteString.copyFrom(decodedString);
        } catch (Exception ignored) {}

        WindSeedClientNotify.AreaNotify areaNotify = WindSeedClientNotify.AreaNotify.newBuilder()
            .setAreaId(101701)
            .setAreaCode(bs)
            .setAreaType(1)
            .build();

        WindSeedClientNotify.Builder proto = WindSeedClientNotify.newBuilder()
            .setAreaNotify(areaNotify);

        this.setData(proto);
    }

}
