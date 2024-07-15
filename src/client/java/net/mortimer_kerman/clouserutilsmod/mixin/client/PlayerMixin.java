package net.mortimer_kerman.clouserutilsmod.mixin.client;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;

import net.mortimer_kerman.clouserutilsmod.ClouserUtilsModClient;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntityMixin
{
    @Shadow @Final private PlayerAbilities abilities;

    @Inject(at = @At("HEAD"), method = "tick()V")
    private void tick(CallbackInfo info)
    {
        if (!getWorld().isClient) return;

        if(abilities.flying || isFallFlying() || isInFluid()) return;

        Vec3d velocity = getVelocity();

        double motionX = velocity.x;
        double motionY = velocity.y;
        double motionZ = velocity.z;

        if (!ClouserUtilsModClient.canPlayerFall) motionY = 0;

        setVelocity(motionX, motionY, motionZ);
    }
    
    @Override
    protected void onLookDirectionChange(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci)
    {
        ci.cancel();
        float f = (float)cursorDeltaY * 0.15f;
        float g = (float)cursorDeltaX * 0.15f;

        if (!ClouserUtilsModClient.axisYOn) f = 0;
        if (!ClouserUtilsModClient.axisXOn) g = 0;

        this.setPitch(this.getPitch() + f);
        this.setYaw(this.getYaw() + g);
        this.setPitch(MathHelper.clamp(this.getPitch(), -90.0f, 90.0f));
        this.prevPitch += f;
        this.prevYaw += g;
        this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0f, 90.0f);
        if (this.getVehicle() != null) {
            this.getVehicle().onPassengerLookAround(((PlayerEntity)(Object)this));
        }
    }

    @Inject(at = @At("HEAD"), method = "jump()V", cancellable = true)
    protected void onJump(CallbackInfo ci)
    {
        if (!ClouserUtilsModClient.canJump) ci.cancel();
    }

    @Override
    protected void stepHeight(CallbackInfoReturnable<Float> cir)
    {
        float f = ClouserUtilsModClient.stepHeight;
        cir.setReturnValue(this.getControllingPassenger() instanceof PlayerEntity ? Math.max(f, 1.0F) : f);
    }
}
