package com.adoptme.app.data.local

/**
 * Credentials of the local demonstration administrator.
 * The password is hashed before it is written to Room and is not a server secret.
 */
object DemoAccounts {
    const val ADMIN_EMAIL = "admin@adoptme.local"
    const val ADMIN_PASSWORD = "Admin12345"
    const val ADMIN_NAME = "Администратор"
    const val NOTICE =
        "Это локальная демонстрационная роль на устройстве. Она не является серверной проверкой прав."
}
