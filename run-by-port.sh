#!/bin/zsh
java -Dserver.port=$1 -jar target/camel-1.0.0.jar tw.com.fistbank.App
