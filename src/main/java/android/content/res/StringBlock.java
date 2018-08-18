package android.content.res;

import java.io.IOException;

public class StringBlock {
	private int[] m_stringOffsets;
	private int[] m_strings;
	private int[] m_styleOffsets;
	private int[] m_styles;
	private static int m_charFormat = 0;
	
	// magic number 0x001c0001
	private static final int CHUNK_TYPE = 1835009;

	public static StringBlock read(IntReader reader) throws IOException {
		ChunkUtil.readCheckType(reader, CHUNK_TYPE);
		int chunkSize = reader.readInt();
		int stringCount = reader.readInt();
		// always 0x00000000
		int styleCount = reader.readInt();
		m_charFormat = reader.readInt();
		int stringsOffset = reader.readInt();
		int stylesOffset = reader.readInt();
		StringBlock block = new StringBlock();
		block.m_stringOffsets = reader.readIntArray(stringCount);
		if (styleCount != 0) {
			block.m_styleOffsets = reader.readIntArray(styleCount);
		}
		int size = (stylesOffset != 0 ? stylesOffset : chunkSize) - stringsOffset;
		if (size % 4 != 0) {
			throw new IOException("String data size is not multiple of 4 (" + size + ").");
		}
		block.m_strings = reader.readIntArray(size / 4);
		if (stylesOffset != 0) {
			size = chunkSize - stylesOffset;
			if (size % 4 != 0) {
				throw new IOException("Style data size is not multiple of 4 (" + size + ").");
			}
			block.m_styles = reader.readIntArray(size / 4);
		}
		return block;
	}

	public int getCount() {
		return this.m_stringOffsets == null ? 0 : this.m_stringOffsets.length;
	}

	public String getString(int index) {
		if ((index < 0) || (this.m_stringOffsets == null) || (index >= this.m_stringOffsets.length)) {
			return null;
		}
		int offset = this.m_stringOffsets[index];
		StringBuilder result;
		if (m_charFormat == 0) {
			int length = getShort(this.m_strings, offset);
			result = new StringBuilder(length);
			for (; length != 0; length--) {
				offset += 2;
				result.append((char) getShort(this.m_strings, offset));
			}
		} else {
			int length = getShort(this.m_strings, offset) & 0xFF;
			short dc = 0;
			result = new StringBuilder(length);
			for (; length != 0; length--) {
				offset += 2;
				dc = (short) getShort(this.m_strings, offset);
				result.append((char) (dc & 0xFF));
				length--;
				if ((length == 0) || (dc >> 8 == 0)) {
					break;
				}
				result.append((char) (dc >> 8));
			}
		}
		return result.toString();
	}

	public CharSequence get(int index) {
		return getString(index);
	}

	public String getHTML(int index) {
		String raw = getString(index);
		if (raw == null) {
			return raw;
		}
		int[] style = getStyle(index);
		if (style == null) {
			return raw;
		}
		StringBuilder html = new StringBuilder(raw.length() + 32);
		int offset = 0;
		for (;;) {
			int i = -1;
			for (int j = 0; j != style.length; j += 3) {
				if ((style[(j + 1)] != -1) && ((i == -1) || (style[(i + 1)] > style[(j + 1)]))) {
					i = j;
				}
			}
			int start = i == -1 ? raw.length() : style[(i + 1)];
			for (int j = 0; j != style.length; j += 3) {
				int end = style[(j + 2)];
				if ((end != -1) && (end < start)) {
					if (offset <= end) {
						html.append(raw, offset, end + 1);
						offset = end + 1;
					}
					style[(j + 2)] = -1;
					html.append('<');
					html.append('/');
					html.append(getString(style[j]));
					html.append('>');
				}
			}
			if (offset < start) {
				html.append(raw, offset, start);
				offset = start;
			}
			if (i == -1) {
				break;
			}
			html.append('<');
			html.append(getString(style[i]));
			html.append('>');
			style[(i + 1)] = -1;
		}
		return html.toString();
	}

	public int find(String string) {
		if (string == null) {
			return -1;
		}
		for (int i = 0; i != this.m_stringOffsets.length; i++) {
			int offset = this.m_stringOffsets[i];
			int length = getShort(this.m_strings, offset);
			if (length == string.length()) {
				int j = 0;
				for (; j != length; j++) {
					offset += 2;
					if (string.charAt(j) != getShort(this.m_strings, offset)) {
						break;
					}
				}
				if (j == length) {
					return i;
				}
			}
		}
		return -1;
	}

	private int[] getStyle(int index) {
		if ((this.m_styleOffsets == null) || (this.m_styles == null) || (index >= this.m_styleOffsets.length)) {
			return null;
		}
		int offset = this.m_styleOffsets[index] / 4;
		int count = 0;
		for (int i = offset; i < this.m_styles.length; i++) {
			if (this.m_styles[i] == -1) {
				break;
			}
			count++;
		}
		if ((count == 0) || (count % 3 != 0)) {
			return null;
		}
		int[] style = new int[count];
		int i = offset;
		int j = 0;
		while (i < this.m_styles.length) {
			if (this.m_styles[i] == -1) {
				break;
			}
			style[(j++)] = this.m_styles[(i++)];
		}
		return style;
	}

	private static final int getShort(int[] array, int offset) {
		if (offset >= array.length * 4) {
			offset = 0;
		}
		int value = array[(offset / 4)];
		if (offset % 4 / 2 == 0) {
			return value & 0xFFFF;
		}
		return value >>> 16;
	}

}
