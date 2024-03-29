



考虑的因素

1、Mock 三方系统

2、入参构造：

3、bytecode 插入

4、选择包



Restful 工具对比

| 工具名称     | 算法                   | 白盒                |
| ------------ | ---------------------- | ------------------- |
| EvoMaster    | MIO algorithm          | White-Box,Black-Box |
| RESTler      | search-based algorithm | Black-Box           |
| RestTestGen  |                        | Black-Box           |
| RESTest      | model-based            | Black-Box           |
| Schemathesis | propertybased          | Black-Box           |
| Dredd        |                        | Black-Box           |
| Tcases       |                        | black-box           |
| bBOXRT       |                        | black-box           |
| APIFuzzer    |                        | black-box           |
|              |                        |                     |



## UT 方法调研

1、代码路径分析：Symbolic Execution

2、参数构造：解析 wsf 规则，基于 pair-wise 方法构造参数

2、方法执行记录：启动程序，自动喂参，记录执行参数

4、根据方法参数自动 mock

3、用例生成

4、用例验证


## 例子


首先，来个例子

```json
基于以下代码生成 Java UT 代码"""{
  "classname" : "com.imagedance.zpai.controller.ImageController",
  "storedCandidateMap" : {
    "com.imagedance.zpai.controller.ImageController#addCollectImage#(Lcom/imagedance/zpai/model/ImageCollect;)Lcom/imagedance/zpai/model/ResultVo;" : [ {
      "lineNumbers" : [ 74, 75 ],
      "testAssertions" : {
        "subAssertions" : [ {
          "subAssertions" : [ ],
          "expression" : "SELF",
          "expectedValue" : "{\"code\":\"0\",\"message\":\"ok\",\"data\":\"\"}",
          "id" : "6a21ca85-31e1-45db-82bd-e98d7a98649e",
          "assertionType" : "EQUAL",
          "key" : "/"
        } ],
        "expression" : "SELF",
        "expectedValue" : null,
        "id" : "220de3fe-bce2-45bd-9114-e65b87810729",
        "assertionType" : "ALLOF",
        "key" : null
      },
      "candidateId" : "bacaee95-94cc-4c8c-90b2-408a5e119ab8",
      "name" : "test addCollectImage returns expected value when",
      "description" : "assert that the response value matches expected value",
      "methodArguments" : [ "{\"userId\":\"1\",\"imageId\":\"1\",\"createTime\":\"1\",\"deleteTime\":\"1\"}" ],
      "returnValue" : "{\"code\":\"0\",\"message\":\"ok\",\"data\":\"\"}",
      "returnValueClassname" : "com.imagedance.zpai.model.ResultVo",
      "metadata" : {
        "recordedBy" : "Administrator",
        "hostMachineName" : "Administrator",
        "timestamp" : 53179334160000,
        "candidateStatus" : "PASSING"
      },
      "sessionIdentifier" : -749579435,
      "probSerializedValue" : "eyJjb2RlIjoiMCIsIm1lc3NhZ2UiOiJvayIsImRhdGEiOiIifQ==",
      "mockIds" : [ ],
      "exception" : false,
      "method" : {
        "name" : "addCollectImage",
        "signature" : "(Lcom/imagedance/zpai/model/ImageCollect;)Lcom/imagedance/zpai/model/ResultVo;",
        "className" : "com.imagedance.zpai.controller.ImageController",
        "methodHash" : 0
      }
    } ],
    "com.imagedance.zpai.controller.ImageController#queryCollectImages#(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)Lcom/imagedance/zpai/model/ResultVo;" : [ {
      "lineNumbers" : [ 88, 89 ],
      "testAssertions" : {
        "subAssertions" : [ {
          "subAssertions" : [ ],
          "expression" : "SELF",
          "expectedValue" : "{\"code\":\"0\",\"message\":\"ok\",\"data\":[]}",
          "id" : "5c6ac592-efe0-4ada-adc2-c4fd7e1e05bc",
          "assertionType" : "EQUAL",
          "key" : "/"
        } ],
        "expression" : "SELF",
        "expectedValue" : null,
        "id" : "53d15c17-1d68-4a85-a9b1-5ad1f5330335",
        "assertionType" : "ALLOF",
        "key" : null
      },
      "candidateId" : "85d61afe-50ef-4936-86c0-65293753a3bc",
      "name" : "test queryCollectImages returns expected value when",
      "description" : "assert that the response value matches expected value",
      "methodArguments" : [ "\"1\"", "\"1\"", "0", "0" ],
      "returnValue" : "{\"code\":\"0\",\"message\":\"ok\",\"data\":[]}",
      "returnValueClassname" : "com.imagedance.zpai.model.ResultVo",
      "metadata" : {
        "recordedBy" : "Administrator",
        "hostMachineName" : "Administrator",
        "timestamp" : 53350742652200,
        "candidateStatus" : "PASSING"
      },
      "sessionIdentifier" : -1203553817,
      "probSerializedValue" : "eyJjb2RlIjoiMCIsIm1lc3NhZ2UiOiJvayIsImRhdGEiOltdfQ==",
      "mockIds" : [ ],
      "exception" : false,
      "method" : {
        "name" : "queryCollectImages",
        "signature" : "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)Lcom/imagedance/zpai/model/ResultVo;",
        "className" : "com.imagedance.zpai.controller.ImageController",
        "methodHash" : 0
      }
    } ]
  },
  "declaredMockMap" : { }
}"""
```

