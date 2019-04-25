# Update
一个用于app更新 下载新版本安装包并校验安装的库
## Gradle 依赖
在根build.gradle文件中配置:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
在应用的build.gradle文件中配置:
```
dependencies {
    implementation 'com.github.DiuChen:Update:1.0.1'
}
```
