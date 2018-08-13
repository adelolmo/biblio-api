# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/xenial64"
  config.vm.network "forwarded_port", guest: 8090, host: 18090
  config.vm.network "forwarded_port", guest: 8091, host: 18091
  config.vm.provider "virtualbox" do |vb|
    vb.name = "biblio-api"
    vb.customize ["modifyvm", :id, "--uartmode1", "file", File.join(Dir.pwd, "biblio-api-vm.log")]
  end
  config.vm.provision "shell", inline: <<-SHELL
    hostname vm
    echo vm > /etc/hostname

    echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee /etc/apt/sources.list.d/webupd8team-java.list
    echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
    echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
    apt-get update
    apt-get install -y oracle-java8-set-default postgresql-9.5 openssl
    
    mkdir -p /etc/letsencrypt/live/$(hostname)
    openssl req -new -x509 -nodes -days 1825 -out /etc/letsencrypt/live/$(hostname)/fullchain.pem -keyout /etc/letsencrypt/live/$(hostname)/privkey.pem -subj "/C=US/ST=Denial/L=Springfield/O=Dis/CN=example.com" 
    
    dpkg -i /vagrant/target/releases/*.deb
  SHELL
end
