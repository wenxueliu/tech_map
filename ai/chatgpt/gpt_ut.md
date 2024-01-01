gpt_ut



## GPT Cookbook

### 参数

language: 

test_model:

code:

### 模板

```
[system]
You are a world-class {{language}} developer with an eagle eye for unintended bugs and edge cases. You carefully explain code with great detail and accuracy. You organize your explanations in markdown-formatted, bulleted lists.

[user]
Please explain the following {{language}} function. Review what each element of the function is doing precisely and what the author's intentions may have been. Organize your explanation as a markdown-formatted, bulleted list.
​```{{language}}
{{code}}
​```
```



第二步

```
[user]
A good unit test suite should aim to:
- Test the function's behavior for a wide range of possible inputs
- Test edge cases that the author may not have foreseen
- Take advantage of the features of `{{test_model}}` to make the tests easy to write and maintain
- Be easy to read and understand, with clean code and descriptive names
- Be deterministic, so that the tests always pass or fail in the same way

To help unit test the function above, list diverse scenarios that the function should be able to handle (and under each scenario, include a few examples as sub-bullets).
```



第三步

```
[user]
In addition to those scenarios above, list a few rare or unexpected edge cases (and as before, under each edge case, include a few examples as sub-bullets).
```

第四步

```
[system]
You are a world-class {{language}} developer with an eagle eye for unintended bugs and edge cases. You write careful, accurate unit tests. When asked to reply only with code, you write all of your code in a single block.

[user]
Using {{language}} and the `{{test_model}}` package, write a suite of unit tests for the function, following the cases above. Include helpful comments to explain each line. Reply only with code
```



### 案例 1

#### 案例

第一步

```
Please explain the following Java function getImagesWithinRadius. Review what each element of the function is doing precisely and what the author's intentions may have been. Organize your explanation as a markdown-formatted, bulleted list. 
​```java
@Data
public class GisLocation {
    private String id;
    private Double longitude;
    private Double latitude;
}

public class GisService implements IGisService {
    private final ImageGisDao imageGisDao;
    
    @Override
    public List<GisLocation> getImagesWithinRadius(double latitude, double longitude, double radius) {
        return imageGisDao.getImagesWithinRadius(latitude, longitude, radius * 1000); // Convert to metres
    }
}
​```
```

第二步

```
the parameters Constraints
- double latitude: [-90,90]
- double longitude: [-180, 180]
- double radius: [1,10]

A good unit test suite should aim to:
- Test the function's behavior for a wide range of possible inputs
- Test edge cases that the author may not have foreseen
- Take advantage of the features of `Junit5`, `Mockito` to make the tests easy to write and maintain
- Be easy to read and understand, with clean code and descriptive names
- Be deterministic, so that the tests always pass or fail in the same way

To help unit test the function above, list diverse scenarios that the function should be able to handle (and under each scenario, include a few examples as sub-bullets).all examples as json format. for example, 
{
    "xxxx_cas": [
    	{"latitude": 91.0, "longitude": 20.0, "radius": 5.0}
    ],
    "yyyy_cas": [
    	{"latitude": 91.0, "longitude": 20.0, "radius": 5.0}
    ],
}
```

第三步

```
In addition to those scenarios above, list a few rare or unexpected edge cases (and as before, under each edge case, include a few examples as sub-bullets). the result add as above json.
```

第四步

valid_cases

```
"valid_cases": [
        {"latitude": 0.0, "longitude": 0.0, "radius": 1.0},
        {"latitude": -90.0, "longitude": -180.0, "radius": 10.0},
        {"latitude": 90.0, "longitude": 180.0, "radius": 5.0}
    ]

You are a world-class Java developer with an eagle eye for unintended bugs and edge cases. You write careful, accurate unit tests. When asked to reply only with code.

Using Java and the `JUnit`, `Mockito` package, write a suite of unit tests for the function, following the cases above. Include all cases follow a naming standard of Given_x_When_y_Then_z, each unit test method as mock, given, when, then, assert.
```

edge_cases

```
"edge_cases": [
        {"latitude": -90.0, "longitude": 180.0, "radius": 1.0},
        {"latitude": 90.0, "longitude": -180.0, "radius": 10.0}
    ]
You are a world-class Java developer with an eagle eye for unintended bugs and edge cases. You write careful, accurate unit tests. When asked to reply only with code.

Using Java and the `JUnit`, `Mockito` package, write a suite of unit tests for the function, following the cases above. Include all cases follow a naming standard of Given_x_When_y_Then_z, each unit test method as given, when, then, assert.
```

out_of_upper_bound_cases

```
"out_of_upper_bound_cases": [
        {"latitude": 91.0, "longitude": 0.0, "radius": 5.0},
        {"latitude": 0.0, "longitude": 181.0, "radius": 5.0},
        {"latitude": 0.0, "longitude": 0.0, "radius": 11.0}
    ]
You are a world-class Java developer with an eagle eye for unintended bugs and edge cases. You write careful, accurate unit tests. When asked to reply only with code.

Using Java and the `JUnit`, `Mockito` package, write a suite of unit tests for the function, following the cases above. Include all cases follow a naming standard of Given_x_When_y_Then_z, each unit test method as given, when, then, assert.
```

out_of_lower_bound_cases

```
"out_of_lower_bound_cases": [
        {"latitude": -91.0, "longitude": 0.0, "radius": 5.0},
        {"latitude": 0.0, "longitude": -181.0, "radius": 5.0},
        {"latitude": 0.0, "longitude": 0.0, "radius": 0.0}
    ]
You are a world-class Java developer with an eagle eye for unintended bugs and edge cases. You write careful, accurate unit tests. When asked to reply only with code.

Using Java and the `JUnit`, `Mockito` package, write a suite of unit tests for the function, following the cases above. Include all cases follow a naming standard of Given_x_When_y_Then_z, each unit test method as given, when, then, assert.
```

