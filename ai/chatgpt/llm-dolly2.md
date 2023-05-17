[Dolly 2.0](https://www.databricks.com/blog/2023/04/12/dolly-first-open-commercially-viable-instruction-tuned-llm) is a 12B parameter language model trained and instruction fine-tuned by [Databricks](https://www.databricks.com/). By instruction fine-tuning the large language model (LLM), we obtain an LLM better suited for human interactivity. Crucially, Databricks released all code, model weights, and their fine-tuning dataset with an open-source license that permits commercial use. This makes Dolly 2.0 the world's first, truly open-source instruction-tuned LLM.



### 加载模型

配置

```python
from utils.setup import dolly_config_setup

config_name = "dolly_pod4" if number_of_ipus == 4 else "dolly_pod16"
config, *_ = dolly_config_setup("config/inference.yml", "release", config_name)
config
```



```
import api

# changing these parameters will trigger a recompile.
sequence_length = 512  # max 2048
micro_batch_size = 4

dolly_pipeline = api.DollyPipeline(
    config, sequence_length=sequence_length, micro_batch_size=micro_batch_size
)
```



### 推理



示例 1

```python
answer = dolly_pipeline(
    "give me an example code of spring ioc in java",
    temperature=0.6,
    k=5,
    output_length=None,
    print_live=True,
    print_final=True,
)
```



生成结果

```java
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public User findUserByUsername(String username) {
        return userDao.findByName(username);
    }
}

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByName(String name) {
        return userRepository.findByName(name);
    }
}

@Service
public class UserServiceImpl2 implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public User findUserByUsername(String username) {
        return userDao.findByName(username);
    }
}
```



示例 2

```python
answer = dolly_pipeline(
    "give me an example code of spring controller for image upload and download in java",
    temperature=0.4,
    k=5,
    output_length=None,
    print_live=True,
    print_final=True,
)
```



结果

```java
@RequestMapping(value="/upload", method = RequestMethod.POST)
public String upload(@RequestParam("file") MultipartFile file,
        HttpServletResponse response) throws IOException {

    if (file.isEmpty()) {
        throw new RuntimeException("File is empty");
    }

    String fileName = file.getOriginalFilename();
    file.transferTo(new File(System.getProperty("user.home") +
            System.getProperty("file.separator") + "Desktop" +
            System.getProperty("file.separator") + "images" +
            System.getProperty("file.separator") + fileName));

    response.setHeader("Content-Disposition", "inline; filename=" + fileName);
    response.setContentLength(file.getSize());
    response.getOutputStream().write(file.getBytes());
    return "success";
}

@RequestMapping(value="/download", method = RequestMethod.GET)
public void download(@RequestParam("fileName") String fileName) {
    System.out.println(fileName);
    new File(System.getProperty("user.home") +
            System.getProperty("file.separator") + "Desktop" +
            System.getProperty("file.separator") + "images" +
            System.getProperty("file.separator") + fileName).getAbsolutePath());
}
```

