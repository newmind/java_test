# Random int generator

## create database and tables
```sh
> mysql -uroot -p sql\create.sql
```
## drop tables and database
```sh
> mysql -uroot -p sql\drop.sql
```

## run generator
```sh
> mvn package
> java -jar target/generator-1.0-SNAPSHOT.jar
```


