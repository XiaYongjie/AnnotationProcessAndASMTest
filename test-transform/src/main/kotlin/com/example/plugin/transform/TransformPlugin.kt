package com.example.plugin.transform

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class TransformPlugin :Plugin<Project> {
    override fun apply(project: Project) {
        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)
        //only application module needs this plugin to generate register code
        if (isApp) {
            project.logger.quiet("Project enable arouter-register plugin")
            val android = project.extensions.getByType(AppExtension::class.java)
            val transformImpl = MethodTimerTransform(project)
            android.registerTransform(transformImpl)
        }
    }
}