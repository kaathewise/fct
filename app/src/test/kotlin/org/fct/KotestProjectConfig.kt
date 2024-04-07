package org.fct
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode

object KotestProjectConfig : AbstractProjectConfig() {
    override var displayFullTestPath : Boolean? = true
    override val isolationMode = IsolationMode.InstancePerLeaf
}