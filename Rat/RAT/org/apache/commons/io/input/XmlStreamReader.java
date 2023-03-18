//Raddon On Top!

package org.apache.commons.io.input;

import java.util.regex.*;
import org.apache.commons.io.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.text.*;

public class XmlStreamReader extends Reader
{
    private static final String UTF_8 = "UTF-8";
    private static final String US_ASCII = "US-ASCII";
    private static final String UTF_16BE = "UTF-16BE";
    private static final String UTF_16LE = "UTF-16LE";
    private static final String UTF_32BE = "UTF-32BE";
    private static final String UTF_32LE = "UTF-32LE";
    private static final String UTF_16 = "UTF-16";
    private static final String UTF_32 = "UTF-32";
    private static final String EBCDIC = "CP1047";
    private static final ByteOrderMark[] BOMS;
    private static final ByteOrderMark[] XML_GUESS_BYTES;
    private static final Pattern CHARSET_PATTERN;
    public static final Pattern ENCODING_PATTERN;
    private static final String RAW_EX_1 = "Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch";
    private static final String RAW_EX_2 = "Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] unknown BOM";
    private static final String HTTP_EX_1 = "Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], BOM must be NULL";
    private static final String HTTP_EX_2 = "Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], encoding mismatch";
    private static final String HTTP_EX_3 = "Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], Invalid MIME";
    private final Reader reader;
    private final String encoding;
    private final String defaultEncoding;
    
    static String getContentTypeEncoding(final String httpContentType) {
        String encoding = null;
        if (httpContentType != null) {
            final int i = httpContentType.indexOf(";");
            if (i > -1) {
                final String postMime = httpContentType.substring(i + 1);
                final Matcher m = XmlStreamReader.CHARSET_PATTERN.matcher(postMime);
                encoding = (m.find() ? m.group(1) : null);
                encoding = ((encoding != null) ? encoding.toUpperCase(Locale.ROOT) : null);
            }
        }
        return encoding;
    }
    
    static String getContentTypeMime(final String httpContentType) {
        String mime = null;
        if (httpContentType != null) {
            final int i = httpContentType.indexOf(";");
            if (i >= 0) {
                mime = httpContentType.substring(0, i);
            }
            else {
                mime = httpContentType;
            }
            mime = mime.trim();
        }
        return mime;
    }
    
    private static String getXmlProlog(final InputStream inputStream, final String guessedEnc) throws IOException {
        String encoding = null;
        if (guessedEnc != null) {
            final byte[] bytes = IOUtils.byteArray();
            inputStream.mark(8192);
            int offset;
            int max;
            int c;
            int firstGT;
            String xmlProlog;
            for (offset = 0, max = 8192, c = inputStream.read(bytes, offset, max), firstGT = -1, xmlProlog = ""; c != -1 && firstGT == -1 && offset < 8192; offset += c, max -= c, c = inputStream.read(bytes, offset, max), xmlProlog = new String(bytes, 0, offset, guessedEnc), firstGT = xmlProlog.indexOf(62)) {}
            if (firstGT == -1) {
                if (c == -1) {
                    throw new IOException("Unexpected end of XML stream");
                }
                throw new IOException("XML prolog or ROOT element not found on first " + offset + " bytes");
            }
            else {
                final int bytesRead = offset;
                if (bytesRead > 0) {
                    inputStream.reset();
                    final BufferedReader bReader = new BufferedReader(new StringReader(xmlProlog.substring(0, firstGT + 1)));
                    final StringBuffer prolog = new StringBuffer();
                    String line;
                    while ((line = bReader.readLine()) != null) {
                        prolog.append(line);
                    }
                    final Matcher m = XmlStreamReader.ENCODING_PATTERN.matcher(prolog);
                    if (m.find()) {
                        encoding = m.group(1).toUpperCase(Locale.ROOT);
                        encoding = encoding.substring(1, encoding.length() - 1);
                    }
                }
            }
        }
        return encoding;
    }
    
    static boolean isAppXml(final String mime) {
        return mime != null && (mime.equals("application/xml") || mime.equals("application/xml-dtd") || mime.equals("application/xml-external-parsed-entity") || (mime.startsWith("application/") && mime.endsWith("+xml")));
    }
    
