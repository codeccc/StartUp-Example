# StartUp-Example
**JetPack**组件**App StartUp**使用示例，通过伪Sdk代码实现Sdk链式启动和懒启动。

## App StartUp 简介

App Startup 是 Android Jetpack 家族的成员，提供了一种在应用程序启动时，简单、高效的初始化组件的方法。无论是App开发人员，还是Library开发人员都可以使用App Startup来简化启动过程，并显式地设置初始化顺序。
App Startup 不需要为每个组件的初始化定义单独的 ContentProvider，它提供了一个 ContentProvider 来运行所有依赖项的初始化，从而显著的提高了应用程序的启动速度。

**App StartUp**的地址：[查看官方英文文档](https://developer.android.google.cn/topic/libraries/app-startup)

**StatuUp Example**示例：[查看StartUp-Example](https://github.com/codeccc/StartUp-Example)

## JetPack组件Statup的基本使用方法
### 一、基本使用

#### 1. 在app的build.gradle中添加**App StartUp**依赖
```groovy
implementation "androidx.startup:startup-runtime:1.1.1"
```
#### 2. 新建类，继承自 `Initializer<T>` ，并实现`create()`和`dependencies()`

T泛型使用需要初始化的Sdk的名称，以[SdkA](app/src/main/java/com/example/startup_example/task/SdkA.kt)为例，并在`create()`方法中完成对SdkA的初始化：
```kotlin
class AInitializer : Initializer<SdkA> {
    override fun create(context: Context): SdkA {
        return SdkA.create(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
```

由于[SdkA](app/src/main/java/com/example/startup_example/task/SdkA.kt)是独立Sdk，不与其他依赖产生关联，所以 ` dependencies()`方法返回空列表。

#### 3. 在[AndroidManifest.xml](app/src/main/AndroidManifest.xml)中注册`StartUp`的Provider组件

除了`meta-data`的name需要指向自己实现的[AInitializer](app/src/main/java/com/example/startup_example/task/AInitializer.kt)类以外，其余需按如下格式来写：
```xml
 <provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="com.example.startup_example.task.AInitializer"
        android:value="androidx.startup" />
</provider>
```
`android:value="androidx.startup"`中的value值固定为`androidx.startup`。

至此，便已实现通过`StartUp`初始化第三方Sdk的功能，当app运行时会自动初始化三方SDK。

### 二、进阶使用

#### 1、延迟初始化SDK，实现SDK懒加载

1. 在普通使用的基础之上，首先将[AndroidManifest.xml](app/src/main/AndroidManifest.xml)中注册的`Initializer`所在的`meta-data`标签中添加`tools:node="remove"`
```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="com.example.startup_example.task.EInitializer"
        android:value="androidx.startup"
        tools:node="remove" />
</provider>
```

2. 在需要初始化的地方，手动调用代码初始化Sdk
```kotlin
//延迟初始化Sdk
AppInitializer.getInstance(applicationContext)
    .initializeComponent(EInitializer::class.java)
```

完整代码如下：
```kotlin
binding.fab.setOnClickListener { view ->

    //延迟初始化Sdk
    AppInitializer.getInstance(applicationContext)
        .initializeComponent(EInitializer::class.java)
        
    Snackbar.make(view, "初始化SdkE", Snackbar.LENGTH_LONG)
        .setAnchorView(R.id.fab)
        .setAction("Action", null).show()
}
```

#### 2、实现Sdk链式启动

链式启动，即Sdk启动存在先后顺序或相互关联，比如需要先启动SdkA后再启动SdkB。

假设[SdkB](app/src/main/java/com/example/startup_example/task/SdkB.kt)需要在[SdkA](app/src/main/java/com/example/startup_example/task/SdkA.kt)初始化后初始化，则[SdkB](app/src/main/java/com/example/startup_example/task/SdkB.kt)的[BInitializer](app/src/main/java/com/example/startup_example/task/BInitializer.kt)代码可定义如下：
```kotlin
class BInitializer : Initializer<SdkB> {
    override fun create(context: Context): SdkB {
        //加入延时方便查看启动先后顺序
        Thread.sleep(1000)
        return SdkB.create(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(AInitializer::class.java)
    }
}
```
在[AndroidManifest.xml](app/src/main/AndroidManifest.xml)注册[BInitializer](app/src/main/java/com/example/startup_example/task/BInitializer.kt)：
```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data
        android:name="com.example.startup_example.task.AInitializer"
        android:value="androidx.startup" />
    
    <!--注册SdkB的启动类-->
    <meta-data
        android:name="com.example.startup_example.task.BInitializer"
        android:value="androidx.startup" />
</provider>
```

此时便完成了Sdk的链式启动，对Sdk初始化进行打印，app运行后的打印日志如下：
```
2022-07-15 18:18:17.890 3915-3915/com.example.startup_example D/StartUp: SdkA被初始化了
2022-07-15 18:18:18.891 3915-3915/com.example.startup_example D/StartUp: SdkB被初始化了
```

### 代码示例
完整example代码地址： [StartUp-Example](https://github.com/codeccc/StartUp-Example)

示例代码的Sdk依赖说明：

![Sdk依赖启动关系](images/1.png)

SdkE启动图示：

![SdkE启动图示](images/2.png)

控制台中各Sdk初始化打印日志：
```
2022-07-15 18:18:17.890 3915-3915/com.example.startup_example D/StartUp: SdkA被初始化了
2022-07-15 18:18:18.891 3915-3915/com.example.startup_example D/StartUp: SdkB被初始化了
2022-07-15 18:18:19.394 3915-3915/com.example.startup_example D/StartUp: SdkC被初始化了
2022-07-15 18:18:19.898 3915-3915/com.example.startup_example D/StartUp: SdkD被初始化了
2022-07-15 18:19:40.135 3915-3915/com.example.startup_example D/StartUp: SdkE被初始化了
```

![日志](images/3.png)
