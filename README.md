# SoftwareEngine
MindMap Android App for SE Course Work.

## 基本开发环境安装
### [Java]
* 下载JDK（点普通下载）：http://rj.baidu.com/soft/detail/14459.html?ald
* 环境变量配置：https://jingyan.baidu.com/article/f96699bb8b38e0894e3c1bef.html

### [Android]
* Android Studio下载安装（下这个网址里推荐的那个）：http://www.android-studio.org/
* 第一次运行会自动识别JDK，能成功创建一个Project就行。

### [Git]
* 下载Git工具（这不是GitHub，是Git，最基本的依赖工具）：https://git-scm.com/downloads
* 下载页面选64-bit Git for Windows Setup.
* 安装全部下一步。
* 生成Git SSH Key（这一步必须，否则不能提交或者Clone代码）：http://blog.csdn.net/lsyz0021/article/details/52064829

### [GitHub]
* 图形界面桌面客户端下载：https://desktop.github.com/
* 安装成功后打开，点左上角的“+”，选择“Clone”，在Filter搜索框里搜“SoftwareEngine”，或者直接找到Fight1127栏下的该项目，再点“Clone SoftwareEngine，到自己电脑。
* 在点左上角顺数第3个按钮，点开后显示的是”Create New Branch“，即新建一个分支，避免把master分支搞炸了，名字随便取。

## 导入工程
* 打开Android Studio，选择第二个”Open an existing Android Studio Project“，然后选择刚才你Clone的那个项目目录即SoftwareEngine，点OK导入。
* 导入时间久，完成后如果有错，另外解决。
* 如果没错，Studio会识别到这是可以Git的项目，右下角弹出小框会提示”XXX Unregister XXX“什么的字样（忘了具体内容），点小三角展开后点击”Add root“即可。
