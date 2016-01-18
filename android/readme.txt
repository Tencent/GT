1.本Andorid工程主体源码是GT Console的源码。

2.本工程依赖于三个jar:
    (1)android-support-v13.jar
    (2)mid-sdk-2.xx.jar，请到mta.qq.com网站下载
    (3)mta-sdk-2.x.x.jar，请到mta.qq.com网站下载
请编译前将上述jar包放到工程根路径下的libs目录中。

如使用抓包功能，需将tcpdump下载至工程根目录下的/res/raw/目录中。tcpdump请到http://www.androidtcpdump.com/android-tcpdump/downloads 下载最新版即可

3.具体模块描述请参考src目录中各package中的package-info.java文件。

4.根目录下的sdk目录，是GT SDK的源码及其调试的壳工程。需要将GT SDK导出jar包时，请选中src目录，及在eclipse中即时生成的gen目录中的package:com.tencent.wstt.gt及其子package导出jar即可。

5.根目录下的demo目录，是合入了GT SDK的一个被测app样例工程，可以直接用eclipse导入。