生成的 UT 代码
```java
import com.imagedance.zpai.controller.ImageController;
import com.imagedance.zpai.model.ImageCollect;
import com.imagedance.zpai.model.ResultVo;
import org.junit.Test;
import static org.junit.Assert.*;

public class ImageControllerTest {

    @Test
    public void testAddCollectImage() {
        ImageController imageController = new ImageController();
        ImageCollect imageCollect = new ImageCollect("1", "1", "1", "1");
        ResultVo resultVo = imageController.addCollectImage(imageCollect);
        assertEquals("0", resultVo.getCode());
        assertEquals("ok", resultVo.getMessage());
        assertNull(resultVo.getData());
    }

    @Test
    public void testQueryCollectImages() {
        ImageController imageController = new ImageController();
        ResultVo resultVo = imageController.queryCollectImages("1", "1", 0, 0);
        assertEquals("0", resultVo.getCode());
        assertEquals("ok", resultVo.getMessage());
        assertTrue(resultVo.getData().isEmpty());
    }
}

```


例子2：包含 Mock 数据

```json
基于以下代码生成 Java UT 代码"""{
  "classname" : "com.imagedance.zpai.service.ImageMetaService",
  "storedCandidateMap" : { },
  "declaredMockMap" : {
    "com.imagedance.zpai.service.ImageMetaService#queryImages#(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/util/List;" : [ {
      "id" : "c6c2d078-f4b6-4c09-9a70-808b0042a185",
      "name" : "mock response imageMetaService.queryImages",
      "fieldTypeName" : "com.imagedance.zpai.service.ImageMetaService",
      "sourceClassName" : "com.imagedance.zpai.service.impl.ImageServiceImpl",
      "fieldName" : "imageMetaService",
      "methodName" : "queryImages",
      "whenParameter" : [ {
        "name" : "imageId",
        "type" : "ANY_OF_TYPE",
        "value" : "java.lang.String"
      }, {
        "name" : "userId",
        "type" : "ANY_STRING",
        "value" : "null"
      }, {
        "name" : "currentPage",
        "type" : "ANY_OF_TYPE",
        "value" : "int"
      }, {
        "name" : "pageSize",
        "type" : "ANY_OF_TYPE",
        "value" : "int"
      } ],
      "thenParameter" : [ {
        "returnParameter" : {
          "declaredMocks" : [ ],
          "value" : "[]",
          "returnValueType" : "REAL",
          "className" : "java.util.List<com.imagedance.zpai.model.ImageInfo>"
        },
        "methodExitType" : "NORMAL"
      } ]
    } ]
  }
}"""
```

生成的 UT 的代码

```java
import com.imagedance.zpai.model.ImageInfo;
import com.imagedance.zpai.service.ImageMetaService;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ImageMetaServiceTest {

    @Test
    public void testQueryImages() {
        // 创建 ImageMetaService 的 mock 对象
        ImageMetaService imageMetaService = Mockito.mock(ImageMetaService.class);

        // 设置 mock 对象的返回值
        when(imageMetaService.queryImages("imageId", "userId", 1, 10)).thenReturn(new ArrayList<>());

        // 调用 queryImages 方法并验证结果
        List<ImageInfo> result = imageMetaService.queryImages("imageId", "userId", 1, 10);
        assertEquals(0, result.size());
    }
}

```


参考

An Empirical Evaluation of Using Large Language Models for Automated Unit Test Generation 

 https://arxiv.org/pdf/2302.06527.pdf

Unit Test Generation using Generative AI : A Comparative Performance Analysis of Autogeneration Tools https://arxiv.org/pdf/2312.10622.pdf

Reinforcement Learning from Automatic Feedback for High-Quality Unit Test Generation https://www.franktip.org/pubs/testpilot2024.pdf

https://arxiv.org/pdf/2204.08348.pdf

https://github.com/codingsoo/REST_Go

https://arxiv.org/pdf/2204.08348v3.pdf

https://dl.acm.org/doi/pdf/10.1145/3551349.3559498

https://arxiv.org/pdf/2204.12148.pdf

https://dl.acm.org/doi/10.1145/3617175#d1e1509

https://arxiv.org/pdf/2312.00894.pdf

https://github.com/unloggedio/unlogged-sdk

https://read.unlogged.io/directinvoke/

https://github.com/Codium-ai/pr-agent

https://symflower.com/en/company/blog/2023/software-testing-trends-2024/

https://symflower.com/en/company/blog/  博客内容不错

https://symflower.com/en/company/blog/2023/symflower-github-copilot/

https://symflower.com/en/company/blog/2022/methods-for-automated-test-value-generation/ 

https://symflower.com/en/company/blog/2021/symbolic-execution/

https://link.springer.com/content/pdf/10.1007/s11219-023-09620-w.pdf