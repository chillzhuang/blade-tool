package org.springblade.core.tool.support;

import org.springblade.core.tool.utils.StringPool;
import org.springframework.lang.Nullable;

import java.io.Writer;

/**
 * FastStringWriter
 *
 * @author L.cm
 */
public class FastStringWriter extends Writer {
	private StringBuilder builder;

	public FastStringWriter() {
		builder = new StringBuilder(64);
	}

	public FastStringWriter(final int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException("Negative builderfer size");
		}
		builder = new StringBuilder(capacity);
	}

	public FastStringWriter(@Nullable final StringBuilder builder) {
		this.builder = builder != null ? builder : new StringBuilder(64);
	}

	/**
	 * Gets the underlying StringBuilder.
	 *
	 * @return StringBuilder
	 */
	public StringBuilder getBuilder() {
		return builder;
	}

	@Override
	public void write(int c) {
		builder.append((char) c);
	}

	@Override
	public void write(char[] cbuilder, int off, int len) {
		if ((off < 0) || (off > cbuilder.length) || (len < 0) ||
			((off + len) > cbuilder.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		builder.append(cbuilder, off, len);
	}

	@Override
	public void write(String str) {
		builder.append(str);
	}

	@Override
	public void write(String str, int off, int len) {
		builder.append(str.substring(off, off + len));
	}

	@Override
	public FastStringWriter append(CharSequence csq) {
		if (csq == null) {
			write(StringPool.NULL);
		} else {
			write(csq.toString());
		}
		return this;
	}

	@Override
	public FastStringWriter append(CharSequence csq, int start, int end) {
		CharSequence cs = (csq == null ? StringPool.NULL : csq);
		write(cs.subSequence(start, end).toString());
		return this;
	}

	@Override
	public FastStringWriter append(char c) {
		write(c);
		return this;
	}

	@Override
	public String toString() {
		return builder.toString();
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
		builder.setLength(0);
		builder.trimToSize();
	}
}
