package com.example.plugin.transform

import org.gradle.api.Plugin
import org.gradle.api.Project

class TransformPlugin :Plugin<Project> {
    override fun apply(project: Project) {
        println("--------TransformPlugin-----")
    }
}