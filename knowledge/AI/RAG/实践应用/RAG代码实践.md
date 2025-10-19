# RAG代码实践指南

## 概述
- **分类**: AI/机器学习/RAG/实践应用
- **创建时间**: 2025-10-19
- **更新时间**: 2025-10-19
- **关键词**: RAG, Python, 代码示例, 实践项目

## 1. 基础RAG实现

### 1.1 使用LangChain实现简单RAG

```python
from langchain.document_loaders import TextLoader
from langchain.text_splitter import CharacterTextSplitter
from langchain.embeddings import OpenAIEmbeddings
from langchain.vectorstores import Chroma
from langchain.chat_models import ChatOpenAI
from langchain.chains import RetrievalQA

class SimpleRAG:
    def __init__(self, openai_api_key):
        self.embeddings = OpenAIEmbeddings(openai_api_key=openai_api_key)
        self.llm = ChatOpenAI(openai_api_key=openai_api_key)
        self.vector_store = None
        self.qa_chain = None

    def load_documents(self, file_path):
        """加载文档"""
        loader = TextLoader(file_path)
        documents = loader.load()
        return documents

    def split_documents(self, documents, chunk_size=1000, chunk_overlap=200):
        """文档分块"""
        text_splitter = CharacterTextSplitter(
            chunk_size=chunk_size,
            chunk_overlap=chunk_overlap
        )
        chunks = text_splitter.split_documents(documents)
        return chunks

    def create_vector_store(self, chunks):
        """创建向量数据库"""
        self.vector_store = Chroma.from_documents(
            documents=chunks,
            embedding=self.embeddings
        )

    def create_qa_chain(self):
        """创建问答链"""
        self.qa_chain = RetrievalQA.from_chain_type(
            llm=self.llm,
            chain_type="stuff",
            retriever=self.vector_store.as_retriever()
        )

    def query(self, question):
        """查询回答"""
        if not self.qa_chain:
            raise ValueError("Please create QA chain first")
        return self.qa_chain.run(question)

# 使用示例
rag = SimpleRAG("your-openai-api-key")
documents = rag.load_documents("document.txt")
chunks = rag.split_documents(documents)
rag.create_vector_store(chunks)
rag.create_qa_chain()
result = rag.query("什么是RAG？")
print(result)
```

### 1.2 使用LlamaIndex实现

```python
from llama_index import SimpleDirectoryReader, VectorStoreIndex, ServiceContext
from llama_index.llms import OpenAI
from llama_index.embeddings import OpenAIEmbedding

class LlamaIndexRAG:
    def __init__(self, openai_api_key):
        self.llm = OpenAI(api_key=openai_api_key)
        self.embed_model = OpenAIEmbedding(api_key=openai_api_key)
        self.service_context = ServiceContext.from_defaults(
            llm=self.llm,
            embed_model=self.embed_model
        )
        self.index = None

    def load_documents(self, directory_path):
        """加载目录中的文档"""
        documents = SimpleDirectoryReader(directory_path).load_data()
        self.index = VectorStoreIndex.from_documents(
            documents,
            service_context=self.service_context
        )

    def create_query_engine(self):
        """创建查询引擎"""
        if not self.index:
            raise ValueError("Please load documents first")
        return self.index.as_query_engine()

    def query(self, question):
        """查询"""
        query_engine = self.create_query_engine()
        response = query_engine.query(question)
        return response

# 使用示例
rag = LlamaIndexRAG("your-openai-api-key")
rag.load_documents("./documents/")
result = rag.query("RAG的优势是什么？")
print(result)
```

## 2. 高级RAG功能

### 2.1 混合检索

```python
from langchain.retrievers import BM25Retriever, EnsembleRetriever
from langchain.vectorstores import FAISS

class HybridRAG:
    def __init__(self, openai_api_key):
        self.embeddings = OpenAIEmbeddings(openai_api_key=openai_api_key)
        self.llm = ChatOpenAI(openai_api_key=openai_api_key)
        self.vector_store = None
        self.bm25_retriever = None
        self.ensemble_retriever = None

    def setup_retrievers(self, documents):
        """设置混合检索器"""
        # 向量检索
        self.vector_store = FAISS.from_documents(documents, self.embeddings)
        vector_retriever = self.vector_store.as_retriever(search_kwargs={"k": 5})

        # BM25检索
        self.bm25_retriever = BM25Retriever.from_documents(documents)
        self.bm25_retriever.k = 5

        # 集成检索
        self.ensemble_retriever = EnsembleRetriever(
            retrievers=[vector_retriever, self.bm25_retriever],
            weights=[0.7, 0.3]
        )

    def query_with_sources(self, question):
        """带来源的查询"""
        if not self.ensemble_retriever:
            raise ValueError("Please setup retrievers first")

        # 检索相关文档
        docs = self.ensemble_retriever.get_relevant_documents(question)

        # 构建上下文
        context = "\n".join([doc.page_content for doc in docs])

        # 构建prompt
        prompt = f"""
        基于以下上下文信息回答问题，并引用信息来源：

        上下文：
        {context}

        问题：{question}

        请回答并注明信息来源。
        """

        # 生成回答
        response = self.llm.invoke(prompt)
        return response.content, docs
```

### 2.2 重排序（Reranking）

