#!/bin/bash

tgtdir=./target

mkdir -p "$tgtdir"

cd "$tgtdir"

# Download all
##############

mkdir -p download
pushd download

#! Add this to your ~/.curlrc (UX) or ~/_curlrc (Windows)
# proxy = <proxy_host>:<proxy_port>

function download {
  url="$1"
  file="$2"
  if [ \! -f "$file" ]; then
    echo Downloading "$file"...
    curl -k -L -o "$file.downloading" $url
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
    #(cd "$source"; zip -qr9 - .) > "$file.archiving"
    (cd "$source"; 7z a -r -tzip -mx9 ../"$file.archiving" . )
   mv "$source/../$file.archiving" "$file"
  else
    echo "$file" already archived, skipping
  fi
}

jmeter_zip=apache-jmeter-2.13.zip
download https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-2.13.zip "$jmeter_zip"
jmpluginstd_zip=JMeterPlugins-Standard-1.3.1.zip
download http://jmeter-plugins.org/downloads/file/JMeterPlugins-Standard-1.3.1.zip "$jmpluginstd_zip"
jmpluginext_zip=JMeterPlugins-Extras-1.3.1.zip
download http://jmeter-plugins.org/downloads/file/JMeterPlugins-Extras-1.3.1.zip "$jmpluginext_zip"
gcviewer_jar=gcviewer-1.34.1.jar
download http://sourceforge.net/projects/gcviewer/files/gcviewer-1.34.1.jar/download "$gcviewer_jar"

mkdir -p nbm
pushd nbm
download https://java.net/downloads/visualvm/release136/com-sun-tools-visualvm-modules-visualgc_1.nbm com-sun-tools-visualvm-modules-visualgc_1.nbm
download https://java.net/downloads/visualvm/release136/com-sun-tools-visualvm-modules-threadinspect.nbm com-sun-tools-visualvm-modules-threadinspect.nbm
download https://java.net/downloads/visualvm/release136/com-sun-tools-visualvm-modules-oqlsyntax.nbm com-sun-tools-visualvm-modules-oqlsyntax.nbm
download https://java.net/projects/tda/downloads/download/visualvm/net-java-dev-tda-visualvm-logfile.nbm net-java-dev-tda-visualvm-logfile.nbm
download https://java.net/projects/tda/downloads/download/visualvm/net-java-dev-tda.nbm net-java-dev-tda.nbm
download https://java.net/projects/tda/downloads/download/visualvm/net-java-dev-tda-visualvm.nbm net-java-dev-tda-visualvm.nbm
popd

uname="$(uname)"
if [ "$uname" \!= "Darwin" ]; then
  os_name="win32-x64"
  #mat_zip=MemoryAnalyzer-1.5.0.20150527-win32.win32.x86_64.zip
  #mat_url="http://www.eclipse.org/downloads/download.php?file=/mat/1.5/rcp/$mat_zip&mirror_id=468"
  #mat_url="http://eclipse.mirror.kangaroot.net/mat/1.5/rcp/$mat_zip"
  #mat_url="http://download.eclipse.org/mat/1.5/rcp/$mat_zip"
  mat_zip=MemoryAnalyzer-1.6.0.20160531-win32.win32.x86_64.zip
  #mat_url="http://mirror.switch.ch/eclipse/mat/1.6/rcp/$mat_zip"
  mat_url="http://download.eclipse.org/mat/1.6/rcp/$mat_zip"
  download "$mat_url" "$mat_zip"
  curl_7z=curl_X64_ssl.7z
  download http://www.paehl.com/open_source/downloads/curl_X64_ssl.7z "$curl_7z"

  #This one cannot be easily downloaded under a portable form
  javasdk_zip=java-sdk-1.8.0_112-x64.zip
  archive /C/javadev/tools/java/sdk/1.8.0_112-x64 "$javasdk_zip"
      # Also includes:
      #Visual VM
      #Mission Control
else
  os_name="osx"
  mat_zip=MemoryAnalyzer-1.5.0.20150527-macosx.cocoa.x86_64.zip
  download http://mirror.switch.ch/eclipse/mat/1.5/rcp/MemoryAnalyzer-1.5.0.20150527-macosx.cocoa.x86_64.zip "$mat_zip"
fi

threadlogic_jar=ThreadLogic-2.0.217.jar
download https://java.net/projects/threadlogic/downloads/download/ThreadLogic-2.0.217.jar "$threadlogic_jar"

gatling_zip=gatling-charts-highcharts-bundle-2.1.7-bundle.zip
gatling_url="https://repo1.maven.org/maven2/io/gatling/highcharts/gatling-charts-highcharts-bundle/2.1.7/$gatling_zip"
download "$gatling_url" "$gatling_zip"

popd


# Pre-install
#############