semi_bound_cases

```
    "semi_bound_cases": [
        {"latitude": 89.9999, "longitude": 179.999, "radius": 9.999},
        {"latitude": -89.9999, "longitude": -179.9999, "radius": 9.9999}
    ]
    You are a world-class Java developer with an eagle eye for unintended bugs and edge cases. You write careful, accurate unit tests. When asked to reply only with code.

Using Java and the `JUnit`, `Mockito` package, write a suite of unit tests for the function, following the cases above. Include all cases follow a naming standard of Given_x_When_y_Then_z, each unit test method as given, when, then, assert.
```



### 案例 2

测试参数构造

```
You're a world-class Java developer with a keen sight for detecting the most elusive bugs and considering edge cases.
​```java
@Data
public class ImageLocationDistanceVo {
    private Double longitude;
    private Double latitude;
    double radius;
}
@Getter
@Setter
@Accessors(chain = true)
@TableName("image_location")
public class ImageLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    
    private String imageId;
    
    private Double longitude;
    
    private Double latitude;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
public class ImageLocationServiceImpl extends ServiceImpl<ImageLocationDao, ImageLocation> implements IImageLocationService {
    @Override
    public List<ImageLocation> searchLocation(ImageLocationDistanceVo imageLocationDistanceVo) {
        Point point = new Point(imageLocationDistanceVo.getLatitude(), imageLocationDistanceVo.getLongitude());
        Distance distance = new Distance(imageLocationDistanceVo.getRadius());
        List<String> imageIds = geoService.searchGeoNearbyIds(REDIS_GEO_KEY, point, distance);
        if (CollectionUtils.isEmpty(imageIds)) {
            imageGisDao.getImagesWithinRadius(imageLocationDistanceVo.getLatitude(), imageLocationDistanceVo.getLongitude(),
                    imageLocationDistanceVo.getRadius() * 1000);
        }
        return imageLocationDao.selectBatchIds(imageIds);
    }
}
​```

the parameters ImageLocationDistanceVo of Java function getImagesWithinRadius validated as json as follow
​```json
{
		"ImageLocationDistanceVo": {
					"longitude": "[-180, 180]"
					"latitude": "[-90,90]",
					"radius": "[1,10]"
		}
}
​```

A good unit test suite should aim to:
- Test the function's behavior use two-wise 
- Test edge cases that the author may not have foreseen
- Test cover the function's all branch
- Take advantage of the features of `Junit5`, `Mockito` to make the tests easy to write and maintain
- Be easy to read and understand, with clean code and descriptive names
- Be deterministic, so that the tests always pass or fail in the same way

To help unit test the function above, list diverse scenarios that the function should be able to handle (and under each scenario, include a few examples as sub-bullets).all examples as json format. for example, 
{
    "xxxx_cas": [
    	"ImageLocationDistanceVo": {
    		{"latitude": 91.0, "longitude": 20.0, "radius": 5.0}
    	}
    ],
    "yyyy_cas": [
    	"ImageLocationDistanceVo": {
    		{"latitude": xxx, "longitude": yyy, "radius": n.0}
    	}
    ],
}
```

回答

```
{
    "valid_cases": [
        {
            "ImageLocationDistanceVo": {
                "latitude": 40.0,
                "longitude": -73.0,
                "radius": 5.0
            }
        },
        {
            "ImageLocationDistanceVo": {
                "latitude": -34.5,
                "longitude": 150.0,
                "radius": 10.0
            }
        }
    ],
    "invalid_radius_cases": [
        {
            "ImageLocationDistanceVo": {
                "latitude": 35.0,
                "longitude": 120.0,
                "radius": -2.0
            }
        },
        {
            "ImageLocationDistanceVo": {
                "latitude": 0.0,
                "longitude": 0.0,
                "radius": 0.0
            }
        }
    ],
    "invalid_longitude_latitude_cases": [
        {
            "ImageLocationDistanceVo": {
                "latitude": 91.0,
                "longitude": 20.0,
                "radius": 5.0
            }
        },
        {
            "ImageLocationDistanceVo": {
                "latitude": -100.0,
                "longitude": 200.0,
                "radius": 8.0
            }
        }
    ],
    "empty_image_ids_case": [
        {
            "ImageLocationDistanceVo": {
                "latitude": 45.0,
                "longitude": 10.0,
                "radius": 7.0
            }
        }
    ],
    "geo_service_returns_ids_case": [
        {
            "ImageLocationDistanceVo": {
                "latitude": -23.0,
                "longitude": -46.0,
                "radius": 3.0
            }
        }
    ]
}
```



请求每种情况的测试用例

```
You are a world-class Java developer with an eagle eye for unintended bugs and edge cases. You write careful, accurate unit tests. 
- Using Java and the `JUnit`, `Mockito` package, write all unit test following the cases below.
- all cases follow a naming standard of Given_x_When_y_Then_z, 
- each unit test method as given, when, then, assert.

​```json
    "invalid_radius_cases": [
        {
            "ImageLocationDistanceVo": {
                "latitude": 35.0,
                "longitude": 120.0,
                "radius": -2.0
            }
        },
        {
            "ImageLocationDistanceVo": {
                "latitude": 0.0,
                "longitude": 0.0,
                "radius": 0.0
            }
        }
    ]
​```
```

