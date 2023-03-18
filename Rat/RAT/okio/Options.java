//Raddon On Top!

package okio;

import java.util.*;

public final class Options extends AbstractList<ByteString> implements RandomAccess
{
    final ByteString[] byteStrings;
    final int[] trie;
    
    private Options(final ByteString[] byteStrings, final int[] trie) {
        this.byteStrings = byteStrings;
        this.trie = trie;
    }
    
    public static Options of(final ByteString... byteStrings) {
        if (byteStrings.length == 0) {
            return new Options(new ByteString[0], new int[] { 0, -1 });
        }
        final List<ByteString> list = new ArrayList<ByteString>(Arrays.asList(byteStrings));
        Collections.sort(list);
        final List<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); ++i) {
            indexes.add(-1);
        }
        for (int i = 0; i < list.size(); ++i) {
            final int sortedIndex = Collections.binarySearch((List<? extends Comparable<? super ByteString>>)list, byteStrings[i]);
            indexes.set(sortedIndex, i);
        }
        if (list.get(0).size() == 0) {
            throw new IllegalArgumentException("the empty byte string is not a supported option");
        }
        for (int a = 0; a < list.size(); ++a) {
            final ByteString prefix = list.get(a);
            int b = a + 1;
            while (b < list.size()) {
                final ByteString byteString = list.get(b);
                if (!byteString.startsWith(prefix)) {
                    break;
                }
                if (byteString.size() == prefix.size()) {
                    throw new IllegalArgumentException("duplicate option: " + byteString);
                }
                if (indexes.get(b) > indexes.get(a)) {
                    list.remove(b);
                    indexes.remove(b);
                }
                else {
                    ++b;
                }
            }
        }
        final Buffer trieBytes = new Buffer();
        buildTrieRecursive(0L, trieBytes, 0, list, 0, list.size(), indexes);
        final int[] trie = new int[intCount(trieBytes)];
        for (int j = 0; j < trie.length; ++j) {
            trie[j] = trieBytes.readInt();
        }
        if (!trieBytes.exhausted()) {
            throw new AssertionError();
        }
        return new Options(byteStrings.clone(), trie);
    }
    
    private static void buildTrieRecursive(final long nodeOffset, final Buffer node, final int byteStringOffset, final List<ByteString> byteStrings, int fromIndex, final int toIndex, final List<Integer> indexes) {
        if (fromIndex >= toIndex) {
            throw new AssertionError();
        }
        for (int i = fromIndex; i < toIndex; ++i) {
            if (byteStrings.get(i).size() < byteStringOffset) {
                throw new AssertionError();
            }
        }
        ByteString from = byteStrings.get(fromIndex);
        final ByteString to = byteStrings.get(toIndex - 1);
        int prefixIndex = -1;
        if (byteStringOffset == from.size()) {
            prefixIndex = indexes.get(fromIndex);
            ++fromIndex;
            from = byteStrings.get(fromIndex);
        }
        if (from.getByte(byteStringOffset) != to.getByte(byteStringOffset)) {
            int selectChoiceCount = 1;
            for (int j = fromIndex + 1; j < toIndex; ++j) {
                if (byteStrings.get(j - 1).getByte(byteStringOffset) != byteStrings.get(j).getByte(byteStringOffset)) {
                    ++selectChoiceCount;
                }
            }
            final long childNodesOffset = nodeOffset + intCount(node) + 2L + selectChoiceCount * 2;
            node.writeInt(selectChoiceCount);
            node.writeInt(prefixIndex);
            for (int k = fromIndex; k < toIndex; ++k) {
                final byte rangeByte = byteStrings.get(k).getByte(byteStringOffset);
                if (k == fromIndex || rangeByte != byteStrings.get(k - 1).getByte(byteStringOffset)) {
                    node.writeInt(rangeByte & 0xFF);
                }
            }
            final Buffer childNodes = new Buffer();
            int rangeEnd;
            for (int rangeStart = fromIndex; rangeStart < toIndex; rangeStart = rangeEnd) {
                final byte rangeByte2 = byteStrings.get(rangeStart).getByte(byteStringOffset);
                rangeEnd = toIndex;
                for (int l = rangeStart + 1; l < toIndex; ++l) {
                    if (rangeByte2 != byteStrings.get(l).getByte(byteStringOffset)) {
                        rangeEnd = l;
                        break;
                    }
                }
                if (rangeStart + 1 == rangeEnd && byteStringOffset + 1 == byteStrings.get(rangeStart).size()) {
                    node.writeInt((int)indexes.get(rangeStart));
                }
                else {
                    node.writeInt((int)(-1L * (childNodesOffset + intCount(childNodes))));
                    buildTrieRecursive(childNodesOffset, childNodes, byteStringOffset + 1, byteStrings, rangeStart, rangeEnd, indexes);
                }
            }
            node.write(childNodes, childNodes.size());
        }
        else {
            int scanByteCount = 0;
            for (int j = byteStringOffset, max = Math.min(from.size(), to.size()); j < max && from.getByte(j) == to.getByte(j); ++j) {
                ++scanByteCount;
            }
            final long childNodesOffset = nodeOffset + intCount(node) + 2L + scanByteCount + 1L;
            node.writeInt(-scanByteCount);
            node.writeInt(prefixIndex);
            for (int k = byteStringOffset; k < byteStringOffset + scanByteCount; ++k) {
                node.writeInt(from.getByte(k) & 0xFF);
            }
            if (fromIndex + 1 == toIndex) {
                if (byteStringOffset + scanByteCount != byteStrings.get(fromIndex).size()) {
                    throw new AssertionError();
                }
                node.writeInt((int)indexes.get(fromIndex));
            }
            else {
                final Buffer childNodes = new Buffer();
                node.writeInt((int)(-1L * (childNodesOffset + intCount(childNodes))));
                buildTrieRecursive(childNodesOffset, childNodes, byteStringOffset + scanByteCount, byteStrings, fromIndex, toIndex, indexes);
                node.write(childNodes, childNodes.size());
            }
        }
    }
    
    @Override
    public ByteString get(final int i) {
        return this.byteStrings[i];
    }
    
    @Override
    public final int size() {
        return this.byteStrings.length;
    }
    
    private static int intCount(final Buffer trieBytes) {
        return (int)(trieBytes.size() / 4L);
    }
}
