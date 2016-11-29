create table "TEN_SUM_FIELD"
   ("ID" NUMBER(10,0) NOT NULL ENABLE,
    "FIELD_IDENTIFIER" VARCHAR2(100 CHAR) NOT NULL ENABLE,
    "DISPLAY_NAME" VARCHAR2(50 CHAR) NOT NULL ENABLE,
	"MANDATORY" NUMBER(1),
	"VISIBLE" NUMBER(1),
    "CREATED" DATE NOT NULL ENABLE,
    "CREATOR" VARCHAR2(50 CHAR) NOT NULL ENABLE,
    "UPDATED" DATE NOT NULL ENABLE,
    "UPDATED_BY" VARCHAR2(50 CHAR) NOT NULL ENABLE,
     CONSTRAINT "TE_SU_FI_PK" PRIMARY KEY ("ID")
     USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS
     STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
     PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
     TABLESPACE "GTS_IND" ENABLE,
	 CONSTRAINT "TE_SU_FD_UK" UNIQUE ("FIELD_IDENTIFIER")
  ) SEGMENT CREATION IMMEDIATE
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "GTS_TAB";
  
  
create table "TEN_SUM_SELECTIONLIST"
   ("ID" NUMBER(10,0) NOT NULL ENABLE,
    "FIELD_IDENTIFIER" VARCHAR2(100 CHAR) NOT NULL ENABLE,
    "SELECT_OPTION" VARCHAR2(50 CHAR) NOT NULL ENABLE,
    "CREATED" DATE NOT NULL ENABLE,
    "CREATOR" VARCHAR2(50 CHAR) NOT NULL ENABLE,
    "UPDATED" DATE NOT NULL ENABLE,
    "UPDATED_BY" VARCHAR2(50 CHAR) NOT NULL ENABLE,
     CONSTRAINT "TE_SU_SL_PK" PRIMARY KEY ("ID")
     USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS
     STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
     PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
     TABLESPACE "GTS_IND" ENABLE,
     CONSTRAINT "TE_SU_FI_FK" FOREIGN KEY ("FIELD_IDENTIFIER") REFERENCES "TEN_SUM_FIELD" ("FIELD_IDENTIFIER") ON DELETE CASCADE ENABLE
  ) SEGMENT CREATION IMMEDIATE
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "GTS_TAB";
  
  CREATE SEQUENCE "TEN_SUM_SELECTIONLIST_SEQ" MINVALUE 0 MAXVALUE 9999999999 
       INCREMENT BY 1 START WITH 1 CACHE 50 NOORDER NOCYCLE;