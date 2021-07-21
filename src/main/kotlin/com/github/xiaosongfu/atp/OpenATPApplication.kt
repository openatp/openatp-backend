package com.github.xiaosongfu.atp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OpenATPApplication

fun main(args: Array<String>) {
	runApplication<OpenATPApplication>(*args)
}
