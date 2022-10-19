#!/bin/bash
java ^
     -cp conf;target/* ^
    -Dspring.application.admin.enabled=false ^
    -Dfile.encoding=UTF8 ^
    -Djava.awt.headless=true ^
    -Dlogger.file=conf/logback.xml ^
    -Dloader.main=tw.com.firstbank.App ^
    -Dspring.profiles.active=dev ^
    -Dserver.port=%1 ^
    -Xms256m ^
    -Xmx1024m ^
    -XX:+HeapDumpOnOutOfMemoryError ^
    org.springframework.boot.loader.PropertiesLauncher
