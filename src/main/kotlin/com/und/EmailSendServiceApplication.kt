package com.und


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = arrayOf("com.und"))
@EnableFeignClients
class EmailSendServiceApplication

fun main(args: Array<String>) {

    SpringApplication.run(EmailSendServiceApplication::class.java, *args)
}





