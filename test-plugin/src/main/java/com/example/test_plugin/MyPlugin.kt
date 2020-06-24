package com.example.test_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.task("task-my-plugin"){
            print("----this is my first Task-----------")
        }
    }
}