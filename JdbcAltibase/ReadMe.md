# 빌드

## SiardKR 전체 빌드

SiardKR의 최상위 경로에 allDeploy.cmd를 제공하고 있는데 이를 이용해 전체 모듈의 바이너리를 빌드해서 디플로이 할 수 있다. 하지만  SiardKR 자체적으로 윈도우용 cmd파일만 제공하고 리눅스용 셸스크립트 파일은 제공하지 않는 것으로 보인다.

1. SiardKR은 빌드툴로 Ant를 사용하기 때문에 Ant가 설치되지 않은 경우 [http://ant.apache.org](http://ant.apache.org)에서 먼저 ANT를 설치해야 한다. 또한 컴파일을 위해 JDK 1.8 이상이 요구된다.
2. 각 모듈안에 있는 build.properties.template 파일을 build.properties 로 복사한다.
3. 모듈별로 build.properties 파일을 다음과 같이 일괄 수정한다.
    1. git관련 설정이 있을 경우 빌드시 소스를 GitHub 레파지토리로 push를 하기 때문에 비활성화 한다.

        ```
        #git=/usr/bin/git
        ```

    2. 빌드 후 zip 파일로부터 바이너리 파일을 압축해제 하려면 dirdeploy에 디렉토리 경로를 입력한다.

        ```
        dirdeploy=D:\\siard
        ```
    3. java8rtjar 에 rt.jar 파일의 경로를 입력한다.
        ```
        java8rtjar=/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar
        ```
    4. SiardApi의 build.properties 파일에서 xjc에 적절한 디렉토리 경로를 입력한다.
        ```
        xjc=/usr/lib/jvm/java-8-openjdk-amd64/bin/xjc
        ```
4. 윈도우 프롬프트에서 allDeploy.cmd 를 실행하여 바이너리 파일을 빌드한다.

    ```
    D:\opt\SiardKR>allDeploy.cmd
    ----- DEPLOY - EnterUtilities
    .
    Buildfile: D:\opt\SiardKR\EnterUtilities\build.xml
    
    clean:
         [echo] enterutils: clean
    
    init:
         [echo] enterutils: init
        [mkdir] Created dir: D:\opt\SiardKR\EnterUtilities\build
        [mkdir] Created dir: D:\opt\SiardKR\EnterUtilities\build\classes
        [mkdir] Created dir: D:\opt\SiardKR\EnterUtilities\build\tests
        [mkdir] Created dir: D:\opt\SiardKR\EnterUtilities\tmp
        [mkdir] Created dir: D:\opt\SiardKR\EnterUtilities\dist
        [mkdir] Created dir: D:\opt\SiardKR\EnterUtilities\doc\javadoc
    
    check:
         [echo] builddate: 13. Jan 2022
         [copy] Copying 1 file to D:\opt\SiardKR\EnterUtilities\tmp
         [echo] version: 2.1
         [echo] revold: 95
    [replaceregexp] The following file is missing: 'D:\opt\SiardKR\EnterUtilities\tmp\branch.properties'
         [echo] branch: ${branch}
    ...
    ...
    ```

5. dirdeploy에 설정한 디렉토리에서 모든 바이너리 파일이 deploy된 것을 확인할 수 있다.

    ```
    D:\siard>dir
     Volume in drive D is DATA
     Volume Serial Number is 5026-59F3
    
     Directory of D:\siard
    
    2022-01-13  오전 11:44    <DIR>          .
    2022-01-13  오전 11:44    <DIR>          ..
    2022-01-13  오전 11:43    <DIR>          enterutils
    2022-01-13  오전 11:43    <DIR>          jdbcaccess
    2022-01-13  오전 11:43    <DIR>          jdbcaltibase
    2022-01-13  오전 11:43    <DIR>          jdbcbase
    2022-01-13  오전 11:43    <DIR>          jdbcdb2
    2022-01-13  오전 11:43    <DIR>          jdbch2
    2022-01-13  오전 11:43    <DIR>          jdbcmssql
    2022-01-13  오전 11:43    <DIR>          jdbcmysql
    2022-01-13  오전 11:43    <DIR>          jdbcoracle
    2022-01-13  오전 11:43    <DIR>          jdbcpostgres
    2022-01-13  오전 11:44    <DIR>          siardcmd
    2022-01-13  오전 11:44    <DIR>          Siard_suite-2.1
    2022-01-13  오전 11:43    <DIR>          zip64
    ```


## JdbcAltibase만 따로 빌드

1. JdbcAltibase디렉토리 안에 있는 build.properties.template 파일을 build.properties로 복사한다.
2. build.properties 파일 수정
    1. git관련 설정이 있을 경우 빌드시 소스를 GitHub 레파지토리로 push를 하기 때문에 비활성화 한다.

        ```
        #git=/usr/bin/git
        ```

    2. 빌드 후 zip 파일로부터 바이너리 파일을 압축해제 하려면 dirdeploy에 디렉토리 경로를 입력한다.

        ```
        dirdeploy=/home/test/opt/siard
        ```

3. ant deploy 타겟을 이용해 바이너리 파일 생성
    1. deploy 타켓 실행 시 내부적으로 build, jar, tests, jartest, zip 타겟을 수행한다.

        ```
        $ ~/work/SiardKR.git/JdbcAltibase > ant deploy
        Buildfile: /home/yjpark/work/SiardKR.git/JdbcAltibase/build.xml
        
        clean:
             [echo] jdbcaltibase: clean
           [delete] Deleting directory /home/yjpark/work/SiardKR.git/JdbcAltibase/build/classes
           [delete] Deleting directory /home/yjpark/work/SiardKR.git/JdbcAltibase/build/tests
           [delete] Deleting directory /home/yjpark/work/SiardKR.git/JdbcAltibase/tmp
           [delete] Deleting directory /home/yjpark/work/SiardKR.git/JdbcAltibase/dist
        
        init:
             [echo] jdbcaltibase: init
            [mkdir] Created dir: /home/yjpark/work/SiardKR.git/JdbcAltibase/build/classes
            [mkdir] Created dir: /home/yjpark/work/SiardKR.git/JdbcAltibase/build/tests
            [mkdir] Created dir: /home/yjpark/work/SiardKR.git/JdbcAltibase/tmp
            [mkdir] Created dir: /home/yjpark/work/SiardKR.git/JdbcAltibase/dist
        
        check:
             [echo] builddate: 11. Jan 2022
             [copy] Copying 1 file to /home/yjpark/work/SiardKR.git/JdbcAltibase/tmp
             [echo] version: 2.1
             [echo] revold: 81
        [replaceregexp] The following file is missing: '/home/yjpark/work/SiardKR.git/JdbcAltibase/tmp/branch.properties'
             [echo] branch: ${branch}
             [echo] branch.master: ${branch.master}
             [echo] build.properties.configured: ${build.properties.configured}
        
        commit:
        
        pull:
        
        revision:
             [echo] jdbcaltibase: revision
             [copy] Copying 1 file to /home/yjpark/work/SiardKR.git/JdbcAltibase/tmp
        [propertyfile] Updating property file: /home/yjpark/work/SiardKR.git/JdbcAltibase/tmp/revision.properties
             [echo] revision: 82
        
        build:
             [echo] jdbcaltibase: build
            [javac] Compiling 22 source files to /home/yjpark/work/SiardKR.git/JdbcAltibase/build/classes
        
        jar:
             [echo] jdbcaltibase: jar
              [jar] Building jar: /home/yjpark/work/SiardKR.git/JdbcAltibase/dist/jdbcaltibase.jar
        
        tests:
             [echo] jdbcaltibase: tests
            [javac] Compiling 10 source files to /home/yjpark/work/SiardKR.git/JdbcAltibase/build/tests
        
        jartest:
             [echo] jdbcaltibase: jartest
              [jar] Building jar: /home/yjpark/work/SiardKR.git/JdbcAltibase/dist/jdbcaltibase-test.jar
        
        zip:
             [echo] jdbcaltibase: zip
              [zip] Building zip: /home/yjpark/work/SiardKR.git/JdbcAltibase/dist/jdbcaltibase-2.1.82.zip
        
        push:
        
        deploy:
             [echo] jdbcaltibase: deploy
           [delete] Deleting directory /home/yjpark/opt/siard/jdbcaltibase
            [unzip] Expanding: /home/yjpark/work/SiardKR.git/JdbcAltibase/dist/jdbcaltibase-2.1.82.zip into /home/yjpark/opt/siard
        
        BUILD SUCCESSFUL
        Total time: 1 second
        ```

    2. 정상적으로 빌드시 JdbcAltibase/dist 디렉토리에는 zip파일과 jar파일들이 생성되며 dirdeploy 경로에는 zip파일이 압축해제된 형태로 존재하게 된다.

        ```bash
        $ ~/work/SiardKR/JdbcAltibase/dist > ls -al 
        drwxr-xr-x  2 yjpark yjpark    4096  1월 11 14:55 .
        drwxr-xr-x 12 yjpark yjpark    4096  1월 11 14:55 ..
        -rw-r--r--  1 yjpark yjpark 3000963  1월 11 14:55 jdbcaltibase-2.1.81.zip
        -rw-r--r--  1 yjpark yjpark   65482  1월 11 14:55 jdbcaltibase-test.jar
        -rw-r--r--  1 yjpark yjpark   37872  1월 11 14:55 jdbcaltibase.jar
        ```


## SiardGui만 따로 빌드

SiardGui에는 JdbcAltibase를 포함한 모든 라이브러리가 포함되기 때문에 SiardGui만 빌드해도 SiardCmd나 SiardGui 프로그램을 사용할 수 있다.

1. SiardGui디렉토리 안에 있는 build.properties.template 파일을 build.properties로 복사한다.
2. build.properties 파일 수정
    1. git관련 설정이 있을 경우 빌드시 소스를 GitHub 레파지토리로 push를 하기 때문에 비활성화 한다.

        ```
        #git=/usr/bin/git
        ```

    2. 빌드 후 zip 파일로부터 바이너리 파일을 압축해제 하려면 dirdeploy에 디렉토리 경로를 입력한다.

        ```
        dirdeploy=/home/test/opt/siard
        ```

3. ant deploy 타겟을 이용해 바이너리 파일 생성
    1. deploy 타켓 실행 시 dirdeploy경로에 모든 바이너리 파일이 생성된다.

        ```
        $ ~/opt/siard/Siard_suite-2.1 > ls -al
        합계 180
        drwxr-xr-x 7 yjpark yjpark  4096  1월 13 12:03 .
        drwxr-xr-x 5 yjpark yjpark  4096  1월 13 12:03 ..
        -rw-r--r-- 1 yjpark yjpark 17598 10월 28 17:19 LICENSE.txt
        -rw-r--r-- 1 yjpark yjpark   971 10월 28 17:19 README.txt
        -rw-r--r-- 1 yjpark yjpark  1902 10월 28 17:19 RELEASE.txt
        drwxr-xr-x 4 yjpark yjpark  4096  1월 13 12:03 doc
        drwxr-xr-x 2 yjpark yjpark  4096  1월 13 12:03 etc
        drwxr-xr-x 2 yjpark yjpark  4096  1월 13 12:03 hxd
        drwxr-xr-x 2 yjpark yjpark  4096  1월 13 12:03 lib
        -rw-r--r-- 1 yjpark yjpark   318 10월 28 17:19 log4j.properties
        -rw-r--r-- 1 yjpark yjpark  8060 10월 28 17:19 siardfromdb.cmd
        -rw-r--r-- 1 yjpark yjpark 17411 10월 28 17:19 siardfromdb.ps1
        -rwxr-xr-x 1 yjpark yjpark  4668 10월 28 17:19 siardfromdb.sh
        -rw-r--r-- 1 yjpark yjpark  8047 10월 28 17:19 siardgui.cmd
        -rw-r--r-- 1 yjpark yjpark  8990 10월 28 17:19 siardgui.ico
        -rw-r--r-- 1 yjpark yjpark 17328 10월 28 17:19 siardgui.ps1
        -rwxr-xr-x 1 yjpark yjpark  4609 12월 28 10:38 siardgui.sh
        -rw-r--r-- 1 yjpark yjpark  8093 10월 28 17:19 siardtodb.cmd
        -rw-r--r-- 1 yjpark yjpark 17483 10월 28 17:19 siardtodb.ps1
        -rwxr-xr-x 1 yjpark yjpark  4659 10월 28 17:19 siardtodb.sh
        drwxr-xr-x 2 yjpark yjpark  4096  1월 13 12:03 testfiles
        ```


# 개발 환경

JdbcAltibase 에는 이클립스나 인텔리J와 같은 개발툴을 위한 설정파일이 같이 포함되어 있기 때문에 각 개발툴의 프로젝트 임포트 기능을 통해 임포트 후 사용하면 된다.

1. JdbcAltibase 디렉토리에 개발툴을 위한 .project, .classpath 파일 및 .settings 디렉토리가 존재한다.

    ```
    $ ~/work/SiardKR.git/JdbcAltibase > ls -al
    합계 116
    drwxr-xr-x 10 yjpark yjpark  4096  1월 11 17:26 .
    drwxr-xr-x 26 yjpark yjpark  4096  1월 11 17:07 ..
    -rw-r--r--  1 yjpark yjpark   891 10월 28 17:47 .classpath
    -rw-r--r--  1 yjpark yjpark   772 10월 28 17:38 .gitattributes
    -rw-r--r--  1 yjpark yjpark   467 10월 28 17:38 .gitignore
    drwxr-xr-x  2 yjpark yjpark  4096  1월 11 15:54 .idea
    -rw-r--r--  1 yjpark yjpark   371 10월 28 17:43 .project
    drwxr-xr-x  2 yjpark yjpark  4096  1월 11 15:16 .settings
    -rw-r--r--  1 yjpark yjpark  3444  1월 11 15:55 JdbcAltibase.iml
    -rw-r--r--  1 yjpark yjpark 17598 10월 21 17:55 LICENSE.txt
    -rw-r--r--  1 yjpark yjpark   840  1월 11 10:42 README.txt
    -rw-r--r--  1 yjpark yjpark   115  1월 11 10:43 RELEASE.txt
    -rw-r--r--  1 yjpark yjpark   596  1월 11 14:47 ReadMe.md
    drwxr-xr-x  2 yjpark yjpark  4096  1월 11 17:26 build
    -rw-r--r--  1 yjpark yjpark  2083  1월 11 10:38 build.properties
    -rw-r--r--  1 yjpark yjpark  2089  1월  7 11:57 build.properties.template
    -rw-r--r--  1 yjpark yjpark 14308  1월 11 16:05 build.xml
    drwxr-xr-x  2 yjpark yjpark  4096 10월 28 17:20 etc
    drwxr-xr-x  2 yjpark yjpark  4096 12월 23 10:57 lib
    drwxr-xr-x  3 yjpark yjpark  4096 11월  5 18:20 out
    drwxr-xr-x  4 yjpark yjpark  4096 10월 28 17:20 src
    drwxr-xr-x  3 yjpark yjpark  4096 10월 28 17:20 test
    ```


# 테스트

### JdbcAltibase 테스트 케이스 수행

JdbcAltibase에 포함되어 있는 junit 테스트 케이스를 수행하려면 빌드파일을 일부 수정하고 테스트용 ant 타켓을 실행하면 된다.

1. SiardKR의 테스트셋은 기본적으로 utf8 캐릭터셋에서 동작하므로 알티베이스 디비 인스턴스도 기본 캐릭터 셋을 UTF-8로 셋팅해야 한다. 
2. JdbcAltibase/build.properties파일에서 테스트용 알티베이스 서버 접속 정보 추가.

    ```
    # ----------------------------------------------------------------------
    # db connection properties
    # if dbuser is undefined, no tests with Altibase database will be run by build
    dbhost=127.0.0.1
    dbport=20300
    dbcatalog=mydb
    dbuser=testuser
    dbpassword=testpwd
    dbauser=sys
    dbapassword=manager
    ```

3. build.xml파일에서 junit 테스트 부분 주석 해제.

    ```xml
    <!-- line : 240
            <junit haltonerror="true" haltonfailure="true" fork="true" printsummary="on" dir="${basedir}">
                  <sysproperty key="java.util.logging.config.file" value="${diretc}/debug.properties"/>
                  <formatter type="plain"/>
                  <classpath>
                    <pathelement path="${dirbuildtests}"/>
                    <pathelement path="${dirdist}/${jarfile}"/>
                    <pathelement path="${cpaltibasejdbc}"/>
                    <pathelement path="${cpjdbcbase-test}"/>
                    <pathelement path="${cpsqlparser}"/>
                    <pathelement path="${cpenterutils}"/>
                    <pathelement path="${cpantlr}}"/>
                    <pathelement path="${cpjunit}"/>
                  </classpath>
                  <test name="${classdatasourcetester}"
                      outfile="${filedatasourcetest}"
                      todir="${dirtmp}"/>
            </junit>
            <junit haltonerror="true" haltonfailure="false" fork="true" printsummary="on" dir="${basedir}" showoutput="yes">
                  <sysproperty key="java.util.logging.config.file" value="${diretc}/debug.properties"/>
                  <formatter type="plain"/>
                  <classpath>
                    <pathelement path="${dirbuildtests}"/>
                    <pathelement path="${dirdist}/${jarfile}"/>
                    <pathelement path="${cpaltibasejdbc}"/>
                    <pathelement path="${cpjdbcbase-test}"/>
                    <pathelement path="${cpsqlparser}"/>
                    <pathelement path="${cpenterutils}"/>
                    <pathelement path="${cpantlr}"/>
                    <pathelement path="${cpjunit}"/>
                  </classpath>
                  <test name="${classjdbctestsuite}"
                      outfile="${filejdbctests}"
                      todir="${dirtmp}"/>
            </junit>
    -->
    ```

4. tests 타켓으로 JdbcAltibase 테스트케이스 수행

    ```
    ~/work/SiardKR.git/JdbcAltibase > ant tests
    Buildfile: /home/yjpark/work/SiardKR.git/JdbcAltibase/build.xml
    
    clean:
         [echo] jdbcaltibase: clean
       [delete] Deleting directory /home/yjpark/work/SiardKR.git/JdbcAltibase/build/classes
       [delete] Deleting directory /home/yjpark/work/SiardKR.git/JdbcAltibase/build/tests
       [delete] Deleting directory /home/yjpark/work/SiardKR.git/JdbcAltibase/tmp
       [delete] Deleting directory /home/yjpark/work/SiardKR.git/JdbcAltibase/dist
    
    init:
         [echo] jdbcaltibase: init
        [mkdir] Created dir: /home/yjpark/work/SiardKR.git/JdbcAltibase/build/classes
        [mkdir] Created dir: /home/yjpark/work/SiardKR.git/JdbcAltibase/build/tests
        [mkdir] Created dir: /home/yjpark/work/SiardKR.git/JdbcAltibase/tmp
        [mkdir] Created dir: /home/yjpark/work/SiardKR.git/JdbcAltibase/dist
    
    check:
         [echo] builddate: 11. Jan 2022
         [copy] Copying 1 file to /home/yjpark/work/SiardKR.git/JdbcAltibase/tmp
         [echo] version: 2.1
         [echo] revold: 80
    [replaceregexp] The following file is missing: '/home/yjpark/work/SiardKR.git/JdbcAltibase/tmp/branch.properties'
         [echo] branch: ${branch}
         [echo] branch.master: ${branch.master}
         [echo] build.properties.configured: ${build.properties.configured}
    
    commit:
    
    pull:
    
    revision:
         [echo] jdbcaltibase: revision
         [copy] Copying 1 file to /home/yjpark/work/SiardKR.git/JdbcAltibase/tmp
    [propertyfile] Updating property file: /home/yjpark/work/SiardKR.git/JdbcAltibase/tmp/revision.properties
         [echo] revision: 81
    
    build:
         [echo] jdbcaltibase: build
        [javac] Compiling 22 source files to /home/yjpark/work/SiardKR.git/JdbcAltibase/build/classes
    
    jar:
         [echo] jdbcaltibase: jar
          [jar] Building jar: /home/yjpark/work/SiardKR.git/JdbcAltibase/dist/jdbcaltibase.jar
    
    tests:
         [echo] jdbcaltibase: tests
        [javac] Compiling 10 source files to /home/yjpark/work/SiardKR.git/JdbcAltibase/build/tests
        [junit] Running ch.admin.bar.siard2.jdbcx.AltibaseDataSourceTester
        [junit] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.086 sec
        [junit] Running ch.admin.bar.siard2.jdbc._AltibaseJdbcTestSuite
        [junit] server: 127.0.0.1 (null)
        [junit] sock_bind_addr: null (null)
        [junit] port: 20300 (null)
        [junit] database: mydb (null)
        [junit] user: null (null)
        [junit] password: null (null)
        [junit] conntype: TCP (null)
        ...
        ...
    BUILD SUCCESSFUL
    Total time: 11 seconds
    ```

5. 상세한 테스트 결과는 JdbcAltibase/tmp/jdbc-tests.txt 파일에서 확인 가능.

    ```
    [jdbc-tests.txt]
    Testsuite: ch.admin.bar.siard2.jdbc._AltibaseJdbcTestSuite
    Tests run: 435, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 9.336 sec
    ------------- Standard Output ---------------
    server: 127.0.0.1 (null)
    sock_bind_addr: null (null)
    port: 20300 (null)
    database: mydb (null)
    user: null (null)
    password: null (null)
    conntype: TCP (null)
    ib_latency: 0 (null)
    ib_conchkspin: 0 (null)
    privilege: null (null)
    ...
    ...
    ```


### SiardCmd 테스트 케이스 수행

SiardCmd에 포함되어 있는 Altibase용 테스트 프로그램으로 추출, 재현 기능을 검증한다. 테스트를 수행하려면 빌드파일을 일부 수정해야 한다.

1. SiardCmd/build.properties파일 안에 알티베이스 테스트용 서버 접속 정보 추가.

    ```
    # ----------------------------------------------------------------------
    # db connection properties for altibase
    # if dbusermysql is undefined, no tests with Altibase databases will be run by build
    dbhostAltibase=127.0.0.1
    dbportAltibase=20300
    dbcatalogAltibase=mydb
    dbuserAltibase=sys
    dbpasswordAltibase=manager
    dbauserAltibase=sys
    dbapasswordAltibase=manager
    ```

2. SiardCmd/build.xml 파일에서 Altibase 테스트 관련 junit 설정부분의 주석 해제.

    ```xml
    <target name="tests-altibase" depends="tests" if="dbuserAltibase">
            <echo message="${ant.project.name}: test-altibase run" />
            <!-- line : 557
            <junit haltonerror="true" haltonfailure="true" fork="true" printsummary="on" dir="${basedir}">
                  <sysproperty key="java.util.logging.config.file" value="${diretc}/debug.properties"/>
                  <formatter type="plain"/>
                  <classpath>
                    <pathelement path="${dirbuildtests}"/>
                    <pathelement path="${dirdist}/${jarfile}"/>
                    <pathelement path="${cpantlr}"/>
                    <pathelement path="${cpenterutils}"/>
                    <pathelement path="${cpsqlparser}"/>
                    <pathelement path="${cpjts}"/>
                    <pathelement path="${cpjdbcbase}"/>
                    <pathelement path="${cpjdbcbase-test}"/>
                    <pathelement path="${cpjdbcaltibase}"/>
                    <pathelement path="${cpjdbcaltibase-test}"/>
                    <pathelement path="${cpjaxb}"/>
                    <pathelement path="${cpzip64}"/>
                    <pathelement path="${cpwoodstox}"/>
                    <pathelement path="${cpmsv}"/>
                    <pathelement path="${cpsiardapi}"/>
                    <pathelement path="${cpjunit}"/>
                    <pathelement path="${cpjsch}" />
                  </classpath>
                  <test name="${classaltibasetestsuite}"
                      outfile="${filealtibasetests}"
                      todir="${dirtmp}"/>
            </junit>
            -->
    ```

3. tests-altibase 타겟을 실행하여 SiardCmd의 알티베이스 테스트 수행.

    ```
    $ ~/work/SiardKR.git/SiardCmd > ant tests-altibase
    Buildfile: /home/yjpark/work/SiardKR.git/SiardCmd/build.xml
    
    clean:
         [echo] siardcmd: clean
       [delete] Deleting directory /home/yjpark/work/SiardKR.git/SiardCmd/build/classes
       [delete] Deleting directory /home/yjpark/work/SiardKR.git/SiardCmd/build/tests
       [delete] Deleting directory /home/yjpark/work/SiardKR.git/SiardCmd/tmp
       [delete] Deleting directory /home/yjpark/work/SiardKR.git/SiardCmd/dist
    
    init:
         [echo] siardcmd: init
        [mkdir] Created dir: /home/yjpark/work/SiardKR.git/SiardCmd/build/classes
        [mkdir] Created dir: /home/yjpark/work/SiardKR.git/SiardCmd/build/tests
        [mkdir] Created dir: /home/yjpark/work/SiardKR.git/SiardCmd/tmp
        [mkdir] Created dir: /home/yjpark/work/SiardKR.git/SiardCmd/dist
    
    check:
         [echo] builddate: 11. Jan 2022
         [copy] Copying 1 file to /home/yjpark/work/SiardKR.git/SiardCmd/tmp
         [echo] version: 2.1
         [echo] revold: 183
    [replaceregexp] The following file is missing: '/home/yjpark/work/SiardKR.git/SiardCmd/tmp/branch.properties'
         [echo] branch: ${branch}
         [echo] branch.master: ${branch.master}
         [echo] build.properties.configured: ${build.properties.configured}
    
    commit:
    
    pull:
    
    revision:
         [echo] siardcmd: revision
         [copy] Copying 1 file to /home/yjpark/work/SiardKR.git/SiardCmd/tmp
    [propertyfile] Updating property file: /home/yjpark/work/SiardKR.git/SiardCmd/tmp/revision.properties
         [echo] revision: 184
    
    build:
         [echo] siardcmd: build (lib/siardapi.jar;lib/jdbcaccess.jar;lib/jackcess-2.1.6a.jar;lib/commons-lang-2.6.jar;lib/commons-logging-1.1.3.jar;lib/jdbcdb2.jar;lib/db2jcc4.jar;lib/jdbcmysql.jar;lib/mysql-connector-java-8.0.18.jar;lib/jdbcoracle.jar;lib/ojdbc6.jar;lib/xdb6.jar;lib/xmlparserv2.jar;lib/jdbcmssql.jar;lib/sqljdbc41.jar;lib/jdbch2.jar;lib/h2-1.3.176.jar;lib/jdbcbase.jar;lib/enterutils.jar;lib/jsch-0.1.55.jar;)
        [javac] Compiling 21 source files to /home/yjpark/work/SiardKR.git/SiardCmd/build/classes
        [javac] Note: /home/yjpark/work/SiardKR.git/SiardCmd/src/ch/config/db/HistoryDAO.java uses unchecked or unsafe operations.
        [javac] Note: Recompile with -Xlint:unchecked for details.
    
    jar:
         [echo] siardcmd: jar
          [jar] Building jar: /home/yjpark/work/SiardKR.git/SiardCmd/dist/siardcmd.jar
    
    tests:
         [echo] siardcmd: tests build
        [javac] Compiling 47 source files to /home/yjpark/work/SiardKR.git/SiardCmd/build/tests
    
    tests-altibase:
         [echo] siardcmd: test-altibase run
        [junit] Running ch.admin.bar.siard2.cmd._AltibaseTestSuite
        [junit] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.987 sec
    
    BUILD SUCCESSFUL
    Total time: 5 seconds
    ```

4. SiardCmd/tmp/altibase-tests.txt 파일에서 상세 결과 확인.

    ```
    [altibase-tests.txt]
    Testsuite: ch.admin.bar.siard2.cmd._AltibaseTestSuite
    Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 5.161 sec
    ------------- Standard Output ---------------
    testAltibaseFromDb
    Create TestSqlDatabase
    ...
    ------------- ---------------- ---------------
    
    Testcase: testAltibaseFromDb took 3.947 sec
    Testcase: testSampleToAltibase took 0.738 sec
    Testcase: testAltibaseToAltibase took 0.448 sec
    ```
5. 테스트 수행시 SQLITE에서 HISTORY테이블을 찾지 못해 실패하는 경우가 있는데 이 경우 siardgui를 실행시키고 생성된 siard.db파일을 SiardCmd/db 디렉토리에 복사하고 실행시키면 된다.

# 제약사항
JdbcAltibase는 Jdbc Spec 4.x를 필요로 하기 때문에 알티베이스 서버 7.2 버전 이상에서 정상 동작한다.