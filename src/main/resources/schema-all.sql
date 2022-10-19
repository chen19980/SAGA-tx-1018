-- journal id sequence
CREATE SEQUENCE journal_seq
MINVALUE 1
MAXVALUE 999999999
INCREMENT BY 1
START WITH 1
NOCACHE
NOCYCLE;

-- journal table
create table ir_journal (
  id BIGINT  default journal_seq.nextval, --AUTO_INCREMENT, identity,
  ir_master_id varchar(16),
  amt decimal(18,2) default 0,
  tx_guid varchar(64),
  status varchar(12), -- tx status start, complete, compensate
  ts timestamp default current_timestamp,
  primary key (id)
);

-- master table
create table ir_master (
  id varchar(16),  -- account
  balance decimal(18,2) default 0,
  hold_mark varchar(40),
  primary key (id)
);

-- detail table
create table ir_detail (
  tx_guid varchar(64),
  ir_master_id varchar(16),
  amt decimal(18,2) default 0, -- tx amount
  balance decimal(18,2) default 0,
  ts timestamp default current_timestamp,
  primary key (tx_guid)
);

---- saga log
--DROP TABLE IF EXISTS saga_log;
create table saga_log (
  id  varchar(100),
  seq varchar(100),
  start_ts timestamp,
  end_ts timestamp,
  status varchar(12), -- start, complete, compensate
  input varchar(2000),
  output varchar(2000),
  before varchar(2000),
  after varchar(2000),
  primary key (id, seq)
);

