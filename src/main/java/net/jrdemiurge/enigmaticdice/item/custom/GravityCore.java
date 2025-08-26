package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.*;

// TODO если возможно то делать модельку игрока фиолетовой под увеличенной гравитацией
public class GravityCore extends Item implements ICurioItem {

    private static final UUID GRAVITY_BOOST_UUID = UUID.fromString("fa944f2a-d00f-48bb-a6c9-b2d0115ba628");
    private static AttributeModifier GRAVITY_MODIFIER;

    private static final WeakHashMap<Player, Boolean> gravityActive = new WeakHashMap<>();
    private static final WeakHashMap<Player, Deque<Double>> lastYVelocities = new WeakHashMap<>();

    public GravityCore(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide || !(slotContext.entity() instanceof Player player))
            return;
        if (GRAVITY_MODIFIER == null) {
            GRAVITY_MODIFIER = new AttributeModifier(GRAVITY_BOOST_UUID, "Gravity boost", Config.GravityCoreGravityMultiplier - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }
        AttributeInstance gravityAttr = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        if (gravityAttr != null && gravityAttr.hasModifier(GRAVITY_MODIFIER)) {
            gravityAttr.removeModifier(GRAVITY_MODIFIER);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide || !(slotContext.entity() instanceof Player player))
            return;

        if (player.isCreative() || player.isSpectator()) return;

        AttributeInstance gravityAttr = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        if (gravityAttr == null) return;

        if (GRAVITY_MODIFIER == null) {
            GRAVITY_MODIFIER = new AttributeModifier(GRAVITY_BOOST_UUID, "Gravity boost", Config.GravityCoreGravityMultiplier - 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        }

        boolean gravityEnabled = gravityActive.getOrDefault(player, false);

        if (player.isShiftKeyDown() && !player.onGround() && !gravityEnabled) {
            gravityActive.put(player, true);
            if (!gravityAttr.hasModifier(GRAVITY_MODIFIER)) {
                gravityAttr.addTransientModifier(GRAVITY_MODIFIER);
            }
        }

        Deque<Double> velocities = lastYVelocities.computeIfAbsent(player, p -> new ArrayDeque<>());
        if (gravityEnabled){
            velocities.addLast(player.getDeltaMovement().y);
            if (velocities.size() > 5) {
                velocities.removeFirst();
            }
        }

        if (player.onGround() && gravityEnabled) {
            gravityActive.put(player, false);
            if (gravityAttr.hasModifier(GRAVITY_MODIFIER)) {
                gravityAttr.removeModifier(GRAVITY_MODIFIER);
            }

            double minY = velocities.stream().min(Double::compareTo).orElse(0.0);
            velocities.clear();

            if (minY > -1.8) return;

            Level world = player.level();

            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.5f, 1F / (player.getRandom().nextFloat() * 0.4F + 0.8F));

            double totalDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE) * Config.GravityCoreImpactDamageCoefficient * Math.abs(minY);
            float radius = (float) Math.abs(minY) * (float) Config.GravityCoreImpactRadiusCoefficient;
            AABB stompBox = new AABB(
                    player.getX() - radius, player.getY() - 1.1, player.getZ() - radius,
                    player.getX() + radius, player.getY() + 1.1, player.getZ() + radius
            );

            List<LivingEntity> victims = player.level().getEntitiesOfClass(
                    LivingEntity.class, stompBox,
                    e -> e != player && e.isAlive() && e.isPickable()
            );

            for (LivingEntity victim : victims) {
                if (checkFriendlyFire(victim, player)) {
                    victim.hurt(player.damageSources().playerAttack(player), (float) totalDamage);
                    victim.setDeltaMovement(victim.getDeltaMovement().multiply(0.0, 2.0, 0.0));
                }
            }

            BlockState block = world.getBlockState(player.blockPosition().below());
            double particlesPerBlock = 20.0D;
            double numberOfParticles = radius * particlesPerBlock;

            for (int i = 0; i < numberOfParticles; i++) {
                float angle = (float) (i / numberOfParticles * 360.0f);
                double d0 = player.getX() + radius * Mth.sin(angle);
                double d1 = player.getY() + 0.15;
                double d2 = player.getZ() + radius * Mth.cos(angle);

                if (world instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, block),
                            d0, d1, d2,
                            1,
                            0, 0, 0,
                            0.0D
                    );
                }
            }
        }
    }

    public static void jump(Player player) {
        if (player.getCooldowns().isOnCooldown(ModItems.GRAVITY_CORE.get())) {
            return;
        }

        double jumpMultiplier = Config.GravityCoreJumpStrength;
        double jumpBoostPower = player.hasEffect(MobEffects.JUMP) ? 0.1F * ((float)player.getEffect(MobEffects.JUMP).getAmplifier() + 1.0F) : 0.0F;
        double jumpPower = (0.42 + jumpBoostPower) * jumpMultiplier;

        Vec3 vec3 = player.getDeltaMovement();
        player.setDeltaMovement(vec3.x, jumpPower, vec3.z);
        if (player.isSprinting()) {
            float f = player.getYRot() * ((float)Math.PI / 180F);
            player.setDeltaMovement(player.getDeltaMovement().add((double)(-Mth.sin(f) * 0.2F * jumpMultiplier), 0.0D, (double)(Mth.cos(f) * 0.2F * jumpMultiplier)));
        }

        player.hasImpulse = true;
        player.awardStat(Stats.JUMP);

        ((ServerPlayer) player).connection.send(new ClientboundSetEntityMotionPacket(player));

        if (Config.GravityCoreCooldown != 0) {
            player.getCooldowns().addCooldown(ModItems.GRAVITY_CORE.get(), Config.GravityCoreCooldown);
        }
    }

    public static boolean checkFriendlyFire(LivingEntity target, LivingEntity attacker) {
        Team attackerTeam = attacker.getTeam();
        Team entityTeam = target.getTeam();
        if (entityTeam != null && attackerTeam == entityTeam && !attackerTeam.isAllowFriendlyFire()) {
            return false;
        } else {
            if (target instanceof OwnableEntity tameable && tameable.getOwner() != null) {
                LivingEntity owner = tameable.getOwner();
                if (owner == attacker) {
                    return false;
                }
                Team ownerTeam = owner.getTeam();
                if (ownerTeam != null && attackerTeam == ownerTeam && !attackerTeam.isAllowFriendlyFire()) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isWearingGravityCore(LivingEntity livingEntity) {
        return CuriosApi.getCuriosInventory(livingEntity)
                .map(handler -> !handler.findCurios(ModItems.GRAVITY_CORE.get()).isEmpty())
                .orElse(false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            int gravityMultiplier = (int) Config.GravityCoreGravityMultiplier;

            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.gravity_core_1"));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.gravity_core_2", gravityMultiplier)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.gravity_core_3"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.gravity_core_4"));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.gravity_core_5"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
    }
}
