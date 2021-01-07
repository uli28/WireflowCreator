package com.uli28.wireflowcreator.wireflows.config

import androidx.test.platform.app.InstrumentationRegistry
import com.uli28.wireflowcreator.wireflows.config.ConfigParameter.Companion.ENABLE_WIREFLOW_CREATION
import java.lang.reflect.Field

class BuildConfigValueProvider {
    companion object{
        private var cachedIsWireflowEnabledValue: Boolean? = null
        private var buildConfigEnvironmentVariable: String? = null
        fun isWireflowCreationEnabled(): Boolean {
            cachedIsWireflowEnabledValue?.let{
                return cachedIsWireflowEnabledValue as Boolean
            }
            InstrumentationRegistry.getArguments().getString(ENABLE_WIREFLOW_CREATION)
                ?.let{
                    cachedIsWireflowEnabledValue = it.toBoolean()
                    return it.toBoolean()
                }
            buildConfigEnvironmentVariable?.let{
                cachedIsWireflowEnabledValue = it.toBoolean()
                return it.toBoolean()
            }
            return false
        }

        fun setIsWireflowCreationEnabled(environmentPropertyIsEnabled: String?) {
            cachedIsWireflowEnabledValue = null
            buildConfigEnvironmentVariable = environmentPropertyIsEnabled
        }

        fun getBuildConfigValue(
            packageName: String,
            fieldName: String?
        ): Any? {
            try {
                val clazz =
                    Class.forName("$packageName.BuildConfig")
                val field: Field = clazz.getField(fieldName!!)
                return field.get(null)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            return null
        }
    }
}