```python
from sentence_transformers import CrossEncoder
import numpy as np

class RerankingRAG:
    def __init__(self, openai_api_key, rerank_model_name="BAAI/bge-reranker-base"):
        self.embeddings = OpenAIEmbeddings(openai_api_key=openai_api_key)
        self.llm = ChatOpenAI(openai_api_key=openai_api_key)
        self.reranker = CrossEncoder(rerank_model_name)
        self.vector_store = None

    def rerank_documents(self, query, documents, top_k=5):
        """重排序文档"""
        # 准备输入对
        pairs = [[query, doc.page_content] for doc in documents]

        # 计算相关性分数
        scores = self.reranker.predict(pairs)

        # 排序
        scored_docs = list(zip(documents, scores))
        scored_docs.sort(key=lambda x: x[1], reverse=True)

        # 返回top_k文档
        return [doc for doc, score in scored_docs[:top_k]]

    def query_with_rerank(self, question):
        """带重排序的查询"""
        # 初步检索
        initial_docs = self.vector_store.similarity_search(question, k=20)

        # 重排序
        reranked_docs = self.rerank_documents(question, initial_docs)

        # 构建上下文并生成回答
        context = "\n".join([doc.page_content for doc in reranked_docs])

        prompt = f"""
        基于以下经过重排序的高质量上下文回答问题：

        {context}

        问题：{question}
        """

        response = self.llm.invoke(prompt)
        return response.content
```

## 3. 性能优化

### 3.1 缓存机制

```python
import pickle
import hashlib
from functools import wraps

class CachedRAG:
    def __init__(self, cache_dir="./cache"):
        self.cache_dir = cache_dir
        os.makedirs(cache_dir, exist_ok=True)

    def cache_result(self, func):
        """结果缓存装饰器"""
        @wraps(func)
        def wrapper(*args, **kwargs):
            # 生成缓存键
            key = hashlib.md5(
                str(args).encode() + str(kwargs).encode()
            ).hexdigest()
            cache_file = os.path.join(self.cache_dir, f"{key}.pkl")

            # 尝试从缓存读取
            if os.path.exists(cache_file):
                with open(cache_file, 'rb') as f:
                    return pickle.load(f)

            # 计算结果并缓存
            result = func(*args, **kwargs)
            with open(cache_file, 'wb') as f:
                pickle.dump(result, f)

            return result
        return wrapper

    @cache_result
    def cached_query(self, question):
        """带缓存的查询"""
        # 实际的查询逻辑
        pass
```

### 3.2 批量处理

```python
from concurrent.futures import ThreadPoolExecutor
import asyncio

class BatchRAG:
    def __init__(self, max_workers=5):
        self.max_workers = max_workers

    def batch_query(self, questions):
        """批量查询"""
        with ThreadPoolExecutor(max_workers=self.max_workers) as executor:
            futures = [executor.submit(self.query, q) for q in questions]
            results = [future.result() for future in futures]
        return results

    async def async_query(self, question):
        """异步查询"""
        # 异步查询实现
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(None, self.query, question)

    async def batch_async_query(self, questions):
        """批量异步查询"""
        tasks = [self.async_query(q) for q in questions]
        return await asyncio.gather(*tasks)
```

## 4. 完整项目示例

### 4.1 项目结构
```
rag_project/
├── config/
│   ├── config.yaml
│   └── logging.conf
├── src/
│   ├── __init__.py
│   ├── document_processor.py
│   ├── retriever.py
│   ├── generator.py
│   └── api.py
├── data/
│   ├── documents/
│   └── cache/
├── tests/
│   └── test_rag.py
├── requirements.txt
└── main.py
```

### 4.2 配置文件

```yaml
# config/config.yaml
model:
  embedding: "text-embedding-3-small"
  llm: "gpt-3.5-turbo"
  temperature: 0.1

retrieval:
  chunk_size: 1000
  chunk_overlap: 200
  top_k: 5
  rerank: true

cache:
  enabled: true
  ttl: 3600

logging:
  level: "INFO"
  file: "logs/rag.log"
```

### 4.3 API接口

```python
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import uvicorn

app = FastAPI(title="RAG API")

class QueryRequest(BaseModel):
    question: str
    top_k: int = 5

class QueryResponse(BaseModel):
    answer: str
    sources: list

# 初始化RAG系统
rag_system = RAGSystem()

@app.post("/query", response_model=QueryResponse)
async def query_endpoint(request: QueryRequest):
    try:
        answer, sources = rag_system.query(request.question, request.top_k)
        return QueryResponse(answer=answer, sources=sources)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/upload")
async def upload_document(file: UploadFile = File(...)):
    try:
        # 保存文件
        file_path = f"data/documents/{file.filename}"
        with open(file_path, "wb") as f:
            content = await file.read()
            f.write(content)

        # 处理文档
        rag_system.add_document(file_path)
        return {"message": "Document uploaded successfully"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
```

## 相关链接
- [[LangChain官方文档]]
- [[LlamaIndex教程]]
- [[FastAPI部署指南]]
- [[Docker容器化部署]]

## 实践建议
- [ ] 从简单的文本问答开始
- [ ] 逐步增加复杂功能
- [ ] 重视错误处理和日志
- [ ] 建立完善的测试体系
- [ ] 关注性能监控和优化