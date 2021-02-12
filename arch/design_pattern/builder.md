builder



Builder 模式实例

```java
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import static feign.Util.UTF_8;
import static feign.Util.checkNotNull;
import static feign.Util.checkState;
import static feign.Util.decodeOrDefault;
import static feign.Util.valuesOrEmpty;

/**
 * An immutable response to an http invocation which only returns string content.
 */
public final class Response implements Closeable {

  private final int status;
  private final String reason;
  private final Map<String, Collection<String>> headers;
  private final Body body;
  private final Request request;

  private Response(Builder builder) {
    checkState(builder.request != null, "original request is required");
    this.status = builder.status;
    this.request = builder.request;
    this.reason = builder.reason; // nullable
    this.headers = (builder.headers != null)
        ? Collections.unmodifiableMap(caseInsensitiveCopyOf(builder.headers))
        : new LinkedHashMap<>();
    this.body = builder.body; // nullable
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    int status;
    String reason;
    Map<String, Collection<String>> headers;
    Body body;
    Request request;

    Builder() {}

    Builder(Response source) {
      this.status = source.status;
      this.reason = source.reason;
      this.headers = source.headers;
      this.body = source.body;
      this.request = source.request;
    }

    /** @see Response#status */
    public Builder status(int status) {
      this.status = status;
      return this;
    }

    /** @see Response#reason */
    public Builder reason(String reason) {
      this.reason = reason;
      return this;
    }

    /** @see Response#headers */
    public Builder headers(Map<String, Collection<String>> headers) {
      this.headers = headers;
      return this;
    }

    /** @see Response#body */
    public Builder body(Body body) {
      this.body = body;
      return this;
    }

    /** @see Response#body */
    public Builder body(InputStream inputStream, Integer length) {
      this.body = InputStreamBody.orNull(inputStream, length);
      return this;
    }

    /** @see Response#body */
    public Builder body(byte[] data) {
      this.body = ByteArrayBody.orNull(data);
      return this;
    }

    /** @see Response#body */
    public Builder body(String text, Charset charset) {
      this.body = ByteArrayBody.orNull(text, charset);
      return this;
    }

    /**
     * @see Response#request
     */
    public Builder request(Request request) {
      checkNotNull(request, "request is required");
      this.request = request;
      return this;
    }

    public Response build() {
      return new Response(this);
    }
  }

  /**
   * status code. ex {@code 200}
   *
   * See <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html" >rfc2616</a>
   */
  public int status() {
    return status;
  }

  /**
   * Nullable and not set when using http/2
   *
   * See https://github.com/http2/http2-spec/issues/202
   */
  public String reason() {
    return reason;
  }

  /**
   * Returns a case-insensitive mapping of header names to their values.
   */
  public Map<String, Collection<String>> headers() {
    return headers;
  }

  /**
   * if present, the response had a body
   */
  public Body body() {
    return body;
  }

  /**
   * the request that generated this response
   */
  public Request request() {
    return request;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("HTTP/1.1 ").append(status);
    if (reason != null)
      builder.append(' ').append(reason);
    builder.append('\n');
    for (String field : headers.keySet()) {
      for (String value : valuesOrEmpty(headers, field)) {
        builder.append(field).append(": ").append(value).append('\n');
      }
    }
    if (body != null)
      builder.append('\n').append(body);
    return builder.toString();
  }

  @Override
  public void close() {
    Util.ensureClosed(body);
  }
```



生产上的实例

feign.Response

