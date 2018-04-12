package com.und.config

import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel


interface EventStream {

    @Input("emailEvent")
    fun emailEventSend(): SubscribableChannel

    @Input("EmailReadEvent")
    fun EmailReadEvent(): SubscribableChannel

    @Input("clickTrackEvent")
    fun clickTrackEvent(): SubscribableChannel


    @Output("emailEvent")
    fun outputEmailEvent(): MessageChannel
}