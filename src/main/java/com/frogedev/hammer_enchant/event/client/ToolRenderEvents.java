package com.frogedev.hammer_enchant.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.frogedev.hammer_enchant.HammerEnchantMod;
import com.frogedev.hammer_enchant.event.MiningShapeEvents;
import com.frogedev.hammer_enchant.util.MiningShapeHelpers;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = HammerEnchantMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ToolRenderEvents {
    /**
     * Maximum number of blocks from the iterator to render
     */
    private static final int MAX_BLOCKS = 60;

    private enum ToolMode {
        None(null, 0F, 0F, 0F),
        Mine(MiningShapeEvents.MiningHandler.INSTANCE, 1F, 0.4F, 0.4F),
        Till(MiningShapeEvents.TillingHandler.INSTANCE, 0.8F, 1F, 0F);

        final float r, g, b;
        final MiningShapeHelpers.MiningShapeHandler handler;

        ToolMode(MiningShapeHelpers.MiningShapeHandler handler, float r, float g, float b) {
            this.handler = handler;
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    private static final ToolMode[] MODE_ATTEMPT_ORDER = new ToolMode[]{ToolMode.Till, ToolMode.Mine};

    /**
     * Renders the outline on the extra blocks
     *
     * @param event the highlight event
     */
    @SubscribeEvent
    static void renderBlockHighlights(RenderHighlightEvent.Block event) {
        Level level = Minecraft.getInstance().level;
        Player player = Minecraft.getInstance().player;
        if (level == null || player == null) {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        if (!MiningShapeHelpers.hasMiningShapeModifiers(tool)) {
            return;
        }

        BlockHitResult blockTrace = event.getTarget();
        BlockPos origin = blockTrace.getBlockPos();

        ToolMode activeMode = ToolMode.None;

        // Find the active tool mode.
        for (ToolMode candidateMode : MODE_ATTEMPT_ORDER) {
            if (candidateMode.handler.shouldTryHandler(player, tool) && candidateMode.handler.testOrigin(level, player, tool, origin)) {
                activeMode = candidateMode;
                break;
            }
        }

        // If no tool mode qualifies, do nothing.
        if (activeMode == ToolMode.None) {
            return;
        }

        Iterator<BlockPos> breakableBlocks = MiningShapeHelpers.getCandidateBlockPositions(
                player,
                tool,
                Minecraft.getInstance().hitResult,
                origin,
                activeMode.handler
        );

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

            if (level.getWorldBorder().isWithinBounds(pos)) {
                rendered++;
                highlightBlock(pos, matrices, level, camX, camY, camZ, buffers, 0.0f, activeMode.r, activeMode.g, activeMode.b);
            }
        } while (rendered < MAX_BLOCKS && breakableBlocks.hasNext());

        matrices.popPose();
        event.setCanceled(true);
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
                .getShape(level, pos)
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