    static boolean isTextXml(final String mime) {
        return mime != null && (mime.equals("text/xml") || mime.equals("text/xml-external-parsed-entity") || (mime.startsWith("text/") && mime.endsWith("+xml")));
    }
    
    public XmlStreamReader(final File file) throws IOException {
        this(Objects.requireNonNull(file, "file").toPath());
    }
    
    public XmlStreamReader(final InputStream inputStream) throws IOException {
        this(inputStream, true);
    }
    
    public XmlStreamReader(final InputStream inputStream, final boolean lenient) throws IOException {
        this(inputStream, lenient, null);
    }
    
    public XmlStreamReader(final InputStream inputStream, final boolean lenient, final String defaultEncoding) throws IOException {
        Objects.requireNonNull(inputStream, "inputStream");
        this.defaultEncoding = defaultEncoding;
        final BOMInputStream bom = new BOMInputStream((InputStream)new BufferedInputStream(inputStream, 8192), false, XmlStreamReader.BOMS);
        final BOMInputStream pis = new BOMInputStream((InputStream)bom, true, XmlStreamReader.XML_GUESS_BYTES);
        this.encoding = this.doRawStream(bom, pis, lenient);
        this.reader = new InputStreamReader((InputStream)pis, this.encoding);
    }
    
    public XmlStreamReader(final InputStream inputStream, final String httpContentType) throws IOException {
        this(inputStream, httpContentType, true);
    }
    
    public XmlStreamReader(final InputStream inputStream, final String httpContentType, final boolean lenient) throws IOException {
        this(inputStream, httpContentType, lenient, null);
    }
    
    public XmlStreamReader(final InputStream inputStream, final String httpContentType, final boolean lenient, final String defaultEncoding) throws IOException {
        Objects.requireNonNull(inputStream, "inputStream");
        this.defaultEncoding = defaultEncoding;
        final BOMInputStream bom = new BOMInputStream((InputStream)new BufferedInputStream(inputStream, 8192), false, XmlStreamReader.BOMS);
        final BOMInputStream pis = new BOMInputStream((InputStream)bom, true, XmlStreamReader.XML_GUESS_BYTES);
        this.encoding = this.processHttpStream(bom, pis, httpContentType, lenient);
        this.reader = new InputStreamReader((InputStream)pis, this.encoding);
    }
    
    public XmlStreamReader(final Path file) throws IOException {
        this(Files.newInputStream(Objects.requireNonNull(file, "file"), new OpenOption[0]));
    }
    
    public XmlStreamReader(final URL url) throws IOException {
        this(Objects.requireNonNull(url, "url").openConnection(), null);
    }
    
    public XmlStreamReader(final URLConnection conn, final String defaultEncoding) throws IOException {
        Objects.requireNonNull(conn, "conn");
        this.defaultEncoding = defaultEncoding;
        final boolean lenient = true;
        final String contentType = conn.getContentType();
        final InputStream inputStream = conn.getInputStream();
        final BOMInputStream bom = new BOMInputStream((InputStream)new BufferedInputStream(inputStream, 8192), false, XmlStreamReader.BOMS);
        final BOMInputStream pis = new BOMInputStream((InputStream)bom, true, XmlStreamReader.XML_GUESS_BYTES);
        if (conn instanceof HttpURLConnection || contentType != null) {
            this.encoding = this.processHttpStream(bom, pis, contentType, true);
        }
        else {
            this.encoding = this.doRawStream(bom, pis, true);
        }
        this.reader = new InputStreamReader((InputStream)pis, this.encoding);
    }
    
