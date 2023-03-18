//Raddon On Top!

package org.apache.commons.codec.language.bm;

import org.apache.commons.codec.*;
import java.util.regex.*;
import java.util.*;

public class Lang
{
    private static final Map<NameType, Lang> Langs;
    private static final String LANGUAGE_RULES_RN = "org/apache/commons/codec/language/bm/%s_lang.txt";
    private final Languages languages;
    private final List<LangRule> rules;
    
    public static Lang instance(final NameType nameType) {
        return Lang.Langs.get(nameType);
    }
    
    public static Lang loadFromResource(final String languageRulesResourceName, final Languages languages) {
        final List<LangRule> rules = new ArrayList<LangRule>();
        try (final Scanner scanner = new Scanner(Resources.getInputStream(languageRulesResourceName), "UTF-8")) {
            boolean inExtendedComment = false;
            while (scanner.hasNextLine()) {
                String line;
                final String rawLine = line = scanner.nextLine();
                if (inExtendedComment) {
                    if (!line.endsWith("*/")) {
                        continue;
                    }
                    inExtendedComment = false;
                }
                else if (line.startsWith("/*")) {
                    inExtendedComment = true;
                }
                else {
                    final int cmtI = line.indexOf("//");
                    if (cmtI >= 0) {
                        line = line.substring(0, cmtI);
                    }
                    line = line.trim();
                    if (line.length() == 0) {
                        continue;
                    }
                    final String[] parts = line.split("\\s+");
                    if (parts.length != 3) {
                        throw new IllegalArgumentException("Malformed line '" + rawLine + "' in language resource '" + languageRulesResourceName + "'");
                    }
                    final Pattern pattern = Pattern.compile(parts[0]);
                    final String[] langs = parts[1].split("\\+");
                    final boolean accept = parts[2].equals("true");
                    rules.add(new LangRule(pattern, (Set)new HashSet(Arrays.asList(langs)), accept));
                }
            }
        }
        return new Lang(rules, languages);
    }
    
    private Lang(final List<LangRule> rules, final Languages languages) {
        this.rules = Collections.unmodifiableList((List<? extends LangRule>)rules);
        this.languages = languages;
    }
    
    public String guessLanguage(final String text) {
        final Languages.LanguageSet ls = this.guessLanguages(text);
        return ls.isSingleton() ? ls.getAny() : "any";
    }
    
    public Languages.LanguageSet guessLanguages(final String input) {
        final String text = input.toLowerCase(Locale.ENGLISH);
        final Set<String> langs = new HashSet<String>(this.languages.getLanguages());
        for (final LangRule rule : this.rules) {
            if (rule.matches(text)) {
                if (rule.acceptOnMatch) {
                    langs.retainAll(rule.languages);
                }
                else {
                    langs.removeAll(rule.languages);
                }
            }
        }
        final Languages.LanguageSet ls = Languages.LanguageSet.from(langs);
        return ls.equals(Languages.NO_LANGUAGES) ? Languages.ANY_LANGUAGE : ls;
    }
    
    static {
        Langs = new EnumMap<NameType, Lang>(NameType.class);
        for (final NameType s : NameType.values()) {
            Lang.Langs.put(s, loadFromResource(String.format("org/apache/commons/codec/language/bm/%s_lang.txt", s.getName()), Languages.getInstance(s)));
        }
    }
    
    private static final class LangRule
    {
        private final boolean acceptOnMatch;
        private final Set<String> languages;
        private final Pattern pattern;
        
        private LangRule(final Pattern pattern, final Set<String> languages, final boolean acceptOnMatch) {
            this.pattern = pattern;
            this.languages = languages;
            this.acceptOnMatch = acceptOnMatch;
        }
        
        public boolean matches(final String txt) {
            return this.pattern.matcher(txt).find();
        }
    }
}
