# Install

Use cygwin.

```sh
$ vagrant plugin install vagrant-vbguest
$ vagrant plugin upgrade vagrant-vbguest
$ vagrant up
# This will likely fail the first time => run it again
$ vagrant up
$ vagrant ssh
```

# History

Was initially created as:

```sh
$ vagrant init centos/7
```
