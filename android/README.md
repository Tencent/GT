# <font color=black>GT3.1 说明</font>

3.1相对于之前2.x的版本是一个重大的更新，新版本保留了2.x版本插桩出参入参的功能，其他方面的变化如下。


### <font color=#436EEE>一、GT3.1相对于2.x版本的变化</font>

1) 2.x的版本测试流畅值需要root手机，这一点在6.x之后的手机上越来越难。**3.1采用了在被测应用内嵌sdk的方式来获取流畅度，因此3.1版本的使用必须集成SDK，*不再支持独立使用*。**。

2) GT3.1引入了hook能力，可以获取更丰富的应用信息，例如页面加载速度，**卡顿代码调用栈、IO使用情况**等等；

3) 3.1直接以测试报告的形式图文展示所有性能数据；

4) 新增了**卡顿代码调用栈、页面加载速度、页面布局渲染速度、IO使用情况、分线程CPU时间片统计**等多个纬度的性能数据；


### <font color=#436EEE>二、SDK的引入：</font>

SDK的引入完全可以参照GTDemo工程，这是一个已经引入了sdk的样例

#### 注意事项

1) 因为在dependencies里有这句，所以libs目录下的jar包在工程构建的时候会自动引用；

2) 如果src目录下没有jniLibs目录，可以手动创建；

3) 如果工程中原来没有so，那么armeabi armeabi-v7a这几个目录，全都放到jniLibs里即可；

4) 如果工程原来已经有so或者有c/c++代码，那么abi的选择应当于原工程保持一致。比如原来的apk里只有armeabi这个目录，那么只在jniLibs里加入armeabi这个目录。

5) 不要忘记在工程中调用GTRController.init(Context context)方法；


### <font color=#436EEE>三、测试和查看报告：</font>

#### ① 测试流程：

1) 安装GT工具

2) 在GT工具中选取被测应用(**必须引入GT SDK， 否则获取不到测试数据**)，并点击“开始”，开始对被测应用的测试；此时可以在GT中打开悬浮窗，查看被测应用实时数据；

3) 测试完成后，在GT工具中点击“导出数据”，然后选取导出方式(从3.1开始，可以选择导出到微信还是本地目录/sdcard/GTRData)，然后选择要导出的数据，选择完后，数据会保存到/sdcard/GTRData或者微信中。

4) 将导出的文件data.js复制到<u>GT/GT_Report/data</u>目录下，替换原有的data.js即可

5) 双击”GT/GT_Report/性能结果.html”即可查看报告

#### ② 注意事项：

1) 测试期间不要杀死被测应用，重新启动应用相当于开始新的测试；

2) 每次点击间隔尽量大于5秒；

3) 尽可能遍历应用所有应用的所有功能和页面；

# <font color=black>GT3.1 User Manual</font>

GT v3.1 is an important update of the previous version 2.x. Apart from keeping the feature of implanting incoming and outgoing parameters, the new changes are:


### <font color=#436EEE>Ⅰ. Improvement of GT 3.1</font>
1) Previous versions need root permission from Android devices to obtain smoothness values, which is getting much harder for devices of Android 6.x and later. **Smoothness and other features are no longer accessible using standalone GT console only, and GT SDK must be configured properly to obtain data**;

2) The hook technique enables GT3.1 to acquire multidimensional application runtime status, like page loading time, **UI block call stacks, and IO usage**;

3) Report charts are used to demonstrate performance statistics;

4) Newly-added performance presentation pages, such as **UI block call stacks, page loading time, layout rendering speed, IO usage and CPU time slice statistics per thread**.


### <font color=#436EEE>Ⅱ. Importing GT SDK </font>

GTDemo is a good example of how to import GT SDK into target application.

#### Notes：

1) Jar archives under the 'libs' directory are compiled into project as dependency libraries; 

2) 'jniLibs' directory under 'src' directory is used by Gradle by default to find .so libraries, create it if not existed;

3) If your project does not have any .so library, copy everything from 'armeabi', 'armeabi-v7a' and 'x-86' directories into 'jniLibs';

4) If your project has .so libraries or native code, copy only the SDK .so libraries of desired ABIs. For example, copy only .so library in armeabi into jniLibs if you only have armeabi compiled;

5) Remember to call GTRController.init(Context context) to initialze and configure GT, usually in the Application class.

### <font color=#436EEE>Ⅲ. Test Your Apps and Read the Report</font>

#### ① Testing Procedure：

1) Install GT console(App) apk;

2) Choose one application to test(**Again, GT SDK is a must to get data**)，Press "start" button to trgger the test process; during this time, you can open the float window to see the real-time output;

3) When the test finishes, press "export" to choose the data and to determine where to export your result data. Options are local storage(sdcard/GTRData) and Wechat;

4) Copy the exported data.js file to the <u>GT/GT_Report/data</u> directory, overwriting the one that exists if needed;

5) Double-click the ”GT/GT_Report/性能结果.html” icon to read the report.

#### ② Notes：

1) Killing and restarting the target application processes means to restart the test;

2) The interval between two press actions is better not to be shorter than 5 seconds;

3) Go through as many features and pages as possible.
