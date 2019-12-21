/*
 * $ javac FormatDigestMessage.java
 * $ jar cfe FormatDigestMessage.jar FormatDigestMessage FormatDigestMessage.class
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FormatDigestMessage {
    private static String activateLinkAux(final String aContent,
                                   final String aRegEx,
                                   final String aReplacement) {
        final Matcher lMatcher = Pattern.compile(aRegEx,
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL).matcher(aContent);
        final StringBuffer lStringBuffer = new StringBuffer();
        while (lMatcher.find()) {
            lMatcher.appendReplacement(lStringBuffer, String.format(aReplacement, lMatcher.group(0).trim()));
        }
        lMatcher.appendTail(lStringBuffer);
        return lStringBuffer.toString();
    }

    // https://stackoverflow.com/a/28269120
    private static String activateLinksInDigest(final String aDigest) {
        return activateLinkAux(
                aDigest,
                "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)",
                "<a href=\"%1$s\" title=\"%1$s\" target=\"_blank\">%1$s</a>"
        );
    }

    private static String activateUsersInDigest(final String aDigest) {
        return activateLinkAux(
                aDigest,
                "\\B@[a-z0-9_-]+",
                "<a href=\"https://t.me/%1$s\" title=\"%1$s\" target=\"_blank\">%1$s</a>"
        ).replaceAll("https://t.me/@", "https://t.me/");
    }

    public static void main(String[] args) {
        System.out.println(activateUsersInDigest(activateLinksInDigest(args[0])));
    }
}
