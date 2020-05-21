Digest Service
==============

Digest Service is a Telegram bot for group chats combined with website. The main functionality of the service is to collect messages with the **#digest** tag in Telegram chats and display them using the **/digest** command. In addition, the bot has other features e.g. it can subscribe users to various information channels and show various quotes of currencies and precious metals.

![Digest Service Telegram bot](image/digest_service_telegram_bot.png)

The main technologies, libraries and frameworks on which Digest Service is running.

1. [Java/JVM](https://www.oracle.com/java/) language and platform by Oracle Corporation.

2. [Spring Boot](https://spring.io/projects/spring-boot) by Pivotal Software (now VMWare) and its frameworks.

    * [Spring Framework](https://spring.io/projects/spring-framework) for core features and serving web content.
    * [Spring Data JPA](https://spring.io/projects/spring-data-jpa) as a database engine layer.
    * [Spring Security](https://spring.io/projects/spring-security) for authorization purposes.
    * [Thymeleaf](https://www.thymeleaf.org/) as an HTML template engine.

3. [Java Telegram Bot API](https://github.com/pengrad/java-telegram-bot-api) library by [@pengrad](https://github.com/pengrad) for Telegram bot implementation.

![Digest Service web site](image/digest_service_web_site.png)

The Digest Service website allows you to see all the digests left by users in the main Telegram chat. In addition, the website has a search through digests, some statistics and special APIs. An example of a website ["MotoFan.Ru news in Telegram group!"](https://digest.exlmoto.ru/) that is launched using the Digest Service.

![Digest Service control panel](image/digest_service_control_panel.png)

A special control module allows administrators to manage the Digest Service e.g. delete or fix digests, send messages on behalf of the Telegram bot, change some settings etc.

## Requirements

1. [Java Runtime Environment 8+](https://www.oracle.com/java/technologies/javase-jre8-downloads.html) for running or [Java Development Kit 8+](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) for building (tested with JRE 8).
2. [PostgreSQL](https://www.postgresql.org/) database.
3. [Nginx](https://www.nginx.com/) web server (optional).

## Build & Test & Run

For example, on Linux:

1. Install JDK 8+ via your package manager.

2. Clone source code of the Digest Service via Git:

    ```shell script
    cd ~/Deploy/
    git clone https://github.com/EXL/DigestService DigestService
    ```

3. Build standalone JAR package via Gradle:

    ```shell script
    cd ~/Deploy/DigestService
    ./gradlew clean
    ./gradlew bootJar
    ```

4. Build standalone JAR package with running all tests (optional, database installation required):

    ```shell script
    cd ~/Deploy/DigestService
    ./gradlew clean
    DB_CONNECTION=jdbc:postgresql://localhost:5432/<database name> DB_USERNAME=<username> DB_PASSWORD=<password> HOST=//localhost:8080/ TG_TOKEN=<token> TG_CHAT=<chat id> PROTECT=false ./gradlew build
    ```

5. Run the Digest Service application (optional, database installation required):

    ```shell script
    cd ~/Deploy/DigestService
    DB_CONNECTION=jdbc:postgresql://localhost:5432/<database name> DB_USERNAME=<username> DB_PASSWORD=<password> HOST=//localhost:8080/ TG_TOKEN=<token> TG_CHAT=<chat id> PROTECT=false java -jar build/libs/digest-service-<version>.jar
    ```

## Deploy

For example, on clean [CentOS 7](https://wiki.centos.org/Download) Linux distribution:

1. Enable [EPEL repository](https://fedoraproject.org/wiki/EPEL) for CentOS 7:

    ```shell script
    sudo yum -y install epel-release
    ```

2. Install necessary and optional packages, settings, and update system:

    ```shell script
    cd ~/
    git clone <this repository url> DigestService

    sudo su

    passwd root

    yum -y upgrade
    yum -y install vim git logrotate openssh deltarpm yum-utils p7zip p7zip-plugins

    timedatectl set-timezone "Europe/Moscow"

    firewall-cmd --zone=public --permanent --add-service=http
    firewall-cmd --zone=public --permanent --add-service=https
    firewall-cmd --reload

    exit
    ```

3. Install and create PostgreSQL database:

    ```shell script
    sudo yum -y install postgresql-server postgresql-contrib
    sudo postgresql-setup initdb
    sudo systemctl start postgresql
    sudo systemctl enable postgresql
    sudo -i -u postgres
    vim data/pg_hba.conf # Replace "ident" to "md5".
    createdb digest
    createuser --interactive # user, n, n, n.
    psql
    ALTER USER user WITH PASSWORD 'password';
    \q
    exit
    ```

4. Install Java Environment and test application running:

    ```shell script
    sudo yum -y install java-1.8.0-openjdk # Or just "java" package.

    scp ~/Deploy/DigestService/build/libs/digest-service-<version>.jar <username>@<host-address>:/home/<username> # Run this command on build host.
    sudo mv ~/digest-service-<version>.jar /srv/

    DB_CONNECTION=jdbc:postgresql://localhost:5432/digest DB_USERNAME=user DB_PASSWORD=password HOST=//digest.exlmoto.ru/ TG_TOKEN=<token> TG_CHAT=<chat id> PROTECT=false java -jar /srv/digest-service-<version>.jar
    ```

5. Daemonize application service via [systemd](https://github.com/systemd/systemd):

    ```shell script
    cd ~/DigestService/
    sudo cp util/digest.service /etc/systemd/system

    sudo EDITOR=vim systemctl edit digest

    [Service]
    Environment=HOST=//digest.exlmoto.ru/
    Environment=TG_TOKEN=<token>
    Environment=TG_CHAT=<chat id>
    Environment=DB_CONNECTION=jdbc:postgresql://localhost:5432/digest
    Environment=DB_USERNAME=user
    Environment=DB_PASSWORD=password
    Environment=PROTECT=false

    cat /etc/systemd/system/digest.service.d/override.conf
    sudo chmod 0600 /etc/systemd/system/digest.service.d/override.conf
    cat /etc/systemd/system/digest.service.d/override.conf

    sudo systemctl enable digest
    sudo systemctl start digest

    sudo systemctl stop digest # Stop Digest Service.
    journalctl -u digest # Show Digest Service logs.
    journalctl -fu digest # Show Digest Service tail log.
    ```

6. Install Nginx server and Certbot (optional):

    ```shell script
    sudo yum -y install nginx certbot python2-certbot-nginx

    sudo setsebool -P httpd_can_network_connect 1

    sudo reboot

    sudo systemctl start nginx

    sudo certbot certonly --nginx
    echo "0 0,12 * * * root python -c 'import random; import time; time.sleep(random.random() * 3600)' && certbot renew -q" | sudo tee -a /etc/crontab > /dev/null

    cd ~/DigestService/
    sudo cp util/nginx/digest.conf /etc/nginx/conf.d/
    sudo vim /etc/nginx/conf.d/digest.conf # Change "digest.exlmoto.ru" address to yours e.g. ":%s/digest\.exlmoto\.ru/test\.exlmoto\.ru/g".

    sudo systemctl restart nginx
    sudo systemctl enable nginx
    ```

7. Add administrator profiles (optional) and finish deploying:

    Go to the **/obey/** page with "password" password and any username to enter control module. Then add some administrator profiles to the **Member** table and relaunch Digest Service with `PROTECT=true` environment variable:

    ```shell script
    sudo EDITOR=vim systemctl edit digest

    [Service]
    ...
    Environment=PROTECT=true

    cat /etc/systemd/system/digest.service.d/override.conf
    sudo chmod 0600 /etc/systemd/system/digest.service.d/override.conf
    cat /etc/systemd/system/digest.service.d/override.conf

    sudo systemctl restart digest
    ```

    Now you can sign in to the control module only with an administrator profiles information.

    *Note:* You can use the ID of your main Telegram chat as a parameter for `TG_CHAT` property and your host url for `HOST` property instead of "digest.exlmoto.ru" address.

8. Restart the server after completing the Digest Service configuration.

## Additional Information

1. [Digest Bot](https://github.com/EXL/DigestBot) is an old JavaScript implementation of similar Telegram bot.
2. Please see [Settings.md](doc/Settings.md) document for information about various Digest Service properties.
3. Please read ["Creating Digest Service" (in Russian)](https://exlmoto.ru/digest-service) article for more information about creating Digest Service project.
