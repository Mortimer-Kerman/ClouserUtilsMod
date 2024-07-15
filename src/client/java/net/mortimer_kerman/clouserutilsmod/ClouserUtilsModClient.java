package net.mortimer_kerman.clouserutilsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.world.ClientWorld;
import net.mortimer_kerman.clouserutilsmod.argument.Operation;

public class ClouserUtilsModClient implements ClientModInitializer
{
	public static boolean movementOn = true;
	public static boolean axisXOn = true;
	public static boolean axisYOn = true;
	public static boolean canJump = true;
	public static boolean zKeysLookOn = false;
	public static float stepHeight = 0.6f;
	public static boolean canPlayerFall = true;
	public static boolean cameraClip = true;
	public static float eyeHeight = 1;

	public static boolean perspectiveLocked = false;

	public static long NextShakeEnd = 0;
	public static float ShakeStenght = 0;

	public static boolean LockFOVAutoChange = false;

	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(Payloads.BooleanPayload.ID, (payload, context) -> {
			switch (payload.strId()) {
				case ClouserUtilsMod.MOVEMENT_ON:
					movementOn = payload.value();
					break;
				case ClouserUtilsMod.AXIS_X_ON:
					axisXOn = payload.value();
					break;
				case ClouserUtilsMod.AXIS_Y_ON:
					axisYOn = payload.value();
					break;
				case ClouserUtilsMod.CANJUMP_ON:
					canJump = payload.value();
					break;
				case ClouserUtilsMod.PERSPECTIVE_LOCKED_ON:
					perspectiveLocked = payload.value();
					break;
				case ClouserUtilsMod.PLAYER_FALL:
					canPlayerFall = payload.value();
					break;
				case ClouserUtilsMod.INVENTORY:
					if(payload.value()) {
						if (context.client().interactionManager.hasRidingInventory()) {
							context.player().openRidingInventory();
						} else {
							context.client().getTutorialManager().onInventoryOpened();
							context.client().setScreen(new InventoryScreen(context.player()));
						}
					}
					else if (context.client().currentScreen instanceof HandledScreen screen) screen.close();
					break;
				case ClouserUtilsMod.CAMERA_CLIP:
					cameraClip = payload.value();
					break;
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.FloatPayload.ID, (payload, context) -> {
			switch (payload.strId()) {
				case ClouserUtilsMod.PLAYER_STEP_HEIGHT -> stepHeight = payload.value();
				case ClouserUtilsMod.PLAYER_EYE_HEIGHT -> eyeHeight = payload.value();
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.IntPayload.ID, (payload, context) -> {
			switch (payload.strId()) {
				case ClouserUtilsMod.PERSPECTIVE_DATA:
					switch (payload.value()) {
						case 0 -> context.client().options.setPerspective(Perspective.FIRST_PERSON);
						case 1 -> context.client().options.setPerspective(Perspective.THIRD_PERSON_BACK);
						case 2 -> context.client().options.setPerspective(Perspective.THIRD_PERSON_FRONT);
					}
					break;
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.EmptyPayload.ID, (payload, context) -> {
			switch (payload.strId()) {
				case ClouserUtilsMod.CLEARCHAT:
					if (context.client().inGameHud != null) context.client().inGameHud.getChatHud().clear(false);
					break;
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.Vec3dCouplePayload.ID, (payload, context) -> {
			if(payload.strId().equals(ClouserUtilsMod.VELOCITY_CHANGE)) {
				if (context.player() == null) return;
				context.player().setVelocity(context.player().getVelocity().multiply(payload.value1()).add(payload.value2()));
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.FloatIntPayload.ID, (payload, context) -> {
			switch (payload.strId()) {
				case ClouserUtilsMod.SHAKE_CAMERA:
					ClientWorld world = context.client().world;
					if (world == null) return;
					NextShakeEnd = world.getTime() + payload.valueI();
					ShakeStenght = payload.valueF();
					break;
				case ClouserUtilsMod.FOV_MODIFIER:
					SimpleOption<Integer> fov = context.client().options.getFov();
					LockFOVAutoChange = true;
					switch (Operation.fromInt(payload.valueI())) {
						case Operation.SET -> fov.setValue((int)payload.valueF());
						case Operation.ADD -> fov.setValue((int)(fov.getValue() + payload.valueF()));
						case Operation.REMOVE -> fov.setValue((int)(fov.getValue() - payload.valueF()));
						case Operation.MULTIPLY -> fov.setValue((int)(fov.getValue() * payload.valueF()));
						case Operation.DIVIDE -> fov.setValue((int)(fov.getValue() / payload.valueF()));
					}
					LockFOVAutoChange = false;
					break;
			}
		});
	}
}
