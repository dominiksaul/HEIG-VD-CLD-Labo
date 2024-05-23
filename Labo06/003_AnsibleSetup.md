# Task 3: Install Ansible

Now that you have created a VM on Google Cloud, we can configure it to add all the required software for our needs. For that we are going to use Ansible. In this task you will install Ansible on your local machine.

The installation procedure depends on the OS on your local machine. If you run Windows it is recommended you use a Linux installation to run Ansible (Windows Subsystem for Linux with Debian/Ubuntu is fine).

To install on Linux: Use Python's package manager `pip`:

```bash
sudo pip install ansible
```

To install on macOS: Use the Homebrew package manager `brew`:

```bash
brew install ansible
```

Verify that Ansible is installed correctly by running:

```bash
ansible --version
```

//TODO
[OUTPUT]
```bash
$ ansible --version
ansible [core 2.14.3]
  config file = None
  configured module search path = ['/home/dsaul/.ansible/plugins/modules', '/usr/share/ansible/plugins/modules']
  ansible python module location = /usr/lib/python3/dist-packages/ansible
  ansible collection location = /home/dsaul/.ansible/collections:/usr/share/ansible/collections
  executable location = /usr/bin/ansible
  python version = 3.11.2 (main, Mar 13 2023, 12:18:29) [GCC 12.2.0] (/usr/bin/python3)
  jinja version = 3.1.2
  libyaml = True
```

You should see output similar to the following:

![Ansible Version](./img/ansibleVersion.png)