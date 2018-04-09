package com.und.config

import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBinding(EventStream::class)
class ApplicationConfig