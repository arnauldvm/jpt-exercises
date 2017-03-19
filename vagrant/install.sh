#!/bin/sh

# Use cygwin.

vagrant plugin install vagrant-proxyconf
vagrant up
# This will likely fail the first time => run it again
vagrant plugin install vagrant-vbguest
vagrant plugin update vagrant-vbguest
vagrant halt
vagrant up
vagrant up
#vagrant provision

