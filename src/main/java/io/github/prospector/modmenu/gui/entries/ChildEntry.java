package io.github.prospector.modmenu.gui.entries;

import io.github.prospector.modmenu.gui.ModListEntry;
import io.github.prospector.modmenu.gui.ModListWidget;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.DrawableHelper;

public class ChildEntry extends ModListEntry {
	private boolean bottomChild;
	private ParentEntry parent;

	public ChildEntry(ModContainer container, ParentEntry parent, ModListWidget list, boolean bottomChild) {
		super(container, list);
		this.bottomChild = bottomChild;
		this.parent = parent;
	}

	@Override
	public void method_6700(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float f) {
		super.method_6700(index, x, y, rowWidth, rowHeight, mouseX, mouseY, hovered, f);
		x += 4;
		int color = 0xFFA0A0A0;
		DrawableHelper.fill(x, y - 2, x + 1, y + (bottomChild ? rowHeight / 2 : rowHeight + 2), color);
		DrawableHelper.fill(x, y + rowHeight / 2, x + 7, y + rowHeight / 2 + 1, color);
	}

	@Override
	public int getXOffset() {
		return 13;
	}
}
