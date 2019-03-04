
CREATE USER convenio IDENTIFIED BY convenio;

GRANT CREATE SESSION TO convenio WITH ADMIN OPTION;

GRANT UNLIMITED TABLESPACE TO convenio;

GRANT CONNECT,RESOURCE,DBA TO convenio;

ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED; -- SET IT TO UNLIMITED

ALTER PROFILE DEFAULT LIMIT COMPOSITE_LIMIT UNLIMITED PASSWORD_LIFE_TIME UNLIMITED PASSWORD_REUSE_TIME UNLIMITED PASSWORD_REUSE_MAX UNLIMITED PASSWORD_VERIFY_FUNCTION NULL PASSWORD_LOCK_TIME UNLIMITED PASSWORD_GRACE_TIME UNLIMITED FAILED_LOGIN_ATTEMPTS UNLIMITED;

ALTER USER convenio ACCOUNT UNLOCK;

COMMIT;

--

SELECT owner, table_name
FROM all_tables where owner like 'CONVENIO';

ALTER SESSION SET CURRENT_SCHEMA = convenio;

COMMIT;

create sequence beneficiario_seq start with 1 increment by 1;

create table convenio.beneficiario (handle number(19,0) not null, nu_cartao varchar2(20 char) not null, nu_contrato varchar2(20 char) not null, nu_cpf varchar2(11 char) not null, dt_adesao timestamp, email varchar2(250 char) not null, familia number(19,0) not null, nome varchar2(250 char) not null, primary key (handle));

COMMIT;

SELECT owner, table_name
FROM all_tables where owner like 'CONVENIO';

--

SELECT * FROM CONVENIO.BENEFICIARIO;

INSERT INTO CONVENIO.BENEFICIARIO
(handle,familia,nome,email,nu_cpf,nu_cartao,nu_contrato,dt_adesao) VALUES ( CONVENIO.BENEFICIARIO_SEQ.nextVal,777,'raphael','rabreu@redhat.com','11111111111','10010','00001',sysdate);

COMMIT;

SELECT * FROM CONVENIO.BENEFICIARIO;