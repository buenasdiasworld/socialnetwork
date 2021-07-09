package main.core;

import org.jsoup.Jsoup;

public class TextUtilities {
    private TextUtilities() {
        throw new IllegalStateException("Utility class");
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}
