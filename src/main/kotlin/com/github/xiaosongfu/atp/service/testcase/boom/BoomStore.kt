package com.github.xiaosongfu.atp.service.testcase.boom

class BoomStore {
    data class Store(
        val env: HashMap<String, String> = hashMapOf()
    )

    companion object {
        private val storePool = hashMapOf<String, Store>()

        private fun init(executeSessionId: String): Store {
            synchronized(this) {
                storePool[executeSessionId] = Store()
            }

            return storePool[executeSessionId]!!
        }

        fun saveEnv(executeSessionId: String, envName: String, envValue: String) {
            (storePool[executeSessionId] ?: init(executeSessionId)).env[envName] = envValue
        }

        fun readEnvs(executeSessionId: String): HashMap<String, String> {
            return (storePool[executeSessionId] ?: init(executeSessionId)).env
        }
    }
}
