# UniAccounts - Work 1 (Creational Design Patterns, Java)

Core of the student-account system using three creational patterns:
**Builder**, **Singleton**, **Prototype**.

## Structure
```
src/com/uniaccounts/
  Main.java                          demo / client
  model/StudentAccount.java          Builder (nested) + Prototype (copy)
  prototype/Prototype.java           Prototype interface
  repository/AccountRepository.java  Singleton (central repository)
  repository/DuplicateAccountException.java
  audit/AuditLogger.java             Singleton (audit log)
docs/                                README, UML, report, screenshots
```

## Requirements
JDK 17 or newer (tested with JDK 21). No external libraries.

## Compile and run
Linux / macOS: `./run.sh`
Windows: `run.bat`

Or manually:
```
mkdir out
javac -d out $(find src -name "*.java")     # Windows: javac -d out src\com\uniaccounts\*.java src\com\uniaccounts\*\*.java
java -cp out com.uniaccounts.Main
```

## Pattern map
| Pattern   | Class(es) | Problem solved |
|-----------|-----------|----------------|
| Builder   | `StudentAccount.Builder` | 8 fields, 5 optional -> readable construction + validation |
| Singleton | `AccountRepository`, `AuditLogger` | exactly one repository and one audit log |
| Prototype | `Prototype`, `StudentAccount.copy()/copyFor()` | create hundreds of same-batch accounts from a template |

## Validation rules
id not empty and unique in the repository; fullName not empty; email contains `@`;
level in {L1, L2, L3, M1, M2} (default L1); scholarship default false; courses default empty; phone default null.
