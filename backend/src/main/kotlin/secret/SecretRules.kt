package com.nexus.domain.secret

enum class SecretRules(val rule: Any) {
    Size(8),
    MaxSize(32),
    WithoutChars(" '\""),
    MustHaveChar("!@#$%^&*()-_=+[]{}|;:,.<>/?")
}
