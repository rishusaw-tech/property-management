package com.pmfms.util;

import java.security.SecureRandom;

/**
 * Generates unique, human-readable business codes (BRD 8.1 - auto-generated
 * Property/Unit IDs; also used for leases, invoices, work orders and assets).
 * Format: PREFIX-<base36 timestamp><2 random chars>, e.g. PROP-LZ3K9QX7AB
 */
public final class CodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private CodeGenerator() {
    }

    public static String generate(String prefix) {
        String ts = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
        StringBuilder rnd = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            rnd.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return prefix + "-" + ts + rnd;
    }
}
