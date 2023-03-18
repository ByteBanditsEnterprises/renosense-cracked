//Raddon On Top!

package okhttp3.internal.publicsuffix;

import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import java.net.*;
import java.nio.charset.*;
import okhttp3.internal.platform.*;
import java.io.*;
import okio.*;

public final class PublicSuffixDatabase
{
    public static final String PUBLIC_SUFFIX_RESOURCE = "publicsuffixes.gz";
    private static final byte[] WILDCARD_LABEL;
    private static final String[] EMPTY_RULE;
    private static final String[] PREVAILING_RULE;
    private static final byte EXCEPTION_MARKER = 33;
    private static final PublicSuffixDatabase instance;
    private final AtomicBoolean listRead;
    private final CountDownLatch readCompleteLatch;
    private byte[] publicSuffixListBytes;
    private byte[] publicSuffixExceptionListBytes;
    
    public PublicSuffixDatabase() {
        this.listRead = new AtomicBoolean(false);
        this.readCompleteLatch = new CountDownLatch(1);
    }
    
    public static PublicSuffixDatabase get() {
        return PublicSuffixDatabase.instance;
    }
    
    public String getEffectiveTldPlusOne(final String domain) {
        if (domain == null) {
            throw new NullPointerException("domain == null");
        }
        final String unicodeDomain = IDN.toUnicode(domain);
        final String[] domainLabels = unicodeDomain.split("\\.");
        final String[] rule = this.findMatchingRule(domainLabels);
        if (domainLabels.length == rule.length && rule[0].charAt(0) != '!') {
            return null;
        }
        int firstLabelOffset;
        if (rule[0].charAt(0) == '!') {
            firstLabelOffset = domainLabels.length - rule.length;
        }
        else {
            firstLabelOffset = domainLabels.length - (rule.length + 1);
        }
        final StringBuilder effectiveTldPlusOne = new StringBuilder();
        final String[] punycodeLabels = domain.split("\\.");
        for (int i = firstLabelOffset; i < punycodeLabels.length; ++i) {
            effectiveTldPlusOne.append(punycodeLabels[i]).append('.');
        }
        effectiveTldPlusOne.deleteCharAt(effectiveTldPlusOne.length() - 1);
        return effectiveTldPlusOne.toString();
    }
    
