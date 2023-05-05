# Mock-server 사용자 메뉴얼

## 기본 에러
### Not Found Error
- 요청한 URI와 매칭되는 URL이 없을 경우 응답
### Multiple URL Detect
- 요청된 URI와 매칭되는 URL 결과가 복수 일때 응답
### Internal Error
- 응답 수행 중에 'Not Found Error', 'Multiple URL Detect' 발생 조건을 제외한 에러 응답 

## 응답 조건
> SpEL(Spring Expression Language) 을 이용하여 결과가 반드시 'boolean' 으로 반환 되어야 한다.
> 요청 헤더 및 파라미터 는 Map으로 제공되므로, 접근시 param[name] 과 같은 패턴으로 접근한다. 

## 데이터 바인딩
### 포맷
- 변수 : ${variablePath}
- 함수 : #{functionName(...args)}

### 제공 변수 명
> 요청 Body는 JSON이 아닐 경우 String 으로만 사용 가능

| 이름      | 설명                        | 비고                     |
|---------|---------------------------|------------------------|
| request | HttpServletRequest 기준 데이터 |                        |
| header  | 요청 헤더                     |                        |
| body    | 요청 Body 데이터               | JSON 일 경우만 JsonPATH 지원 |
| param   | 요청 파라미터                   |                        |
| error   | 에러 정보                     | 기본 에러 정의에서만 사용 가능      |