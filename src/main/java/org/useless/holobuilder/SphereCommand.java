package org.useless.holobuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.ClientCommand;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.util.helper.MathHelper;

import java.util.Locale;

public class SphereCommand extends ClientCommand {
	public SphereCommand(Minecraft minecraft) {
		super(minecraft, "hologram", "holo");
	}

	@Override
	public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
		if (strings.length < 1) return false;
		String subCommand = strings[0];
		switch (subCommand.toLowerCase(Locale.ROOT)){
			case "reset":
				HoloBuilder.holoCache.resetData();
				return true;
			case "renderdist":
			{
				if (strings.length < 2) return false;
				double r = Double.parseDouble(strings[1]);
				HoloBuilder.holoRenderDist = r * r;
				return true;
			}
			case "cube":
			{
				int x;
				int y;
				int z;
				int sizeX;
				int sizeY;
				int sizeZ;
				int blockID = Block.glass.id;
				int meta = 0;
				if (strings.length < 7){
					x = MathHelper.floor_double(commandSender.getPlayer().x);
					y = MathHelper.floor_double(commandSender.getPlayer().y - commandSender.getPlayer().bbHeight);
					z = MathHelper.floor_double(commandSender.getPlayer().z);
					sizeX = Integer.parseInt(strings[1]);
					sizeY = Integer.parseInt(strings[2]);
					sizeZ = Integer.parseInt(strings[3]);
					if (strings.length == 6){
						blockID = Integer.parseInt(strings[4]);
						meta = Integer.parseInt(strings[5]);
					}
				} else {
					x = Integer.parseInt(strings[1]);
					y = Integer.parseInt(strings[2]);
					z = Integer.parseInt(strings[3]);
					sizeX = Integer.parseInt(strings[4]);
					sizeY = Integer.parseInt(strings[5]);
					sizeZ = Integer.parseInt(strings[6]);
					if (strings.length >= 8){
						blockID = Integer.parseInt(strings[7]);
						meta = Integer.parseInt(strings[8]);
					}
				}
				HoloBuilder.addCube(x, y, z, sizeX, sizeY, sizeZ, blockID, meta);
				return true;
			}
			case "sphere":
			{
				double x;
				double y;
				double z;
				double radius;
				int blockId = Block.glass.id;
				int meta = 0;
				if (strings.length < 5) {
					x = MathHelper.floor_double(commandSender.getPlayer().x);
					y = MathHelper.floor_double(commandSender.getPlayer().y - commandSender.getPlayer().bbHeight);
					z = MathHelper.floor_double(commandSender.getPlayer().z);
					radius = Double.parseDouble(strings[1]);
				} else {
					x = Double.parseDouble(strings[1]);
					y = Double.parseDouble(strings[2]);
					z = Double.parseDouble(strings[3]);
					radius = Double.parseDouble(strings[4]);
					if (strings.length > 5){
						blockId = Integer.parseInt(strings[5]);
						meta = Integer.parseInt(strings[6]);
					}
				}
				HoloBuilder.addSphere(x, y, z, radius, blockId, meta);
				return true;
			}
			case "color":
			{
				if (strings.length == 2){
					long color;
					try{
						color = Long.parseLong(strings[1]);
					} catch (NumberFormatException e){
						try {
							color = Long.decode("0x" + strings[1]);
						} catch (NumberFormatException v){
							try {
								color = Long.decode(strings[1]);
							} catch (NumberFormatException n){
								return false;
							}
						}
					}
					HoloBuilder.r = ((color & 0x00FF0000) >> 16 )/255f;
					HoloBuilder.g = ((color & 0x0000FF00) >> 8 )/255f;
					HoloBuilder.b = ((color & 0x000000FF) >> 0)/255f;
					HoloBuilder.a = ((color & 0xFF000000) >> 24)/255f;
					if (HoloBuilder.a == 0) HoloBuilder.a = 1f;
					return true;
				}
				if (strings.length < 5) return false;
				HoloBuilder.r = Float.parseFloat(strings[1]);
				HoloBuilder.g = Float.parseFloat(strings[2]);
				HoloBuilder.b = Float.parseFloat(strings[3]);
				HoloBuilder.a = Float.parseFloat(strings[4]);
				return true;
			}
			default:
				return false;
		}

	}

	@Override
	public boolean opRequired(String[] strings) {
		return false;
	}

	@Override
	public void sendCommandSyntax(CommandHandler commandHandler, CommandSender sender) {
		sender.sendMessage("/hologram reset");
		sender.sendMessage("/hologram renderdist <radius>");

		sender.sendMessage("/hologram cube <xSize> <ySize> <zSize>");
		sender.sendMessage("/hologram cube <xSize> <ySize> <zSize> <id> <meta>");
		sender.sendMessage("/hologram cube <x> <y> <z> <xSize> <ySize> <zSize>");
		sender.sendMessage("/hologram cube <x> <y> <z> <xSize> <ySize> <zSize> <id> <meta>");

		sender.sendMessage("/hologram sphere <radius>");
		sender.sendMessage("/hologram sphere <x> <y> <z> <radius>");
		sender.sendMessage("/hologram sphere <x> <y> <z> <radius> <id> <meta>");

		sender.sendMessage("/hologram color <argb>");
		sender.sendMessage("/hologram color <r> <g> <b> <a>");
	}
}
