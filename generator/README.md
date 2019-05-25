# Random int generator

## create database and tables
mysql계정은  적절하게 바꿔줘야 함
계정 정보는 generator/src/main/resources/META-INF/persistence.xml 에서도 수정해줘야 함
```sh
> mysql -uroot -p1234 < sql/create.sql
```
## drop tables and database
```sh
> mysql -uroot -p1234 < sql/drop.sql
```

## run generator
```sh
> mvn exec:java
```

