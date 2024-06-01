package org.useless.holobuilder.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.useless.holobuilder.HoloBuilder;

@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {

	@Shadow
	public Minecraft mc;

	@Inject(method = "renderWorld(FJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/debug/Debug;change(Ljava/lang/String;)V", ordinal = 9))
	public void inject(float partialTick, long updateRenderersUntil, CallbackInfo ci){
		HoloBuilder.render(mc, partialTick);
	}
}
