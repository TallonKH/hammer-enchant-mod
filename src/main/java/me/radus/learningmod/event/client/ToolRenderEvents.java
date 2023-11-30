package me.radus.learningmod.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.radus.learningmod.LearningMod;
import me.radus.learningmod.ModEnchantments;
import me.radus.learningmod.enchantment.HammerEnchantment;
import me.radus.learningmod.util.BlockBreakingHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = LearningMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ToolRenderEvents {
    /**
     * Maximum number of blocks from the iterator to render
     */
    private static final int MAX_BLOCKS = 60;

    /**
     * Renders the outline on the extra blocks
     *
     * @param event the highlight event
     */
    @SubscribeEvent
    static void renderBlockHighlights(RenderHighlightEvent.Block event) {
        Level world = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (world == null || player == null) {
            return;
        }
        // must be targeting a block
        HitResult result = Minecraft.getInstance().hitResult;
        if (result == null || result.getType() != Type.BLOCK) {
            return;
        }
        BlockHitResult blockTrace = event.getTarget();
        BlockPos origin = blockTrace.getBlockPos();
        BlockState state = world.getBlockState(origin);
        if (!state.getBlock().canHarvestBlock(state, world, origin, player)) {
            return;
        }

        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.HAMMER_ENCHANTMENT.get(), player);
        if (enchantmentLevel <= 0) {
            return;
        }

        Iterator<BlockPos> breakableBlocks = BlockBreakingHelper.getBreakableBlocks(player, origin).iterator();
        if (!breakableBlocks.hasNext()) {
            return;
        }

        // set up renderer
        LevelRenderer worldRender = event.getLevelRenderer();
        PoseStack matrices = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();
        VertexConsumer vertexBuilder = buffers.getBuffer(RenderType.lines());
        matrices.pushPose();

        // start drawing
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Entity viewEntity = camera.getEntity();
        Vec3 camPos = camera.getPosition();
        final double camX = camPos.x();
        final double camY = camPos.y();
        final double camZ = camPos.z();
        int rendered = 0;

        CollisionContext collisionContext = CollisionContext.of(viewEntity);

        do {
            BlockPos pos = breakableBlocks.next();

            if (world.getWorldBorder().isWithinBounds(pos)) {
                rendered++;
                highlightBlock(pos, matrices, world, camX, camY, camZ, buffers, 0.0f, 1.0f, 0.0f, 0.0f);
            }
        } while (rendered < MAX_BLOCKS && breakableBlocks.hasNext());

        matrices.popPose();

        if (rendered > 0) {
            event.setResult(Event.Result.DENY);
        }
    }

    // From SupportBlockRenderer:highlightPosition
    private static void highlightBlock(BlockPos pos, PoseStack poseStack, Level level, double pCamX, double pCamY, double pCamZ, MultiBufferSource bufferSource, double pBias, float pRed, float pGreen, float pBlue) {
//        double d0 = (double) pos.getX() - pCamX - 2.0d * pBias;
//        double d1 = (double) pos.getY() - pCamY - 2.0d * pBias;
//        double d2 = (double) pos.getZ() - pCamZ - 2.0d * pBias;
//        double d3 = d0 + 1.0d + 4.0d * pBias;
//        double d4 = d1 + 1.0d + 4.0d * pBias;
//        double d5 = d2 + 1.0d + 4.0d * pBias;
        VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.lines());
        VoxelShape shape = level
                .getBlockState(pos)
                .getCollisionShape(level, pos, CollisionContext.empty())
                .move(pos.getX(), pos.getY(), pos.getZ());

//        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), d0, d1, d2, d3, d4, d5, pRed, pGreen, pBlue, 0.4F);
        LevelRenderer.renderVoxelShape(poseStack, vertexBuilder, shape, -pCamX, -pCamY, -pCamZ, pRed, pGreen, pBlue, 1.0F, false);
    }

    /**
     * Renders the block damage process on the extra blocks
     */
    /*
    @SubscribeEvent
    static void renderBlockDamageProgress(RenderLevelStageEvent event) {
        // validate required variables are set
        MultiPlayerGameMode controller = Minecraft.getInstance().gameMode;
        if (controller == null || !controller.isDestroying()) {
            return;
        }
        Level world = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (world == null || player == null || Minecraft.getInstance().getCameraEntity() == null) {
            return;
        }
        // must have the right tags
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || !stack.is(TinkerTags.Items.HARVEST)) {
            return;
        }
        // must be targeting a block
        HitResult result = Minecraft.getInstance().hitResult;
        if (result == null || result.getType() != Type.BLOCK) {
            return;
        }
        // find breaking progress
        BlockHitResult blockTrace = (BlockHitResult) result;
        BlockPos target = blockTrace.getBlockPos();
        BlockDestructionProgress progress = null;
        for (Int2ObjectMap.Entry<BlockDestructionProgress> entry : Minecraft.getInstance().levelRenderer.destroyingBlocks.int2ObjectEntrySet()) {
            if (entry.getValue().getPos().equals(target)) {
                progress = entry.getValue();
                break;
            }
        }
        if (progress == null) {
            return;
        }
        // determine extra blocks to highlight
        BlockState state = world.getBlockState(target);
        Iterator<BlockPos> extraBlocks = tool.getDefinition().getData().getAOE().getBlocks(tool, stack, player, state, world, target, blockTrace.getDirection(), IAreaOfEffectIterator.AOEMatchType.BREAKING).iterator();
        if (!extraBlocks.hasNext()) {
            return;
        }

        // set up buffers
        PoseStack matrices = event.getPoseStack();
        matrices.pushPose();
        MultiBufferSource.BufferSource vertices = event.getLevelRenderer().renderBuffers.crumblingBufferSource();
        VertexConsumer vertexBuilder = vertices.getBuffer(ModelBakery.DESTROY_TYPES.get(progress.getProgress()));

        // finally, render the blocks
        Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        double x = renderInfo.getPosition().x;
        double y = renderInfo.getPosition().y;
        double z = renderInfo.getPosition().z;
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        int rendered = 0;
        do {
            BlockPos pos = extraBlocks.next();
            matrices.pushPose();
            matrices.translate(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
            PoseStack.Pose entry = matrices.last();
            VertexConsumer blockBuilder = new SheetedDecalTextureGenerator(vertexBuilder, entry.pose(), entry.normal());
            dispatcher.renderBreakingTexture(world.getBlockState(pos), pos, world, matrices, blockBuilder);
            matrices.popPose();
            rendered++;
        } while (rendered < MAX_BLOCKS && extraBlocks.hasNext());
        // finish rendering
        matrices.popPose();
        vertices.endBatch();
    }
     */
}