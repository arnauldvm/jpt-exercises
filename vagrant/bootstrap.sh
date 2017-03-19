#!/bin/sh

# update all existing packages 
yum update -y

yum install -y git
yum install -y java-1.8.0-openjdk-devel