\rm -rf install/local
\rm -rf install/app*
\rm -rf install/exercise*
\rm -f install/setenv.*
mkdir -p install/local
pushd install/local

unzip -q "../../download/$jmeter_zip"
jmeter_dir="$(echo apache-jmeter-*)"
pushd apache-jmeter-*
unzip -oq "../../../download/$jmpluginstd_zip"
unzip -oq "../../../download/$jmpluginext_zip"
popd
find $jmeter_dir/bin \( -name '*.sh' -o -name 'jmeter' \) -exec chmod +x {} \;

if [ "$uname" \!= "Darwin" ]; then
  java_dir="$(echo "$javasdk_zip" | perl -pe 's/\.zip$//')"
  mkdir "$java_dir"
  pushd "$java_dir"
  unzip -q "../../../download/$javasdk_zip"
  popd
fi

cp -r ../../download/nbm .

unzip -q "../../download/$mat_zip"
mat_dir="$(echo mat*)"
if [ "$uname" \!= "Darwin" ]; then
  find $mat_dir \( -name '*.exe' -o -name '*.dll' \) -exec chmod +x {} \;
fi

gcviewer_dir="$(echo "$gcviewer_jar" | perl -pe 's/\.jar$//')"
mkdir "$gcviewer_dir"
cp -p "../../download/$gcviewer_jar" "$gcviewer_dir"

threadlogic_dir="$(echo "$threadlogic_jar" | perl -pe 's/\.jar$//')"
mkdir "$threadlogic_dir"
cp -p "../../download/$threadlogic_jar" "$threadlogic_dir"

if [ "$uname" \!= "Darwin" ]; then
  curl_dir="$(echo "$curl_7z" | perl -pe 's/\.7z$//')"
  mkdir "$curl_dir"
  pushd "$curl_dir"
  7z x "../../../download/$curl_7z"
  popd
fi

unzip -q "../../download/$gatling_zip"
gatling_dir="$(echo gatling*)"

cd ..
echo "#!/bin/bash

