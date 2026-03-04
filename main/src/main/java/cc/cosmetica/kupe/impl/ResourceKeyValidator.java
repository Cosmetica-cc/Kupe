package cc.cosmetica.kupe.impl;

/**
 * Validate Resource Keys with platform independent code.
 */
public class ResourceKeyValidator {
    public static boolean isValidNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return false;
        }

        for (int i = 0; i < namespace.length(); i++) {
            char c = namespace.charAt(i);

            if (isLowercaseLetter(c) ||
                    isDigit(c) ||
                    c == '_' ||
                    c == '-' ||
                    c == '.') {
                continue;
            }

            return false;
        }

        return true;
    }

    public static boolean isValidPath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        if (path.startsWith("/") || path.endsWith("/")) {
            return false;
        }

        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);

            if (isLowercaseLetter(c) ||
                    isDigit(c) ||
                    c == '_' ||
                    c == '-' ||
                    c == '.' ||
                    c == '/') {
                continue;
            }

            return false;
        }

        return true;
    }

    private static boolean isLowercaseLetter(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
