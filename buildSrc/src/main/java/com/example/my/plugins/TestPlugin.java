package com.example.my.plugins;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

class TestPlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        System.out.println("-------project-------");
        project.task("my_task").doFirst(new Action<Task>() {
            @Override
            public void execute(Task task) {
                System.out.println("-------start-------");
            }
        }).doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                System.out.println("-------doLast-------");
            }
        });
    }
}
