package com.uniaccounts.audit;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton pattern - role: SINGLETON (the unique audit log).
 * Implementation: "initialization-on-demand holder" -> lazy and thread-safe
 * without explicit synchronization of getInstance().
 */
public final class AuditLogger {

    private final List<String> entries = new ArrayList<>();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private AuditLogger() { }                      // nobody else can call new

    private static class Holder {                  // loaded only on first getInstance()
        private static final AuditLogger INSTANCE = new AuditLogger();
    }

    public static AuditLogger getInstance() { return Holder.INSTANCE; }

    public synchronized void log(String operation) {
        entries.add("[" + LocalTime.now().format(FMT) + "] " + operation);
    }

    public synchronized List<String> getEntries() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public synchronized void print() {
        System.out.println("---- AUDIT LOG (" + entries.size() + " entries) ----");
        entries.forEach(System.out::println);
    }
}
