# 实验资源预约借用系统

本项目采用 Java Swing 和 MySQL，实现实验室预约、设备借还、故障报修、权限控制及数据统计等功能，作为 Scrum 课程团队迭代的初始版本。

## 一、运行环境

- JDK 17
- MySQL 8.0
- IntelliJ IDEA
- Maven，由 IDEA 同步项目依赖

## 二、获取项目

在 IDEA 中选择“从版本控制获取”，填写仓库地址：

https://github.com/ss13114541551-png/LabEquipmentManagementSystem.git

下载并打开项目，等待 Maven 依赖同步完成，将项目 SDK 设置为 Java 17。

## 三、初始化数据库

每位组员使用自己电脑上的 MySQL。

### 1. 创建数据库

使用本机 MySQL 的 root 账号连接数据库，执行：

```sql
CREATE DATABASE lab_equipment_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE lab_equipment_db;
```

### 2. 导入数据

在 IDEA 数据库面板中找到 lab_equipment_db，右键选择：

导入/导出 → 使用 mysql 还原

选择项目中的 database/init.sql，并指定本机 mysql.exe 的路径，执行导入。

也可以使用 MySQL 命令行客户端，在执行 USE lab_equipment_db 后输入：

```text
source D:/你的项目路径/database/init.sql
```

将路径替换为自己电脑上的实际位置。

导入后应包含12张数据表、2个视图和13个触发器。

注意：init.sql 用于首次初始化，重复导入会重建同名表并覆盖原有数据。视图和触发器指定的定义者为 root@localhost，建议在本地使用 root 账号导入。

## 四、配置数据库连接

在 src/main/resources 中，将：

db.properties.example

复制一份，改名为：

db.properties

打开新文件，填写本机 MySQL 账号和密码：

```properties
db.url=jdbc:mysql://localhost:3306/lab_equipment_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Taipei&useSSL=false&allowPublicKeyRetrieval=true
db.username=root
db.password=填写自己电脑的MySQL密码
```

如果 MySQL 端口不是3306，需要同步修改连接地址。

db.properties 属于个人配置，已通过 .gitignore 排除，不提交到仓库。

## 五、启动项目

在 IDEA 中找到：

src/main/java/com/shao/labequipment/Main.java

右键选择“运行 Main.main()”。

启动类完整名称：

com.shao.labequipment.Main

## 六、演示账号

以下账号仅用于本地课程演示，初始密码均为123456：

| 角色 | 用户名 |
|---|---|
| 普通学生 | student01 |
| 实验室管理员 | labadmin01 |
| 系统管理员 | sysadmin |

数据库连接账号用于 Java 连接 MySQL；演示账号用于系统登录界面，两者不同。

## 七、常见问题

- 提示缺少 db.properties：按照第四节复制配置文件。
- 提示 Access denied：检查数据库用户名和密码。
- 提示无法连接数据库：确认 MySQL 服务已启动，地址和端口正确。
- 提示表或视图不存在：确认已完整导入 database/init.sql。
