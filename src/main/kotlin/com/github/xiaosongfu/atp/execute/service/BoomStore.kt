package com.github.xiaosongfu.atp.execute.service

class BoomStore {
    data class Store(
        val env: HashMap<String, String> = hashMapOf()
    )

    companion object {
        private val storePool = hashMapOf<String, Store>()

        private fun init(executeId: String): Store {
            synchronized(this) {
                storePool[executeId] = Store()
            }

            return storePool[executeId]!!
        }

        fun saveEnv(executeId: String, envName: String, envValue: String) {
            (storePool[executeId] ?: init(executeId)).env[envName] = envValue
        }

        fun readEnvs(executeId: String): HashMap<String, String> {
            return (storePool[executeId] ?: init(executeId)).env
        }
    }
}