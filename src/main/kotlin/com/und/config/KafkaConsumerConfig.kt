package com.und.config

import com.und.kafkalisterner.EmailListener
import com.und.model.Email
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import java.util.*

@Configuration
@EnableKafka
class KafkaConsumerConfig {

    @Value("\${kafka.ip}")
    lateinit private var ip: String

    @Value("\${kafka.port}")
    lateinit private var port: String




    @Bean
    fun consumerFactory(): ConsumerFactory<String, Email> {
        return DefaultKafkaConsumerFactory<String, Email>(consumerConfigs(), StringDeserializer(),
                JsonDeserializer(Email::class.java))
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Email> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Email>()
        factory.consumerFactory = consumerFactory()
        factory.setConcurrency(3)
        factory.containerProperties.pollTimeout = 3000
        return factory
    }


    @Bean
    fun consumerConfigs(): Map<String, Any> {
        val propsMap = HashMap<String, Any>()
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "$ip:$port")
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false)
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100")
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000")
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java)
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer::class.java)
        propsMap.put( ConsumerConfig.GROUP_ID_CONFIG, "EventGroup")
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
        return propsMap
    }


    //TODO remove these beans initialization to another app
    @Bean
    fun listener(): EmailListener {
        return EmailListener()
    }
}
