# FeatureHelper

使用命令生成洞穴、结构和地物。仅用于学习和交流。

## 命令格式

* 结构：
```
/structure start <x> <y> <z> <structureName> [debug] [dataTag]
/structure continue
/structure component <x> <y> <z> <compName> [dataTag]
/structure bb <hide|show>
```

* 洞穴：
```
/cave start <x> <y> <z> tunnel [yaw] [pitch] [radius] [length] [debug]
/cave start <x> <y> <z> room [radius]
/cave continue
/cave trail <hide|show>
/cavehell start <x> <y> <z> tunnel [yaw] [pitch] [radius] [length] [debug]
/cavehell start <x> <y> <z> room [radius]
/cavehell continue
/cavehell trail <hide|show>
/ravine start <x> <y> <z> tunnel [yaw] [pitch] [radius] [length] [debug]
/ravine continue
/ravine trail <hide|show>
```

* 地物：
```
/populate <x> <y> <z> <featureName> [args...]
```

## 画廊

* 海底神殿内景：

![monument](img/monument.png)

* forge的美丽bug，安装forge后主世界岩浆高度下降一格：

![lava](img/lava.png)

---

注意事项：
* 如果你使用eclipse开发，配置环境之前请解压eclipse.zip
* 配置环境之前，在gradle.properties中修改你的gradle-user-home
