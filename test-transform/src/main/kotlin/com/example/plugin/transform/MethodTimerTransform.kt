package com.example.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class MethodTimerTransform(var project: Project) : Transform() {


    override fun getName(): String {
        return "MethodTimerTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        var startTime = System.currentTimeMillis()
        println("----------transform start:${startTime}-----------")
        var inputs = transformInvocation?.inputs
        val outputProvider = transformInvocation?.outputProvider
        outputProvider?.deleteAll()

        inputs?.forEach {
            it.directoryInputs.forEach { dInput ->
                try {
                    handleDirectoryInput(dInput, outputProvider!!)
                }catch (e:Exception){
                    e.printStackTrace()
                }


            }
            it.jarInputs.forEach { jInpt ->
                try {
                    handleJarInputs(jInpt, outputProvider!!)
                }catch (e:Exception){
                    e.printStackTrace()
                }



            }
        }
        var endTime = System.currentTimeMillis()
        println("-----------transform end${endTime}-------------")
        println("-----------transform cost: ${endTime - startTime}-------------")
    }

    /**
     * 处理文件目录下的class文件
     */
    private fun handleDirectoryInput(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider
    ) {
        //是否是目录
        if (directoryInput.file.isDirectory()) {
            //列出目录所有文件（包含子文件夹，子文件夹内文件）
            directoryInput.file.walk().maxDepth(Int.MAX_VALUE).filter { it.isFile }
                .forEach { file ->
                    var name = file.name
                    if (name.endsWith(".class")) {
                        println("-----------handleDirectoryInput ${name}-----true------")
                        val classReader = ClassReader(file.readBytes())
                        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                        val cv = LifecycleClassVisitor(classWriter)
                        classReader.accept(cv, EXPAND_FRAMES)
                        val code = classWriter.toByteArray()
                        val fos = FileOutputStream(
                            file.parentFile.absolutePath + File.separator + name
                        )
                        fos.write(code)
                        fos.close()
                    }
                }

        }
        //处理完输入文件之后，要把输出给下一个任务
        val dest = outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes,
            directoryInput.scopes,
            Format.DIRECTORY
        )
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    private fun handleJarInputs(jarInput: JarInput, outputProvider: TransformOutputProvider) {
        if (jarInput.file.absolutePath.endsWith(".jar")) {
            //重名名输出文件,因为可能同名,会覆盖
            var jarName = jarInput.name
            val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length - 4)
            }
            val jarFile = JarFile(jarInput.file)
            val enumeration = jarFile.entries()
            val tmpFile = File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            //避免上次的缓存被重复插入
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            val jarOutputStream = JarOutputStream(FileOutputStream(tmpFile))
            //用于保存
            while (enumeration.hasMoreElements()) {
                val jarEntry = (enumeration.nextElement()) as JarEntry
                val inputStream = jarFile.getInputStream(jarEntry)
                var entryName = jarEntry.name
                val zipEntry = ZipEntry(entryName)
                jarOutputStream.putNextEntry(zipEntry)
                //插桩class
                //class文件处理
                if (entryName.endsWith("Activity.class")) {
                    println("----------- handleJarInputs $entryName -------true----")
                    val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    val cv = LifecycleClassVisitor(classWriter)
                    classReader.accept(cv, EXPAND_FRAMES)
                    val code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            //结束
            jarOutputStream.close()
            jarFile.close()
            val dest = outputProvider.getContentLocation(
                jarName + md5Name,
                jarInput.contentTypes, jarInput.scopes, Format.JAR
            )
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }
}

/**
 * 处理Jar中的class文件
 */