    String calculateHttpEncoding(final String httpContentType, final String bomEnc, final String xmlGuessEnc, final String xmlEnc, final boolean lenient) throws IOException {
        if (lenient && xmlEnc != null) {
            return xmlEnc;
        }
        final String cTMime = getContentTypeMime(httpContentType);
        final String cTEnc = getContentTypeEncoding(httpContentType);
        final boolean appXml = isAppXml(cTMime);
        final boolean textXml = isTextXml(cTMime);
        if (!appXml && !textXml) {
            final String msg = MessageFormat.format("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], Invalid MIME", cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
            throw new XmlStreamReaderException(msg, cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
        }
        if (cTEnc == null) {
            if (appXml) {
                return this.calculateRawEncoding(bomEnc, xmlGuessEnc, xmlEnc);
            }
            return (this.defaultEncoding == null) ? "US-ASCII" : this.defaultEncoding;
        }
        else if (cTEnc.equals("UTF-16BE") || cTEnc.equals("UTF-16LE")) {
            if (bomEnc != null) {
                final String msg = MessageFormat.format("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], BOM must be NULL", cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
                throw new XmlStreamReaderException(msg, cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
            }
            return cTEnc;
        }
        else if (cTEnc.equals("UTF-16")) {
            if (bomEnc != null && bomEnc.startsWith("UTF-16")) {
                return bomEnc;
            }
            final String msg = MessageFormat.format("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], encoding mismatch", cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
            throw new XmlStreamReaderException(msg, cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
        }
        else if (cTEnc.equals("UTF-32BE") || cTEnc.equals("UTF-32LE")) {
            if (bomEnc != null) {
                final String msg = MessageFormat.format("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], BOM must be NULL", cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
                throw new XmlStreamReaderException(msg, cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
            }
            return cTEnc;
        }
        else {
            if (!cTEnc.equals("UTF-32")) {
                return cTEnc;
            }
            if (bomEnc != null && bomEnc.startsWith("UTF-32")) {
                return bomEnc;
            }
            final String msg = MessageFormat.format("Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], encoding mismatch", cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
            throw new XmlStreamReaderException(msg, cTMime, cTEnc, bomEnc, xmlGuessEnc, xmlEnc);
        }
    }
    
    String calculateRawEncoding(final String bomEnc, final String xmlGuessEnc, final String xmlEnc) throws IOException {
        if (bomEnc == null) {
            if (xmlGuessEnc == null || xmlEnc == null) {
                return (this.defaultEncoding == null) ? "UTF-8" : this.defaultEncoding;
            }
            if (xmlEnc.equals("UTF-16") && (xmlGuessEnc.equals("UTF-16BE") || xmlGuessEnc.equals("UTF-16LE"))) {
                return xmlGuessEnc;
            }
            return xmlEnc;
        }
        else if (bomEnc.equals("UTF-8")) {
            if (xmlGuessEnc != null && !xmlGuessEnc.equals("UTF-8")) {
                final String msg = MessageFormat.format("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch", bomEnc, xmlGuessEnc, xmlEnc);
                throw new XmlStreamReaderException(msg, bomEnc, xmlGuessEnc, xmlEnc);
            }
            if (xmlEnc != null && !xmlEnc.equals("UTF-8")) {
                final String msg = MessageFormat.format("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch", bomEnc, xmlGuessEnc, xmlEnc);
                throw new XmlStreamReaderException(msg, bomEnc, xmlGuessEnc, xmlEnc);
            }
            return bomEnc;
        }
        else if (bomEnc.equals("UTF-16BE") || bomEnc.equals("UTF-16LE")) {
            if (xmlGuessEnc != null && !xmlGuessEnc.equals(bomEnc)) {
                final String msg = MessageFormat.format("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch", bomEnc, xmlGuessEnc, xmlEnc);
                throw new XmlStreamReaderException(msg, bomEnc, xmlGuessEnc, xmlEnc);
            }
            if (xmlEnc != null && !xmlEnc.equals("UTF-16") && !xmlEnc.equals(bomEnc)) {
                final String msg = MessageFormat.format("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch", bomEnc, xmlGuessEnc, xmlEnc);
                throw new XmlStreamReaderException(msg, bomEnc, xmlGuessEnc, xmlEnc);
            }
            return bomEnc;
        }
        else {
            if (!bomEnc.equals("UTF-32BE") && !bomEnc.equals("UTF-32LE")) {
                final String msg = MessageFormat.format("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] unknown BOM", bomEnc, xmlGuessEnc, xmlEnc);
                throw new XmlStreamReaderException(msg, bomEnc, xmlGuessEnc, xmlEnc);
            }
            if (xmlGuessEnc != null && !xmlGuessEnc.equals(bomEnc)) {
                final String msg = MessageFormat.format("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch", bomEnc, xmlGuessEnc, xmlEnc);
                throw new XmlStreamReaderException(msg, bomEnc, xmlGuessEnc, xmlEnc);
            }
            if (xmlEnc != null && !xmlEnc.equals("UTF-32") && !xmlEnc.equals(bomEnc)) {
                final String msg = MessageFormat.format("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch", bomEnc, xmlGuessEnc, xmlEnc);
                throw new XmlStreamReaderException(msg, bomEnc, xmlGuessEnc, xmlEnc);
            }
            return bomEnc;
        }
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
    }
    
    private String doLenientDetection(String httpContentType, XmlStreamReaderException ex) throws IOException {
        if (httpContentType != null && httpContentType.startsWith("text/html")) {
            httpContentType = httpContentType.substring("text/html".length());
            httpContentType = "text/xml" + httpContentType;
            try {
                return this.calculateHttpEncoding(httpContentType, ex.getBomEncoding(), ex.getXmlGuessEncoding(), ex.getXmlEncoding(), true);
            }
            catch (XmlStreamReaderException ex2) {
                ex = ex2;
            }
        }
        String encoding = ex.getXmlEncoding();
        if (encoding == null) {
            encoding = ex.getContentTypeEncoding();
        }
        if (encoding == null) {
            encoding = ((this.defaultEncoding == null) ? "UTF-8" : this.defaultEncoding);
        }
        return encoding;
    }
    
    private String doRawStream(final BOMInputStream bom, final BOMInputStream pis, final boolean lenient) throws IOException {
        final String bomEnc = bom.getBOMCharsetName();
        final String xmlGuessEnc = pis.getBOMCharsetName();
        final String xmlEnc = getXmlProlog((InputStream)pis, xmlGuessEnc);
        try {
            return this.calculateRawEncoding(bomEnc, xmlGuessEnc, xmlEnc);
        }
        catch (XmlStreamReaderException ex) {
            if (lenient) {
                return this.doLenientDetection(null, ex);
            }
            throw ex;
        }
    }
    
    public String getDefaultEncoding() {
        return this.defaultEncoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    private String processHttpStream(final BOMInputStream bom, final BOMInputStream pis, final String httpContentType, final boolean lenient) throws IOException {
        final String bomEnc = bom.getBOMCharsetName();
        final String xmlGuessEnc = pis.getBOMCharsetName();
        final String xmlEnc = getXmlProlog((InputStream)pis, xmlGuessEnc);
        try {
            return this.calculateHttpEncoding(httpContentType, bomEnc, xmlGuessEnc, xmlEnc, lenient);
        }
        catch (XmlStreamReaderException ex) {
            if (lenient) {
                return this.doLenientDetection(httpContentType, ex);
            }
            throw ex;
        }
    }
    
    @Override
    public int read(final char[] buf, final int offset, final int len) throws IOException {
        return this.reader.read(buf, offset, len);
    }
    
    static {
        BOMS = new ByteOrderMark[] { ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE };
        XML_GUESS_BYTES = new ByteOrderMark[] { new ByteOrderMark("UTF-8", new int[] { 60, 63, 120, 109 }), new ByteOrderMark("UTF-16BE", new int[] { 0, 60, 0, 63 }), new ByteOrderMark("UTF-16LE", new int[] { 60, 0, 63, 0 }), new ByteOrderMark("UTF-32BE", new int[] { 0, 0, 0, 60, 0, 0, 0, 63, 0, 0, 0, 120, 0, 0, 0, 109 }), new ByteOrderMark("UTF-32LE", new int[] { 60, 0, 0, 0, 63, 0, 0, 0, 120, 0, 0, 0, 109, 0, 0, 0 }), new ByteOrderMark("CP1047", new int[] { 76, 111, 167, 148 }) };
        CHARSET_PATTERN = Pattern.compile("charset=[\"']?([.[^; \"']]*)[\"']?");
        ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding[\\s]*=[\\s]*((?:\".[^\"]*\")|(?:'.[^']*'))", 8);
    }
}
