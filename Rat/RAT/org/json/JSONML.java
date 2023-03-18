//Raddon On Top!

package org.json;

import java.util.*;

public class JSONML
{
    private static Object parse(final XMLTokener x, final boolean arrayForm, final JSONArray ja, final boolean keepStrings, final int currentNestingDepth) throws JSONException {
        return parse(x, arrayForm, ja, keepStrings ? JSONMLParserConfiguration.KEEP_STRINGS : JSONMLParserConfiguration.ORIGINAL, currentNestingDepth);
    }
    
    private static Object parse(final XMLTokener x, final boolean arrayForm, final JSONArray ja, final JSONMLParserConfiguration config, final int currentNestingDepth) throws JSONException {
        String closeTag = null;
        JSONArray newja = null;
        JSONObject newjo = null;
        String tagName = null;
        while (x.more()) {
            Object token = x.nextContent();
            if (token == XML.LT) {
                token = x.nextToken();
                if (token instanceof Character) {
                    if (token == XML.SLASH) {
                        token = x.nextToken();
                        if (!(token instanceof String)) {
                            throw new JSONException("Expected a closing name instead of '" + token + "'.");
                        }
                        if (x.nextToken() != XML.GT) {
                            throw x.syntaxError("Misshaped close tag");
                        }
                        return token;
                    }
                    else if (token == XML.BANG) {
                        final char c = x.next();
                        if (c == '-') {
                            if (x.next() == '-') {
                                x.skipPast("-->");
                            }
                            else {
                                x.back();
                            }
                        }
                        else if (c == '[') {
                            token = x.nextToken();
                            if (!token.equals("CDATA") || x.next() != '[') {
                                throw x.syntaxError("Expected 'CDATA['");
                            }
                            if (ja == null) {
                                continue;
                            }
                            ja.put((Object)x.nextCDATA());
                        }
                        else {
                            int i = 1;
                            do {
                                token = x.nextMeta();
                                if (token == null) {
                                    throw x.syntaxError("Missing '>' after '<!'.");
                                }
                                if (token == XML.LT) {
                                    ++i;
                                }
                                else {
                                    if (token != XML.GT) {
                                        continue;
                                    }
                                    --i;
                                }
                            } while (i > 0);
                        }
                    }
                    else {
                        if (token != XML.QUEST) {
                            throw x.syntaxError("Misshaped tag");
                        }
                        x.skipPast("?>");
                    }
                }
                else {
                    if (!(token instanceof String)) {
                        throw x.syntaxError("Bad tagName '" + token + "'.");
                    }
                    tagName = (String)token;
                    newja = new JSONArray();
                    newjo = new JSONObject();
                    if (arrayForm) {
                        newja.put((Object)tagName);
                        if (ja != null) {
                            ja.put((Object)newja);
                        }
                    }
                    else {
                        newjo.put("tagName", tagName);
                        if (ja != null) {
                            ja.put((Object)newjo);
                        }
                    }
                    token = null;
                    while (true) {
                        if (token == null) {
                            token = x.nextToken();
                        }
                        if (token == null) {
                            throw x.syntaxError("Misshaped tag");
                        }
                        if (!(token instanceof String)) {
                            if (arrayForm && newjo.length() > 0) {
                                newja.put((Object)newjo);
                            }
                            if (token == XML.SLASH) {
                                if (x.nextToken() != XML.GT) {
                                    throw x.syntaxError("Misshaped tag");
                                }
                                if (ja != null) {
                                    break;
                                }
                                if (arrayForm) {
                                    return newja;
                                }
                                return newjo;
                            }
                            else {
                                if (token != XML.GT) {
                                    throw x.syntaxError("Misshaped tag");
                                }
                                if (currentNestingDepth == config.getMaxNestingDepth()) {
                                    throw x.syntaxError("Maximum nesting depth of " + config.getMaxNestingDepth() + " reached");
                                }
                                closeTag = (String)parse(x, arrayForm, newja, config, currentNestingDepth + 1);
                                if (closeTag == null) {
                                    break;
                                }
                                if (!closeTag.equals(tagName)) {
                                    throw x.syntaxError("Mismatched '" + tagName + "' and '" + closeTag + "'");
                                }
                                tagName = null;
                                if (!arrayForm && newja.length() > 0) {
                                    newjo.put("childNodes", newja);
                                }
                                if (ja != null) {
                                    break;
                                }
                                if (arrayForm) {
                                    return newja;
                                }
                                return newjo;
                            }
                        }
                        else {
                            final String attribute = (String)token;
                            if (!arrayForm && ("tagName".equals(attribute) || "childNode".equals(attribute))) {
                                throw x.syntaxError("Reserved attribute.");
                            }
                            token = x.nextToken();
                            if (token == XML.EQ) {
                                token = x.nextToken();
                                if (!(token instanceof String)) {
                                    throw x.syntaxError("Missing value");
                                }
                                newjo.accumulate(attribute, config.isKeepStrings() ? ((String)token) : XML.stringToValue((String)token));
                                token = null;
                            }
                            else {
                                newjo.accumulate(attribute, "");
                            }
                        }
                    }
                }
            }
            else {
                if (ja == null) {
                    continue;
                }
                ja.put((token instanceof String) ? (config.isKeepStrings() ? XML.unescape((String)token) : XML.stringToValue((String)token)) : token);
            }
        }
        throw x.syntaxError("Bad XML");
    }
    
