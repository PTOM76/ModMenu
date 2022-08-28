package io.github.prospector.modmenu.gui.entries;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.prospector.modmenu.ModMenu;
import io.github.prospector.modmenu.gui.ModListEntry;
import io.github.prospector.modmenu.gui.ModListSearch;
import io.github.prospector.modmenu.gui.ModListWidget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;

import net.fabricmc.loader.api.ModContainer;

public class ParentEntry extends ModListEntry {
	private static final Identifier PARENT_MOD_TEXTURE = new Identifier(ModMenu.MOD_ID, "textures/gui/parent_mod.png");
	protected List<ModContainer> children;
	protected ModListWidget list;
	protected boolean hoveringIcon = false;

	public ParentEntry(ModContainer parent, List<ModContainer> children, ModListWidget list) {
		super(parent, list);
		this.children = children;
		this.list = list;
	}

	@Override
	public void method_6700(int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float f) {
		super.method_6700(index, y, x, rowWidth, rowHeight, mouseX, mouseY, hovered, f);
		TextRenderer font = client.textRenderer;
		int childrenBadgeHeight = font.fontHeight;
		int childrenBadgeWidth = font.fontHeight;
		int children = ModListSearch.search(list.getParent().getSearchInput(), getChildren()).size();
		int childrenWidth = font.getStringWidth(Integer.toString(children)) - 1;
		if (childrenBadgeWidth < childrenWidth + 4) {
			childrenBadgeWidth = childrenWidth + 4;
		}
		int childrenBadgeX = x + 32 - childrenBadgeWidth;
		int childrenBadgeY = y + 32 - childrenBadgeHeight;
		int childrenOutlineColor = 0x8810d098;
		int childrenFillColor = 0x88046146;
		DrawableHelper.fill(childrenBadgeX + 1, childrenBadgeY, childrenBadgeX + childrenBadgeWidth - 1, childrenBadgeY + 1, childrenOutlineColor);
		DrawableHelper.fill(childrenBadgeX, childrenBadgeY + 1, childrenBadgeX + 1, childrenBadgeY + childrenBadgeHeight - 1, childrenOutlineColor);
		DrawableHelper.fill(childrenBadgeX + childrenBadgeWidth - 1, childrenBadgeY + 1, childrenBadgeX + childrenBadgeWidth, childrenBadgeY + childrenBadgeHeight - 1, childrenOutlineColor);
		DrawableHelper.fill(childrenBadgeX + 1, childrenBadgeY + 1, childrenBadgeX + childrenBadgeWidth - 1, childrenBadgeY + childrenBadgeHeight - 1, childrenFillColor);
		DrawableHelper.fill(childrenBadgeX + 1, childrenBadgeY + childrenBadgeHeight - 1, childrenBadgeX + childrenBadgeWidth - 1, childrenBadgeY + childrenBadgeHeight, childrenOutlineColor);
		font.draw(Integer.toString(children), childrenBadgeX + childrenBadgeWidth / 2 - childrenWidth / 2, childrenBadgeY + 1, 0xCACACA);
		this.hoveringIcon = mouseX >= x - 1 && mouseX <= x - 1 + 32 && mouseY >= y - 1 && mouseY <= y - 1 + 32;
		if (isMouseOver(mouseX, mouseY)) {
			DrawableHelper.fill(x, y, x + 32, y + 32, 0xA0909090);
			this.client.getTextureManager().bindTexture(PARENT_MOD_TEXTURE);
			int xOffset = list.getParent().showModChildren.contains(getMetadata().getId()) ? 32 : 0;
			int yOffset = hoveringIcon ? 32 : 0;
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			DrawableHelper.drawTexture(x, y, xOffset, yOffset, 32 + xOffset, 32 + yOffset, 256, 256);
		}
	}

	@Override
	public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
		if (hoveringIcon) {
			String id = getMetadata().getId();
			if (list.getParent().showModChildren.contains(id)) {
				list.getParent().showModChildren.remove(id);
			} else {
				list.getParent().showModChildren.add(id);
			}
			list.filter(list.getParent().getSearchInput(), false);
		}
		return super.mouseClicked(index, mouseX, mouseY, button, x, y);
	}

	public void setChildren(List<ModContainer> children) {
		this.children = children;
	}

	public void addChildren(List<ModContainer> children) {
		this.children.addAll(children);
	}

	public void addChildren(ModContainer... children) {
		this.children.addAll(Arrays.asList(children));
	}

	public List<ModContainer> getChildren() {
		return children;
	}

	public boolean isMouseOver(double double_1, double double_2) {
		return Objects.equals(this.list.getEntryAtPos(double_1, double_2), this);
	}
}
