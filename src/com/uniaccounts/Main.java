package com.uniaccounts;

import com.uniaccounts.audit.AuditLogger;
import com.uniaccounts.model.StudentAccount;
import com.uniaccounts.repository.AccountRepository;
import com.uniaccounts.repository.DuplicateAccountException;

import java.util.List;

/** Demo of Work 1: Builder + Singleton + Prototype. */
public class Main {

    public static void main(String[] args) {
        System.out.println("===== 1. BUILDER =====");
        demoBuilder();
        System.out.println("\n===== 2. SINGLETON =====");
        demoSingleton();
        System.out.println("\n===== 3. PROTOTYPE =====");
        demoPrototype();
        System.out.println("\n===== FINAL STATE =====");
        System.out.println("Accounts in repository: " + AccountRepository.getInstance().count());
        AuditLogger.getInstance().print();
    }

    // --- Builder -------------------------------------------------------
    static void demoBuilder() {
        AccountRepository repo = AccountRepository.getInstance();

        // Only mandatory fields: optional ones take their default value
        StudentAccount minimal = new StudentAccount.Builder("20240001", "Amine Bouzid", "amine@univ.dz").build();
        repo.save(minimal);
        System.out.println("Minimal : " + minimal);

        // Mandatory + some optional fields, readable and order-independent
        StudentAccount full = new StudentAccount.Builder("20240002", "Sara Khelifi", "sara@univ.dz")
                .phone("0555123456")
                .level("M1")
                .major("Software Engineering")
                .scholarship(true)
                .addCourse("Design Patterns")
                .addCourse("Distributed Systems")
                .build();
        repo.save(full);
        System.out.println("Full    : " + full);

        // Invalid data is rejected at build() time
        tryBuild("missing id",     () -> new StudentAccount.Builder("", "No Id", "x@univ.dz").build());
        tryBuild("email without @", () -> new StudentAccount.Builder("20240003", "Bad Mail", "bad.mail.univ.dz").build());
        tryBuild("unknown level",   () -> new StudentAccount.Builder("20240004", "Bad Level", "b@univ.dz").level("L9").build());
    }

    private static void tryBuild(String label, Runnable r) {
        try {
            r.run();
            System.out.println("Rejected? (" + label + ") -> NO, unexpected");
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected (" + label + "): " + e.getMessage());
        }
    }

    // --- Singleton -----------------------------------------------------
    static void demoSingleton() {
        AccountRepository r1 = AccountRepository.getInstance();
        AccountRepository r2 = AccountRepository.getInstance();
        AuditLogger l1 = AuditLogger.getInstance();
        AuditLogger l2 = AuditLogger.getInstance();
        System.out.println("Repository: r1 == r2 ? " + (r1 == r2));
        System.out.println("AuditLogger: l1 == l2 ? " + (l1 == l2));
        System.out.println("Accounts seen through r2 (saved via r1): " + r2.count());

        // Uniqueness rule of the id
        try {
            r2.save(new StudentAccount.Builder("20240001", "Another Student", "other@univ.dz").build());
        } catch (DuplicateAccountException e) {
            System.out.println("Duplicate rejected: " + e.getMessage());
        }
    }

    // --- Prototype -----------------------------------------------------
    static void demoPrototype() {
        AccountRepository repo = AccountRepository.getInstance();

        // Template of the "L2 Computer Science" batch (built once)
        StudentAccount template = new StudentAccount.Builder("TEMPLATE", "Template", "template@univ.dz")
                .level("L2")
                .major("Computer Science")
                .courses(List.of("Algorithms", "Databases", "Java OOP"))
                .build();
        System.out.println("Template: " + template);

        // Clone the template and change only the identity
        String[][] newcomers = {
                {"20240101", "Yacine Mansouri", "yacine@univ.dz"},
                {"20240102", "Lina Hamdi",      "lina@univ.dz"},
                {"20240103", "Karim Belaid",    "karim@univ.dz"}
        };
        for (String[] n : newcomers) {
            StudentAccount a = template.copyFor(n[0], n[1], n[2]);
            repo.save(a);
            System.out.println("Cloned : " + a);
        }

        // Proof of deep copy: modifying one clone changes neither template nor siblings
        StudentAccount first = repo.findById("20240101").orElseThrow();
        first.addCourse("Machine Learning");
        System.out.println("After adding a course to 20240101 only:");
        System.out.println("  20240101 courses = " + first.getCourses());
        System.out.println("  20240102 courses = " + repo.findById("20240102").orElseThrow().getCourses());
        System.out.println("  template courses = " + template.getCourses());
        System.out.println("clone != template ? " + (first != template));
    }
}
