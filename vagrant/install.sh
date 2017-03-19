#!/bin/sh

# Use cygwin.

vagrant plugin install vagrant-proxyconf
vagrant up
# This will likely fail the first time => run it again
vagrant halt
vagrant up
vagrant up
#vagrant provision

