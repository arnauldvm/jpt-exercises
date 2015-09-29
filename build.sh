#!/bin/bash

tgtdir=./build

mkdir -p "$tgtdir"

cd "$tgtdir"

# Download all
##############

mkdir download
pushd download

#! Add this to your ~/.curlrc
# proxy = <proxy_host>:<proxy_port>

function download {
  url="$1"
  file="$2"
  if [ \! -f "$file" ]; then
    echo Downloading "$file"...
    curl -L -o "$file.downloading" $url
   mv "$file.downloading" "$file"
  else
    echo "$file" already downloaded, skipping
  fi
}

function archive {
  source="$1"
  file="$2"
  if [ \! -f "$file" ]; then
    echo Archiving "$file"...
    (cd "$source"; zip -qr9 - .) > "$file.archiving"
   mv "$file.archiving" "$file"
  else
    echo "$file" already archived, skipping
  fi
}

jmeter_zip=apache-jmeter-2.13.zip
download http://www.eu.apache.org/dist//jmeter/binaries/apache-jmeter-2.13.zip "$jmeter_zip"
jmpluginstd_zip=JMeterPlugins-Standard-1.3.0.zip
download http://jmeter-plugins.org/downloads/file/JMeterPlugins-Standard-1.3.0.zip "$jmpluginstd_zip"
jmpluginext_zip=JMeterPlugins-Extras-1.3.0.zip
download http://jmeter-plugins.org/downloads/file/JMeterPlugins-Extras-1.3.0.zip "$jmpluginext_zip"
gcviewer_jar=gcviewer-1.34.1.jar
download http://sourceforge.net/projects/gcviewer/files/gcviewer-1.34.1.jar/download "$gcviewer_jar"

if [ "$(uname)" \!= "Darwin" ]; then
  mat_zip=MemoryAnalyzer-1.5.0.20150527-win32.win32.x86_64.zip
  #download "http://www.eclipse.org/downloads/download.php?file=/mat/1.5/rcp/MemoryAnalyzer-1.5.0.20150527-win32.win32.x86_64.zip&mirror_id=468" "$mat_zip"
  download "http://eclipse.mirror.kangaroot.net/mat/1.5/rcp/MemoryAnalyzer-1.5.0.20150527-win32.win32.x86_64.zip" "$mat_zip"
  curl_7z=curl_X64_ssl.7z
  download http://www.paehl.com/open_source/downloads/curl_X64_ssl.7z "$curl_7z"

  #This one cannot be easily downloaded under a portable form
  javasdk_zip=java-sdk-1.8.0_25-x64.zip
  archive /C/javadev/tools/java/sdk/1.8.0_25-x64 "$javasdk_zip"
      # Also includes:
      #Visual VM
      #Mission Control
else
  mat_zip=MemoryAnalyzer-1.5.0.20150527-macosx.cocoa.x86_64.zip
  http://mirror.switch.ch/eclipse/mat/1.5/rcp/MemoryAnalyzer-1.5.0.20150527-macosx.cocoa.x86_64.zip
fi

threadlogic_jar=ThreadLogic-2.0.217.jar
download https://java.net/projects/threadlogic/downloads/download/ThreadLogic-2.0.217.jar "$threadlogic_jar"

popd


# Pre-install
#############

mkdir -p install/local
pushd install/local

unzip -q "../../download/$jmeter_zip"
jmeter_dir="$(echo apache-jmeter-*)"
pushd apache-jmeter-*
unzip -oq "../../../download/$jmpluginstd_zip"
unzip -oq "../../../download/$jmpluginext_zip"
popd
find $jmeter_dir/bin \( -name '*.sh' -o -name 'jmeter' \) -exec chmod +x {} \;

if [ "$(uname)" \!= "Darwin" ]; then
  java_dir="$(echo "$javasdk_zip" | perl -pe 's/\.zip$//')"
  mkdir "$java_dir"
  pushd "$java_dir"
  unzip -q "../../../download/$javasdk_zip"
  popd
fi

unzip -q "../../download/$mat_zip"
mat_dir="$(echo mat*)"
if [ "$(uname)" \!= "Darwin" ]; then
  find $mat_dir \( -name '*.exe' -o -name '*.dll' \) -exec chmod +x {} \;
fi

gcviewer_dir="$(echo "$gcviewer_jar" | perl -pe 's/\.jar$//')"
mkdir "$gcviewer_dir"
cp -p "../../download/$gcviewer_jar" "$gcviewer_dir"

threadlogic_dir="$(echo "$threadlogic_jar" | perl -pe 's/\.jar$//')"
mkdir "$threadlogic_dir"
cp -p "../../download/$threadlogic_jar" "$threadlogic_dir"