    public static JSONArray toJSONArray(final String string) throws JSONException {
        return (JSONArray)parse(new XMLTokener(string), true, null, JSONMLParserConfiguration.ORIGINAL, 0);
    }
    
    public static JSONArray toJSONArray(final String string, final boolean keepStrings) throws JSONException {
        return (JSONArray)parse(new XMLTokener(string), true, null, keepStrings, 0);
    }
    
    public static JSONArray toJSONArray(final String string, final JSONMLParserConfiguration config) throws JSONException {
        return (JSONArray)parse(new XMLTokener(string), true, null, config, 0);
    }
    
    public static JSONArray toJSONArray(final XMLTokener x, final JSONMLParserConfiguration config) throws JSONException {
        return (JSONArray)parse(x, true, null, config, 0);
    }
    
    public static JSONArray toJSONArray(final XMLTokener x, final boolean keepStrings) throws JSONException {
        return (JSONArray)parse(x, true, null, keepStrings, 0);
    }
    
    public static JSONArray toJSONArray(final XMLTokener x) throws JSONException {
        return (JSONArray)parse(x, true, null, false, 0);
    }
    
    public static JSONObject toJSONObject(final String string) throws JSONException {
        return (JSONObject)parse(new XMLTokener(string), false, null, false, 0);
    }
    
    public static JSONObject toJSONObject(final String string, final boolean keepStrings) throws JSONException {
        return (JSONObject)parse(new XMLTokener(string), false, null, keepStrings, 0);
    }
    
    public static JSONObject toJSONObject(final String string, final JSONMLParserConfiguration config) throws JSONException {
        return (JSONObject)parse(new XMLTokener(string), false, null, config, 0);
    }
    
    public static JSONObject toJSONObject(final XMLTokener x) throws JSONException {
        return (JSONObject)parse(x, false, null, false, 0);
    }
    
    public static JSONObject toJSONObject(final XMLTokener x, final boolean keepStrings) throws JSONException {
        return (JSONObject)parse(x, false, null, keepStrings, 0);
    }
    
    public static JSONObject toJSONObject(final XMLTokener x, final JSONMLParserConfiguration config) throws JSONException {
        return (JSONObject)parse(x, false, null, config, 0);
    }
    
    public static String toString(final JSONArray ja) throws JSONException {
        final StringBuilder sb = new StringBuilder();
        String tagName = ja.getString(0);
        XML.noSpace(tagName);
        tagName = XML.escape(tagName);
        sb.append('<');
        sb.append(tagName);
        Object object = ja.opt(1);
        int i;
        if (object instanceof JSONObject) {
            i = 2;
            final JSONObject jo = (JSONObject)object;
            for (final String key : jo.keySet()) {
                final Object value = jo.opt(key);
                XML.noSpace(key);
                if (value != null) {
                    sb.append(' ');
                    sb.append(XML.escape(key));
                    sb.append('=');
                    sb.append('\"');
                    sb.append(XML.escape(value.toString()));
                    sb.append('\"');
                }
            }
        }
        else {
            i = 1;
        }
        final int length = ja.length();
        if (i >= length) {
            sb.append('/');
            sb.append('>');
        }
        else {
            sb.append('>');
            do {
                object = ja.get(i);
                ++i;
                if (object != null) {
                    if (object instanceof String) {
                        sb.append(XML.escape(object.toString()));
                    }
                    else if (object instanceof JSONObject) {
                        sb.append(toString((JSONObject)object));
                    }
                    else if (object instanceof JSONArray) {
                        sb.append(toString((JSONArray)object));
                    }
                    else {
                        sb.append(object.toString());
                    }
                }
            } while (i < length);
            sb.append('<');
            sb.append('/');
            sb.append(tagName);
            sb.append('>');
        }
        return sb.toString();
    }
    
    public static String toString(final JSONObject jo) throws JSONException {
        final StringBuilder sb = new StringBuilder();
        String tagName = jo.optString("tagName");
        if (tagName == null) {
            return XML.escape(jo.toString());
        }
        XML.noSpace(tagName);
        tagName = XML.escape(tagName);
        sb.append('<');
        sb.append(tagName);
        for (final String key : jo.keySet()) {
            if (!"tagName".equals(key) && !"childNodes".equals(key)) {
                XML.noSpace(key);
                final Object value = jo.opt(key);
                if (value == null) {
                    continue;
                }
                sb.append(' ');
                sb.append(XML.escape(key));
                sb.append('=');
                sb.append('\"');
                sb.append(XML.escape(value.toString()));
                sb.append('\"');
            }
        }
        final JSONArray ja = jo.optJSONArray("childNodes");
        if (ja == null) {
            sb.append('/');
            sb.append('>');
        }
        else {
            sb.append('>');
            for (int length = ja.length(), i = 0; i < length; ++i) {
                final Object object = ja.get(i);
                if (object != null) {
                    if (object instanceof String) {
                        sb.append(XML.escape(object.toString()));
                    }
                    else if (object instanceof JSONObject) {
                        sb.append(toString((JSONObject)object));
                    }
                    else if (object instanceof JSONArray) {
                        sb.append(toString((JSONArray)object));
                    }
                    else {
                        sb.append(object.toString());
                    }
                }
            }
            sb.append('<');
            sb.append('/');
            sb.append(tagName);
            sb.append('>');
        }
        return sb.toString();
    }
}
