package webp.plugin

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class WebpConvertBuildPlugin implements Plugin<Project> {


    @Override
    void apply(Project project) {
        def hasApp = project.plugins.withType(AppPlugin)

        def variants = hasApp ? project.android.applicationVariants : project.android.libraryVariants

        WebpInfo config = project.extensions.create("webpinfo", WebpInfo);

//        int n = 0
        project.afterEvaluate {

            variants.all { variant ->

                def flavor = variant.getVariantData().getVariantConfiguration().getFlavorName()
                def buildType = variant.getVariantData().getVariantConfiguration().getBuildType().name

                if (config.skipDebug == true && "${buildType}".contains("debug")) {
                    if(config.isShowLog == true){
                        println "skipDebug webpConvertPlugin Task!!!!!!"
                    }

                    return
                }

                def dx = project.tasks.findByName("process${variant.name.capitalize()}Resources")
                def webpConvertPlugin = "webpConvertPlugin${variant.name.capitalize()}"
                project.task(webpConvertPlugin) << {
                    String resPath = "${project.buildDir}/intermediates/res/${flavor}/${buildType}"
                    println "resPath:" + resPath
                    def dir = new File("${resPath}")
                    dir.eachDirMatch(~/drawable[a-z0-9-]*/) { drawDir ->
                        if(config.isShowLog == true){
                            println "drawableDir:" + drawDir
                        }
                        def file = new File("${drawDir}")
                        file.eachFile { filename ->
//                            println "filename:" + filename
                            def name = filename.name
                            def f = new File("${project.projectDir}/webp_white_list.txt")
                            if (!f.exists()) {
                                f.createNewFile()
                            }
                            def isInWhiteList = false
                            f.eachLine { whiteName ->
//                                println "find white list line: ${whiteName}"
                                if (name.equals(whiteName)) {
                                    isInWhiteList = true
                                }
                            }
                            if (!isInWhiteList) {
                                if (name.endsWith(".jpg") || name.endsWith(".png")) {
                                    if (!name.contains(".9")) {

                                        def picName = name.split('\\.')[0]
                                        def suffix = name.split('\\.')[1]
                                        if(config.isShowLog == true){
                                            println "find target pic >>>>>>>>>>>>>" + name
                                            println "picName:" + picName
                                        }

//                                        if (n < 5) {
//                                            println "find target pic >>>>>>>>>>>>>" + name
//                                           def tSource = Tinify.fromFile("${filename}");
//                                            tSource.toFile("${drawDir}/${picName}tiny.${suffix}");
//                                            n++
//                                        }

                                        "cwebp -q 75 -m 6 ${filename} -o ${drawDir}/${picName}.webp".execute()
                                        sleep(10)
                                        "rm ${filename}".execute()
                                        if(config.isShowLog == true){
                                            println "delete:" + "${filename}"
                                            println "generate:" + "${drawDir}/${picName}.webp"
                                        }

                                    }
                                }
                            }

                        }
                    }
                }

                project.tasks.findByName(webpConvertPlugin).dependsOn dx.taskDependencies.getDependencies(dx)
                dx.dependsOn project.tasks.findByName(webpConvertPlugin)
            }

        }

    }
}


class WebpInfo {
    boolean skipDebug
    boolean isShowLog
}
