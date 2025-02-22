# CalendarView
[![](https://jitpack.io/v/wangyu4life/CalendarView.svg)](https://jitpack.io/#wangyu4life/CalendarView)


## 一个多功能日历
### 功能
1. 支持水平和垂直滑动
2. 支持单选，多选，范围选择以及不可选日期
3. 支持默认文字样式，选中文字样式
4. 支持当前日期样式，选中样式，不可选样式
5. 支持跳转到指定日期

|![](https://s3.bmp.ovh/imgs/2025/02/22/72ff205eb1d4d858.gif) | ![](https://s3.bmp.ovh/imgs/2025/02/22/c5fbe7ff296daa26.gif) | ![](https://s3.bmp.ovh/imgs/2025/02/22/c2b0aeac453ea4f4.gif)|
|![](https://s3.bmp.ovh/imgs/2025/02/22/254727d67bb7162d.gif) | ![](https://s3.bmp.ovh/imgs/2025/02/22/78ca4f1aeb4686ba.gif)|

### 使用（Gradle）
首先，在项目的 `build.gradle（project）` 文件里面添加:

```gradle
allprojects {
	repositories {  

        maven { url "https://jitpack.io" }
		
    }
}
```

然后，在你需要用到CalendarView的module中的 `build.gradle（module）` 文件里面添加：
```gradle
dependencies {  

        implementation 'com.github.wangyu4life:CalendarView:1.0.0'

}
```