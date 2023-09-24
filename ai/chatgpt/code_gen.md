



```
假设你是一名java技术专家，下面是一些 Java 实体类
"""
public class ImageLocation {
    private String locationId;
    private String imageId; 
}
"""
参考以上实体类，根据如下业务逻辑和约束一步一步生成Java代码，业务逻辑描述如下：
"""
存储图片地理信息
1、根据将图片地理信息写入数据库
"""
约束：
1. 采用 Spring MVC 架构
2. 生成 Controller 类的类名和方法名
3. 生成 Service 层的接口类名和方法名
4. 根据 Service 实现类生成 Dao 层类及方法名
```





## 文心一言

问：

```
假设你是一名java技术专家，下面是一些 Java 实体类
"""
public class ImageLocation {
    private String imageId; 
    private double longitude;
    private double latitude;
    private String geoHash;
}
"""
参考以上实体类，根据如下业务逻辑和约束一步一步生成Java代码，业务逻辑描述如下：
"""
存储图片地理信息
1、根据将图片地理信息写入数据库
"""
约束：
1. 以markdown格式生成相关对象建表 SQL 语句，包括列名,数据类型，是否为空，注释
2. 所有表默认增 create_time 和 update_time 两个列
3. 仅生成建表sql
```



答：

根据给定的 ImageLocation 实体类，我们可以生成相应的数据库建表 SQL 语句。

```sql
CREATE TABLE image_location (  
    location_id VARCHAR(255) NOT NULL COMMENT '地点ID',  
    image_id VARCHAR(255) NOT NULL COMMENT '图片ID',  
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',  
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',  
    PRIMARY KEY (location_id, image_id)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

上述 SQL 语句创建了一个名为 image_location 的表，其中包含以下列：

location_id：存储地点ID，数据类型为 VARCHAR(255)，非空约束，注释为 '地点ID'。
image_id：存储图片ID，数据类型为 VARCHAR(255)，非空约束，注释为 '图片ID'。
create_time：存储创建时间，数据类型为 TIMESTAMP，默认值为当前时间戳，注释为 '创建时间'。
update_time：存储更新时间，数据类型为 TIMESTAMP，默认值为当前时间戳，更新时自动更新为当前时间戳，注释为 '更新时间'。
该表的主键为 (location_id, image_id)，使用 InnoDB 存储引擎，字符集为 utf8mb4。



根据表结构生成 Controller、Service、Mapper 如下类

```

"""
package com.imagedance.zpai.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/image-location")
public class ImageLocationController {
}

package com.imagedance.zpai.service;
import com.imagedance.zpai.model.po.ImageLocation;
import com.baomidou.mybatisplus.extension.service.IService;
public interface IImageLocationService extends IService<ImageLocation> {

}


package com.imagedance.zpai.service.impl;
import com.imagedance.zpai.model.po.ImageLocation;
import com.imagedance.zpai.dao.ImageLocationMapper;
import com.imagedance.zpai.service.IImageLocationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
@Service
public class ImageLocationServiceImpl extends ServiceImpl<ImageLocationMapper, ImageLocation> implements IImageLocationService {
}


package com.imagedance.zpai.dao;
import com.imagedance.zpai.model.po.ImageLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageLocationMapper extends BaseMapper<ImageLocation> {

}


@Getter
@Setter
@Accessors(chain = true)
@TableName("image_location")
public class ImageLocation implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String imageId;
    private Double longitude;
    private Double latitude;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
"""
假设你是一名java技术专家，将在如上类中实现如下业务功能
"""
存储图片地理信息
1、根据将图片地理信息写入数据库
"""

```

答

```

```



### 问

```

"""
package com.imagedance.zpai.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/image-location")
public class ImageLocationController {
}

package com.imagedance.zpai.service;
import com.imagedance.zpai.model.po.ImageLocation;
import com.baomidou.mybatisplus.extension.service.IService;
public interface IImageLocationService extends IService<ImageLocation> {

}


package com.imagedance.zpai.service.impl;
import com.imagedance.zpai.model.po.ImageLocation;
import com.imagedance.zpai.dao.ImageLocationMapper;
import com.imagedance.zpai.service.IImageLocationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
@Service
public class ImageLocationServiceImpl extends ServiceImpl<ImageLocationMapper, ImageLocation> implements IImageLocationService {
}


package com.imagedance.zpai.dao;
import com.imagedance.zpai.model.po.ImageLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageLocationMapper extends BaseMapper<ImageLocation> {

}


@Getter
@Setter
@Accessors(chain = true)
@TableName("image_location")
public class ImageLocation implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String imageId;
    private Double longitude;
    private Double latitude;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
"""
假设你是一名java技术专家，将在如上类中实现如下业务功能
"""
存储图片地理信息
1、根据 imageId 查询地理位置
"""
约束：
1.  只输出代码，不要解释
```



答

```
package com.imagedance.zpai.controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/image-location")
public class ImageLocationController {

    @Autowired
    private IImageLocationService imageLocationService;

    @GetMapping("/get-by-image-id")
    public ImageLocation getByImageId(@RequestParam("imageId") String imageId) {
        return imageLocationService.getByImageId(imageId);
    }
}

package com.imagedance.zpai.service;
import com.imagedance.zpai.model.po.ImageLocation;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IImageLocationService extends IService<ImageLocation> {
    ImageLocation getByImageId(String imageId);
}

package com.imagedance.zpai.service.impl;
import com.imagedance.zpai.model.po.ImageLocation;
import com.imagedance.zpai.dao.ImageLocationMapper;
import com.imagedance.zpai.service.IImageLocationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ImageLocationServiceImpl extends ServiceImpl<ImageLocationMapper, ImageLocation> implements IImageLocationService {
    @Override
    public ImageLocation getByImageId(String imageId) {
        return this.getOne(new QueryWrapper<ImageLocation>().eq("imageId", imageId));
    }
}

