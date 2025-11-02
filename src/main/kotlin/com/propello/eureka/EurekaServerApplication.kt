package com.propello.eureka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

/**
 * Propello Eureka Server - Service Discovery
 *
 * This is a standalone Eureka service registry that allows microservices to:
 * - Register themselves on startup
 * - Discover other services
 * - Load balance across multiple instances
 * - Monitor service health
 */
@SpringBootApplication
@EnableEurekaServer
class EurekaServerApplication

fun main(args: Array<String>) {
    runApplication<EurekaServerApplication>(*args)
}
