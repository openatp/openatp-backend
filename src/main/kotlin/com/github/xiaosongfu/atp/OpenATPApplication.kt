package com.github.xiaosongfu.atp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.github.xiaosongfu.atp", "com.github.xiaosongfu.jakarta")
class OpenATPApplication

fun main(args: Array<String>) {
	runApplication<OpenATPApplication>(*args)
}
