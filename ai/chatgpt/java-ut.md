

```
假设你是 Java 技术专家，解析ImageController类中 deleteCollectImage 函数依赖其他类的函数 
​```
public class ImageController {
		@Autowired
    private ImageService imageService;
    
    @Autowired
    private HttpServletRequest request;

    @PostMapping("/collect/add")
    public ResultVo<String> addCollectImage(@RequestBody ImageCollect imageCollect) {
        imageCollect.setUserId(getUnionId());
        imageService.addCollectImage(imageCollect);
        return success();
    }
    
    private String getUnionId() {
        dumpHeaders();
        String unionId = getHeader("X-WX-UNIONID");
        if (StringUtils.isBlank(unionId)) {
            unionId = getHeader("X-WX-OPENID");
            if (StringUtils.isBlank(unionId)) {
                throw new ServiceException(ErrorCodes.INVALID_UER);
            }
        }
        log.info("union id {}", unionId);
        return unionId;
    }
    private void dumpHeaders() {
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String headerKey = enumeration.nextElement();
            log.info("header {} {}", headerKey, request.getHeader(headerKey));
        }
    }
}

package com.imagedance.zpai.service;
public interface ImageService {
	  void addCollectImage(ImageCollect imageCollect);
}

    
package com.imagedance.zpai.model;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;


@TableName("images_collect")
@Accessors(chain = true)
@Data
public class ImageCollect {
    private String userId;

    private String imageId;

    private String createTime;

    private String deleteTime;
}

