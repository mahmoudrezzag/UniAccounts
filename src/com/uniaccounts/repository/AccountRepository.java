package com.uniaccounts.repository;

import com.uniaccounts.audit.AuditLogger;
import com.uniaccounts.model.StudentAccount;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Singleton pattern - role: SINGLETON (the unique central repository).
 * Every part of the program calls getInstance() and sees the same accounts.
 */
public final class AccountRepository {

    private final Map<String, StudentAccount> accounts = new LinkedHashMap<>();

    private AccountRepository() { }

    private static class Holder {
        private static final AccountRepository INSTANCE = new AccountRepository();
    }

    public static AccountRepository getInstance() { return Holder.INSTANCE; }

    /** Saves an account; the id must be unique. Every operation is audited. */
    public synchronized void save(StudentAccount account) {
        if (accounts.containsKey(account.getId())) {
            AuditLogger.getInstance().log("REJECTED duplicate id " + account.getId());
            throw new DuplicateAccountException(account.getId());
        }
        accounts.put(account.getId(), account);
        AuditLogger.getInstance().log("CREATED account " + account.getId() + " (" + account.getFullName() + ")");
    }

    public synchronized Optional<StudentAccount> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public synchronized List<StudentAccount> findAll() {
        return new ArrayList<>(accounts.values());
    }

    public synchronized int count() { return accounts.size(); }
}
