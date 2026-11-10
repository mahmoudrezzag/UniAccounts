package com.uniaccounts.repository;

public class DuplicateAccountException extends RuntimeException {
    public DuplicateAccountException(String id) {
        super("An account with id '" + id + "' already exists in the repository");
    }
}