package com.imagedance.zpai.dao;
import com.imagedance.zpai.model.po.ImageLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageLocationMapper extends BaseMapper<ImageLocation> {
}

@Getter
@Setter
@Accessors(chain = true)
@TableName("image_location")
public class ImageLocation implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String imageId;
    private Double longitude;
    private Double latitude;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

```



### 根据地理位置查询图片

```

"""
package com.imagedance.zpai.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/image-location")
public class ImageLocationController {
}

package com.imagedance.zpai.service;
import com.imagedance.zpai.model.po.ImageLocation;
import com.baomidou.mybatisplus.extension.service.IService;
public interface IImageLocationService extends IService<ImageLocation> {

}


package com.imagedance.zpai.service.impl;
import com.imagedance.zpai.model.po.ImageLocation;
import com.imagedance.zpai.dao.ImageLocationMapper;
import com.imagedance.zpai.service.IImageLocationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
@Service
public class ImageLocationServiceImpl extends ServiceImpl<ImageLocationMapper, ImageLocation> implements IImageLocationService {
}


package com.imagedance.zpai.dao;
import com.imagedance.zpai.model.po.ImageLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageLocationMapper extends BaseMapper<ImageLocation> {

}


@Getter
@Setter
@Accessors(chain = true)
@TableName("image_location")
public class ImageLocation implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String imageId;
    private Double longitude;
    private Double latitude;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
"""
假设你是一名java技术专家，将在如上类中实现如下业务功能
"""
存储图片地理信息
1、根据地理位置查询图片信息
"""
约束：
1.  只输出代码，不要解释
```





```

"""
package com.imagedance.zpai.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/image-location")
public class ImageLocationController {
}

package com.imagedance.zpai.service;
import com.imagedance.zpai.model.po.ImageLocation;
import com.baomidou.mybatisplus.extension.service.IService;
public interface IImageLocationService extends IService<ImageLocation> {

}


package com.imagedance.zpai.service.impl;
import com.imagedance.zpai.model.po.ImageLocation;
import com.imagedance.zpai.dao.ImageLocationMapper;
import com.imagedance.zpai.service.IImageLocationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
@Service
public class ImageLocationServiceImpl extends ServiceImpl<ImageLocationMapper, ImageLocation> implements IImageLocationService {
}


package com.imagedance.zpai.dao;
import com.imagedance.zpai.model.po.ImageLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageLocationMapper extends BaseMapper<ImageLocation> {

}


@Getter
@Setter
@Accessors(chain = true)
@TableName("image_location")
public class ImageLocation implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String imageId;
    private Double longitude;
    private Double latitude;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

package com.imagedance.zpai.model;

import lombok.Data;
import lombok.experimental.Accessors;
public class ResultVo<T> {
    private String code = "0";
    private String message;
    private T data;
    public static <T> ResultVo<T> success(T data) {
        return new ResultVo<T>().setCode("0").setMessage("ok").setData(data);
    }
    public static ResultVo<String> success() {
        return new ResultVo<String>().setCode("0").setMessage("ok").setData("");
    }
    public static ResultVo<String> fail(ErrorCodes errorCodes) {
        return new ResultVo<String>().setCode(errorCodes.getCode()).setMessage(errorCodes.getMsg());
    }
}
"""
假设你是一名java技术专家，将在如上类中实现如下业务功能
"""
存储图片地理信息
1、根据imageId删除地理位置信息
"""
约束：
1. 只输出代码，不要解释
2. controller 所有接口返回为 ResultVo
```





## ChatGPT



```
假设你是一名java技术专家，下面是一些 Java 实体类
"""
public class ImageLocation {
    private String imageId;
    private Double longitude;
    private Double latitude;
    private String geoHash;
}
public class ImageInfo {
    private String id;
    private String name;
    private String description;
    private String url;
}
public class ImageDTO {
    private String id;
    private String name;
    private String description;
    private String url;
    private String geoHash;
    private Double longitude;
    private Double latitude;
}

"""
参考以上实体类，根据如下业务逻辑 以markdown格式生成相关对象建表 SQL 语句及索引：
"""
存储图片
1. 从 ImageDTO 解析出 ImageInfo 和 ImageLocation
2. 将 ImageInfo 写入数据库
3. 将 ImageLocation 写入数据库

根据 imageId 查询图片信息
1. 先从 redis 根据 imageId 查询 ImageDTO，如果查询到，返回；否则，继续
2. 根据 imageId 查询 ImageLocation
3. 根据 imageId 查询 ImageInfo
4. 将 ImageLocation 和 ImageInfo 组合为 ImageDTO

根据经纬度查询所有图片信息
1. 调用 redis 根据经纬度计算 x 公里的所有经纬度
2. 根据经纬度列表找到所有的 ImageLocation
3. 根据 ImageLocation 的 imageId 查询所有图片，返回ImageDTO

删除图片的地理信息
1. 根据 imageId 删除 ImageLocation

删除图片
1. 从 redis 中删除 imageId 对应的ImageDTO
2. 从数据库删除 imageId 对应的 ImageInfo
3. 从数据库删除 imageId 对应的 ImageLocation
"""
约束：
1. 所有表默认增 create_time 和 update_time 两个列
2. 表名和字段名都采用匈牙利命名
3. DTO 对象不需要创建对应的数据库表
4. 仅生成建表sql
```









## 总结

文心一言的建表能力比 chatgpt 强



