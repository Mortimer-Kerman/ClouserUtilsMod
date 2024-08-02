package net.mortimer_kerman.clouserutilsmod.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import net.mortimer_kerman.clouserutilsmod.ClouserUtilsMod;
import net.mortimer_kerman.clouserutilsmod.ClouserUtilsModClient;

import net.mortimer_kerman.clouserutilsmod.Payloads;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin
{
    @Shadow @Final private String translationKey;

    @Shadow private boolean pressed;

    @Inject(at = @At("RETURN"), method = "isPressed", cancellable = true)
    public void isPressed(CallbackInfoReturnable<Boolean> cir)
    {
        boolean movementKey = translationKey.equals("key.forward")
                            ||translationKey.equals("key.back")
                            ||translationKey.equals("key.left")
                            ||translationKey.equals("key.right")
                            ||translationKey.equals("key.jump");

        cir.setReturnValue(cir.getReturnValue() && (ClouserUtilsModClient.movementOn || !movementKey));
    }

    @Inject(at = @At("HEAD"), method = "setPressed")
    public void setPressed(boolean pressed, CallbackInfo ci)
    {
        if (!ClouserUtilsModClient.zKeysLookOn) return;
        if (this.pressed == pressed) return;
        if (!translationKey.equals("key.forward") && !translationKey.equals("key.back")) return;
    }
}
