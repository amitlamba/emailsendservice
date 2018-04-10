package com.und.model.utils

enum class EmailMessageType(val value: Short) {
    TRANSACTIONAL(1),
    PROMOTIONAL(2);

    companion object {
        private val map = EmailMessageType.values().associateBy(EmailMessageType::value)
        fun fromValue(type: Short) = map[type]
    }
}