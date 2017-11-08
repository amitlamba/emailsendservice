package com.und.config

import com.und.model.Email
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import java.util.*

@Configuration
@EnableKafka
class KafkaProducerConfig {

    @Value("\${kafka.ip}")
    lateinit private var ip: String

    @Value("\${kafka.port}")
    lateinit private var port: String

    @Bean
    fun producerFactory(): ProducerFactory<String, Email> {
        return DefaultKafkaProducerFactory<String, Email>(producerConfigs())
    }


    @Bean
    fun producerConfigs(): Map<String, Any> {
        val props = HashMap<String, Any>()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "$ip:$port")
        props.put(ProducerConfig.RETRIES_CONFIG, 1)
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384)
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1)
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432)
        //props.put(ProducerConfig.ACKS_CONFIG, "0")
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy")
        //props.put(ProducerConfig.CLIENT_ID_CONFIG, "1")
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer::class.java)

        //props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 120000);
        return props
    }


    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Email> {
        return KafkaTemplate<String, Email>(producerFactory())
    }
}
