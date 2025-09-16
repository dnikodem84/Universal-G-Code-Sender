#!/usr/bin/env bash
export JAVA_HOME=$(/usr/libexec/java_home -V 17 2>&1 | grep -i 17 | sed 's@.*/Lib@/Lib@');

"/Applications/Apache NetBeans.app/Contents/Resources/netbeans//java/maven/bin/mvn" install -DskipTests=true && \
"/Applications/Apache NetBeans.app/Contents/Resources/netbeans//java/maven/bin/mvn" nbm:run-platform -pl ugs-platform/application