if [ "$(uname)" \!= "Darwin" ]; then
  curl_dir="$(echo "$curl_7z" | perl -pe 's/\.7z$//')"
  mkdir "$curl_dir"
  pushd "$curl_dir"
  7z x "../../../download/$curl_7z"
  popd
fi

cd ..
echo "#!/bin/bash

root_dir=\"\$(cd \"\$(dirname \"\$1\")\"; pwd)\"/local

if [ -z \"\$PATH_OLD\" ]; then
  PATH_OLD=\"\$PATH\"
fi
PATH=\"\$PATH_OLD\"
" > setenv.sh

if [ "$(uname)" \!= "Darwin" ]; then
echo "
JAVA_HOME=\"\$root_dir/$java_dir\"
PATH=\"\$JAVA_HOME/bin:\$PATH\"

CURL_HOME=\"\$root_dir/$curl_dir\"
PATH=\"\$PATH:\$CURL_HOME/openssl\"
" >> setenv.sh
fi

echo "
JMETER_HOME=\"\$root_dir/$jmeter_dir\"
PATH=\"\$PATH:\$JMETER_HOME/bin\"
alias jm=jmeter

MAT_HOME=\"\$root_dir/$mat_dir\"
#PATH=\"\$PATH:\$MAT_HOME\"
alias mat='$MAT_HOME/MemoryAnalyzer -data $MAT_HOME/workspace -vm $JAVA_HOME/bin/javaw.exe -vmargs -Xms256m -Xms4g&'

GCVIEWER_HOME=\"\$root_dir/$gcviewer_dir\"
alias gcviewer='javaw -jar \"\$GCVIEWER_HOME/$gcviewer_jar\"&'

THREADLOGIC_HOME=\"\$root_dir/$threadlogic_dir\"
alias threadlogic='javaw -jar \"\$THREADLOGIC_HOME/$threadlogic_jar\"&'

alias perf='typeperf \"\\System\\Processor Queue Length\" \"\\Processor(_Total)\\% Interrupt Time\" \"\\Processor(_Total)\\% User Time\" \"\\Processor(_Total)\\% Privileged Time\" \"\\System\\File Read Bytes/sec\" \"\\System\\File Write Bytes/sec\"'

echo "Following tools installed: java, jm, mat, gcviewer, threadlogic, curl, perf"
" >> setenv.sh
chmod +x setenv.sh

echo "
@SET \"root_dir=%~dp0\\local\"

@IF \"%PATH_OLD%\"==\"\" SET \"PATH_OLD=%PATH%\"
@SET \"PATH=%PATH_OLD%\"

@SET \"JAVA_HOME=%root_dir%\\$java_dir\"
@SET \"PATH=%JAVA_HOME%\\bin;%PATH%\"

@SET \"JMETER_HOME=%root_dir%\\$jmeter_dir\"
@SET \"PATH=%PATH%;%JMETER_HOME%\\bin\"
@DOSKEY jm=START /MIN CMD /C jmeter

@SET \"MAT_HOME=%root_dir%\\$mat_dir\"
::@SET \"PATH=%PATH%;%MAT_HOME%\"
@DOSKEY mat=START %MAT_HOME%\\MemoryAnalyzer -data %MAT_HOME%\\workspace -vm %JAVA_HOME%\\bin\\javaw.exe -vmargs -Xms256m -Xms4g

@SET \"GCVIEWER_HOME=%root_dir%\\$gcviewer_dir\"
@DOSKEY gcviewer=javaw -jar \"%GCVIEWER_HOME%\\$gcviewer_dir.jar\"&

@SET \"THREADLOGIC_HOME=%root_dir%\\$threadlogic_dir\"
@DOSKEY threadlogic=javaw -jar \"%THREADLOGIC_HOME%\\$threadlogic_dir.jar\"&

@SET \"CURL_HOME=%root_dir%\\$curl_dir\\openssl\"
@SET \"PATH=%PATH%;%CURL_HOME%\"

@DOSKEY perf=typeperf \"\\System\\Processor Queue Length\" \"\\Processor(_Total)\\%% Interrupt Time\" \"\\Processor(_Total)\\%% User Time\" \"\\Processor(_Total)\\%% Privileged Time\" \"\\System\\File Read Bytes/sec\" \"\\System\\File Write Bytes/sec\"

@ECHO "Following tools installed: java, jm, mat, gcviewer, threadlogic, curl, perf"
" > setenv.bat
unix2dos setenv.bat

popd

\rm -f jpt-env.zip
archive install jpt-env.zip

