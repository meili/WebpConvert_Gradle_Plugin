package webp.plugin

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class WebpConvertBuildPlugin implements Plugin<Project> {
    WebpInfo config;

    @Override
    void apply(Project project) {
        def hasApp = project.plugins.withType(AppPlugin)

        def variants = hasApp ? project.android.applicationVariants : project.android.libraryVariants

        config = project.extensions.create("webpinfo", WebpInfo);

        project.afterEvaluate {

            variants.all { variant ->

                def flavor = variant.getVariantData().getVariantConfiguration().getFlavorName()
                def buildType = variant.getVariantData().getVariantConfiguration().getBuildType().name

                if (config.skipDebug == true && "${buildType}".contains("debug")) {
                    printlog "skipDebug webpConvertPlugin Task!!!!!!"

                    return
                }

                def dx = project.tasks.findByName("process${variant.name.capitalize()}Resources")
                def webpConvertPlugin = "webpConvertPlugin${variant.name.capitalize()}"
                project.task(webpConvertPlugin) << {
                    String resPath = "${project.buildDir}/intermediates/res/${flavor}/${buildType}"
                    println "resPath:" + resPath
                    def dir = new File("${resPath}")
                    dir.eachDirMatch(~/drawable[a-z0-9-]*/) { drawDir ->
                        printlog "drawableDir:" + drawDir
                        def file = new File("${drawDir}")
                        file.eachFile { filename ->
                            def name = filename.name
                            def f = new File("${project.projectDir}/webp_white_list.txt")
                            if (!f.exists()) {
                                f.createNewFile()
                            }
                            def isInWhiteList = false
                            f.eachLine { whiteName ->
                                if (name.equals(whiteName)) {
                                    isInWhiteList = true
                                }
                            }
                            if (!isInWhiteList) {
                                if (name.endsWith(".jpg") || name.endsWith(".png")) {
                                    if (!name.contains(".9")) {

                                        def picName = name.split('\\.')[0]
                                        def suffix = name.split('\\.')[1]
                                        printlog "find target pic >>>>>>>>>>>>>" + name
                                        printlog "picName:" + picName


                                        "cwebp -q 75 -m 6 ${filename} -o ${drawDir}/${picName}.webp".execute()
                                        sleep(10)
                                        "rm ${filename}".execute()
                                        printlog "delete:" + "${filename}"
                                        printlog "generate:" + "${drawDir}/${picName}.webp"

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

    void printlog(String msg) {
        if (config.isShowLog == true) {
            println msg
        }
    }
}


class WebpInfo {
    boolean skipDebug
    boolean isShowLog
}
