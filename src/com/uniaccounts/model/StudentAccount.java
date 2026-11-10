package com.uniaccounts.model;

import com.uniaccounts.prototype.Prototype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Student account of the UniAccounts system.
 *
 * Builder pattern   - role: PRODUCT (built by the nested class {@link Builder}).
 * Prototype pattern - role: CONCRETE PROTOTYPE (implements {@link Prototype}).
 *
 * The constructor is private: the only ways to obtain an account are the
 * Builder or a copy of an existing account (a batch template).
 */
public class StudentAccount implements Prototype<StudentAccount> {

    public static final Set<String> VALID_LEVELS = Set.of("L1", "L2", "L3", "M1", "M2");
    public static final String DEFAULT_LEVEL = "L1";

    // Mandatory fields
    private String id;
    private String fullName;
    private String email;
    // Optional fields
    private String phone;          // default: null
    private String level;          // default: "L1"
    private String major;
    private boolean scholarship;   // default: false
    private List<String> courses;  // default: empty list

    /** Private: forces the use of the Builder (or copy()). */
    private StudentAccount(Builder b) {
        this.id = b.id;
        this.fullName = b.fullName;
        this.email = b.email;
        this.phone = b.phone;
        this.level = b.level;
        this.major = b.major;
        this.scholarship = b.scholarship;
        this.courses = new ArrayList<>(b.courses); // defensive copy
    }

    // ------------------------------------------------------------------
    // Prototype pattern - clone operation
    // ------------------------------------------------------------------

    /** Deep copy of the whole account (the course list is duplicated). */
    @Override
    public StudentAccount copy() {
        return new Builder(id, fullName, email)
                .phone(phone).level(level).major(major)
                .scholarship(scholarship).courses(courses).build();
    }

    /**
     * Clones this account (used as a "batch template") and changes only the
     * student's identity. The phone number is personal, so it is reset to null.
     */
    public StudentAccount copyFor(String newId, String newFullName, String newEmail) {
        StudentAccount c = copy();
        c.id = requireNotBlank(newId, "id");
        c.fullName = requireNotBlank(newFullName, "fullName");
        c.email = requireEmail(newEmail);
        c.phone = null;
        return c;
    }

    // ------------------------------------------------------------------
    // Validation helpers (shared by Builder and Prototype)
    // ------------------------------------------------------------------
    private static String requireNotBlank(String v, String field) {
        if (v == null || v.isBlank())
            throw new IllegalArgumentException("'" + field + "' is mandatory and cannot be empty");
        return v.trim();
    }

    private static String requireEmail(String v) {
        requireNotBlank(v, "email");
        if (!v.contains("@"))
            throw new IllegalArgumentException("'email' must contain '@' : " + v);
        return v.trim();
    }

    // ------------------------------------------------------------------
    // Getters (no setters: an account changes only through Builder/copy)
    // ------------------------------------------------------------------
    public String getId()            { return id; }
    public String getFullName()      { return fullName; }
    public String getEmail()         { return email; }
    public String getPhone()         { return phone; }
    public String getLevel()         { return level; }
    public String getMajor()         { return major; }
    public boolean isScholarship()   { return scholarship; }
    public List<String> getCourses() { return Collections.unmodifiableList(courses); }

    /** Adds a course to this account only (never to its template). */
    public void addCourse(String course) { courses.add(course); }

    @Override
    public String toString() {
        return String.format("StudentAccount{id=%s, name=%s, email=%s, phone=%s, level=%s, major=%s, scholarship=%b, courses=%s}",
                id, fullName, email, phone, level, major, scholarship, courses);
    }

    // ==================================================================
    // Builder pattern - role: CONCRETE BUILDER
    // ==================================================================
    public static class Builder {
        // mandatory
        private final String id;
        private final String fullName;
        private final String email;
        // optional, with default values
        private String phone = null;
        private String level = DEFAULT_LEVEL;
        private String major = null;
        private boolean scholarship = false;
        private List<String> courses = new ArrayList<>();

        /** The three mandatory fields are required by the Builder's constructor. */
        public Builder(String id, String fullName, String email) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
        }

        public Builder phone(String phone)    { this.phone = phone; return this; }
        public Builder level(String level)    { this.level = level; return this; }
        public Builder major(String major)    { this.major = major; return this; }
        public Builder scholarship(boolean s) { this.scholarship = s; return this; }

        public Builder courses(List<String> courses) {
            this.courses = new ArrayList<>(courses);
            return this;
        }

        public Builder addCourse(String course) {
            this.courses.add(course);
            return this;
        }

        /** Validates every rule, then creates the account. */
        public StudentAccount build() {
            requireNotBlank(id, "id");
            requireNotBlank(fullName, "fullName");
            requireEmail(email);
            if (level == null || !VALID_LEVELS.contains(level))
                throw new IllegalArgumentException("'level' must be one of "
                        + new TreeSet<>(VALID_LEVELS) + " : " + level);
            return new StudentAccount(this);
        }
    }
}
