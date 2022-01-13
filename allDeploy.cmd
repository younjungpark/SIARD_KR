@ECHO off

REM - EnterUtilities
@ECHO ----- DEPLOY - EnterUtilities
@ECHO .
CD EnterUtilities
CALL ANT deploy
@ECHO .
CD ..

REM - JdbcBase
@ECHO ----- DEPLOY - JdbcBase
@ECHO .
CD JdbcBase
CALL ANT deploy
@ECHO .
CD ..

REM - JdbcAccess
@ECHO ----- DEPLOY - JdbcAccess
@ECHO .
CD JdbcAccess
CALL ANT deploy
@ECHO .
CD ..

REM - JdbcDb2
@ECHO ----- DEPLOY - JdbcDb2
@ECHO .
CD JdbcDb2
CALL ANT deploy
@ECHO .
CD ..

REM - JdbcH2
@ECHO ----- DEPLOY - JdbcH2
@ECHO .
CD JdbcH2
CALL ANT deploy
@ECHO .
CD ..

REM - JdbcMsSql
@ECHO ----- DEPLOY - JdbcMsSql
@ECHO .
CD JdbcMsSql
CALL ANT deploy
@ECHO .
CD ..

REM - JdbcMySql
@ECHO ----- DEPLOY - JdbcMySql
@ECHO .
CD JdbcMySql
CALL ANT deploy
@ECHO .
CD ..

REM - JdbcOracle
@ECHO ----- DEPLOY - JdbcOracle
@ECHO .
CD JdbcOracle
CALL ANT deploy
@ECHO .
CD ..

REM - JdbcPostgres
@ECHO ----- DEPLOY - JdbcPostgres
@ECHO .
CD JdbcPostgres
CALL ANT deploy
@ECHO .
CD ..

REM - JdbcCubrid
@ECHO ----- DEPLOY - JdbcCubrid
@ECHO .
CD JdbcCubrid
CALL ANT deploy
@ECHO .
CD ..

REM - JdbcAltibase
@ECHO ----- DEPLOY - JdbcAltibase
@ECHO .
CD JdbcAltibase
CALL ANT deploy
@ECHO .
CD ..

REM - SqlParser
@ECHO ----- DEPLOY - SqlParser
@ECHO .
CD SqlParser
CALL ANT deploy
@ECHO .
CD ..

REM - Zip64File
@ECHO ----- DEPLOY - Zip64File
@ECHO .
CD Zip64File
CALL ANT deploy
@ECHO .
CD ..

REM - SiardApi
@ECHO ----- DEPLOY - SiardApi
@ECHO .
CD SiardApi
CALL ANT deploy
@ECHO .
CD ..

REM - SiardCmd
@ECHO ----- DEPLOY - SiardCmd
@ECHO .
CD SiardCmd
CALL ANT deploy
@ECHO .
CD ..

REM - SiardGui
@ECHO ----- DEPLOY - SiardGui
@ECHO .
CD SiardGui
CALL ANT deploy
@ECHO .
CD ..
