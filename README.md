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
## 基本使用
```
new UpdateUtil.Builder()
                        .setActivity(MainActivity.this)
                        .setUrl("https://my-test-1253832037.cos.ap-chengdu.myqcloud.com/app-release.apk")
                        .setNewVersionCode(2)
                        .setUpdateListener(new UpdateUtil.UpdateListener() {
                            @Override
                            public void upDateStart(long max) {
                                logTv.setText("开始下载");
                            }

                            @Override
                            public void upDateProgress(int progress) {
                                logTv.setText("下载进度:" + progress);
                            }

                            @Override
                            public void upDateComplete(String path) {
                                logTv.setText("下载完成 路径:" + path);
                            }

                            @Override
                            public void upDateFail(String message) {
                                logTv.setText("下载失败:" + message);
                            }
                        })
                        .build()
                        .start();
```