function msyspath {
  { cd 2>/dev/null \"\$1\" && pwd -W ||
  echo \"\$1\" | sed 's|^/\\([a-z]\\)/|\\1:/|'; }
# | sed 's|/|\\\\|g'
}

function convertpath {
  uname=\"\$(uname)\"
  if [[ \"\$uname\" == MINGW* ]]; then msyspath \"\$1\"
  elif [[ \"\$uname\" == CYGWIN* ]]; then cygpath -m \"\$1\"
  else echo \"\$1\"
  fi
}

root_dir=\"\$(cd \"\$(dirname \"\$1\")\"; pwd)\"/local
root_dir_bis=\"\$(convertpath \"\$root_dir\")\"
  # java does not expect to see /C/... in the path, but C:/...

if [ -z \"\$PATH_OLD\" ]; then
  PATH_OLD=\"\$PATH\"
fi
PATH=\"\$PATH_OLD\"
" > setenv.sh

if [ "$uname" \!= "Darwin" ]; then
echo "
JAVA_HOME=\"\$root_dir/$java_dir\"
PATH=\"\$JAVA_HOME/bin:\$PATH\"
JAVA_HOME_BIS=\"\$root_dir_bis/$java_dir\"

CURL_HOME=\"\$root_dir/$curl_dir\"
PATH=\"\$PATH:\$CURL_HOME/winssl\"
" >> setenv.sh
JAVAW=javaw
else
JAVAW=java
fi

echo "
#JMETER_HOME=\"\$root_dir/$jmeter_dir\"
#PATH=\"\$PATH:\$JMETER_HOME/bin\"
JMETER_HOME_BIS=\"\$root_dir_bis/$jmeter_dir\"
alias jmeter='\"\$JMETER_HOME_BIS/bin/jmeter.sh\"'

#MAT_HOME=\"\$root_dir/$mat_dir\"
#PATH=\"\$PATH:\$MAT_HOME\"
MAT_HOME_BIS=\"\$root_dir_bis/$mat_dir\"
alias mat='\"\$MAT_HOME_BIS/MemoryAnalyzer\" -data \"\$MAT_HOME_BIS/workspace\" -vm \"\$JAVA_HOME_BIS/bin/'$JAVAW'.exe\" -vmargs -Xms256m -Xms4g&'

GCVIEWER_HOME_BIS=\"\$root_dir_bis/$gcviewer_dir\"
alias gcviewer=$JAVAW' -jar \"\$GCVIEWER_HOME_BIS/$gcviewer_jar\"&'

THREADLOGIC_HOME_BIS=\"\$root_dir_bis/$threadlogic_dir\"
alias threadlogic=$JAVAW' -jar \"\$THREADLOGIC_HOME_BIS/$threadlogic_jar\"&'

GATLING_HOME=\"\$root_dir/$gatling_dir\"
PATH=\"\$GATLING_HOME/bin:\$PATH\"
" >> setenv.sh

if [ "$uname" \!= "Darwin" ]; then
echo "

alias perf='typeperf \"\\System\\Processor Queue Length\" \"\\Thread(_Total)\\Context Switches/sec\" \"\\Processor(_Total)\\% Interrupt Time\" \"\\Processor(_Total)\\% User Time\" \"\\Processor(_Total)\\% Privileged Time\" \"\\System\\File Read Bytes/sec\" \"\\System\\File Write Bytes/sec\"'
" >> setenv.sh
else

echo "
alias perf=\"top -o wq -n 0 -s 1 -l 0 | perl -pe 's/\n/ - /; s/Processes/\nProcesses/'\"
# See also: http://dtrace.org/blogs/brendan/2011/10/10/top-10-dtrace-scripts-for-mac-os-x/
" >> setenv.sh
fi

echo "
echo 'Following tools installed: java, jmeter, mat, gcviewer, threadlogic, curl, perf, (gatling)'
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

@SET \"MAT_HOME=%root_dir%\\$mat_dir\"
::@SET \"PATH=%PATH%;%MAT_HOME%\"

@SET \"GCVIEWER_HOME=%root_dir%\\$gcviewer_dir\"

@SET \"THREADLOGIC_HOME=%root_dir%\\$threadlogic_dir\"

@SET \"CURL_HOME=%root_dir%\\$curl_dir\\winssl\"
@SET \"PATH=%PATH%;%CURL_HOME%\"

@SET \"CMD_HOME=%root_dir%\\bin\"
@SET \"PATH=%CMD_HOME%;%PATH%\"

@SET \"GATLING_HOME=%root_dir%\\$gatling_dir\"
@SET \"PATH=%GATLING_HOME%\\bin;%PATH%\"

@ECHO "Following tools installed: java, jm, mat, gcviewer, threadlogic, curl, perf, \(gatling\)"
" > setenv.bat
unix2dos setenv.bat

mkdir -p local/bin
pushd local/bin
echo "@START /MIN CMD /C jmeter" > jm.cmd
echo "@javaw -jar \"%GCVIEWER_HOME%\\$gcviewer_dir.jar\"&" > gcviewer.cmd
echo "@javaw -jar \"%THREADLOGIC_HOME%\\$threadlogic_dir.jar\"&" > threadlogic.cmd
echo "@START %MAT_HOME%\\MemoryAnalyzer -data %MAT_HOME%\\workspace -vm %JAVA_HOME%\\bin\\javaw.exe -vmargs -Xms256m -Xms4g" > mat.cmd
echo "@typeperf \"\\System\\Processor Queue Length\" \"\\Thread(_Total)\\Context Switches/sec\" \"\\Processor(_Total)\\%% Interrupt Time\" \"\\Processor(_Total)\\%% User Time\" \"\\Processor(_Total)\\%% Privileged Time\" \"\\System\\File Read Bytes/sec\" \"\\System\\File Write Bytes/sec\"" > perf.cmd
unix2dos *.cmd
popd

popd

kit_file=jpt-kit-${os_name}.zip
\rm -f $kit_file
archive install $kit_file

\rm -rf tmp
git clone https://github.com/arnauldvm/jpt-exercises.git tmp
apps_file=jpt-apps.zip
mv tmp/apps/setenv.sh .
rm $apps_file
archive tmp/apps $apps_file
cp -rp tmp/apps/* install
mv ./setenv.sh tmp/apps

\rm -rf solutions
mkdir -p solutions

for w in 1 2; do
  echo Retrieve solution for workshop $w
  mkdir -p solutions/w$w/jmeter
  git --git-dir=tmp/.git --work-tree=tmp checkout solutions/w$w --
  cp -rp tmp/solutions/app01 solutions/w$w/jmeter/app01
done

for w in 3 4 5a; do
  echo Retrieve solution for workshop $w
  mkdir -p solutions/w$w/jmeter
  git --git-dir=tmp/.git --work-tree=tmp checkout solutions/w$w --
  cp -rp tmp/solutions/app01 solutions/w$w/jmeter/app01
  mkdir -p solutions/w$w/apps
  cp -rp tmp/apps/app01 solutions/w$w/apps/app01
done

for w in 5b; do
  echo Retrieve solution for workshop $w
  mkdir -p solutions/w$w/apps
  git --git-dir=tmp/.git --work-tree=tmp checkout solutions/w$w --
  cp -rp tmp/apps/exercise12 solutions/w$w/apps/exercise12
done

solutions_file=jpt-solutions.zip
rm $solutions_file
archive solutions $solutions_file
\rm -rf tmp
