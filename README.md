# POC
#### fuse-rest-soap-infinispan-oracle

```
#fuse #camel #rest #soap #infinispan #oracle #blueprint #redhat
```
#### POC SCENARIO

- Expose REST endpoint;
- Filter by: handle, family, name, card, cpf;
- At least one parameter must be passed;
- Integrate w/ Infinispan w/ 20 minutes until cache expiration;
- Use Camel JavaDSL RouteBuilder class.


#### TECHNICAL REQUIREMENTS

```
```

#### OPENSHIFT INSTALLATION/CONFIGURATION

```
```

Openshift Application templates can be found at:<br>
https://github.com/jboss-fuse/application-templates/tree/master/quickstarts



#### STANDALONE LOCAL RUN

```
# download oracle-xe docker image
docker pull wnameless/oracle-xe-11g
```

```
# start oracle-xe
docker run -d -p 49161:1521 -e ORACLE_ALLOW_REMOTE=true wnameless/oracle-xe-11g
```
port: 49161<br>
sid: xe<br>
username: system<br>
password: oracle

```
# start jboss-data-grid with port offset equals to 100
./standalone.sh -Djboss.socket.binding.port-offset=100
```

```
mvn spring-boot:run
```

Check if database was created:

```
SELECT owner, table_name
     FROM all_tables where owner like 'CONVENIO';
   
SELECT * FROM CONVENIO.BENEFICIARIO;

-- insert a record for testing purposes.
INSERT INTO CONVENIO.BENEFICIARIO
  (handle,familia,nome,email,nu_cpf,nu_cartao,nu_contrato,dt_adesao) VALUES (
   CONVENIO.BENEFICIARIO_SEQ.nextVal,777,'raphael','raphael@test.com','11111111111','10010','00001',sysdate);

COMMIT;
```

Open Postman and use the available collection at:
`documentation/poc.postman_collection.json`

The endpoint is located at:
`http://localhost:8080/api/v1/beneficiario-cache/1`

This endpoint will try to get the Entity<beneficiario> from cache.
If it exists, returns otherwise will call database and cache for create a new entry.

That will return a JSON response like:
```json
{
    "timestamp": {
        "year": 2019,
        "month": "FEBRUARY",
        "dayOfMonth": 11,
        "dayOfWeek": "MONDAY",
        "dayOfYear": 42,
        "monthValue": 2,
        "hour": 19,
        "minute": 12,
        "second": 17,
        "nano": 205000000,
        "chronology": {
            "id": "ISO",
            "calendarType": "iso8601"
        }
    },
    "httpStatus": 200,
    "message": "OK",
    "items": [
        {
            "handle": 1,
            "familia": 777,
            "nome": "raphael",
            "email": "raphael@test.com",
            "cpf": "22222222222",
            "cartao": "100230",
            "contrato": "10000000001",
            "dataAdesao": 1549508938000
        }
    ]
}
```

### APPENDIX A
#### ORACLE USER (LOCAL ENVIRONMENT W/ DOCKER)

```
CREATE USER convenio IDENTIFIED BY convenio;

GRANT CREATE SESSION TO convenio WITH ADMIN OPTION;

GRANT UNLIMITED TABLESPACE TO convenio;

GRANT CONNECT,RESOURCE,DBA TO convenio;

ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED; -- SET IT TO UNLIMITED

ALTER PROFILE DEFAULT LIMIT COMPOSITE_LIMIT UNLIMITED PASSWORD_LIFE_TIME UNLIMITED PASSWORD_REUSE_TIME UNLIMITED PASSWORD_REUSE_MAX UNLIMITED PASSWORD_VERIFY_FUNCTION NULL PASSWORD_LOCK_TIME UNLIMITED PASSWORD_GRACE_TIME UNLIMITED FAILED_LOGIN_ATTEMPTS UNLIMITED;

ALTER USER convenio ACCOUNT UNLOCK;
```

port: 49161<br>
sid: xe<br>
username: convenio<br>
password: convenio<br>

### RELEASE NOTES

#### 1.0.0
 - First release
 

### LICENSE

Apache License Version 2.0