package io.github.prospector.modmenu.gui;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.prospector.modmenu.ModMenu;
import io.github.prospector.modmenu.util.BadgeRenderer;
import io.github.prospector.modmenu.util.HardcodedUtil;
import io.github.prospector.modmenu.util.RenderUtils;
import net.minecraft.client.texture.TextureUtil;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class ModListEntry implements EntryListWidget.Entry {
	public static final Identifier UNKNOWN_ICON = new Identifier("textures/misc/unknown_pack.png");
	private static final Logger LOGGER = LogManager.getLogger();

	protected final MinecraftClient client;
	protected final ModContainer container;
	protected final ModMetadata metadata;
	protected final ModListWidget list;
	protected Identifier iconLocation;
	protected boolean hovered;

	public ModListEntry(ModContainer container, ModListWidget list) {
		this.container = container;
		this.list = list;
		this.metadata = container.getMetadata();
		this.client = MinecraftClient.getInstance();
	}

	public void updateHovered(boolean hovered) {
		this.hovered = hovered;
	}

	@Override
	public void updatePosition(int index, int x, int y) {
		// NO-OP
	}

	@Override
	public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
		updateHovered(hovered);
		x += getXOffset();
		rowWidth -= getXOffset();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.bindIconTexture();
		GlStateManager.enableBlend();
		DrawableHelper.drawTexture(x, y, 0.0F, 0.0F, 32, 32, 32, 32);
		GlStateManager.disableBlend();
		String name = HardcodedUtil.formatFabricModuleName(metadata.getName());
		String trimmedName = name;
		int maxNameWidth = rowWidth - 32 - 3;
		TextRenderer font = this.client.textRenderer;
		if (font.getStringWidth(name) > maxNameWidth) {
			trimmedName = font.trimToWidth(name, maxNameWidth - font.getStringWidth("...")) + "...";
		}
		font.draw(trimmedName, x + 32 + 3, y + 1, 0xFFFFFF);
		new BadgeRenderer(x + 32 + 3 + font.getStringWidth(name) + 2, y, x + rowWidth, container, list.getParent()).draw(mouseX, mouseY);
		String description = metadata.getDescription();
		if (description.isEmpty() && HardcodedUtil.getHardcodedDescriptions().containsKey(metadata.getId())) {
			description = HardcodedUtil.getHardcodedDescription(metadata.getId());
		}
		RenderUtils.drawWrappedString(description, (x + 32 + 3 + 4), (y + client.textRenderer.fontHeight + 2), rowWidth - 32 - 7, 2, 0x808080);
	}

	@Override
	public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
		if (hovered) {
			list.select(this);
			return true;
		}
		return false;
	}

	@Override
	public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
		// NO-OP
	}

	private NativeImageBackedTexture createIcon() {
		try {
			Path path = container.getPath(metadata.getIconPath(64 * MinecraftClient.getInstance().options.guiScale).orElse("assets/" + metadata.getId() + "/icon.png"));
			NativeImageBackedTexture cached = this.list.getCachedModIcon(path);
			if (cached != null) {
				return cached;
			}
			if (!Files.exists(path)) {
				ModContainer modMenu = FabricLoader.getInstance().getModContainer(ModMenu.MOD_ID).orElseThrow(IllegalAccessError::new);
				if (HardcodedUtil.getFabricMods().contains(metadata.getId())) {
					path = modMenu.getPath("assets/" + ModMenu.MOD_ID + "/fabric_icon.png");
				} else if (metadata.getId().equals("minecraft")) {
					path = modMenu.getPath("assets/" + ModMenu.MOD_ID + "/mc_icon.png");
				} else {
					path = modMenu.getPath("assets/" + ModMenu.MOD_ID + "/grey_fabric_icon.png");
				}
			}
			cached = this.list.getCachedModIcon(path);
			if (cached != null) {
				return cached;
			}
			try (InputStream inputStream = Files.newInputStream(path)) {
				BufferedImage image = TextureUtil.create(Objects.requireNonNull(inputStream));
				Validate.validState(image.getHeight() == image.getWidth(), "Must be square icon");
				NativeImageBackedTexture tex = new NativeImageBackedTexture(image);
				this.list.cacheModIcon(path, tex);
				return tex;
			}

		} catch (Throwable t) {
			LOGGER.error("Invalid icon for mod {}", this.container.getMetadata().getName(), t);
			return null;
		}
	}

	public ModMetadata getMetadata() {
		return metadata;
	}

	public void bindIconTexture() {
		if (this.iconLocation == null) {
			this.iconLocation = new Identifier("modmenu", metadata.getId() + "_icon");
			NativeImageBackedTexture icon = this.createIcon();
			if (icon != null) {
				this.client.getTextureManager().loadTexture(this.iconLocation, icon);
			} else {
				this.iconLocation = UNKNOWN_ICON;
			}
		}
		this.client.getTextureManager().bindTexture(this.iconLocation);
	}

	public int getXOffset() {
		return 0;
	}
}
