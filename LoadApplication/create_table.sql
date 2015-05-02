-- Create table
--Содержит информацию о результате выполнения операций для систем взаимодействующие через mq
create table APLANA_MQ_RESP
(
  XML_DATA BLOB,
  SYSNAME  VARCHAR2(20),
  STATE    VARCHAR2(10),
  TIME     TIMESTAMP(6)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );


-- Create table
--Содержит статистику по операциям для ПФР веб-сервиса
create table APLANA_PFR_STATISTICS
(
  TIME           TIMESTAMP(6),
  OPERATION_NAME VARCHAR2(50),
  RESPONSE_TIME  NUMBER(20),
  ERROR_MSG      VARCHAR2(200)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Create/Recreate indexes 
create index IDX_PFR_OPERATION_NAME on APLANA_PFR_STATISTICS (OPERATION_NAME)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );


-- Create table
-- Содержит интенсивность отправки сообщений для систем взаимодействующие через mq
create table PUT_SYSTEMS_STATISTICS
(
  TIME        TIMESTAMP(6),
  SYSTEM_NAME VARCHAR2(10)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

-- Create table
--Содержит информацию о длительности транзакций для систем взаимодействующие через mq
create table APLANA_MQ_CORRID
(
  SYSNAME   VARCHAR2(20),
  CORR_ID   VARCHAR2(50),
  TIME_SEND TIMESTAMP(6),
  TIME_RESP TIMESTAMP(6)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Create/Recreate indexes 
create unique index IDX_APLANA_CORR_ID on APLANA_MQ_CORRID (CORR_ID)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
