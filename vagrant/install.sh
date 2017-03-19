#!/bin/sh

# Use cygwin.

PROXY_PORT=8888
PROXY_HOST=$(hostname)

# ? Required on host for VBGuestAdditions download ?
export http_proxy="http://$PROXY_HOST:$PROXY_PORT"
export https_proxy="$http_proxy"
export HTTP_PROXY="$http_proxy"
export HTTPS_PROXY="$http_proxy"
# Required on guest for yum packages management
export VAGRANT_HTTP_PROXY="$http_proxy"
export VAGRANT_HTTPS_PROXY="$http_proxy"

vagrant plugin install vagrant-proxyconf
vagrant up

