package io.github.prospector.modmenu.gui;

import java.util.LinkedList;
import java.util.List;

import io.github.prospector.modmenu.util.HardcodedUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.EntryListWidget;

public class DescriptionListWidget extends EntryListWidget {
	private final ModListScreen parent;
	private final TextRenderer textRenderer;
	private ModListEntry lastSelected = null;
	private List<DescriptionEntry> entries = new LinkedList<>();

	public DescriptionListWidget(MinecraftClient client, int width, int height, int top, int bottom, int entryHeight, ModListScreen parent) {
		super(client, width, height, top, bottom, entryHeight);
		this.parent = parent;
		this.textRenderer = client.textRenderer;
	}

	@Override
	public int getRowWidth() {
		return this.width - 10;
	}

	@Override
	protected int getScrollbarPosition() {
		return this.width - 6 + xStart;
	}

	@Override
	protected int getEntryCount() {
		return entries.size();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		ModListEntry selectedEntry = parent.getSelectedEntry();
		if (selectedEntry != lastSelected) {
			lastSelected = selectedEntry;
			entries.clear();
			scrollAmount = -Float.MAX_VALUE;
			String description = lastSelected.getMetadata().getDescription();
			String id = lastSelected.getMetadata().getId();
			if (description.isEmpty() && HardcodedUtil.getHardcodedDescriptions().containsKey(id)) {
				description = HardcodedUtil.getHardcodedDescription(id);
			}
			if (lastSelected != null && description != null && !description.isEmpty()) {
				for (String line : textRenderer.wrapLines(description.replaceAll("\n", "\n\n"), getRowWidth())) {
					entries.add(new DescriptionEntry(line));
				}
			}
		}
		super.render(mouseX, mouseY, delta);
	}

	@Override
	protected void renderHoleBackground(int y1, int y2, int startAlpha, int endAlpha) {
		// Awful hack but it makes the background "seamless"
		ModListScreen.overlayBackground(xStart, y1, xEnd, y2, 64, 64, 64, startAlpha, endAlpha);
	}

	@Override
	public Entry getEntry(int index) {
		return entries.get(index);
	}

	protected static class DescriptionEntry implements EntryListWidget.Entry {
		protected String text;

		public DescriptionEntry(String text) {
			this.text = text;
		}

		@Override
		public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
			MinecraftClient.getInstance().textRenderer.drawWithShadow(text, x, y, 0xAAAAAA);
		}

		@Override
		public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
			return false; // NO-OP
		}

		@Override
		public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {
			// NO-OP
		}

		@Override
		public void updatePosition(int index, int x, int y) {
			// NO-OP
		}
	}

}
