#!/bin/sh

# Set up proxy
proxy_conf=/etc/profile.d/proxy.sh
echo "export http_proxy=http://avm-pc:8888" > $proxy_conf
echo "export https_proxy=http://avm-pc:8888" >> $proxy_conf

. $proxy_conf


# update all existing packages 
yum update -y

yum install -y git
yum install -y java-1.8.0-openjdk-devel

