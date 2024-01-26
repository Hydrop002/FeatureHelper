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

* 末地城：

![endcity](img/endcity.png)

* 林地府邸的外墙和屋顶被首先生成：

![mansion1](img/mansion1.png)

* 其内每一层，先生成走廊，后生成房间：

![mansion2](img/mansion2.png)

---

注意事项：
* 配置环境之前，在gradle.properties中修改你的gradle-user-home
