# Mock-server

<!-- TOC -->
* [About](#about)
* [TODO](#todo)
* [Context](#context)
  * [request](#request)
  * [error](#error)
* [Template syntax](#template-syntax)
  * [Bind parameter](#bind-parameter)
  * [Function](#function)
    * [date](#date)
    * [now](#now)
    * [num](#num)
    * [replace](#replace)
    * [uuid](#uuid)
    * [spel](#spel)
<!-- TOC -->

## About
- Mock response according to condition using SpEL
- Respond by binding request data to a mock response or converting request data using a function

## TODO
- Support multipart-form data
- Cache support

## Context
- It can be using response condition (SpEL context)
- It can be using response header and body with template syntax

### vars

| Name      | Type   | Description                                                    |
|-----------|--------|----------------------------------------------------------------|
| uuid      | String | Generated uuid when every execution                            |
| timestamp | long   | Start epoch time(System.currentTimeMillis()) of execution      |
| datetime  | String | Start time of execution what ISO formatted LocalDateTime.now() |



### request

| Name          | Type                                 | Description                                                             |
|---------------|--------------------------------------|-------------------------------------------------------------------------|
| url           | String                               | Requested full URL(with host)                                           |
| uri           | String                               | Requested URI (without host)                                            |
| method        | String                               | Requested method (ex. GET)                                              |
| headers       | org.springframework.http.HttpHeaders | Requested HTTP headers                                                  |
| pathVariables | Map<String, String>                  | Parsed path variables from URL                                          |
| params        | MultiValueMap<String, String>        | Requested query parameters                                              |
| body          | Object                               | Requested body If request JSON body, It can be using Map(or JSONObject) |


### error
> This can only be used with 'error' response.

| Name             | Type                | Description                                      |
|------------------|---------------------|--------------------------------------------------|
| name             | String              | Full name of Exception class (with package name) |
| stackTrace       | StackTraceElement[] | Stack traces                                     |
| message          | String              | Exception message                                |
| localizedMessage | String              | Localized Exception message                      |


## Template syntax
> Only support in 'header' and 'body'
- Escape character : Back slash(\)
- Reserved patterns

| Pattern | Description                    |
|---------|--------------------------------|
| @{      | Start of bind parameter syntax |
| }       | End of parameter or function   |
| #{      | Start of function syntax       |
| '       | Wrap argument                  |
| ,       | Arguments delimiter            |

### Bind parameter
- Basically using [JsonPath](https://github.com/json-path/JsonPath)
- A path based on Context 
```spel
@{path}
```

### Function
- Basic syntax
```spel
#{[functionName]('argument', ...)}
```
- Recursively call with another functions
> **Warning** Not recommend because not tested
```spel
#{[functionName]('#{anotherFunction(\\'arguments\\', ...)}', ...)}
```

#### date
```spel
#{date(param, 'out-format')}  // support only 'ISO_DATE_TIME' formatted
or
#{date(param, 'out-format', 'input-format')}
```
- Convert date formatting.
- [Java date/time format](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)
- If param already formatted [ISO_DATE_TIME](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_DATE_TIME)), not required 'input-format'
- Example
    ```spel
    #{date($.request.time, 'yyy-MM-dd')}    # 'ISO_DATE_TIME' formated
    #{date($.request.time, 'yyy-MM-dd', 'yyyy-MM-dd HH:mm:ss')} # not 'ISO_DATE_TIME' formated
    ```

#### now
```spel
#{now()}
or
#{now('date/time format')}
```
- Output current timestamp.
- If not set argument, return current timestamp(epoch)
- If set argument, return formatted current date/time
  - [Java date/time format](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)
- Example
    ```spel
        #{now()}
        #{now('yyyy-MM-dd HH:mm:ss.SSS')}
    ```

#### num
```spel
#{num(param, int)}
or
#{num(param, '#,###')}
```
- Number formatting function.
- If set argument defined type, return cast number
  - type : byte, short, int, double, float, long, bigint, decimal
- If set argument decimal format, return formatted number
  - [DecimalFormat](https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html)

#### replace
```spel
#{replace(param, 'pattern', 'replacement')}
```
- Work as [String.replaceAll](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#replaceAll-java.lang.String-java.lang.String-)

#### uuid
```spel
#{uuid()}
```
- Will be return generate UUID

#### spel
> **Warning** Not tested
```spel
#{spel('SpEL')}
```
- If set SpEL at argument, return expression result.
- [About SpEL](https://docs.spring.io/spring-framework/docs/3.0.x/reference/expressions.html)
- [SpEL Basic example](https://www.baeldung.com/spring-expression-language#parsing)
- Example 
    ```spel
    #{spel('\\Hello\\.length()')}
    #{spel('request.body.str.length()')}
    ```
  
## Examples
- If you want sample response, insert to database using [mock-sample.sql](mock-sample.sql)

### Error response body
#### Case: [RFC-7807](https://www.rfc-editor.org/rfc/rfc7807)
```json
{
  "type": "@{error.name}",
  "title": "Internal server error",
  "status": 500,
  "detail": "@{error.localizedMessage}",
  "instance": "@{request.uri}"
}
```

#### Case: Spring-web default format
```json
{
 "timestamp": #{now()},
 "status": 500,
 "error": "Internal server error",
 "exception": "@{error.name}",
 "message": "@{error.localizedMessage}",
 "path": "@{request.uri}"
}
```