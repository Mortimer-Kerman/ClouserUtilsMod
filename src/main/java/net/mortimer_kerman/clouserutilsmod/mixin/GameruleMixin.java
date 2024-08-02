package net.mortimer_kerman.clouserutilsmod.mixin;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import net.mortimer_kerman.clouserutilsmod.ClouserUtilsMod;

import net.mortimer_kerman.clouserutilsmod.Payloads;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRuleCommand.class)
public abstract class GameruleMixin
{
    @Inject(at = @At("HEAD"), method = "executeSet")
    private static <T extends GameRules.Rule<T>> void SetGravity(CommandContext<ServerCommandSource> context, GameRules.Key<T> key, CallbackInfoReturnable<Integer> cir)
    {
        String channel;
        MinecraftServer server = context.getSource().getServer();
        PacketByteBuf data = PacketByteBufs.create();

        if (key.getName().equals(ClouserUtilsMod.MOVEMENT_GAMERULE.getName())) channel = ClouserUtilsMod.MOVEMENT_ON;
        else if (key.getName().equals(ClouserUtilsMod.AXIS_X_GAMERULE.getName())) channel = ClouserUtilsMod.AXIS_X_ON;
        else if (key.getName().equals(ClouserUtilsMod.AXIS_Y_GAMERULE.getName())) channel = ClouserUtilsMod.AXIS_Y_ON;
        else if (key.getName().equals(ClouserUtilsMod.CANJUMP_GAMERULE.getName())) channel = ClouserUtilsMod.CANJUMP_ON;
        else if (key.getName().equals(ClouserUtilsMod.PERSPECTIVE_LOCKED_GAMERULE.getName())) channel = ClouserUtilsMod.PERSPECTIVE_LOCKED_ON;
        else if (key.getName().equals(ClouserUtilsMod.PLAYER_STEP_GAMERULE.getName()))
        {
            float value = (float)DoubleArgumentType.getDouble(context, "value");

            for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                server.execute(() -> ServerPlayNetworking.send(player, new Payloads.FloatPayload(ClouserUtilsMod.PLAYER_STEP_HEIGHT, value)));
            }
            return;
        }
        else if (key.getName().equals(ClouserUtilsMod.PLAYER_FALL_GAMERULE.getName())) channel = ClouserUtilsMod.PLAYER_FALL;
        else if (key.getName().equals(ClouserUtilsMod.CAMERA_CLIP_GAMERULE.getName())) channel = ClouserUtilsMod.CAMERA_CLIP;
        else if (key.getName().equals(ClouserUtilsMod.PLAYER_EYE_HEIGHT_GAMERULE.getName()))
        {
            float value = (float)DoubleArgumentType.getDouble(context, "value");

            for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                server.execute(() -> ServerPlayNetworking.send(player, new Payloads.FloatPayload(ClouserUtilsMod.PLAYER_EYE_HEIGHT, value)));
            }
            return;
        }
        else return;

        boolean value = BoolArgumentType.getBool(context, "value");

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            server.execute(() -> ServerPlayNetworking.send(player, new Payloads.BooleanPayload(channel, value)));
        }
    }
}
