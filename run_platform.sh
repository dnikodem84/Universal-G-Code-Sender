#!/usr/bin/env bash
export JAVA_HOME=$(/usr/libexec/java_home -V 17 2>&1 | grep -i 17 | sed 's@.*/Lib@/Lib@g' | sed 's@.* @@');
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
"/Applications/Netbeans/Apache NetBeans 17.app/Contents/Resources/Netbeans/netbeans//java/maven/bin/mvn" install -DskipTests=true && \
"/Applications/Netbeans/Apache NetBeans 17.app/Contents/Resources/Netbeans/netbeans//java/maven/bin/mvn" nbm:run-platform -pl ugs-platform/application
