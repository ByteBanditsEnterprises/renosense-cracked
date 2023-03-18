//Raddon On Top!

package org.apache.commons.codec.language.bm;

import org.apache.commons.codec.*;
import java.util.*;

public class Languages
{
    public static final String ANY = "any";
    private static final Map<NameType, Languages> LANGUAGES;
    private final Set<String> languages;
    public static final LanguageSet NO_LANGUAGES;
    public static final LanguageSet ANY_LANGUAGE;
    
    public static Languages getInstance(final NameType nameType) {
        return Languages.LANGUAGES.get(nameType);
    }
    
    public static Languages getInstance(final String languagesResourceName) {
        final Set<String> ls = new HashSet<String>();
        try (final Scanner lsScanner = new Scanner(Resources.getInputStream(languagesResourceName), "UTF-8")) {
            boolean inExtendedComment = false;
            while (lsScanner.hasNextLine()) {
                final String line = lsScanner.nextLine().trim();
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
                    if (line.length() <= 0) {
                        continue;
                    }
                    ls.add(line);
                }
            }
            return new Languages(Collections.unmodifiableSet((Set<? extends String>)ls));
        }
    }
    
    private static String langResourceName(final NameType nameType) {
        return String.format("org/apache/commons/codec/language/bm/%s_languages.txt", nameType.getName());
    }
    
    private Languages(final Set<String> languages) {
        this.languages = languages;
    }
    
    public Set<String> getLanguages() {
        return this.languages;
    }
    
    static {
        LANGUAGES = new EnumMap<NameType, Languages>(NameType.class);
        for (final NameType s : NameType.values()) {
            Languages.LANGUAGES.put(s, getInstance(langResourceName(s)));
        }
        NO_LANGUAGES = new LanguageSet() {
            @Override
            public boolean contains(final String language) {
                return false;
            }
            
            @Override
            public String getAny() {
                throw new NoSuchElementException("Can't fetch any language from the empty language set.");
            }
            
            @Override
            public boolean isEmpty() {
                return true;
            }
            
            @Override
            public boolean isSingleton() {
                return false;
            }
            
            @Override
            public LanguageSet restrictTo(final LanguageSet other) {
                return this;
            }
            
            public LanguageSet merge(final LanguageSet other) {
                return other;
            }
            
            @Override
            public String toString() {
                return "NO_LANGUAGES";
            }
        };
        ANY_LANGUAGE = new LanguageSet() {
            @Override
            public boolean contains(final String language) {
                return true;
            }
            
            @Override
            public String getAny() {
                throw new NoSuchElementException("Can't fetch any language from the any language set.");
            }
            
            @Override
            public boolean isEmpty() {
                return false;
            }
            
            @Override
            public boolean isSingleton() {
                return false;
            }
            
            @Override
            public LanguageSet restrictTo(final LanguageSet other) {
                return other;
            }
            
            public LanguageSet merge(final LanguageSet other) {
                return other;
            }
            
            @Override
            public String toString() {
                return "ANY_LANGUAGE";
            }
        };
    }
    
    public abstract static class LanguageSet
    {
        public static LanguageSet from(final Set<String> langs) {
            return langs.isEmpty() ? Languages.NO_LANGUAGES : new SomeLanguages((Set)langs);
        }
        
        public abstract boolean contains(final String p0);
        
        public abstract String getAny();
        
        public abstract boolean isEmpty();
        
        public abstract boolean isSingleton();
        
        public abstract LanguageSet restrictTo(final LanguageSet p0);
        
        abstract LanguageSet merge(final LanguageSet p0);
    }
    
    public static final class SomeLanguages extends LanguageSet
    {
        private final Set<String> languages;
        
        private SomeLanguages(final Set<String> languages) {
            this.languages = Collections.unmodifiableSet((Set<? extends String>)languages);
        }
        
        @Override
        public boolean contains(final String language) {
            return this.languages.contains(language);
        }
        
        @Override
        public String getAny() {
            return this.languages.iterator().next();
        }
        
        public Set<String> getLanguages() {
            return this.languages;
        }
        
        @Override
        public boolean isEmpty() {
            return this.languages.isEmpty();
        }
        
        @Override
        public boolean isSingleton() {
            return this.languages.size() == 1;
        }
        
        @Override
        public LanguageSet restrictTo(final LanguageSet other) {
            if (other == Languages.NO_LANGUAGES) {
                return other;
            }
            if (other == Languages.ANY_LANGUAGE) {
                return this;
            }
            final SomeLanguages sl = (SomeLanguages)other;
            final Set<String> ls = new HashSet<String>(Math.min(this.languages.size(), sl.languages.size()));
            for (final String lang : this.languages) {
                if (sl.languages.contains(lang)) {
                    ls.add(lang);
                }
            }
            return LanguageSet.from(ls);
        }
        
        public LanguageSet merge(final LanguageSet other) {
            if (other == Languages.NO_LANGUAGES) {
                return this;
            }
            if (other == Languages.ANY_LANGUAGE) {
                return other;
            }
            final SomeLanguages sl = (SomeLanguages)other;
            final Set<String> ls = new HashSet<String>(this.languages);
            for (final String lang : sl.languages) {
                ls.add(lang);
            }
            return LanguageSet.from(ls);
        }
        
        @Override
        public String toString() {
            return "Languages(" + this.languages.toString() + ")";
        }
    }
}
