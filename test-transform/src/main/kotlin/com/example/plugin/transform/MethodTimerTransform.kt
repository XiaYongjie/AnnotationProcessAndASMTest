package com.example.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import kotlin.collections.HashSet

class MethodTimerTransform(var project: Project) : Transform() {


    override fun getName(): String {
        return "MethodTimerTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.PROJECT_ONLY
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        val startTime = System.currentTimeMillis()
         var dir: DirectoryInput? =null
           val jar = HashSet<JarInput>()
        transformInvocation?.inputs?.forEach {input->
            input.directoryInputs.forEach {//工程下的目录
                if(dir == null && it.file != null)
                    dir = it
            }
            input.jarInputs.forEach {//jar和aar中的jar
                jar.add(it)
                insertInitCodeIntoJarFile(it.file)
            }
        }
        var dest = transformInvocation?.outputProvider?.getContentLocation(dir?.name,dir?.contentTypes,dir?.scopes, Format.DIRECTORY)
        FileUtils.copyDirectory(dir?.file,dest)//目录输出
        jar.forEach {//jar输出 ，md5采用jar包名称，因为在重复打包过程中，jar路径会变化，导致包重复
            val newName = it.name.replace(".jar","")+ DigestUtils.md5Hex(it.name)//it.name是jar包名称，对应名称唯一
            val desFile = transformInvocation?.outputProvider?.getContentLocation(newName,it.contentTypes, it.scopes, Format.JAR)//信息会存在json中
            FileUtils.copyFile(it.file, desFile)
        }
        project.logger.quiet("------${getName()}costtime-----"+(System.currentTimeMillis()-startTime)+"ms")

    }

    /**
     * generate code into jar file
     * @param jarFile the jar file
     * @return
     */
    private fun insertInitCodeIntoJarFile(jarFile: File) {
            var optJar =File(jarFile.parent, jarFile.name + ".opt")
            if (optJar.exists())
                optJar.delete()
        var file = JarFile(jarFile)
            val enumeration = file.entries()
            val jarOutputStream = JarOutputStream(FileOutputStream(optJar))

            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration?.nextElement() as JarEntry
                val entryName = jarEntry.name
                project.logger.quiet("------entryName-----${entryName}")
//                val zipEntry =  ZipEntry(entryName)
//                val inputStream = file.getInputStream(jarEntry)
//                jarOutputStream.putNextEntry(zipEntry)
//                inputStream.close()
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            file.close()

            if (jarFile.exists()) {
                jarFile.delete()
            }
            optJar.renameTo(jarFile)
        }


}