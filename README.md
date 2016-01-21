## webp batch convert pics gradle plugin
*Read this in other languages: [简体中文](README.zh-cn.md).*

### Introduce
***

This plugin can find the jpg,png files under the directory of /build/intermediates/res/${flavorName}/${buildType} while gradle building,
and then auto convert them into webp files.

So,the generated apk is included with the webp pics.


The rule to find the target pic:

1. the directories which name starts with 'drawable'
2. the suffix name of the file is 'jpg' or 'png'
3. exclude the .9 pic

The plugin task named 添加一个名为webpConvertPlugin,which will execute before processXXXResource Task 


### Install 'webp'  command tools 
***
1.  Install by homebrew ：
        
        brew install webp

2. install by macports：

    download the necessary pkg on <http://distfiles.macports.org/MacPorts/> and install 
    then run the command lines:

		export PATH=$PATH:/opt/local/bin
		sudo port selfupdate
		sudo port install webp


3. use 'cwebp' command in terminal to check if it installed success.



reference to :
	<https://developers.google.com/speed/webp/docs/precompiled#installing_cwebp_and_dwebp_on_os_x_with_macports>


### Usage：
***
1. add below code in the outer build.gradle file(which is in the same directory of settings.gradle)

     classpath 'com.mogujie.gradle:webpConvertPlugin:1.1.33'
    
2. add below code in the inner build.gradle file(which is in the same directory of src)

    apply plugin: 'webpConvert'

		    webpinfo {
			    //if skip the task when debug
    		    skipDebug = true
    		    //if print log
    		    isShowLog = false
		    }


3. add one file called 'webp_white_list.txt'(it will be created auto if the file don't exist when the plugin executed),it can keep the file you don't want to convert.just as below:
		   
		     bill_footer_sitepro_arrow.png
		    cart_checkbox_false.png


now,clean your project then build it .you will find the webp pics is in your generated apk 

    gradle clean
    gradle assembleDebug

contact me:boyue@mogujie.com


