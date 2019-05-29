# 1. Random int 복사하기

3개의 프로그램으로 구성됨

- generator (DB 에 0.1초단위로 데이터 저장)
- master (DB에서 데이터를 읽어서 slave 로 전송)
- slave (master로부터 수신한 데이터를 DB에 저장)

## 테스트를 위한 준비사항

mysql 은 따로 구동되고 있어야 함.
접속 정보와 계정은 아래의 파일들에서 바꿔줘야 함.
기본으로 localhost 의 root / 1234 로 접근함.

- generator/src/main/resources/META-INF/persistence.xml
- master/src/main/resources/META-INF/persistence.xml
- slave/src/main/resources/META-INF/persistence.xml
- spring-addressbook/src/main/resources/application.properties

## 테스트 시작하기

1. DB 초기화 (데이터베이스: test_jgkim, 테이블: random_src, random_dst 생성함)

```sh
> cd generator
> mysql -uroot -p1234 < sql/create.sql
```

2. slave 실행 (master로부터 수신한 데이터를 random_dst 에 저장)

```sh
# 새로운 터미널에서
> cd slave
>> mvn exec:java
```

3. master 실행 (random_src 테이블에서 데이터를 읽어서 slave 로 전송)

```sh
# 새로운 터미널에서
> cd master
>> mvn exec:java
```

4. generator 실행 (random_src 에 0.1초단위로 데이터 저장)

```sh
# 새로운 터미널에서
> cd generator
> mvn exec:java
```

5. slave 종료한 후 다시 실행

```sh
# 위의 2번 터미널에서
# [Enter] 눌러서 slave 프로그램 종료
# 일정 시간 다시 아래 실행
>> mvn exec:java
```

6. master 종료한 후 다시 실행

```sh
# 위의 3번 터미널에서
# [Enter] 눌러서 master 프로그램 종료
# 일정 시간 다시 아래 실행
>> mvn exec:java
```

7. generator 종료 후 테이블 복사 내역 비교

- generator 터미널에서 [Enter] 로 종료

```sql
select * from test_jgkim.random_src
  where create_time not in (select create_time from test_jgkim.random_dst);
```

8. master, slave [Enter] 로 종료

# 2. 스트링부트 주소록

## 테스트 시작하기

1. 서비스 실행

```sh
> cd spring-addressbook
> ./gradlew bootRun
```

2. cUrl 로 테스트

JSON 데이터 필드는 아래 4가지이다

    { "name": "some_name",
      "phone": "",
      "address": "",
      "note": "" }

```sh
# 새로운 연락처 등록
> curl -X POST -i \
    -H "Content-Type: application/json" \
    -d '{"name":"osci","phone":"02-555-1234"}' \
    http://localhost:8080/contacts
# 연락처 전체 보기
> curl -i http://localhost:8080/contacts
# 특정 연락처의 전화번호 수정
> curl -X PUT -i \
    -H "Content-Type: application/json" \
    -d '{"phone":"01055550000"}' \
    http://localhost:8080/contacts/1
# 특정 연락처만 보기
> curl -i http://localhost:8080/contacts/1
# 특정 연락처 삭제
> curl -X DELETE -i http://localhost:8080/contacts/1
```

    VS Code 상에서 테스트하려면 "REST Client" 확장을 설치후, [test.http](spring-addressbook/src/test/test.http) 에서 테스트 가능

# 3. 테스트 종료

테스트로 사용한 테이블과 DB 를 삭제

```sh
> cd generator
> mysql -uroot -p1234 < sql/drop.sql
```

# 4. Random int 동작방식 설명

주요 가정 :

- master와 slave 는 각각 하나씩만 존재한다.
- 데이터의 [날짜시간]은 중복될 수 없음(하나의 master 가 0.1초 단위로 생성)
- db 가 종료된 상황에 대한 테스트는 하지 않음
- 데이터가 0.1초 단위로 생성되는데, 정밀도 낮아서 오차발생할수 있음

전송포맷은 [날짜시간 랜덤정수\n] 형식임

    2019-05-27 01:30:25.982 19457190\n
    2019-05-27 01:30:26.082 -976451\n
    ...

master-slave 사이의 동작 방식은

1. [master] : db에서 1초단위로 최신 데이터를 읽어서 slave로 보낸다. 마지막으로 보낸 데이터의 날짜시간은 다음에 읽을 시간과 비교하기 위해 저장한다.
2. [slave] : master로부터 수신한 데이터를 바로 db 에 저장한다. 일정 갯수의 데이터를 받았거나(100개), master로부터의 송신이 일정시간(200ms)동안 없었다면, master 에게 '마지막 데이터의 시간'을 전송함
3. [master] : slave로 부터 수신한 '마지막 데이터의 시간'을 파일에 저장한다. master 프로그램이 다시 시작하면 '마지막 데이터의 시간' 이후의 데이터만 db에서 읽어 전송한다.

master 의 경우는 socket read 를 위한 별도의 스레드가 있고, slave 의 경우는 listen 을 위한 스레드와 접속한 client 용 스레드가 각각 있음

# 5. 스트링부트 주소록 동작방식 설명

- gradle, JPA, lombok
- 테이블 생성 옵션은 screate-drop 으로 하여, 종료시 삭제되게 함.

주요 REST Endpoint

    GET /contacts
        : 모든 주소록 얻기
    POST /contacts
        : 새로운 연락처 추가
    PUT /contacts/{id}
        : 특정 id 의 연락처 정보 수정
    DELETE /contacts/{id}
        : id 연락처 삭제

JSON 데이터 필드는 아래 4가지

    { "name": "some_name",
      "phone": "",
      "address": "",
      "note": "" }
