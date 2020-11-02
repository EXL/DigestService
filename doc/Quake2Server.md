Setup Quake II Server on CentOS 7
=================================

## Open 27910 port

Oracle Cloud admin panel:

1. Go to "Primary VNIC" on "Instance Details" and choose selected "Subnet:".
2. Choose default "Security List" and push "Add Ingress Rules" button.
3. Set "Source CIDR" to `0.0.0.0/0`, "IP Protocol" to `UDP` and "Destination port range" to `27910`.
4. Click "Add Ingress Rules" button in that pop-up window.

CentOS 7 host:

```bash
sudo firewall-cmd --zone=public --permanent --add-port=27910/udp
sudo firewall-cmd --reload
sudo firewall-cmd --zone=public --list-all
```

## Deploy Quake II Server

Unpack server distrib and enable systemd servers:

```
7za e q2ded.tar.7z
sudo tar -xvf q2ded.tar -C /srv/
rm -Rf q2ded*

# Enable Quake II Dedicated Service
sudo cp /srv/quake2/q2ded.service /etc/systemd/system/
sudo cp /srv/quake2/q2ded-restart.* /etc/systemd/system/
sudo systemctl enable q2ded
sudo systemctl start q2ded
sudo systemctl enable q2ded-restart.timer
sudo systemctl start q2ded-restart.timer
systemctl list-timers

# Disable Quake II Dedicated Service
sudo systemctl stop q2ded
sudo systemctl disable q2ded
sudo systemctl stop q2ded-restart.timer
sudo systemctl disable q2ded-restart.timer
sudo rm -Rf /etc/systemd/system/q2ded*
```

Set correct time (optionally):

```bash
sudo timedatectl set-timezone "Europe/Moscow"
```

## Building Quake II Server

```bash
sudo yum -y install epel-release
sudo yum -y upgrade
sudo yum -y install vim git logrotate openssh deltarpm yum-utils p7zip p7zip-plugins
sudo yum -y install cmake3 gcc gcc-c++ SDL2-devel libGL-devel libcurl-devel openal-devel

git clone https://github.com/yquake2/yquake2 -b QUAKE2_7_45 --depth=1
mkdir build
cd build/
cmake3 -DCMAKE_BUILD_TYPE=Release ../yquake2/
make VERBOSE=1 -j2

# Create distrib
sudo mv release/ /srv/quake2
sudo mv ~/yquake2/stuff/yq2.cfg /srv/quake2/baseq2/
sudo mv ~/quake2/baseq2/pak* /srv/quake2/baseq2/
sudo mv ~/quake2/baseq2/players /srv/quake2/baseq2/
sudo mv ~/config.cfg /srv/quake2/baseq2/
sudo rm -Rf /srv/quake2/quake2 /srv/quake2/ref_*
sudo mv ~/*.service /srv/quake2/
cd /srv/
sudo tar -cvf q2ded.tar quake2/
cp q2ded.tar ~/
sudo rm q2ded.tar
cd ~/
7za a -t7z -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on q2ded.tar.7z q2ded.tar
rm q2ded.tar
```
