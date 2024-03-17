```
规范
1. Names should not start or end with an underline or a dollar sign. 
2. Using Chinese, Pinyin, or Pinyin-English mixed spelling in naming is strictly prohibited. Accurate English spelling and grammar will make the code readable, understandable, and maintainable.
3. Class names should be nouns in UpperCamelCase except domain models: DO, BO, DTO, VO, etc.
4. Method names, parameter names, member variable names, and local variable names should be written in lowerCamelCase.
5. Constant variable names should be written in upper characters separated by underscores. These names should be semantically complete and clear.
```



```
class Test {
	String _name;
	
	String __age;
	
	String sex_;
	
	String nihao;
	
	Strinig alibaba
	
	String taobao;
  
  String youku;
  
  Strinng Hangzhou;

	String zhengchang;
}
```





```
clsss SenderDO {
	 void test(String userName) {}
}

class SenderDo {
	void test(String userName_) {}
}

class SenderBO {
	void test(String _userName) {}
}

class SenderBo {
	void test(String username) {}
}

class SenderDTO {
	private final String PROTOCOL_TYPE = "RabbitMq"
}

class SenderDto {
	private final String remoteSserver = "RabbitMq"
}
```