    private String[] findMatchingRule(final String[] domainLabels) {
        if (!this.listRead.get() && this.listRead.compareAndSet(false, true)) {
            this.readTheListUninterruptibly();
        }
        else {
            try {
                this.readCompleteLatch.await();
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        synchronized (this) {
            if (this.publicSuffixListBytes == null) {
                throw new IllegalStateException("Unable to load publicsuffixes.gz resource from the classpath.");
            }
        }
        final byte[][] domainLabelsUtf8Bytes = new byte[domainLabels.length][];
        for (int i = 0; i < domainLabels.length; ++i) {
            domainLabelsUtf8Bytes[i] = domainLabels[i].getBytes(StandardCharsets.UTF_8);
        }
        String exactMatch = null;
        for (int j = 0; j < domainLabelsUtf8Bytes.length; ++j) {
            final String rule = binarySearchBytes(this.publicSuffixListBytes, domainLabelsUtf8Bytes, j);
            if (rule != null) {
                exactMatch = rule;
                break;
            }
        }
        String wildcardMatch = null;
        if (domainLabelsUtf8Bytes.length > 1) {
            final byte[][] labelsWithWildcard = domainLabelsUtf8Bytes.clone();
            for (int labelIndex = 0; labelIndex < labelsWithWildcard.length - 1; ++labelIndex) {
                labelsWithWildcard[labelIndex] = PublicSuffixDatabase.WILDCARD_LABEL;
                final String rule2 = binarySearchBytes(this.publicSuffixListBytes, labelsWithWildcard, labelIndex);
                if (rule2 != null) {
                    wildcardMatch = rule2;
                    break;
                }
            }
        }
        String exception = null;
        if (wildcardMatch != null) {
            for (int labelIndex = 0; labelIndex < domainLabelsUtf8Bytes.length - 1; ++labelIndex) {
                final String rule2 = binarySearchBytes(this.publicSuffixExceptionListBytes, domainLabelsUtf8Bytes, labelIndex);
                if (rule2 != null) {
                    exception = rule2;
                    break;
                }
            }
        }
        if (exception != null) {
            exception = "!" + exception;
            return exception.split("\\.");
        }
        if (exactMatch == null && wildcardMatch == null) {
            return PublicSuffixDatabase.PREVAILING_RULE;
        }
        final String[] exactRuleLabels = (exactMatch != null) ? exactMatch.split("\\.") : PublicSuffixDatabase.EMPTY_RULE;
        final String[] wildcardRuleLabels = (wildcardMatch != null) ? wildcardMatch.split("\\.") : PublicSuffixDatabase.EMPTY_RULE;
        return (exactRuleLabels.length > wildcardRuleLabels.length) ? exactRuleLabels : wildcardRuleLabels;
    }
    
    private static String binarySearchBytes(final byte[] bytesToSearch, final byte[][] labels, final int labelIndex) {
        int low = 0;
        int high = bytesToSearch.length;
        String match = null;
        while (low < high) {
            int mid;
            for (mid = (low + high) / 2; mid > -1 && bytesToSearch[mid] != 10; --mid) {}
            ++mid;
            int end;
            for (end = 1; bytesToSearch[mid + end] != 10; ++end) {}
            final int publicSuffixLength = mid + end - mid;
            int currentLabelIndex = labelIndex;
            int currentLabelByteIndex = 0;
            int publicSuffixByteIndex = 0;
            boolean expectDot = false;
            int compareResult;
            while (true) {
                int byte0;
                if (expectDot) {
                    byte0 = 46;
                    expectDot = false;
                }
                else {
                    byte0 = (labels[currentLabelIndex][currentLabelByteIndex] & 0xFF);
                }
                final int byte2 = bytesToSearch[mid + publicSuffixByteIndex] & 0xFF;
                compareResult = byte0 - byte2;
                if (compareResult != 0) {
                    break;
                }
                ++publicSuffixByteIndex;
                ++currentLabelByteIndex;
                if (publicSuffixByteIndex == publicSuffixLength) {
                    break;
                }
                if (labels[currentLabelIndex].length != currentLabelByteIndex) {
                    continue;
                }
                if (currentLabelIndex == labels.length - 1) {
                    break;
                }
                ++currentLabelIndex;
                currentLabelByteIndex = -1;
                expectDot = true;
            }
            if (compareResult < 0) {
                high = mid - 1;
            }
            else if (compareResult > 0) {
                low = mid + end + 1;
            }
            else {
                final int publicSuffixBytesLeft = publicSuffixLength - publicSuffixByteIndex;
                int labelBytesLeft = labels[currentLabelIndex].length - currentLabelByteIndex;
                for (int i = currentLabelIndex + 1; i < labels.length; ++i) {
                    labelBytesLeft += labels[i].length;
                }
                if (labelBytesLeft < publicSuffixBytesLeft) {
                    high = mid - 1;
                }
                else {
                    if (labelBytesLeft <= publicSuffixBytesLeft) {
                        match = new String(bytesToSearch, mid, publicSuffixLength, StandardCharsets.UTF_8);
                        break;
                    }
                    low = mid + end + 1;
                }
            }
        }
        return match;
    }
    
    private void readTheListUninterruptibly() {
        boolean interrupted = false;
        try {
            this.readTheList();
        }
        catch (InterruptedIOException e2) {
            Thread.interrupted();
            interrupted = true;
        }
        catch (IOException e) {
            Platform.get().log(5, "Failed to read public suffix list", (Throwable)e);
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void readTheList() throws IOException {
        final InputStream resource = PublicSuffixDatabase.class.getResourceAsStream("publicsuffixes.gz");
        if (resource == null) {
            return;
        }
        byte[] publicSuffixListBytes;
        byte[] publicSuffixExceptionListBytes;
        try (final BufferedSource bufferedSource = Okio.buffer((Source)new GzipSource(Okio.source(resource)))) {
            final int totalBytes = bufferedSource.readInt();
            publicSuffixListBytes = new byte[totalBytes];
            bufferedSource.readFully(publicSuffixListBytes);
            final int totalExceptionBytes = bufferedSource.readInt();
            publicSuffixExceptionListBytes = new byte[totalExceptionBytes];
            bufferedSource.readFully(publicSuffixExceptionListBytes);
        }
        synchronized (this) {
            this.publicSuffixListBytes = publicSuffixListBytes;
            this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
        }
        this.readCompleteLatch.countDown();
    }
    
    void setListBytes(final byte[] publicSuffixListBytes, final byte[] publicSuffixExceptionListBytes) {
        this.publicSuffixListBytes = publicSuffixListBytes;
        this.publicSuffixExceptionListBytes = publicSuffixExceptionListBytes;
        this.listRead.set(true);
        this.readCompleteLatch.countDown();
    }
    
    static {
        WILDCARD_LABEL = new byte[] { 42 };
        EMPTY_RULE = new String[0];
        PREVAILING_RULE = new String[] { "*" };
        instance = new PublicSuffixDatabase();
    }
}