package com.imagedance.zpai.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
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
​```
输出采用如下格式
​```
{
	"className" : "com.imagedance.zpai.controller.ImageController",
	"methodName" : "deleteCollectImage",
	"args" : [ {
        "type" : "com.imagedance.zpai.model.vo.ImageCollectDeleteVo",
        "name" : "imageCollectDeleteVo",
        "value" : {
          {
            "type" : "java.lang.String",
        		"name" : "imageId",
        		"value" : "xxxx"
          },
          {
            "type" : "java.lang.String",
        		"name" : "userId",
        		"value" : "xxx"
          },
          {
            "type" : "T",
        		"name" : "data",
        		"value" : "xxxx"
          }
        }
   }],
   "return": {
            "type" : "com.imagedance.zpai.model.ResultVo",
        		"name" : "imageId",
        		"value" : {
        				{
            			"type" : "java.lang.String",
        					"name" : "code",
        					"value" : "String"
          			},
          			{
            			"type" : "java.lang.String",
        					"name" : "message",
        					"value" : "String"
          			},
          			{
              		"type" : "T",
        					"name" : "data",
        					"value" : "XX"        			
          			}
        		}
   },
   "dependencies": [{
      "className" : "com.imagedance.zpai.service.impl.ImageService",
    	"methodName" : "deleteCollectImage",
    	"args" : [ {
        "type" : "java.lang.String",
        "name" : "imageId",
        "value" : "String"
      }, {
        "type" : "java.lang.String",
        "name" : "userId",
        "value" : "admin"
      }]
      "return": {
   			{
            "type" : "Void",
        		"value" : "",
        		"name": ""
      }
   }]
}
​```
```



```
假设你是 Java 技术专家，基于如下代码，生成 Spring UT 代码
  {
      "mock" : [ {
        "className" : "com.imagedance.zpai.service.impl.ImageServiceImpl",
        "methodName" : "deleteCollectImage",
        "args" : [ {
          "type" : "java.lang.String",
          "name" : "imageId",
          "value" : "11"
        }, {
          "type" : "java.lang.String",
          "name" : "userId",
          "value" : "admin"
        } ],
        "throwExp" : null,
        "throw" : null,
        "invoking" : false,
        "mock" : true
      } ],
      "className" : "com.imagedance.zpai.controller.ImageController",
      "methodName" : "deleteCollectImage",
      "args" : [ {
        "type" : "com.imagedance.zpai.model.vo.ImageCollectDeleteVo",
        "name" : "imageCollectDeleteVo",
        "value" : {
          "imageId" : "11",
          "userId" : "admin"
        }
      } ]
    }
```







```
假设你是 Java 技术专家，基于如下代码，生成 Spring UT 代码
{
	"events": [{
		"defined_class": "com.imagedance.zpai.controller.ImageController",
		"id": 4495,
		"lineno": 118,
		"method_id": "deleteCollectImage",
		"parameters": [{
			"class": "com.imagedance.zpai.model.vo.ImageCollectDeleteVo",
			"name": "imageCollectDeleteVo",
			"object_id": 1557452412,
			"value": "ImageCollectDeleteVo(imageId=1, userId=admin)"
		}],
		"path": "src/main/java/com/imagedance/zpai/controller/ImageController.java",
	}, {
		"defined_class": "com.imagedance.zpai.service.impl.ImageServiceImpl",
		"id": 4522,
		"lineno": 81,
		"method_id": "deleteCollectImage",
		"parameters": [{
			"class": "java.lang.String",
			"name": "imageId",
			"value": "xxxx"
		}, {
			"class": "java.lang.String",
			"name": "userId",
			"value": "ttt"
		}],
		"path": "src/main/java/com/imagedance/zpai/service/impl/ImageServiceImpl.java",
	}, {
		"defined_class": "com.imagedance.zpai.service.impl.ImageMetaServiceImpl",
		"id": 4523,
		"lineno": 70,
		"method_id": "deleteCollectImage",
		"parameters": [{
			"class": "java.lang.String",
			"name": "imageId",
			"value": "xxxx"
		}, {
			"class": "java.lang.String",
			"name": "userId",
			"value": "ttt"
		}],
		"path": "src/main/java/com/imagedance/zpai/service/impl/ImageMetaServiceImpl.java"
	}, {
		"elapsed": 0.0232,
		"event": "return",
		"id": 4531,
		"parent_id": 4530,
		"thread_id": 31
	}, {
		"elapsed": 0.0564,
		"event": "return",
		"id": 4556,
		"parent_id": 4523,
		"thread_id": 31
	}, {
		"elapsed": 0.0566,
		"event": "return",
		"id": 4557,
		"parent_id": 4522,
		"thread_id": 31
	}, {
		"defined_class": "com.imagedance.zpai.service.impl.ImageServiceImpl",
		"event": "call",
		"id": 4558,
		"lineno": 81,
		"method_id": "deleteCollectImage",
		"parameters": [{
			"class": "java.lang.String",
			"kind": "req",
			"name": "imageId",
			"object_id": 1918007712,
			"value": "1"
		}, {
			"class": "java.lang.String",
			"kind": "req",
			"name": "userId",
			"object_id": 1134648053,
			"value": "admin"
		}],
		"path": "src/main/java/com/imagedance/zpai/service/impl/ImageServiceImpl.java",
	}, {
		"defined_class": "com.imagedance.zpai.service.impl.ImageMetaServiceImpl",
		"event": "call",
		"id": 4559,
		"lineno": 70,
		"method_id": "deleteCollectImage",
		"parameters": [{
			"class": "java.lang.String",
			"kind": "req",
			"name": "imageId",
			"value": "1"
		}, {
			"class": "java.lang.String",
			"kind": "req",
			"name": "userId",
			"value": "admin"
		}],
		"path": "src/main/java/com/imagedance/zpai/service/impl/ImageMetaServiceImpl.java",
	}, {
		"elapsed": 0.0,
		"event": "return",
		"id": 4567,
		"parent_id": 4566,
		"thread_id": 31
	}, {
		"elapsed": 4.0E-4,
		"event": "return",
		"id": 4592,
		"parent_id": 4559,
		"thread_id": 31
	}, {
		"elapsed": 4.0E-4,
		"event": "return",
		"id": 4593,
		"parent_id": 4558,
		"thread_id": 31
	}, {
		"defined_class": "com.imagedance.zpai.model.ResultVo",
		"event": "call",
		"id": 4594,
		"lineno": 20,
		"method_id": "success",
		"path": "src/main/java/com/imagedance/zpai/model/ResultVo.java",
		"static": true,
		"thread_id": 31
	}, {
		"defined_class": "com.imagedance.zpai.model.ResultVo",
		"event": "call",
		"id": 4595,
		"lineno": 7,
		"method_id": "setCode",
		"parameters": [{
			"class": "java.lang.String",
			"kind": "req",
			"name": "code",
			"object_id": 1763399888,
			"value": "0"
		}],
		"path": "src/main/java/com/imagedance/zpai/model/ResultVo.java",
		"receiver": {
			"class": "com.imagedance.zpai.model.ResultVo",
			"object_id": 440157265,
			"value": "ResultVo(code=0, message=null, data=null)"
		},
		"static": false,
		"thread_id": 31
	}, {
		"elapsed": 0.0,
		"event": "return",
		"id": 4596,
		"parent_id": 4595,
		"thread_id": 31
	}, {
		"defined_class": "com.imagedance.zpai.model.ResultVo",
		"event": "call",
		"id": 4597,
		"lineno": 7,
		"method_id": "setMessage",
		"parameters": [{
			"class": "java.lang.String",
			"kind": "req",
			"name": "message",
			"object_id": 1254820028,
			"value": "ok"
		}],
		"path": "src/main/java/com/imagedance/zpai/model/ResultVo.java",
		"static": false,
		"thread_id": 31
	}, {
		"elapsed": 1.0E-4,
		"event": "return",
		"id": 4598,
		"parent_id": 4597,
		"return_value": {
			"class": "com.imagedance.zpai.model.ResultVo",
			"value": "ResultVo(code=0, message=ok, data=null)"
		},
		"thread_id": 31
	}, {
		"defined_class": "com.imagedance.zpai.model.ResultVo",
		"event": "call",
		"id": 4599,
		"lineno": 7,
		"method_id": "setData",
		"parameters": [{
			"class": "java.lang.String",
			"kind": "req",
			"name": "data",
			"object_id": 1547607056,
			"value": ""
		}],
		"path": "src/main/java/com/imagedance/zpai/model/ResultVo.java",
		"receiver": {
			"class": "com.imagedance.zpai.model.ResultVo",
			"object_id": 440157265,
			"value": "ResultVo(code=0, message=ok, data=)"
		},
		"static": false,
		"thread_id": 31
	}, {
		"elapsed": 0.0,
		"event": "return",
		"id": 4600,
		"parent_id": 4599,
		"return_value": {
			"class": "com.imagedance.zpai.model.ResultVo",
			"value": "ResultVo(code=0, message=ok, data=)"
		},
		"thread_id": 31
	}, {
		"elapsed": 2.0E-4,
		"event": "return",
		"id": 4601,
		"parent_id": 4594,
		"return_value": {
			"class": "com.imagedance.zpai.model.ResultVo",
			"object_id": 440157265,
			"value": "ResultVo(code=0, message=ok, data=)"
		},
		"thread_id": 31
	}, {
		"elapsed": 0.0602,
		"event": "return",
		"id": 4602,
		"parent_id": 4495,
		"return_value": {
			"class": "com.imagedance.zpai.model.ResultVo",
			"object_id": 440157265,
			"value": "ResultVo(code=0, message=ok, data=)"
		},
		"thread_id": 31
	}],
	"classMap": [{
		"children": [{
			"children": [{
				"children": [{
					"children": [{
						"children": [{
							"labels": [],
							"location": "src/main/java/com/imagedance/zpai/model/ResultVo.java:20",
							"name": "success",
						}, {
							"labels": [],
							"location": "src/main/java/com/imagedance/zpai/model/ResultVo.java:7",
							"name": "setMessage"
						}, {
							"labels": [],
							"location": "src/main/java/com/imagedance/zpai/model/ResultVo.java:7",
							"name": "setCode",
						}, {
							"labels": [],
							"location": "src/main/java/com/imagedance/zpai/model/ResultVo.java:7",
							"name": "setData",
						}],
						"location": "src/main/java/com/imagedance/zpai/model/ResultVo.java",
						"name": "ResultVo",
					}],
					"name": "model",
					"type": "package"
				}, {
					"children": [{
						"children": [{
							"children": [{
								"labels": [],
								"location": "src/main/java/com/imagedance/zpai/service/impl/ImageMetaServiceImpl.java:70",
								"name": "deleteCollectImage",
							}],
							"location": "src/main/java/com/imagedance/zpai/service/impl/ImageMetaServiceImpl.java",
							"name": "ImageMetaServiceImpl",
						}, {
							"children": [{
								"labels": [],
								"location": "src/main/java/com/imagedance/zpai/service/impl/ImageServiceImpl.java:81",
								"name": "deleteCollectImage",
								"static": false,
								"type": "function"
							}],
							"location": "src/main/java/com/imagedance/zpai/service/impl/ImageServiceImpl.java",
							"name": "ImageServiceImpl",
							"static": false,
							"type": "class"
						}],
						"name": "impl",
						"type": "package"
					}],
					"name": "service",
					"type": "package"
				}, {
					"children": [{
						"children": [{
							"labels": [],
							"location": "src/main/java/com/imagedance/zpai/controller/ImageController.java:118",
							"name": "deleteCollectImage",
							"static": false,
							"type": "function"
						}],
						"location": "src/main/java/com/imagedance/zpai/controller/ImageController.java",
						"name": "ImageController",
						"static": false,
						"type": "class"
					}],
					"name": "controller",
					"type": "package"
				}],
				"name": "zpai",
				"type": "package"
			}],
			"name": "imagedance",
			"type": "package"
		}],
		"name": "com",
		"type": "package"
	}],
	"version": "1.2",
	}
}
```





```
假设你是 Java 技术专家，基于如下代码，生成 Spring UT 代码
{
	"events": [{
		"defined_class": "com.imagedance.zpai.controller.ImageController",
		"id": 4495,
		"lineno": 118,
		"method_id": "deleteCollectImage",
		"parameters": [{
			"class": "com.imagedance.zpai.model.vo.ImageCollectDeleteVo",
			"name": "imageCollectDeleteVo",
			"object_id": 1557452412,
			"value": "ImageCollectDeleteVo(imageId=1, userId=admin)"
		}],
		"path": "src/main/java/com/imagedance/zpai/controller/ImageController.java",
	}, {
		"defined_class": "com.imagedance.zpai.service.impl.ImageServiceImpl",
		"id": 4522,
		"lineno": 81,
		"method_id": "deleteCollectImage",
		"parameters": [{
			"class": "java.lang.String",
			"name": "imageId",
			"value": "xxxx"
		}, {
			"class": "java.lang.String",
			"name": "userId",
			"value": "ttt"
		}],
		"path": "src/main/java/com/imagedance/zpai/service/impl/ImageServiceImpl.java",
	}, {
		"defined_class": "com.imagedance.zpai.service.impl.ImageMetaServiceImpl",
		"id": 4523,
		"lineno": 70,
		"method_id": "deleteCollectImage",
		"parameters": [{
			"class": "java.lang.String",
			"name": "imageId",
			"value": "xxxx"
		}, {
			"class": "java.lang.String",
			"name": "userId",
			"value": "ttt"
		}],
		"path": "src/main/java/com/imagedance/zpai/service/impl/ImageMetaServiceImpl.java"
	}, {
		"defined_class": "com.imagedance.zpai.model.ResultVo",
		"event": "call",
		"id": 4597,
		"lineno": 7,
		"method_id": "setMessage",
		"parameters": [{
			"class": "java.lang.String",
			"kind": "req",
			"name": "message",
			"object_id": 1254820028,
			"value": "ok"
		}],
		"path": "src/main/java/com/imagedance/zpai/model/ResultVo.java",
		"static": false,
		"thread_id": 31
	},{
		"defined_class": "com.imagedance.zpai.model.ResultVo",
		"event": "call",
		"id": 4599,
		"lineno": 7,
		"method_id": "setData",
		"parameters": [{
			"class": "java.lang.String",
			"kind": "req",
			"name": "data",
			"object_id": 1547607056,
			"value": ""
		}],
		"path": "src/main/java/com/imagedance/zpai/model/ResultVo.java",
		"receiver": {
			"class": "com.imagedance.zpai.model.ResultVo",
			"object_id": 440157265,
			"value": "ResultVo(code=0, message=ok, data=)"
		},
		"static": false,
		"thread_id": 31
	}],
	"classMap": [{
		"children": [{
			"children": [{
				"children": [{
					"children": [{
						"children": [{
							"labels": [],
							"location": "src/main/java/com/imagedance/zpai/model/ResultVo.java:20",
							"name": "success",
						}, {
							"labels": [],
							"location": "src/main/java/com/imagedance/zpai/model/ResultVo.java:7",
							"name": "setMessage"
						}, {
							"labels": [],
							"location": "src/main/java/com/imagedance/zpai/model/ResultVo.java:7",
							"name": "setCode",
						}, {
							"labels": [],
							"location": "src/main/java/com/imagedance/zpai/model/ResultVo.java:7",
							"name": "setData",
						}],
						"location": "src/main/java/com/imagedance/zpai/model/ResultVo.java",
						"name": "ResultVo",
					}],
					"name": "model",
					"type": "package"
				}, {
					"children": [{
						"children": [{
							"children": [{
								"labels": [],
								"location": "src/main/java/com/imagedance/zpai/service/impl/ImageMetaServiceImpl.java:70",
								"name": "deleteCollectImage",
							}],
							"location": "src/main/java/com/imagedance/zpai/service/impl/ImageMetaServiceImpl.java",
							"name": "ImageMetaServiceImpl",
						}, {
							"children": [{
								"labels": [],
								"location": "src/main/java/com/imagedance/zpai/service/impl/ImageServiceImpl.java:81",
								"name": "deleteCollectImage",
								"static": false,
								"type": "function"
							}],
							"location": "src/main/java/com/imagedance/zpai/service/impl/ImageServiceImpl.java",
							"name": "ImageServiceImpl",
							"static": false,
							"type": "class"
						}],
						"name": "impl",
						"type": "package"
					}],
					"name": "service",
					"type": "package"
				}, {
					"children": [{
						"children": [{
							"labels": [],
							"location": "src/main/java/com/imagedance/zpai/controller/ImageController.java:118",
							"name": "deleteCollectImage",
							"static": false,
							"type": "function"
						}],
						"location": "src/main/java/com/imagedance/zpai/controller/ImageController.java",
						"name": "ImageController",
						"static": false,
						"type": "class"
					}],
					"name": "controller",
					"type": "package"
				}],
				"name": "zpai",
				"type": "package"
			}],
			"name": "imagedance",
			"type": "package"
		}],
		"name": "com",
		"type": "package"
	}],
	"version": "1.2",
	}
}
```





```
假设你是 Java 技术专家，基于如下代码，生成 Spring UT 代码
[ {
      "children" : [ {
        "children" : [ {
          "children" : [ {
            "children" : [ {
              "children" : null,
              "type" : "method",
              "className" : "com.imagedance.zpai.service.impl.ImageMetaServiceImpl",
              "methodName" : "deleteCollectImage",
              "lineNumber" : -1,
              "args" : [ {
                "type" : "java.lang.String",
                "name" : "imageId",
                "value" : "String"
              }, {
                "type" : "java.lang.String",
                "name" : "userId",
                "value" : "123"
              } ],
              "returnInfo" : null,
              "throwExp" : null,
              "mock" : true,
              "throw" : null,
              "invoking" : false
            } ],
            "type" : "method",
            "className" : "com.imagedance.zpai.service.impl.ImageServiceImpl",
            "methodName" : "deleteCollectImage",
            "args" : [ {
              "type" : "java.lang.String",
              "name" : "imageId",
              "value" : "String"
            }, {
              "type" : "java.lang.String",
              "name" : "userId",
              "value" : "123"
            } ],
            "returnInfo" : null,
            "throwExp" : null,
            "mock" : true,
            "throw" : null,
            "invoking" : false
          } ],
          "type" : "method",
          "className" : "com.imagedance.zpai.service.impl.ImageMetaServiceImpl",
          "methodName" : "deleteCollectImage",
          "args" : [ {
            "type" : "java.lang.String",
            "name" : "imageId",
            "value" : "xxxx"
          }, {
            "type" : "java.lang.String",
            "name" : "userId",
            "value" : "ttt"
          } ],
          "returnInfo" : null,
          "throwExp" : null,
          "mock" : true,
          "throw" : null,
          "invoking" : false
        } ],
        "type" : "method",
        "className" : "com.imagedance.zpai.service.impl.ImageServiceImpl",
        "methodName" : "deleteCollectImage",
        "args" : [ {
          "type" : "java.lang.String",
          "name" : "imageId",
          "value" : "xxxx"
        }, {
          "type" : "java.lang.String",
          "name" : "userId",
          "value" : "ttt"
        } ],
        "returnInfo" : null,
        "throwExp" : null,
        "mock" : true,
        "throw" : null,
        "invoking" : false
      } ],
      "type" : "method",
      "className" : "com.imagedance.zpai.controller.ImageController",
      "methodName" : "deleteCollectImage",
      "args" : [ {
        "type" : "com.imagedance.zpai.model.vo.ImageCollectDeleteVo",
        "name" : "imageCollectDeleteVo",
        "value" : {
          "imageId" : "String",
          "userId" : "123"
        }
      } ],
      "returnInfo" : null,
      "throwExp" : null,
      "mock" : false,
      "throw" : null,
      "invoking" : false
    } ]
```





```
假设你是 Java 技术专家，基于如下代码，生成 Spring UT 代码
[ {
      "children" : [ {
        "children" : [ {
          "children" : null,
          "type" : "method",
          "className" : "com.imagedance.zpai.service.impl.ImageServiceImpl",
          "methodName" : "deleteCollectImage",
          "lineNumber" : -1,
          "args" : [ {
            "type" : "java.lang.String",
            "name" : "imageId",
            "value" : "String"
          }, {
            "type" : "java.lang.String",
            "name" : "userId",
            "value" : "123"
          } ],
          "returnInfo" : null,
          "throwExp" : null,
          "mock" : true,
          "throw" : null,
          "invoking" : false
        } ],
        "type" : "method",
        "className" : "com.imagedance.zpai.service.impl.ImageServiceImpl",
        "methodName" : "deleteCollectImage",
        "lineNumber" : -1,
        "args" : [ {
          "type" : "java.lang.String",
          "name" : "imageId",
          "value" : "xxxx"
        }, {
          "type" : "java.lang.String",
          "name" : "userId",
          "value" : "ttt"
        } ],
        "returnInfo" : null,
        "throwExp" : null,
        "mock" : true,
        "throw" : null,
        "invoking" : false
      } ],
      "type" : "method",
      "className" : "com.imagedance.zpai.controller.ImageController",
      "methodName" : "deleteCollectImage",
      "lineNumber" : -1,
      "args" : [ {
        "type" : "com.imagedance.zpai.model.vo.ImageCollectDeleteVo",
        "name" : "imageCollectDeleteVo",
        "value" : {
          "imageId" : "String",
          "userId" : "123"
        }
      } ],
      "returnInfo" : null,
      "throwExp" : null,
      "mock" : false,
      "throw" : null,
      "invoking" : false
    } ]
```

